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


## How the API responds with validation error messages

### What are Validation Error Messages?

In OpenLMIS APIs, validation errors can happen on PUT, POST, and DELETE. When validation or
permissions are not accepted by the API, invalid requests should respond with a helpful validation
error message. Generally these don't happen for a GET, because a GET is not modifying or
validating any data.

The Goal: We want the APIs to respond with validation error messages in a standard way. This will
allow the APIs and the UI components to all be coded and tested against one standard.

### Example: Permissions/RBAC
The API does a lot of permission checks in case a user tries to make a request without the needed
permissions. For example, a user may try to initiate a requisition at a facility where they don't
have permissions. That should generate a HTTP 403 Forbidden response with a JSON body like this:

```Javascript
{
  "message" : "Action prohibited because user does not have the permission: Requisition - Create",
  "messageKey" : "requisition.error.prohibited.no-permission"
}
```

When creating these error validation messages, we encourage developers to avoid repeating code.
It may be appropriate to write a shared library or a helper class that generates these JSON
validation error responses with a simple constructor.

We also don't want developers to spend lots of time authoring wordy messages. It's best to keep the
messages short, clear and simple.

To aid in translation, we want to fill in variables at the end of the message string after a ":". We
discourage the interpolation of variables into the middle of the message string, because that is a
challenge with our current translation tools. An example of this improper variable use would be:
"User account storeroom123 does not have permission to initiate requisitions at clinic789".

Message key names are important for translations. Keys should follow our [Style Guide i18n Naming
Conventions](https://github.com/OpenLMIS/openlmis-template-service/blob/master/STYLE-GUIDE.md#i18n-naming-conventions).

Future: We may extend this to support an array of multiple messages (each one with its "message" and
"messageKey").

### Example: Validation Not Accepted

The APIs do some validation deep within an object that may need to return a validation error message
for a specific field. For example, a user may try to save a requisition with a Stock Adjustment
decrement that is larger than the amount of stock on hand and stock received. If there were a
beginning quantity of 10, a received quantity of 20 (for a total of 30 on hand), then it would be
invalid to transfer out 100. This kind of validation may be flagged on the client side first and not
sent to the server. But the server also performs validation to make sure data it saves is valid.
This PUT request to save a requisition with an invalid combination of data fields should generate a
HTTP 422 Unprocessable Entity response with a JSON body like this:

```Javascript
{
  "message" : "Requisition cannot be saved with invalid quantity",
  "messageKey" : "requisition.error.quantity.invalid",
  "fields" : [{
    "name" : "requisitionLineItems",
    "id" : "21c3015e-ff67-433a-a342-972805c93aa8",
    "fields" : [{ "name" : "totalLossesAndAdjustments" }]
  }]
}
```

It is optional to provide "fields", but when provided this information helps specific what exact
field has the validation error. This may be used by the UI to provide an error message on a
specific field. In the example above, the nested use of fields specifies that the
"requisitionLineItems" property has the error, the specific line item is UUID "21c301..", and
within that the "totalLossesAndAdjustments" field is the field with the validation error.

Inside "fields" objects, the "name" and "id" are optional. But if there is a specific field or a
specific id that is causing the validation error, we encourage API developers to provide these
when possible. The "id" can help to provide a UUID for the specific part of the record that has an
issue, such as one of the Line Items within a Requisition. The "name" can help when a particular
property is the cause of the validation error. It's important to use the API JSON property names,
not the DB field names and not the UI field names or DOM locations, so that neither side of the
boundary between back-end API and front-end clients apps needs to know too much about the other
side.

Notice that "fields" is an array, so multiple validation errors at any level can be flagged.

Future: We may extend this to support allowing each field to have its own "message" and "messageKey".

### Translation/i18n

As mentioned above, "messageKeys" will help in translation. We expect that the source code of
OpenLMIS services will include message strings written in English. Any running instance of an
OpenLMIS API service may use a translation property file to translate these messages at run time.
So any running OpenLMIS system may be returning its "messages" in the language of choice based
on its configuration.

### Do we return these on Success?

On success of a PUT, POST or DELETE, generally the API should return the updated resource.
Sometimes it may be appropriate to give an empty response. But success responses should not include
a validation message of the type specified here. This will eliminate the practice which was done
in OpenLMIS v2, EG:

```Javascript
PUT /requisitions/75/save.json
Response: HTTP 200 OK
Body: {"success":"R&R saved successfully!"}
```

Generally, success is a 2xx HTTP status code and we don't return validation error messages on
success. Generally, validation errors are 4xx HTTP status codes (client errors). Also, we don't
return these validation error messages for 5xx HTTP status codes (server or network errors);
OpenLMIS software does not always have control over what the stack returns for 5xx responses (those
could come from NGINX or even a load balancer).

### Proposed RAML

```Javascript
schemas:
  - errorResponseFields: |
    { "type": "object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "title": "ErrorResponseFields",
      "description": "Fields listed as part of an Error Response",
      "properties": {
        "name": { "type": "string", "required": false, "title": "name of the property" },
        "id": { "type": "string", "required": false, |
          "title": "id, often a UUID, of the object that caused an error" },
        "fields": {"type": "array", "required": false, |
          "title": "fields within this one that caused the error",
          "items": { "type": "object", "$ref": "#/schemas/errorResponseFields" }, "uniqueItems": false}
      }
    }

  - errorResponse: |
    { "type": "object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "title": "ErrorResponse",
      "description": "Error response",
      "properties": {
        "message": { "type": "string", "required": true, "title": "error message" },
        "messageKey": { "type": "string", "required": true, "title": "key for translations" },
        "fields": {"type": "array", "required": false, "title": "fields that caused the error", |
          "items": { "type": "object", "$ref": "#/schemas/errorResponseFields" }, "uniqueItems": false}
      }
    }

/requisitions:
  /{id}:
    put:
      description: Save a requisition with its line items
      responses:
        403:
        422:
          body:
            application/json:
              schema: errorResponse
```

### HTTP Status Codes

The uses of HTTP 403 and 422 above are examples. Further guidance is coming soon with a suggested
list of all OpenLMIS standard HTTP response codes and how we use them in OpenLMIS services.
