# OpenLMIS Service Template
This template is meant to be a starting point for developing new a new OpenLMIS 3.x Independent Service.

## Prerequisites
* Docker
* PostgreSQL

## Quick Start
1. Fork/clone this repository from GitHub.

 ```
 git clone https://github.com/OpenLMIS/openlmis-template-service.git <openlmis-yourServiceName>
 ```
2. Change all instances of `openlmis-template-service` in the project to `openlmis-yourServiceName`.
3. Change all instances of the version number in the project to your version number.
4. Change the gradle build file to add any dependencies (e.g. JPA, PostgreSQL).
5. Add Java code to the template.
6. If you need to create migrations, use the included gradle task.

 ```
 gradle generateMigration -PmigrationName=<yourMigrationName>
 ```

7. A file will be generated under `src/main/resources/db/migration`. Put your migration SQL into that file.
8. Build the docker image using the Dockerfile provided. An example command:

 ```
 docker build -t "<yourNameSpace>/<openlmis-yourServiceName>" .
 ```

9. Before running the container, make sure you have a PostgreSQL docker container running with the proper configuration. For example:

 ```
 docker run -d -p 5432:5432 --name openlmis-db -e POSTGRES_PASSWORD=p@ssw0rd -e POSTGRES_DB=open_lmis postgres:9.5
 ```

10. If you created any migrations, you will need to run them now.

 ```
 docker run --rm --link openlmis-db <yourNameSpace>/<openlmis-yourServiceName> gradle flywayMigrate
 ```

11. Run the container for the image you just built.

 ```
 docker run -d -p 32770:8080 --name <openlmis-yourServiceName> --link openlmis-db <yourNameSpace>/<openlmis-yourServiceName>
 ```

12. Go to `http://<yourDockerIPAddress>:32770/` to see the service name and version.
13. Go to `http://<yourDockerIPAddress>:32770/api/` to see the APIs.
