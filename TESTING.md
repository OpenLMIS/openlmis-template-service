# Testing Guide
This guide is intended to layout the general automated test strategy for OpenLMIS.

## Test Strategy
OpenLMIS, like many software projects, relies on testing to guide development and prevent regressions.  To effect this we've adopted a standard set of tools to write and execute our tests, and categorize them to understand what types of tests we have, who writes them, when they're written, run, and where they live.

The objective of manual and automatic tests are to catch bugs as quickly as possible after code is committed to make it less time-consuming to fix. We plan to automate tests as much as possible to ensure better control of product quality.
All tickets should be tested manual by QA Team, selected tests will be automated at API level (by Cucumber) and UI level (ie. by Selenium).

Test Strategy document describes the following issues: 
* UI testing – include list of types of devices/browsers are supported and which are prioritize for manual testing
* QA responsibility
* Tools – which tools QA Team use for testing OpenLMIS
* Testing Standards - UI style guide compatibility, translations, performance standards
* Testing workflow – describe workflow for manual testing, automated testing and regression, describe the way of reporting bugs, includes bug report pattern and acceptance criteria workflow
* Testing environments and updating test data
* Regression Testing
* Requirements traceability - describe how to use labels and test cycles to support Zephyr traceability reports

## Manual Testing vs Automated Testing

Testing Standards:
* Follows UI guidelines
* Internationalization
* Form Entry and Errors
* Loading Performance
* Offline Performance
* Role Based Access Control
* Exception Scenarios

### UI Testing

This section includes list of types of devices/browsers are supported and which are prioritize for manual testing.

Past versions of OpenLMIS have officially supported Firefox. For OpenLMIS 3.0, we are prioritizing support of Chrome because of global trends (eg see Mozambique Stats) along with its developer tools and its auto-updating nature.

For QA testing of OpenLMIS our browser version priorities are:
1. Chrome 52+ (test on Chrome 52 and Chrome latest)
2. Firefox 48+ (test on Firefox 48 and Firefox latest)

The next most widely-used browser version is IE 11, but we don't recommend testing and bug fixes specifically for any Internet Explorer compatibility in OpenLMIS.

The operating systems on which we should test in are:
1. Windows 7 (by far the most widely used in Mozambique, Zambia and Benin data AND globally)
2. Windows 10

Note: The QA team is doing some testing using Linux (Ubuntu) workstations. That is fine for testing the API, but Linux is not a priority environment for testing the UI and for final testing of OpenLMIS. It's important to test the UI using Chrome and Firefox in Windows 7 and Windows 10.

In other words, OpenLMIS developers and team members may be using Mac and Linux environments. It is fine to report bugs happening in supported browsers (Chrome and Firefox) on those platforms, but we won't invest QA time in extensive manual testing on Mac or Linux.

We have asked for different OpenLMIS implementations to share their google analytics to better inform how we prioritize and invest in browser and device support going forward.

### Supported Devices

OpenLMIS 3.0 is only officially supporting desktop browsers with use of a pointer (mouse, trackpad, etc). The UI will not necessarily support touch interfaces without a mouse pointer, such as iPad or other tablets. For now, we do not need to conduct testing or file bugs for tablets, smart watches, or other devices.

### Screen Size

We suggest testing with the most popular screen sizes:
1. 1000 x 600 (this is a popular resolution for older desktop screen sizes; it is the 16:9 equivalent of the age-old 1024x768 size)
2. 1300 x 975 (this is a popular resolution for newer laptop or desktop screens)

The UI should work on screens within that range of sizes. Screen size can be simulated in any browser by changing the size of the browser window or using Chrome developer tools.

### Bandwidth

OpenLMIS version 3 is tested using a bandwidth of 384 Kbps, which is equivalent to a 3G (WCDMA standard) connection. We recommend end users using either this speed or higher for optimal usability.

## Responsibility

Developers (all teams):
* Write unit tests for all code (we want test coverage of 80%-100%; currently it is 25%-40%, perhaps because some tests are disabled currently; see [Sonar](sonar.openlmis.org).
* Write integration tests and components tests as assigned in tickets or as part of acceptance criteria.

### Communication

Weekly meetings are scheduled to discuss QA topics and the daily communication is completed in the #QA slack channel for OpenLMIS. The weekly meeting notes are maintained in [QA Weekly meeting notes](https://openlmis.atlassian.net/wiki/spaces/OP/pages/114170699/QA+Weekly+meeting+notes).

## Test Case Best Practices

### Creating a Test Case for an Edge Case or Unhappy Path Scenario

One needs to add several types of details in the test case description. These include pre-conditions, i.e. conditions that need to be met before the test case can be executed (e.g. At least one approved requisition needs to be created in the system; Logging into the application), the acceptance criteria from the ticket, a description of
the scenario/workflow (i.e. whether it is a happy-path-one or an edge case, and what the workflow looks like; e.g. User X authorizes a requisition, User Y rejects it, User X authorizes the requisition again, etc.), and of the test case itself (e.g. Tests whether it is possible to edit an approved requisition; Tests whether authorized users
can approve requisitions). In order to facilitate the execution of test cases concerning requisitions, one has to include the link to the [Requisition States and Workflow](https://openlmis.atlassian.net/wiki/spaces/OP/pages/113973375/Requisition+States+and+Workflow) diagram in the pre-conditions of such test cases.

If possible, one should not include too detailed data in the test case, e.g. user, facility or program names, as they may vary, depending on the implementation. So one should write e.g.: Choose any program (e.g. Family Planning); Knowing the credentials of any Stock Manager (e.g. srmanager2); Knowing the credentials of any user authorized
to approve requisitions (e.g. administrator), instead of: Choose the Family Planning program or Knowing the password of srmanager2. Information on user names, roles and rights is available [here](https://github.com/OpenLMIS/openlmis-referencedata/tree/master/src/main/resources/db/demo-data#roles-users-and-rights).
Providing example test data can be especially helpful for users who are not very familiar with the system. One also has to remember to include the test data that are indispensable for the execution of the test case in the "Test Data" column for a given test step. In principle, all data that are necessary to execute the test case
should be included in it (e.g. mock-ups) - one should write the test case in such a way that it's not necessary to go to the tested ticket in order to test it. 

Ideally, a test case should contain up to 40 steps at most. One can usually diminish their number by describing test actions in a more general manner, e.g.: Approve the requisition, instead of writing: Go to Requisitions > Approve, and describing the rest of the actions necessary for the achievement of the desired goal. Adding suitable pre-conditions,
such as: Initiating, submitting and authorizing a requisition, instead of adding steps describing all of these actions in detail, also results in a shorter test case. Ideally, one should include all actions that are not verifying a given feature/bug fix in the pre-conditions. If it is not possible to keep the test case within the above-mentioned limit,
one should consider creating more than one for a given ticket. Sometimes, splitting the testing of the ticket into more than one test case will prove impossible, though.

If this won’t result in a too long test case, one should include both the test steps describing positive testing (happy path scenario) and those concerning negative testing (edge case/unhappy path scenario) in one test case. A happy path scenario or positive testing consists in using a given feature in the default, most usual way,
e.g. when there is a form with required and optional fields, one can first complete all of them or only the required ones and check what happens when one tries to save it. An edge case or negative testing will consist, in this example, in checking what happens when trying to save the form when one or more of the required fields are blank.
It is advisable to test the happy path first and then, the edge cases, as the happy path is the most likely scenario and will be most frequently followed by users. Edge cases are the less usual and, frequently, not obvious scenarios, which are not likely to occur often but still need to be tested
in order to prevent failures in the application from happening. They are taken into account because of software complexity (many possible variants of its utilization and thus situations that might occur), and because users, like all people, vary and can make use of the application in different ways.

If it proves impossible to contain all workflows in one test case, the happy path and the edge case(s) have to be described in separate ones, e.g. there should be one test case describing the testing of the happy path and one for each edge case.
One also has to write separate test cases if the happy path and the edge case(s) contain contradictory steps or pre-conditions;
e.g. the former concerns approving a newly-created requisition and the latter e.g. approving an already-existing old one, not meeting the currently-tested, new requirement.

### Updating a Test Case for a Bug

When one has to test a bug, one needs to browse through the existing test cases to check whether one that covers the actions performed when testing the bug fix already exists. In virtually all cases, this will be the case. If it is so, one needs to update the test case if necessary and link it to the ticket with the bug.
Writing new test cases for bugs is not recommended and has to be avoided. Some bugs are found during the execution of already-existing test cases. In such a situation, the test case will already be linked to the ticket with the bug. One then needs to review it, and if there is a need for it, update it.
In most cases, this will not be necessary. Note that sometimes, the bug may in fact not be related to the test case during the execution of which it had been discovered, or it might occur by the end of a given test case, and the preceding steps might not be necessary in its reproduction.
In both of these situations, one needs to find the test case that is most likely to cover the feature that the bug concerns and update it accordingly. One also has to keep in mind that test cases have to support and test the functionality, as well as provide a way to ensure that the bug has been fixed.

If the bug wasn’t found during the execution of any test case, one still needs to check whether there is one containing steps enabling one to reproduce the bug. In order to do so, one needs to go to Tests > Search Tests in the top menu on Jira. Then, one is able to browse through all test cases in the project.
Since there might be many of them, it is advisable to use a filter. The first option is to enter word(s) that in one’s opinion might occur in the test case in the Contains text input field and press Enter. The second one is clicking on More and choosing the criteria. Those that are most likely to prove helpful are Label and Component.
One can also use already-existing global filters, which can be found by choosing the "Manage filters" option from the main Jira menu. Then, it'll be possible to search for and use the desired filter(s). They include e.g. Administration tests or Stock Management test cases, and might prove especially useful when searching for test cases.

### Exploratory Testing

When one is familiarizing oneself with the project or when one already knows the application but there are no tickets currently to test, one can perform exploratory testing. This kind of testing is not related to any ticket. It consists in testing the application without any previous plan, exploring it, in a way.
It can be also considered as a form of informal regression testing, as frequently, bugs resulting from regression are found during it. While performing this kind of tests, it is advisable to be as creative as possible – to experiment with testing techniques and test steps, and not to follow the happy path but the edge cases, or to try to find the latter.

Exploratory testing in the UI will focus on testing edge cases and causing errors related to the following categories: Functional issues, UI to Server failure, Configuration issues, Visual inconsistencies, and Presentational issues.

### Bug Tracking

It is important to track the bugs introduced during each sprint. This process helps identify test scenarios that may need more attention or business process clarification. Bug tracking also helps identify delays with ticket completion.
* When a test case fails, a bug must be created and linked to the ticket. This bug must be resolved before the ticket can be marked as done.
* The same test case associated with the sprint's test cycle should be run (previous practice was to create a new test cycle but that makes the status of the test case in the reporting as failed and then a separate row shows as passed, very confusing)
* If bug is created for a test case, it must be resolved before the test is executed again with the sprint cycle. 

### Bug Triage During the Sprint

The bug triage meeting is held twice every sprint - i.e. once a week. Before the meeting, bugs with the "Roadmap" status are attempted to be reproduced and if they still occur, their status is changed to "To Do". If not, they are moved to "Dead".
During the meeting, the bugs reported are analyzed in terms of their significance to the project, their final priority is set and the acceptance criteria are updated.

### Manual Testing Workflow

When the Dev finishes implementing a ticket, he/ she should change status from In Progress to QA. QA should check if all acceptance criteria are correct and actual.

Main QA Manual Workflow consist of following points:
1. QA starts testing when ticket is moved to QA column in Jira sprint board, or when the ticket status has changed to QA. There is no automatic notification for this until the ticket is assigned, so QA must manually check or the Dev must notify the QA.
(Notification to QA must be via QA Slack channel, or directly, and in the General slack channel referencing the ticket that needs to be tested.)
2. QA deploys changes to test environment (uat.openlmis.org).
3. QA creates test case, or executes existing test case that is linked to ticket. QA should test all acceptance criteria in ticket. The test case must be associated with the current sprint test cycle.
4. If bugs are found, QA should change status for In Progress and assigned ticket to proper developer.
    * When a bug is created it must be linked to the ticket.
    * QA notifies developer that ticket has been moved back to In Progress (mention in ticket or mention in QA slack channel).
5. When all works properly QA should change status for Done and assigned ticket to proper team leader.

During the testing process, QA should write comments in the ticket about testing features and add print screens of testing features.

### Workflow Between QA Teams

To improve testing process testers should help each other:
* If one of the team members doesn't have anything to do in their own team they will assist other team with testing
* If any tester needs help testing they can report it on the QA Slack channel

### Ticket Workflow and Prioritize

In the OpenLMIS project tickets may have the following statuses:
* To Do - tickets intended for implementation
* In Progress - tickets during implementation or returned to the developer to fix bugs
* In Review - tickets in code review
* QA - tickets which should be tested
* Done - tickets which work correctly, they are ready to close

Ticket priorities are described in the [Prioritizing Bugs](https://openlmis.readthedocs.io/en/latest/contribute/contributionGuide.html#prioritizing-bugs).

It is very important to start testing tickets with Blocker and Critical priority. When QA is in the middle of testing process for task with a lower priority (ie. Major, Minor or Trivial), and task with higher priority (ie. Blocker or Critical)
returns with changes to QA, as soon as possible QA should complete testing task with lower priority and begin testing task with higher priority.

Additionally tickets in the [Sprint table]( https://openlmis.atlassian.net/secure/RapidBoard.jspa?rapidView=46 ) in QA column should be prioritized. Tickets with high priority (ie. Blocker or Critical) should be located on top of the QA column.
Every morning QA Lead should set task in QA column in the correct order.

### Workflow for Blocked Tickets

During testing there is a problem of blocked tickets. This is the situation when, because of external faults (not related to the content of the task), the tester can not assess a particular task.
In that case, change ticket status to In Progress and assign to appropriate component leader, whose team is able to solve this external problem. It is important to accurately describe the problem in ticket's comment part.
In case of doubt, which component leader to choose, QA should assign ticket to the team leader with a request to identify the right person.

## Testing Environments

Final testing of all features should happen on the [UAT server](https://uat.openlmis.org/). Some testing may also happen on the [test server](https://test.openlmis.org/), in case the UAT server is temporarily unavailable.
The UAT server's rebuilding is triggered manually. In order to rebuild it, and thus perform the test on the latest changes, one has to log into [Jenkins](http://build.openlmis.org/) and schedule a build of the OpenLMIS-3.x-deploy-to-uat job.
One needs to do it when no other jobs are in the build queue, so to to ensure that the server contains the latest changes. After the build is successful, one has to wait for a several minutes and then, it is possible to start testing.
Several minutes before scheduling the re-deploy to UAT, one has to inform others on the #qa Slack channel that one intends to do so because others might be using the server and after the re-deploy, all data they entered will be wiped.
The test server always contains the latest changes but its use is not normally recommended, as it is very frequently rebuilt (after the rebuilding, all data entered during testing are erased) because it is used for development work.
In order to be sure that one contains the most-recent changes, one has to log into the application and look at the upper-right corner, where information on when the software had last been updated is visible.
If only the e.g. Software last updated on 17/05/2018 text is visible, the software is up to date. As soon as it becomes outdated, information that there are updates available appear next to the text concerning the date of the last update.

## Updating Test Data

Before a big regression test, QA must update the test users and scenarios to support each of the features that are part of the big regression test.
It is also important to note that a few test cases entail changing the permissions of demo data users. Before executing such a test case, one needs to inform others on the #qa Slack channel that one is going to change these permissions.
Having executed the test case, one has to restore the user's permissions to the default ones manually or by scheduling a re-deploy to UAT. In both cases, one also has to inform users on #qa that one is changing the user's permissions back to normal.
In general, one should avoid changing the permissions of demo data users if it can be avoided. Instead, one can create a new user and assign suitable rights to them.

## Translation Testing

While OpenLMIS 3.0 supports a robust multi-lingual tool set, English is the only language officially provided with the product itself. The translation tools will allow subsequent implementations to easily provide a translation for Portuguese, French, Spanish, etc.
QA testing activities should verify that message keys are being used everywhere in the UI and in error messages so that every string can be translated, with no hard coding.
The v3 UI is so similar to v2 that QA staff could also apply the Portuguese translation strings from v2 to confirm that translations apply everywhere in v3.

## Tools

* Cucumber and InteliJ - for contract tests
* Browsers - Chrome 52+ (test on Chrome 52 and Chrome latest) and Firefox 48+ (test on Firefox 48 and Firefox latest)
* Rest Clients - Postman and REST Client
* JIRA - issue, task, bug tracking
* Zephyr - test case and test cycle creation
* Confluence - test case templates, testing standards
* Reference Distribution
* Docker 1.11+
* Docker Compose 1.6+

## Requirements Traceability

* Define how to update test cases to ensure requirements traceability (future QA weekly meeting topic)
* Define testing metrics and review process for showcases (future QA weekly meeting topic)

## Product Testing

Product testing is scheduled for each sprint and includes testing for new features or other ad hoc test scenarios. This testing will be tracked using Test Sessions in Zephyr.

## Performance Testing

Apart from the usual manual testing, each ticket with the NeedsPerformanceCheck label has to undergo performance testing. The instructions on how to perform this kind of tests are available in the How do I record more test runs?
and Release testing for 3.3 sections on the [Performance Metrics](https://openlmis.atlassian.net/wiki/spaces/OP/pages/116949318/Performance+Metrics) page, only one has to use the UAT server and the credentials of users available at that instance.
One has to pay attention especially to the CPU throttling (**6x slowdown**) and Network (**Slow 3G**) settings, as they are vital in this kind of testing and render the results of different users comparable.

## Types of Automated Tests
The following test categories have been identified for use in OpenLMIS.  As illustrated in this great [slide deck](http://martinfowler.com/articles/microservice-testing/), we expect the effort/number of tests in each category to reflect the [test pyramid](http://martinfowler.com/articles/microservice-testing/#conclusion-test-pyramid):

1. [Unit](#unit)
2. [Integration](#integration)
3. [Component](#component)
4. [Contract](#contract)
5. [End-to-End](#e2e)

### Unit Tests <a name="unit"></a>

* Who:  written by code-author during implementation
* What: the smallest unit (e.g. one piece of a model's behavior, a function, etc)
* When: at build time, should be /fast/ and targeted - I can run just a portion of the test suite
* Where: Reside inside a service, next to unit under test. Generally able to access package-private scope
* Why: to test fundamental pieces/functionality, helps guide and document design and refactors, protects against regression

#### Unit Test Examples

* __Every single test should be independent and isolated. Unit test shouldn't depend on another unit test.__

  DO NOT:
  ```java
  List<Item> list = new ArrayList<>();

  @Test
  public void shouldContainOneElementWhenFirstElementisAdded() {
    Item item = new Item();
    list.add(item);
    assertEquals(1, list.size());
  }

  @Test
  public void shouldContainTwoElementsWhenNextElementIsAdded() {
    Item item = new Item();
    list.add(item);
    assertEquals(2, list.size());
  }
  ```
* __One behavior should be tested in just one unit test.__

  DO NOT:
  ```java
  @Test
  public void shouldNotBeAdultAndShouldNotBeAbleToRunForPresidentWhenAgeBelow18() {
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
  public void shouldNotBeAdultWhenAgeBelow18() {
    int age = 17;
    boolean isAdult = ageService.isAdult(age);
    assertFalse(isAdult);
  }

  @Test
  public void shouldNotBeAbleToRunForPresidentWhenAgeBelow18() {
    int age = 17;
    boolean isAbleToRunForPresident = electionsService.isAbleToRunForPresident(age)
    assertFalse(isAbleToRunForPresident);
  }
  ```
* __Every unit test should have at least one assertion.__

  DO NOT:
  ```java
  @Test
  public void shouldNotBeAdultWhenAgeBelow18() {
    int age = 17;
    boolean isAdult = ageService.isAdult(age);
  }
  ```

  DO:
  ```java
  @Test
  public void shouldNotBeAdultWhenAgeBelow18() {
    int age = 17;
    boolean isAdult = ageService.isAdult(age);
    assertFalse(isAdult);
  }
  ```
* __Don't make unnecessary assertions. Don't assert mocked behavior, avoid assertions that check the exact same thing as another unit test.__

  DO NOT:
 ```java
  @Test
  public void shouldNotBeAdultWhenAgeBelow18() {
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
  public void shouldNotBeAdultWhenAgeBelow18() {
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
* __Test method name should clearly indicate what is being tested and what is the expected output and condition. The "should - when" pattern should be used in the name.__

  DO:
  ```java
  @Test
  public void shouldNotBeAdultWhenAgeBelow18() {
    ...
  }
  ```
  DO NOT:
  ```java

  @Test
  public void firstTest() {
    ...
  }

  @Test
  public void testIsNotAdult() {
    ...
  }
  ```
* __Unit test should be repeatable - each run should yield the same result.__

  DO NOT:
  ```java
  @Test
  public void shouldNotBeAdultWhenAgeBelow18() {
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
  public void shouldNotBeAdultWhenAgeBelow18() {
    boolean isAdult = ageService.isAdult(age);
    assertTrue(isAdult);
  }
  ```
* __Test should run fast. When we have hundreds of tests we just don't want to wait several minutes till all tests pass.__

  DO NOT:
 ```java
  @Test
  public void shouldNotBeAdultWhenAgeBelow18() {
    int age = 17;
    sleep(1000);
    boolean isAdult = ageService.isAdult(age);
    sleep(1000);
    assertFalse(isAdult);
  }
  ```

### Integration Tests <a name="integration"></a>

* Who: Code author during implementation
* What: Test basic operation of a service to persistent storage or a service to another service.  When another service is required, a test-double should be used, not the actual service.
* When: As explicitly asked for, these tests are typically slower and therefore need to be kept separate from build to not slow development.  Will be run in CI on every change.
* Where: Reside inside a service, separated from other types of tests/code.
* Why:  Ensures that the basic pathways to a service's external run-time dependancies work.  e.g. that a db schema supports the ORM, or a non-responsive service call is gracefully handled.

For testing controllers, they are divided up into unit and integration tests. The controller unit tests will be testing the logic in the controller, while the integration tests will be mostly testing serialization/deserialization (and therefore do not need to test all code paths). In both cases, the underlying services and repositories are mocked.

### Component Tests <a name="component"></a>

* Who: Code author during implementation
* What: Test more complex operations in a service.  When another service is required, a test-double should be used, not the actual service.
* When: As explicitly asked for, these tests are typically slower and therefore need to be kept separate from build to not slow development.  Will be run in CI on every change.
* Where: Reside inside a service, separated from other types of tests/code.
* Why:  Tests interactions between components in a service are working as expected.

These are not integration tests, which strictly test the integration between the service and an external dependency. These test the interactions between components in a service are working correctly. While integration tests just test the basic pathways are working, component tests verify that, based on input, the output matches what is expected.

These are not contract tests, which are more oriented towards business requirements, but are more technical in nature. The contract tests will make certain assumptions about components, and these tests make sure those assumptions are tested.

### Contract Tests <a name="contract"></a>

* Who: Code author during implementation, with input from BA/QA.
* What: Enforces contracts between and to services.
* When: Ran in CI.
* Where: Reside inside separate repository:  [openlmis-contract-tests](http://github.com/openlmis/openlmis-contract-tests).
* Why:  Tests multiple services working together, testing contracts that a Service both provides as well as the requirements a dependant has.

The main difference between contract and integration tests:
In contract tests, all the services under test are *real*, meaning that they will be processing requests and sending responses.
Test doubles, mocking, stubbing should not be a part of contract tests.

Refer to [this doc](https://github.com/OpenLMIS/openlmis-contract-tests/blob/master/README.md) for examples of how to write contract tests.

### End-to-End Tests <a name="e2e"></a>

* Who: QA / developer with input from BA.
* What: Typical/core business scenarios.
* When: Ran in CI.
* Where: Resides in seperate repository.
* Why: Ensures all the pieces are working together to carry-out a business scenario.  Helps ensure end-users can achieve their goals.

## Testing services dependent on external APIs
OpenLMIS is using WireMock for mocking web services. An example integration test can be found here:
https://github.com/OpenLMIS/openlmis-example/blob/master/src/test/java/org/openlmis/example/WeatherServiceTest.java

The stub mappings which are served by WireMock's HTTP server are placed under _src/test/resources/mappings_ and _src/test/resources/__files_
For instructions on how to create them please refer to http://wiremock.org/record-playback.html

## Testing Tools

* spring-boot-starter-test
  * Spring Boot Test
  * JUnit
  * Mockito
  * Hamcrest
* [WireMock](http://wiremock.org)
* [REST Assured](http://rest-assured.io)
* [raml-tester](https://github.com/nidi3/raml-tester)
