# OpenLMIS Service Template
This template is meant to be a starting point for developing a new 
OpenLMIS 3.x Independent Service.

## Prerequisites
* Docker 1.11+

## Quick Start

1. Fork/clone this repository from GitHub.

 ```shell
 git clone https://github.com/OpenLMIS/openlmis-template-service.git 
 <openlmis-your-service-name>
 ```
2. Respectively change all instances of `openlmis-template-service` and
`template-service` within the project to `openlmis-your-service-name` and
`your-service-name`.
3. Change all instances of the default version number ("O.0.1") in the project to your
version number.
4. Change the gradle build file to add any dependencies 
(e.g. JPA, PostgreSQL).
5. Add Java code to the template.
6. Develop w/ Docker by running `docker-compose run <your-service-name>`.
See [Developing w/ Docker](#devdocker).
7. From the development environment, run with: `gradle bootRun`
8. Go to `http://<yourDockerIPAddress>:8080/` to see the service name 
and version. Note that you can determine yourDockerIPAddress by running `docker-machine ip`.
9. Go to `http://<yourDockerIPAddress>:8080/api/` to see the APIs.


## Building & Testing

Gradle is our usual build tool.  This template includes common tasks 
that most Services will find useful:

- `clean` to remove build artifacts
- `build` to build all source
- `generateMigration -PmigrationName=<yourMigrationName>` to create a 
"blank" database migration file. The file
will be generated under `src/main/resources/db/migration`. Put your 
migration SQL into it.

While Gradle is our usual build tool, OpenLMIS v3+ is a collection of 
Independent Services where each Gradle build produces 1 Service. 
To help work with these Services, we use Docker to develop, build and 
publish these.

See [Developing with Docker](#devdocker). 

## <a name="devdocker"></a> Developing with Docker

OpenLMIS utilizes Docker to help with development, building, publishing
and deployment of OpenLMIS Services. This helps keep development to 
deployment environments clean, consistent and reproducible and 
therefore using Docker is recommended for all OpenLMIS projects.

To enable development in Docker, OpenLMIS publishes a couple Docker 
Images:

- [openlmis/dev](https://hub.docker.com/r/openlmis/dev/) - for Service 
development.  Includes the JDK & Gradle plus common build tools.
- [openlmis/postgres](https://hub.docker.com/r/openlmis/postgres/) - for 
quickly standing up a shared PostgreSQL DB

In addition to these Images, each Service includes Docker Compose 
instructions to:

- standup a development environment (run Gradle)
- build a lean image of itself suitable for deployment
- publish its deployment image to a Docker Repository

### Development Environment
Launches into shell with Gradle & JDK available suitable for building 
Service.  PostgreSQL connected suitable for testing. If you run the 
Service, it should be available on port 8080.

```shell
> docker-compose run --service-ports <your-service-name>
$ gradle clean build
$ gradle bootRun
```

### Build Deployment Image
```shell
> docker-compose -f docker-compose.builder.yml run builder
> docker-compose -f docker-compose.builder.yml build image
```

### Publish to Docker Repository
TODO
