# Error Handling Conventions

OpenLMIS would like to follow error handling best practices, this document covers the
conventions we'd _like_ to see followed in the various OpenLMIS components.

## Java

Java and Exceptions have a long history in how they were designed to be used, and how they've often
been abused in terms of the approach to error handling that is employed.

### A pattern for normal error-handling

Most all code has to handle "normal" errors.  Normal errors that include things like malformed, 
incorrect or inappropriate input from a client.  Since most code has to handle errors, it 
should be obvious and straightforward which errors that code handles and how it handles them.
The goal of which is to improve clarity in testing the code and the paths that are taken should an 
error be encountered.

The pattern we'd like to use in OpenLMIS is to avoid throwing exceptions and instead to utilize
the return type of a method to indicate if any error conditions were encountered.

A few simple types to help us with this:

```Java
public class ErrorCode {
  private String code;
  private String label;
  private String message;
  ...
}

public class CheckReturn<E> {
  private E successPayload;
  private List<ErrorCode> errors;
  
  public CheckReturn<E>() {...}
  
  public void addError(ErrorCode errorCode) {...}
  public void setSuccess(E successPayload) {...}
  public boolean hasError() {...}
  public E returnSuccess() { return successPayload; }
}
```

This pattern defines some basic types:

* ErrorCode would likely be an immutable value class that helps us construct errors, their messages 
(and translated message), potentially a numerical code, compare errors, etc.
* CheckReturn is a utility class that helps facilitate returning _this_ OR (exclusive) _that_ 
from a method call.  i.e. either return some successful payload, or indicate that error(s) 
were encountered, _never_ both.

We then use these types in some application code:

```Java
public class Foo {
  public CheckReturn<String> foo() {
    CheckReturn cRet = new CheckReturn<String>();
    ...
    if (someMethod().isPresent()) cRet.addError(...);
    if (someOtherMethod.isPresent()) cRet.addError(...);
    if ( false == cRet.hasErrors() ) cRet.setSuccess("foo");
    return cRet;
  }
}

public class Bar {
  public void bar() {
    Foo myFoo = ...
    CheckReturn<String> fooRet = myFoo.foo();
    if( fooRet.hasErrors() {
      ...
    } else {
      System.out.println("Can foo get a " + fooRet.returnSuccess());
  }
}
```

* Foo returns a CheckReturn which here we can assume is either a successful return of the method
  foo(), OR a failure in that it will add a number of ErrorCodes.
* Bar when using Foo, then asks the CheckReturn from foo() if the method was successful or not. 
  If errors are present, it would handle them, possibly itself returning another CheckReturn.
  
CheckReturn here is meant to convey that a method can either be successful or not.  However there
are variations on this theme:

* a method call might either return something, or it may not - but it may not warrant a full error
code.  This is usually encountered for methods that find and return an object.  In such a case 
instead of returning `null`, we should instead return 
[Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html).
* a method call might need to return something _and_ return that there were some errors. This is 
a less common case, however if it's needed it should be evident in the return type by adding 
another type similar to CheckReturn, that describes this contract.  An example of this is a tuple
which in Java is easily implemented with Functional Java's 
[P2<A, B>](http://www.functionaljava.org/javadoc/4.6/functionaljava/fj/P2.html).


#### Summary

* Exceptions should be avoided.  If an exception is being used, it should be for truly
_exceptional_ conditions; never for simple validation.  Checked exceptions should only be declared 
when the condition is _recoverable_ by the client.  Runtime exceptions should only be used 
to indicate programming errors. _Effective Java_ by Joshua Bloch enumerates these concepts further.
* Error handling should be evident through a method's return type.
* Most error handling should use an "either" approach such as CheckReturn above.
This is most similar to Scala's 
[Optional](http://www.scala-lang.org/api/current/scala/Option.html) or Functional Java's 
[Either](http://www.functionaljava.org/javadoc/4.6/functionaljava/fj/data/Either.html).
* Methods that don't need to return ErrorCodes should avoid returning `null` and 
instead use Java 8's [Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html).
* Methods that need to be able to return both successful objects _and_ errors should use a 
return type which is a tuple, such as Functional Java's 
[P2<A, B>](http://www.functionaljava.org/javadoc/4.6/functionaljava/fj/P2.html).


### Exceptions - what we do want

TODO

### Exceptions - what we don't want

Lets look at a simple example that is indicative of the sort of code we've been writing using
exceptions.  This example consists of a web-endpoint that returns a setting for a given key, which
hands off the work to an application service layer that uses the key provided to find the given
setting.

A controller (HTTP end-point) that is asked to return some setting for a given "key"
```Java
@RequestMapping(value = "/settings/{key}",  method = RequestMethod.GET)
public ResponseEntity<?> getByKey(@PathVariable(value = "key") String key) {
  try {
    ConfigurationSetting setting = configurationSettingService.getByKey(key);
    return new ResponseEntity<>(setting, HttpStatus.OK);
  } catch (ConfigurationSettingException ex) {
    return new ResponseEntity(HttpStatus.NOT_FOUND);
  }
}
```

The service logic that finds the key and returns it (i.e. configurationSettingService above):
```Java
public ConfigurationSetting getByKey(String key) throws ConfigurationSettingException {
  ConfigurationSetting setting = configurationSettingRepository.findOne(key);
  if (setting == null) {
    throw new ConfigurationSettingException("Configuration setting '" + key + "' not found");
  }
  return setting;
}
```

In this example we see that the expected end-point behavior is to either return the setting asked
for and an HTTP 200 (success), or to respond with HTTP 404 - the setting was not found.

This usage of an Exception here is not what we want for a few reasons:

* Not finding the supplied key through a web-endpoint is not exceptional. This is clearly 
 evident in that the behavior desired for the client is for the web-endpoint to return 
 the setting asked for, or a not-found message.
* The situation is not recoverable.  No matter how many times the client attempts to find
 the given key, it will not find it.  If this were a recoverable situation, we would expect that 
 the exception we're handling would be of a type that helps us do _something_ that would allow us
 to retrieve the setting we're seeking.  If the setting configuration lived in an external service,
 then the code that is responsible for interacting with that service might throw checked exceptions
 that would help a client handle situations where it needed to wait for the setting Service to
 become available.  Even if this was an external Service however, we still wouldn't expect to
 handle conditions where the key truly wasn't found nor would we likely expect to see this in the
 web-endpoint logic.
* This code is flagged by static analysis 
 [tools](http://sonar.openlmis.org/issues/search#issues=AVc18ErL0QRqkcp89olY) with the error that 
 this exception should be "Either log or re-throw this exception".  This error could simply be 
 corrected for the static analysis tool by logging the exception, however this would 
 likely result in a plethora of log messages that only indicate that a user asked for a key that
 didn't exist - which as noted previously isn't really appropriate as the condition isn't
 exceptional.
 


## Further information

https://www.ibm.com/developerworks/library/j-jtp05254/

http://stackoverflow.com/questions/613954/the-case-against-checked-exceptions

http://www.shaunabram.com/checked-exceptions-article/

http://www.oracle.com/technetwork/articles/entarch/effective-exceptions2-097044.html

http://alvinalexander.com/scala/best-practice-option-some-none-pattern-scala-idioms

optional:  http://www.oracle.com/technetwork/articles/java/java8-optional-2175753.html