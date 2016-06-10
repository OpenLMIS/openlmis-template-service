# OpenLMIS Service Style Guide
This is a WIP as a style guide for an Independent Service.  Clones of this file should reference this definition.

For the original style guide please see:
https://github.com/OpenLMIS/open-lmis/blob/master/STYLE-GUIDE.md

---

# Java
OpenLMIS has [adopted](https://groups.google.com/d/msg/openlmis-dev/CCwBglBFbpk/pY406WbkAAAJ) the [Google Java Styleguide](https://google.github.io/styleguide/javaguide.html).  These checks are *mostly* encoded in Checkstyle and should be enforced for all contributions.

# Postgres Database
In most cases, the Hibernate DefaultNamingStrategy follows these conventions.  Schemas and table names will however need to be specified.

* Each Independent Service should store it's tables in its own schema.  The convention is to use the Service's name as the schema.  e.g. The Requistion Service uses the `requisition` schema
* Tables, Columns, constraints etc should be all lower case.
* Table names should be pluralized.  This is to avoid *most* used words. e.g. orders instead of order
* Table names with multiple words should be snake_case.
* Column names with multiple words should be merged together.  e.g. `getFirstName()` would map to `firstname`

# i18n Naming Conventions
These naming conventions will be applicable for the messages property files.

* Keys for the messages property files should follow a hierarchy. However, since there is no 
official hierarchy support for property files, keys should follow a naming convention of most to 
least significant.
* Key hierarchy should be delimited with a period (.).
* The first portion of the key should be the name of the Independent Service.
* The second portion of the key should indicate the type of message; error for error messages, 
message for anything not an error.
* The third and following portions will further describe the key.
* Keys should use only lowercase characters (NO camelCase).
* If multiple words are necessary, they should be separated with a dash (-).

Examples:

* `requisition.error.product.code.invalid` - an alternative could be `requisition.error
.product-code.invalid` if code is not a sub-section of product.
* `requisition.message.requisition.created` - requisition successfully created.
* `reference-data.error.facility.not-found` - facility not found.

Note: UI-related keys (labels, buttons, etc.) are not addressed here, as they would be owned by the
UI, and not the Independent Service.

# Unit tests
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
* __Unit tests shouldn't test Spring Contexts. Integration test are better for this purpose.__

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

# Testing services dependent on external APIs
OpenLMIS is using WireMock for mocking web services. An example integration test can be found here:
https://github.com/OpenLMIS/openlmis-example/blob/master/src/test/java/org/openlmis/example/WeatherServiceTest.java

The stub mappings which are served by WireMock's HTTP server are placed under _src/test/resources/mappings_ and _src/test/resources/__files_
For instructions on how to create them please refer to http://wiremock.org/record-playback.html
