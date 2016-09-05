# OpenLMIS Service Style Guide
This is a WIP as a style guide for an Independent Service. Clones of this file should reference 
this definition.

For the original style guide please see:
https://github.com/OpenLMIS/open-lmis/blob/master/STYLE-GUIDE.md

---

## Java
OpenLMIS has [adopted](https://groups.google.com/d/msg/openlmis-dev/CCwBglBFbpk/pY406WbkAAAJ) the
[Google Java Styleguide](https://google.github.io/styleguide/javaguide.html).  These checks are
*mostly* encoded in Checkstyle and should be enforced for all contributions.

## RESTful Interface Design & Documentation
Designing and documenting 

Note: many of these guidelines come from [Best Practices for Designing a Pragmatic RESTful API]
(http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api)

* Result filtering, sorting and searching should be done by query parameters. [Details]
(http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#advanced-queries)
* Return a resource representation after a create/update. [Details]
(http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#useful-post-responses)
* Use camelCase (vs. snake_case) for names, since we are using Java and JSON. [Details]
(http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#snake-vs-camel)
* Don't use response envelopes as default (if not using Spring Data REST). [Details]
(http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#envelope)
* Use JSON encoded bodies for create/update. [Details]
(http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#json-requests)
* Use a clear and consistent error payload. [Details]
(http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#errors)
* Use the HTTP status codes effectively. [Details]
(http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#http-status)
* Resource names should be pluralized and consistent.  e.g. prefer `requisitions`, never 
`requisition`.
* A PUT on a single resource (e.g. PUT /facilities/{id}) is not strictly an update; if the 
resource does not exist, one should be created using the specified identity (assuming the 
identity is a valid UUID).

## Postgres Database
In most cases, the Hibernate DefaultNamingStrategy follows these conventions. Schemas and table 
names will however need to be specified.

* Each Independent Service should store it's tables in its own schema.  The convention is to use 
the Service's name as the schema.  e.g. The Requistion Service uses the `requisition` schema
* Tables, Columns, constraints etc should be all lower case.
* Table names should be pluralized.  This is to avoid *most* used words. e.g. orders instead of 
order
* Table names with multiple words should be snake_case.
* Column names with multiple words should be merged together.  e.g. `getFirstName()` would map to
 `firstname`

## i18n Naming Conventions
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

## Testing

See the [Testing Guide](TESTING.md).
