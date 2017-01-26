# Writing Schema Migrations (Using Flyway)

This document serves as a set of guidelines / best practices for writing Flyway schema migrations
 for an OpenLMIS Service.

## Intro to Flyway migrating a Database

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

* Think about semantics in a migration; keep the changes conceptually self-contained.  A migration
should do one "thing" well similar to best practices with Git commits.
* Keep a migration relatively small; it should only have changes that are all related to each other.
* Try to keep a migration "tied to the code"; meaning it should be added to the codebase around 
the same time as its related code changes. Ideally, this would be in the same source control commit.
* A migration should generally only have schema changes. The only time a migration would have 
data changes (i.e. INSERT/DELETE/etc. statements) is if it is considered "bootstrap" data.
  * **Note:** See [Bootstrap Data Guidelines](#bootstrap) below.
* Migration scripts shouldn't have to check if a table/column exists.  The state of the DB (schema)
is already set by the migration scripts that preceed it.
* Migration scripts shouldn't change.  Especially not after others may have run them.  NEVER after
they have made it into a release.
* Do not create reverse migrations (a migration that reverses an earlier migration). This 
guideline is to keep things simple if the migrations in a dot release upgrade are only partially 
successful. In this case, the recommended upgrade set of steps would be:
  * Perform a full backup of the database.
  * Do an in-place upgrade.
  * If the upgrade fails, revert the deployed service and revert the database to the backup.
* Do not use database dumps or diffs to auto-generate a migration script. (NO pg_dumps!)
* Try to provide comments on table columns. This is especially important as reports access 
database tables directly.
* Use the `gradle generateMigration` task to create the migration script. This will give the 
correct format for the script name (`timestamp__migration_name.sql`).
* When naming a migration script, try to be descriptive of what is in the migration script. For 
example: if you add an email column to a users table, the script might be called 
`20170123120000000__add_email_to_users.sql`.

## <a name="bootstrap">Guidelines about Loading Bootstrap Data</a>

There is a certain set of data without which the system cannot function. In OpenLMIS, this is 
called "bootstrap" data. This is the only kind of data changes that are allowed in migrations.

There are two ways that bootstrap data needs to be loaded into the system:

1. Inter-Service (within a Service)- a Service on startup needs to load data into its own database.
  * An example would be the Reference Data Service needing to insert rights into its own database
   for the system to use.
2. Intra-Service (from one Service to another) - a Service on startup needs to load data into 
another Service's database.
  * An example would be the Requisition Service needs to insert requisition-specific rights into 
  the Reference Data Service's database for the system to use.

Below are the guidelines of how bootstrap data can be loaded for both ways:

1. Within a Service the standard Flyway migrations may load bootstrap data and "upgrade" it as
the Service matures.
  * A notable exception is the first "admin" user in the system. It is assumed that this user 
  will be modified or deleted and should not be updated in future dot releases.
2. A service loading bootstrap data into another service is more complex to solve as it requires 
service-to-service communication. For 3.0, since the only situation where this is expected is the 
creation of rights, all rights will be created by the Reference Data Service, including rights "owned" by other services.
  * For future releases after 3.0, an implementation will be provided where a Service contacts the 
  Reference Data Service to create rights, but the design of this has not yet been determined.
