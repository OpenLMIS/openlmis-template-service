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
* Column names with multiple words should be merged together.  e.g. `getFirstName()` would map to `firstname`
