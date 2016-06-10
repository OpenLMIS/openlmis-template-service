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

# Testing services dependent on external APIs
OpenLMIS is using WireMock for mocking web services. An example integration test can be found here:
https://github.com/OpenLMIS/openlmis-example/blob/master/src/test/java/org/openlmis/example/WeatherServiceTest.java

The stub mappings which are served by WireMock's HTTP server are placed under _src/test/resources/mappings_ and _src/test/resources/__files_
For instructions on how to create them please refer to http://wiremock.org/record-playback.html
