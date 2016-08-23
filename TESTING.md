# Testing Guide
This guide is intended to layout the general automated test strategy for OpenLMIS.

## Test Strategy
OpenLMIS, like many software projects, relies on testing to guide development and prevent regressions.  To effect this we've adopted a standard set of tools to write and execute our tests, and categorize them to understand what types of tests we have, who writes them, when they're written, run, and where they live.


## Types of Tests
The following test categories have been identified for use in OpenLMIS.  As illustrated in this great [slide deck](http://martinfowler.com/articles/microservice-testing/), we expect the effort/number of tests in each category to reflect the [test pyramid](http://martinfowler.com/articles/microservice-testing/#conclusion-test-pyramid):

* [Unit](#unit)
* [Integration](#integration)
* [End-to-End](#e2e)

### Unit tests <a name="unit"></a>

Summary
* Who:  written by code-author
* What: the smallest unit (e.g. one piece of a model's behavior, a function, etc)
* When: at build time, should be /fast/ and targetted - I can run just a portion of the test suite
* Where: able to access package-private scope of thing under test
* Why: to help test the fundamental pieces/functionality, helps guide and document design, protects against regression and guides future re-factors.

#### Unit test examples

* __Every single test should be independent and isolated. Unit test shouldn't depend on another unit test.__

  DO NOT:
  ```java
  List<Item> list = new ArrayList<>();

  @Test
  public void testListSize1() {
    Item item = new Item();
    list.add(item);
    assertEquals(1, list.size());
  }

  @Test
  public void testListSize2() {
    Item item = new Item();
    list.add(item);
    assertEquals(2, list.size());
  }
  ```
* __One behavior should be tested in just one unit test.__

  DO NOT:
  ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18AndIsPersonAbleToRunForPresident() {
    int age = 17;
    boolean isAdult = ageService.isAdult(age);
    assertFalse(isAdult);

    boolean isAbleToRunForPresident = electionsService.isAbleToRunForPresident(age)
    assertFalse(isAbleToRunForPresident);
  }
  ```
  DO:
    ```java

  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    int age = 17;
    boolean isAdult = ageService.isAdult(age);
    assertFalse(isAdult);
  }

  @Test
  public void testIsPersonAbleToRunForPresident() {
    int age = 17;
    boolean isAbleToRunForPresident = electionsService.isAbleToRunForPresident(age)
    assertFalse(isAbleToRunForPresident);
  }
  ```
* __Every unit test should have at least one assertion.__

  DO NOT:
  ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    int age = 17;
    boolean isAdult = ageService.isAdult(age);
  }
  ```

  DO:
  ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    int age = 17;
    boolean isAdult = ageService.isAdult(age);
    assertFalse(isAdult);
  }
  ```
* __Don't make unnecessary assertions. Don't assert mocked behavior, avoid assertions that check the exact same thing as another unit test.__

  DO NOT:
 ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    int age = 17;
    assertEquals(17, age);

    boolean isAdult = ageService.isAdult(age);
    assertFalse(isAdult);
  }
  ```
* __Unit test has to be independent from external resources (i.e. don't connect with databases or servers)__

  DO NOT:
 ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    String uri = String.format("http://127.0.0.1:8080/age/", HOST, PORT);
    HttpPost httpPost = new HttpPost(uri);
    HttpResponse response = getHttpClient().execute(httpPost);
    assertEquals(HttpStatus.ORDINAL_200_OK, response.getStatusLine().getStatusCode());
  }
  ```
* __Unit test shouldn't test Spring Contexts. Integration tests are better for this purpose.__

  DO NOT:
 ```java
  @RunWith(SpringJUnit4ClassRunner.class)
  @ContextConfiguration(locations = {"/services-test-config.xml"})
  public class MyServiceTest implements ApplicationContextAware
  {

    @Autowired
    MyService service;
    ...
      @Override
      public void setApplicationContext(ApplicationContext context) throws BeansException
      {
          // something with the context here
      }
  }
  ```
* __Test method name should consistently show what is being tested.__

  DO:
  ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    ...
  }
  ```
  DO NOT:
  ```java

  @Test
  public void firstTests() {
    ...
  }
  ```
* __Unit test should be repeatable - each run should yield the same result.__

  DO NOT:
  ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    int age = randomGenerator.nextInt(100);
    boolean isAdult = ageService.isAdult(age);
    assertFalse(isAdult);
  }
  ```
* __You should remember about intializing and cleaning each global state between test runs.__

  DO:
  ```java
  @Mock
  private AgeService ageService;
  private age;

  @Before
  public void init() {
    age = 18;
    when(ageService.isAdult(age)).thenReturn(true);
  }

  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    boolean isAdult = ageService.isAdult(age);
    assertTrue(isAdult);
  }
  ```
* __Test should run fast. When we have hundreds of tests we just don't want to wait several minutes till all tests pass.__

  DO NOT:
 ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    int age = 17;
    sleep(1000);
    boolean isAdult = ageService.isAdult(age);
    sleep(1000);
    assertFalse(isAdult);
  }
  ```

### Integration Testing <a name="integration"></a>

Summary:
* Who: Code author
* What: Test basic functionality of the integration of two components
* When: Run-time when the components are wired together
* Where: Can run locally (likely lives in Service), however must useful in CI environments
* Why:  To ensure the basic contracts between Services/components are working and are available

TODO:  write example of a web-service test (webserver to 

1. Webserver to application code
  * tests proper serialization and de-serialization - basic contract
  * typically input and output is JSON and HTTP status codes
  * doesn't tests (mocks) database, external services (e.g. auth), etc
2. Application code to Database
  * tests that ORM/mappers work with DB schema
3. Application code (service) to an external service
  * tests that external service is available (and perhaps correct version)
  * tests basic JSON and HTTP status codes
  * doesn't duplicate integration tests that the external service should have
4. Service Discovery
  * tests if service register endpoints with nginx
  * all other services mocked out, not an e2e test

### End-to-End <a name="e2e"></a>

Summary:
* Who: Likely written by QA 
* What: User-flow (multiple pieces all together like a user would do)
* When: Written when user-visibile functionality is ready
* Where: Stored in Blue, Run in CI typically
* Why: To show user-functionality working - all components are working together

TODO:  needs tool standardization (postman? selenium?)


## Testing services dependent on external APIs
OpenLMIS is using WireMock for mocking web services. An example integration test can be found here:
https://github.com/OpenLMIS/openlmis-example/blob/master/src/test/java/org/openlmis/example/WeatherServiceTest.java

The stub mappings which are served by WireMock's HTTP server are placed under _src/test/resources/mappings_ and _src/test/resources/__files_
For instructions on how to create them please refer to http://wiremock.org/record-playback.html
