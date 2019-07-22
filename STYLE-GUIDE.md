# OpenLMIS Service Style Guide
This is a WIP as a style guide for an Independent Service. Clones of this file should reference
this definition.

---

## Java
OpenLMIS has [adopted](https://groups.google.com/d/msg/openlmis-dev/CCwBglBFbpk/pY406WbkAAAJ) the
[Google Java Styleguide](https://google.github.io/styleguide/javaguide.html).  These checks are
*mostly* encoded in Checkstyle and should be enforced for all contributions.

Some additional guidance:

* Try to keep the number of packages to a minimum. An Independent Service's Java code should
generally all be in one package under `org.openlmis` (e.g. `org.openlmis.requisition`).
* Sub-packages below that should generally follow layered-architecture conventions; most (if not
all) classes should fit in these four: `domain`, `repository`, `service`, `web`. To give specific
 guidance:
    * Things that do not strictly deal with the domain should NOT go in the `domain` package.
    * Serializers/Deserializers of domain classes should go under `domain`, since they have
    knowledge of domain object details.
    * DTO classes, belonging to serialization/deserialization for endpoints, should go under `web`.
    * Exception classes should go with the classes that throw the exception.
    * We do not want separate sub-packages called `exception`, `dto`, `serializer` for these
    purposes.
* When wanting to convert a domain object to/from a DTO, define Exporter/Importer interfaces for
the domain object, and export/import methods in the domain that use the interface methods. Then
create a DTO class that implements the interface methods. (See [Right](https://github.com/OpenLMIS/openlmis-referencedata/blob/master/src/main/java/org/openlmis/referencedata/domain/Right.java)
 and [RightDto](https://github.com/OpenLMIS/openlmis-referencedata/blob/master/src/main/java/org/openlmis/referencedata/dto/RightDto.java)
for details.)
    * Additionally, when Exporter/Importer interfaces reference relationships to other domain
    objects, their Exporter/Importer interfaces should also be used, not DTOs. (See [example](https://github.com/OpenLMIS/openlmis-referencedata/blob/master/src/main/java/org/openlmis/referencedata/domain/Role.java#L219).)
* Even though the no-argument constructor is required by Hibernate for entity objects, do not use
it for object construction (you can set access modifier to `private`); use provided constructors or static factory methods. If one does not
exist, create one using common sense parameters.

## RESTful Interface Design & Documentation
Designing and documenting

Note: many of these guidelines come from
[Best Practices for Designing a Pragmatic RESTful API](http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api).

* Result filtering, sorting and searching should be done by query parameters.
[Details](http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#advanced-queries)
* Return a resource representation after a create/update.
[Details](http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#useful-post-responses)
* Use camelCase (vs. snake_case) for names, since we are using Java and JSON.
[Details](http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#snake-vs-camel)
* Don't use response envelopes as default (if not using Spring Data REST).
[Details](http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#envelope)
* Use JSON encoded bodies for create/update.
[Details](http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#json-requests)
* Use a clear and consistent error payload.
[Details](http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#errors)
* Use the HTTP status codes effectively.
[Details](http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#http-status)
* Resource names should be pluralized and consistent.  e.g. prefer `requisitions`, never
`requisition`.
* Resource representations should use the following naming and patterns:
    * **Essential**: representations which can be no shorter.  Typically this is an id and a code.
    Useful most commonly when the resource is a collection, e.g. `/api/facilities`.
    * **Normal**: representations which typically are returned when asking about a specific
    resource.  e.g. `/api/facilities/{id}`.  Normal representations define the normal transactional
    boundary of that resource, and _do not_ include representations of other resources.
    * **Optional**:  a representation that builds off of the resource's **essential**
    representation, allowing for the client to ask for additional fields to be returned by
    specifying a `fields` query parameter.  The support for these representations is completely, as
    the name implies, optional for a resource to provide.
    [Details](http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#limiting-fields)
    * **Expanded**: a representation which is in part, not very RESTful.  This representation 
    allows for other, related, resources to be included in the response by way of the `expand`
    query parameter.  Support for these representations is also optional, and in part somewhat
    discouraged.
    [Details](http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#autoloading)
* A PUT on a single resource (e.g. PUT /facilities/{id}) is not strictly an update; if the
resource does not exist, one should be created using the specified identity (assuming the
identity is a valid UUID).
* Exceptions, being thrown in exceptional circumstances (according to *Effective Java* by Joshua
Bloch), should return 500-level HTTP codes from REST calls.
* Not all domain objects in the services need to be exposed as REST resources. Care should be
taken to design the endpoints in a way that makes sense for clients. Examples:
    * `RoleAssignment`s are managed under the users resource. Clients just care that users have
    roles; they do not care about the mapping.
    * `RequisitionGroupProgramSchedule`s are managed under the requisitionGroups resource.
    Clients just care that requisition groups have schedules (based on program).
* RESTful endpoints that simply wish to return a JSON value (boolean, number, string) should wrap
 that value in a JSON object, with the value assigned to the property "result". (e.g. `{
 "result": true }`)
    * Note: this is to ensure compliance with all JSON parsers, especially ones that adhere to
    RFC4627, which do not consider JSON values to be valid JSON. See the discussion
    [here](http://stackoverflow.com/questions/18419428/what-is-the-minimum-valid-json).
* When giving names to resources in the APIs, if it is a UUID, its name should have a suffix of "Id"
to show that. (e.g. `/api/users/{userId}/fulfillmentFacilities` has query parameter `rightId` to get
by right UUID.)
* If you are implementing [HTTP caching](http://docs.openlmis.org/en/latest/conventions/performanceTips.html#e-tag-and-if-none-match) 
for an API and the response is a DTO, make sure the DTO implements equals() and hashCode() using 
all its exposed properties. This is because of potential confusion of a property change without a 
change of ETag.

We use RAML (0.8) to document our RESTful APIs, which are then converted into HTML for static API
documentation or Swagger UI for live documentation. Some guidelines for defining APIs in RAML:

* JSON schemas for the RAML should be defined in a separate JSON file, and placed in a `schemas`
subfolder in relation to the RAML file. These JSON schema files would then be referenced in the
RAML file like this (using role as an example):
    ```
    - role: !include schemas/role.json

    - roleArray: |
      {
        "type": "array",
        "items": { "type": "object", "$ref": "schemas/role.json" }
      }
    ```

    * (Note: this practice has been established because RAML 0.8 cannot define an array of a JSON
    schema for a request/response body ([details](http://forums.raml.org/t/set-body-to-be-array-of-defined-schema-objects/1566/3)).
    If the project moves to the RAML 1.0 spec and our [RAML testing tool](https://github.com/nidi3/raml-tester)
    adds support for RAML 1.0, this practice might be revised.)

### Pagination

Many of the GET endpoints that return _collections_ should be paginated at the API level.  We use
the following guidelines for RESTful JSON pagination:

* Pagination options are done by _query_ paramaters.  i.e. use `/api/someResources?page=2` and not
`/api/someResources/page/2`.
* When an endpoint is paginated, and the pagination options are _not_ given, then we return the
full collection.  i.e. a single page with every possible instance of that resource.  It's
therefore up to the client to use collection endpoints responsibly and not over-load the backend.
* A paginated resource that has no items returns a single page, with it's `content` attribute
as empty.
* Resource's which only ever return a single identified item are _not_ paginated.
* For Java Service's the query parameters should be defined by a [Pageable](http://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Pageable.html)
and the response should be a [Page](http://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Page.html).
* Before executing any endpoint, parameters are validated. Currently checked parameters are `page` and `size`. 
Validator is called by interceptor which is registered with `InterceptorRegistry` by using `CustomWebMvcConfigurerAdapter`. 
The endpoint return bad request error with an error message when `page` parameter is defined and `size` parameter is not 
specified or `size` parameter is not greater than zero.

Example Request (note that page is zero-based):
```
GET /api/requisitions/search?page=0&size=5&access_token=<sometoken>
```

Example Response:
```json
{
  "content": [
    {
    ...
    }
  ],
  "totalElements": 13,
  "totalPages": 3,
  "last": false,
  "numberOfElements": 5,
  "first": true,
  "sort": null,
  "size": 5,
  "number": 0
}
```

## Postgres Database

For guidelines on how to write schema migrations using Flyway, see [Writing Schema Migrations
(Using Flyway)](FLYWAY.md).

* Each Independent Service should store its tables in its own schema.  The convention is to use
the Service's name as the schema.  e.g. The Requisition Service uses the `requisition` schema
* Tables, Columns, constraints etc should be all lower case.
* Table names should be pluralized.  This is to avoid *most* used words. e.g. orders instead of
order
* Table names with multiple words should be snake_case.
* Column names with multiple words should be merged together.  e.g. `getFirstName()` would map to
 `firstname`
* Columns of type uuid should end in 'id', including foreign keys.

## RBAC (Roles & Rights) Naming Conventions

* Names for rights in the system should follow a RESOURCE_ACTION pattern and should be all uppercase,
e.g. REQUISITION_CREATE, or FACILITIES_MANAGE. This is so all of the rights of a certain resource can
be ordered together (REQUISITION_CREATE, REQUISITION_AUTHORIZE, etc.).

## i18n (Localization)

### Transifex and the Build Process

OpenLMIS v3 uses Transifex for translating message strings so that the product can be used in
multiple languages. The build process of each OpenLMIS service contains a step to sync message
property files with a corresponding Transifex project. Care should be taken when managing keys in
these files and pushing them to Transifex.

* If message keys are added to the property file, they will be added to the Transifex project,
where they are now available to be translated.
* If message keys or strings are modified in the property file, any translations for them will be
 lost and have to be re-translated.
* If message keys are removed in the property file, they will be removed from the Transifex
project. If they are re-added later, any translations for them will be lost and have to be
re-translated.

### Naming Conventions

These naming conventions will be applicable for the messages property files.

* Keys for the messages property files should follow a hierarchy. However, since there is no
official hierarchy support for property files, keys should follow a naming convention of most to
least significant.
* Key hierarchy should be delimited with a period (.).
* The first portion of the key should be the name of the Independent Service.
* The second portion of the key should indicate the type of message; error for error messages,
message for anything not an error.
* The third and following portions will further describe the key.
* Portions of keys that don't have hierarchy, e.g. `a.b.code.invalidLength` and `a.b.code.invalidFormat`,
should use camelCase.
* Keys should not include hyphens or other punctuation.

Examples:

* `requisition.error.product.code.invalid` - an alternative could be
`requisition.error.productCode.invalid` if code is not a sub-section of product.
* `requisition.message.requisition.created` - requisition successfully created.
* `referenceData.error.facility.notFound` - facility not found.

Note: UI-related keys (labels, buttons, etc.) are not addressed here, as they would be owned by the
UI, and not the Independent Service.

## Testing

See the [Testing Guide](TESTING.md).

## Docker <a href="docker"></a>

Everything deployed in the reference distribution needs to be a Docker container.  Official OpenLMIS containers are made from their respective containers that are published for all to see on our [Docker Hub](https://hub.docker.com/u/openlmis/).

* Dockerfile (Image) [best practices](https://docs.docker.com/engine/userguide/eng-image/dockerfile_best-practices/)
* Keep Images portable & one-command focused.  You should be comfortable publishing these images publicly and openly to the DockerHub.
* Keep Containers ephemeral.  You shouldn't have to worry about throwing one away and starting a new one.
* Utilize docker compose to launch containers as services and map resources
* An OpenLMIS Service should be published in one image found on Docker Hub
* Services and Infrastructure that the OpenLMIS tech committee owns are published under the "openlmis" namespace of docker and on the Docker Hub.
* Avoid [Docker Host Mounting](https://docs.docker.com/engine/tutorials/dockervolumes/#/mount-a-host-directory-as-a-data-volume), as this doesn't work well when deploying to remote hosts (e.g. in CI/CD)

## Gradle Build
Pertaining to the build process performed by Gradle.

* Anything generated by the Gradle build process should go under the `build` folder (nothing
generated should be in the `src` folder).

## Logging

Each Service includes the SLF4J library for generating logging messages.  Each Service should be forwarding these log
statements to a remote logging container.  The Service's logging configuration should indicate the name of the service
the logging statement comes from and should be in UTC.

What generally should be logged:

* DEBUG - should be used to provide more information to developers attempting to debug what happened.  e.g. bad user input,
constraint violations, etc
* INFO - to log processing progress.  If the progress is for a developer to understand what went wrong, use DEBUG.  This
tends to be more useful for performance monitoring and remote production debugging after a client's installation has failed.

Less used:

* FATAL - is reserved for programming errors or system conditions that resulted in the application (Service) terminating.  Developers
should not be using this directly, and instead use ERROR.
* ERROR - is reserved for programming conditions or system conditions that would have resulted in the Service terminating, however some
safety oriented code caught the condition and made it safe.  This should be reserved for a global Service level handler that will convert all Exceptions into a HTTP 5xx level exception.


## Audit Logging

OpenLMIS aims to create a detailed audit log for most all actions that occur within the system.  In practice this
means that as a community we want all RESTful Resources (e.g. `/api/facilities/{id}`) to also have a full audit log
for every change (e.g. `/api/facilities/{id}/auditLog`) and for that audit log to be accessible to the user in a
consistent manner.  

A few special notes:

* When a resource has line items (e.g. Requisition, Order, PoD, Stock Card, etc), the line item would not have its own 
 REST Resource, in that case if changes are made to a line item, those changes need to be surfaced in the lint item's 
 parent.  For example, if a change is made to a Requisition Line Item, then the audit log for that change is available
 in the audit log for the Requisition, as one can't retrieve through the API the single line item.
* There are a few cases where audit logs may not be required by default.  These cases typically involve the resource
 being very transient in nature:  short drafts, created Searches, etc.  When this is in question, explore the requirements
 for how long the resource needs to exist and if it forms part of the system of record in the supply chain.

Most Services use JaVers to log changes to Resources. The audits logs for individual Resources should be exposed via 
endpoints which look as follows:

```
/api/someResources/{id}/auditLog
```

Just as with other paginated endpoints, these requests may be filtered via _page_ and _size_
query paramaters:  `/api/someResources?page=0&size=10`

The returned log may additionally be filtered by _author_ and _changedPropertyName_ query paramaters.
The later specifies that only changes made by a given user should be returned, whereas the later dictates
that only changes related to the named property should be shown.

Each `/api/someResources/{id}/auditLog` endpoint should return a 404 error if and only if the specified {id} does not exist.
In cases where the resource id exists but lacks an associated audit log, an empty array representing the empty audit should be returned.

Within production services, the response bodies returned by these endpoints should correspond
to the JSON schema defined by _auditLogEntryArray_ within _/resources/api-definition.yaml_. It is
recognized and accepted that this differs from the schema intended for use by other collections
throughout the system. Specifically, whereas other collections which support paginated requests are
expected to return pagination-related metadata (eg: "totalElements," "totalPages") within their
response bodies, the responses proffered by /auditLog endpoints do not retur pagination related data.
