# OpenLMIS Service Style Guide
This is a WIP as a style guide for an Independant Service.  Clones of this file should reference this definition.

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

# Unit tests 
* __Every single test should be independent and isolated. Unit test shouldn't deped on another unit test.__ 

  There is one incorrect example below:
  ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    age = 17;
    boolean isAdult = ageService.isAdult(age);
    assertEquals(false, isAdult);
    testIsNotAnAdultIfAgeMoreThan18();
  }
  
  @Test
  public void testIsNotAnAdultIfAgeMoreThan18() {
    age = 19;
    boolean isAdult = ageService.isAdult(age);
    assertEquals(true, isAdult);
  }
  ```
* __One behavior should be tested in just one unit test.__
  
  There is one incorrect example below:
  ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18AndIsPersonAbleToRunForPresident() {
    int age = 17;
    boolean isAdult = ageService.isAdult(age);
    assertEquals(false, isAdult);
    
    boolean isAbleToRunForPresident = electionsService.isAbleToRunForPresident(age)
    assertEquals(false, isAbleToRunForPresident);
  }
  ```
  and correct counterpart:
    ```java
  
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    int age = 17;
    boolean isAdult = ageService.isAdult(age);
    assertEquals(false, isAdult);
  
  @Test
  public void testIsPersonAbleToRunForPresident()
    int age = 17;
    boolean isAbleToRunForPresident = electionsService.isAbleToRunForPresident(age)
    assertEquals(false, isAbleToRunForPresident);
  }
  ```
* __Every unit test should have at least one assertion.__

  There is one incorrect example below:
  ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    int age = 17;
    boolean isAdult = ageService.isAdult(age); 
  }
  ```
  
  and correct counterpart:
  ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    int age = 17;
    boolean isAdult = ageService.isAdult(age);
    assertEquals(false, isAdult);
  }
  ```
* __Don't make unnecessary assertions. Don't assert mocked behavior, avoid assertions that check the exact same thing as another unit test.__

  There is one incorrect example below:
 ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    int age = 17;
    assertEquals(17, age);
    
    boolean isAdult = ageService.isAdult(age);
    assertEquals(false, isAdult);
  }
  ```
* __Unit test have to be independent from external resources (i.e. don't connect with databases or servers)__
  
  There is one incorrect example below:
 ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    String uri = String.format("http://127.0.0.1:8080/age/", HOST, PORT);
    HttpPost httpPost = new HttpPost(uri);
    HttpResponse response = getHttpClient().execute(httpPost);
    assertEquals(HttpStatus.ORDINAL_200_OK, response.getStatusLine().getStatusCode());
  }
  ```
* __Unit test shouldn't test String Contexts. Integration test are better for this purpose.__
 
  There is one incorrect example below:
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

  There is one correct example below:
  ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    ...
  }
  ```
  and incorrect counterpart:
  ```java
  
  @Test
  public void firstTests() {
    ...
  }
  ```
* __Unit test should be repeatable - each run should yield the same result.__
 
  There is one incorrect example below:
  ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    int age = randomGenerator.nextInt(100);
    boolean isAdult = ageService.isAdult(age);
    assertEquals(false, isAdult);
  }
  ```
* __You should remember about intializing and cleaning each global state between test runs.__
 
  There is one incorrect example below:
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
    assertEquals(true, isAdult);
  }
  ```
* __Test should run fast. When we have hundreds of tests we just don't want to wait several minutes till all tests pass.__
 
  There is one incorrect example below:
 ```java
  @Test
  public void testIsNotAnAdultIfAgeLessThan18() {
    int age = 17;
    sleep(1000);
    boolean isAdult = ageService.isAdult(age);
    sleep(1000);
    assertEquals(false, isAdult);
  }
  ```
