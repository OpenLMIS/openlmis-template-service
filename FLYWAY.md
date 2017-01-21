# Writing Schema Migrations (Using Flyway)

This document serves as a set of guidelines / best practices for writing Flyway schema migrations
 for an OpenLMIS Service.

## Flyway in a Database

* Creates a table in the schema called `schema_version`.
* Is smart enough to only apply new migrations to the database.
  * Goes through the Flyway configured folders, looking for migration scripts.
  * If found, compares them to the migrations already applied to see if any new migration scripts
   need to be applied.
  * If new migration scripts are found, applies them to the database and adds them to the 
  `schema_version` table.
* Flyway can also perform other database tasks, such as clean, validate, repair, etc..
* There is a Spring Boot Flyway plugin, which OpenLMIS services use, so that on Spring Boot 
application startup, Flyway is automatically run.

## General Guidelines

* Think about semantics in a migration; keep it conceptually self-contained.
* Keep a migration relatively small; it should only have changes that are all related to each other.
* Try to keep a migration "tied to the code"; meaning it should be added to the codebase around 
the same time as its related code changes. Ideally, this would be in the same source control commit.
* A migration should not have any data changes; schema changes only. (No INSERT/DELETE/etc. 
statements!)
  * This includes bootstrap data. See [Bootstrap Data Guidelines](#bootstrap) below.
* Do not create reverse migrations (a migration that reverses an earlier migration). This 
guideline is to keep things simple if the migrations in a dot release upgrade are only partially 
successful. In this case, the recommended upgrade set of steps would be:
  * Perform a full backup of the database.
  * Do an in-place upgrade.
  * If the upgrade fails, revert the code and revert the database to the backup.
* Do not use database dumps or diffs to auto-generate a migration script. (NO pg_dumps!)
* Try to provide comments on table columns. This is especially important as reports access 
database tables directly.

## <a name="bootstrap">Guidelines about Loading Bootstrap Data</a>

There is a certain set of data without which the system cannot function. In OpenLMIS, this is 
called "bootstrap data". Even this data should not be present in migrations, but loaded 
separately after the migrations.

There are two ways that bootstrap data needs to be loaded into the system:

1. Inter-Service (within a Service)- a Service on startup needs to load data into its own database.
  * An example would be the Reference Data Service needing to insert rights into its own database
   for the system to use.
2. Intra-Service (from one Service to another) - a Service on startup needs to load data into 
another Service's database.
  * An example would be the Requisition Service needs to insert requisition-specific rights into 
  the Reference Data Service's database for the system to use.

Below are the guidelines of how bootstrap data can be loaded for both ways:

1. This can be solved by providing a SQL script called `afterMigrate.sql`, and configuring Flyway
 to be able to find it by adding the location of the script to the `flyway.locations` property in 
 Spring Boot's application.properties file.
2. This problem is more complex to solve, as it requires service-to-service communication. A 
custom implementation of FlywayCallback will probably need to be created so that on its 
afterMigrate() call, it will get a service-based access token from the Auth Service and use that 
token to call the applicable endpoint in the dependent Service. In the example above, the 
endpoint would be the "create right" endpoint in the Reference Data Service.
  * For this to work properly, the Auth Service and the dependent Service (Reference Data in the 
  example) will need to be up and running when the Service is trying to load bootstrap data.