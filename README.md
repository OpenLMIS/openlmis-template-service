# OpenLMIS Service Template
This template is meant to be a starting point for developing a new 
OpenLMIS 3.x Independent Service.

## Prerequisites
* Docker 1.11+
* Docker Compose 1.6+

All other dependencies, such as Java, are delivered automatically via the Docker image. It is unnecessary to install them locally to run the service, though often helpful to do so for the sake of development. See the _Tech_ section of [openlmis/dev](https://hub.docker.com/r/openlmis/dev/) for a list of these optional dependencies.

## Quick Start
1. Fork/clone this repository from GitHub.

 ```shell
 git clone https://github.com/OpenLMIS/openlmis-template-service.git <openlmis-your-service-name>
 ```
2. Respectively change all instances of `openlmis-template-service` and
`template-service` within the project to `openlmis-your-service-name` and
`your-service-name`.
3. Change all instances of the default version number ("0.0.1") in the project to your
version number.
4. Change the gradle build file to add any dependencies 
(e.g. JPA, PostgreSQL).
5. Add Java code to the template.
6. Add an environment file called `.env` to the root folder of the project, with the required 
project settings and credentials. For a starter environment file, you can use [this 
one](https://raw.githubusercontent.com/OpenLMIS/openlmis-ref-distro/master/settings-sample.env). e.g.

 ```shell
 cd <openlmis-your-service-name>
 curl -o .env -L https://raw.githubusercontent.com/OpenLMIS/openlmis-ref-distro/master/settings-sample.env
 ```
7. Develop w/ Docker by running `docker-compose run --service-ports <your-service-name>`.
See [Developing w/ Docker](#devdocker).
8. You should now be in an interactive shell inside the newly created development 
environment, start the Service with: `gradle bootRun`
9. Go to `http://<yourDockerIPAddress>:8080/` to see the service name 
and version. Note that you can determine yourDockerIPAddress by running `docker-machine ip`.
10. Go to `http://<yourDockerIPAddress>:8080/api/` to see the APIs.

## <a name="building">Building & Testing</a>
Gradle is our usual build tool.  This template includes common tasks 
that most Services will find useful:

- `clean` to remove build artifacts
- `build` to build all source. `build`, after building sources, also runs unit tests. Build will be successful only if all tests pass.
- `generateMigration -PmigrationName=<yourMigrationName>` to create a
"blank" database migration file. The file
will be generated under `src/main/resources/db/migration`. Put your
migration SQL into it.
- `test` to run unit tests
- `integrationTest` to run integration tests
- `sonarqube` to execute the SonarQube analysis.

The **test results** are shown in the console.

While Gradle is our usual build tool, OpenLMIS v3+ is a collection of 
Independent Services where each Gradle build produces 1 Service. 
To help work with these Services, we use Docker to develop, build and 
publish these.

See [Developing with Docker](#devdocker). 

## <a name="devdocker">Developing with Docker</a>
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

### <a name="devenv">Development Environment</a>
Launches into shell with Gradle & JDK available suitable for building 
Service.  PostgreSQL connected suitable for testing. If you run the 
Service, it should be available on port 8080.

Before starting the development environment, make sure you have a `.env` file as outlined in the 
Quick Start instructions.

```shell
> docker-compose run --service-ports <your-service-name>
$ gradle clean build
$ gradle bootRun
```

### <a name="buildimage">Build Deployment Image</a>
The specialized docker-compose.builder.yml is geared toward CI and build 
servers for automated building, testing and docker image generation of 
the service.

Before building the deployment image, make sure you have a `.env` file as outlined in the Quick
Start instructions.

```shell
> docker-compose -f docker-compose.builder.yml run builder
> docker-compose -f docker-compose.builder.yml build image
```

### Publish to Docker Repository
TODO

### <a name="dockerfiles">Docker's file details</a>
A brief overview of the purpose behind each docker related file

- `Dockerfile`:  build a deployment ready image of this service 
suitable for publishing.
- `docker-compose.yml`:  base docker-compose file.  Defines the 
basic composition from the perspective of working on this singular 
vertical service.  These aren't expected to be used in the 
composition of the Reference Distribution.
- `docker-compose.override.yml`:  extends the `docker-compose.yml`
base definition to provide for the normal usage of docker-compose
inside of a single Service:  building a development environment.
Wires this Service together with a DB for testing, a gradle cache
volume and maps tomcat's port directly to the host. More on how this
file works: https://docs.docker.com/compose/extends/
- `docker-compose.builder.yml`:  an alternative docker-compose file
suitable for CI type of environments to test & build this Service
and generate a publishable/deployment ready Image of the service.
- `docker-compose.prod.yml`:  Docker-compose file suitable for production.
Contains nginx-proxy image and virtual host configuration of each service.

### <a name="nginx">Running complete application with nginx proxy</a>
1. Enter desired `VIRTUAL_HOST` for each service in the `docker-compose.prod.yml` file.
2. Start up containers
```shell
> docker-compose -f docker-compose.yml -f docker-compose.prod.yml up
```
3. The application should be available at port 80.

### <a name="logging">Logging</a>
Logging is implemented using SLF4J in the code, Logback in Spring Boot, and routed to an 
external Syslog server. There is a default configuration XML (logback.xml) in the resources 
folder. To configure the log level for the development environment, simply modify the logback.xml
to suit your needs.

Configuring log level for a production environment is a bit more complex, as the code has already
been packaged into a Spring Boot jar file. However, the default log configuration XML can be 
overridden by setting the Spring Boot logging.config property to an external logback.xml when the
jar is executed. The container needs to be run with a JAVA_OPTS environment variable set to a 
logback.xml location, and with a volume with the logback.xml mounted to that location. Some docker 
compose instructions have been provided to demonstrate this.

1. Build the deployment image. (See [Build Deployment Image](#buildimage))
2. Get a logback.xml file and modify it to suit your log level configuration.
3. Modify `docker-compose.builder.yml` to point to your logback.xml location.
  a. Under `volumes`, where it shows two logback.xml locations separated by a colon, change the 
  location before the colon.
4. Run the command below.

```shell
> docker-compose -f docker-compose.builder.yml run --service-ports template-service
```

### <a name="internationalization">Internationalization (i18n)</a>
Internationalization is implemented by the definition of two beans found in the Application 
class, localeResolver and messageSource. (Alternatively, they could be defined in an application 
context XML file.) The localeResolver determines the locale, using a cookie named `lang` in the 
request, with `en` (for English) as the default. The messageSource determines where to find the 
message files.

Note there is a custom message source interface, ExposedMessageSource, with a corresponding class
ExposedMessageSourceImpl. These provide a method to get all the messages in a locale-specific 
message file.

See the MessageController class for examples on how to get messages.

Additionally, [Transifex](https://www.transifex.com/) has been integrated into the development and 
build process. In order to sync with the project's resources in Transifex, you must provide 
values for the following keys: `TRANSIFEX_USER`, `TRANSIFEX_PASSWORD`.

For the development environment in Docker, you can sync with Transifex by running the
`sync_transifex.sh` script. This will upload your source messages file to the Transifex project 
and download translated messages files.

The build process has syncing with Transifex seamlessly built-in.

### <a name="debugging">Debugging</a>
To debug the Spring Boot application, use the `--debug-jvm` option.

```shell
$ gradle bootRun --debug-jvm
```

This will enable debugging for the application, listening on port 5005, which the container has 
exposed. Note that the process starts suspended, so the application will not start up until the 
debugger has connected.

## Production by Spring Profile

By default when this service is started, it will clean its schema in the database before migrating
it.  This is meant for use during the normal development cycle.  For production data, this obviously
is not desired as it would remove all of the production data.  To change the default clean & migrate
behavior to just be a migrate behavior (which is still desired for production use), we use a Spring
Profile named `production`.  To use this profile, it must be marked as Active.  The easiest way to
do so is to add to the .env file:

```java
spring_profiles_active=production
```

This will set the similarly named environment variable and limit the profile in use.  The 
expected use-case for this is when this service is deployed through the 
[Reference Distribution](https://github.com/openlmis/openlmis-ref-distro).

### Demo Data
A basic set of demo data is included with this service, defined under `./demo-data/`.  This data may
be optionally loaded by using the `demo-data` Spring Profile.  Setting this profile may be done by
setting the `spring.profiles.active` environment variable.

When building locally from the development environment, you may run:

```shell
$ export spring_profiles_active=demo-data
$ gradle bootRun
```

To see how to set environment variables through Docker Compose, see the 
[Reference Distribution](https://github.com/openlmis/openlmis-ref-distro)


## Environment variables

The following environment variables are common to our services. They can be set either directly in compose files for images or provided as an environment file. See [docker-compose.yml](https://raw.githubusercontent.com/OpenLMIS/openlmis-ref-distro/master/docker-compose.yml) in the reference distribution for example usage. Also take a look at the sample [.env file](https://raw.githubusercontent.com/OpenLMIS/openlmis-config/master/.env) we provide. 

* **BASE_URL** - The base url of the OpenLMIS distribution. Will be used for communication between services. Each service should communicate with others using BASE_URL as the base in order to avoid direct communication, which might not work in more complex deployments. If the PUBLIC_URL variable is not set, this variable will be used for the generated links. This should be an url, for example: https://example.openlmis.org
* **VIRTUAL_HOST** - This is used by the nginx server as the virtual host under which the services are made avialble. This should be a host, for example: example.openlmis.org
* **PUBLIC_URL** - The public url of the OpenLMIS distribution. Will be used in generated links pointing to this distribution. If this variable is not set, the BASE_URL will be used for the generated links. We extract this usage of BASE_URL to another environmental variable because in more complex deployments the BASE_URL does not have to be the base domain name. This should be an url, for example: https://example.openlmis.org
* **CONSUL_HOST** - Identifies the IP address or DNS name of the Consul server. Set this to the host or IP under which the distribution is available and Consul listens for connections. Services should register with Consul under this address. This should be a host or an IP, for example 8.8.8.8.
* **CONSUL_PORT** - The port used by the Consul server - services should use this port to register with Consul. This should be a port number, for example 8500. 8500 is used by default.
* **REQUIRE_SSL** - Whether HTTPS is required. If set to `true`, nginx will redirect all incoming HTTP connections to HTTPS. By default SSL will not be required - either leave it blank or set to `false` if you wish to allow HTTP connections.
* **LOCALE** - Default localized system language. It will be applied to all running services, if this variable is missing default "en" value will be used.
* **CORS_ALLOWED_ORIGINS** - Comma-separated list of origins that are allowed, for example: `https://test.openlmis.org,http://some.external.domain`. `*` allows all origins. Leave empty to disable CORS.
* **CORS_ALLOWED_METHODS** - Comma-separated list of HTTP methods that are allowed for the above origins.

These variables are used by services for their connection to the database (none of these have defaults):

* **DATABASE_URL** - The JDBC url under which the database is accessible. Our services use `jdbc:postgresql://db:5432/open_lmis` for connecting to the PostgreSQL database running in a container.
* **POSTGRES_USER** - The username of the database user that the services should use. This variable is also used by our PostgreSQL container to create a user.
* **POSTGRES_PASSWORD** - The password of the database user that the services should use. This variable is also used by our PostgreSQL container to create a user.

These variables are used by our builds in order to integrate with the [Transifex](https://www.transifex.com/) translation management system:

* **TRANSIFEX_USER** - The username to use with Transifex for updating translations.
* **TRANSIFEX_PASSWORD** - The password to use with Transifex for updating translations.
