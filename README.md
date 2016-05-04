# OpenLMIS Module Template
This template is meant to be a starting point for developing new a new OpenLMIS 3.x Independent Service.

## Quick Start
1. Fork/clone this repository from GitHub.
 ```
 git clone https://github.com/OpenLMIS/openlmis-template-service.git <openlmis-yourServiceName>
 ```
2. Change the gradle build file to add any dependencies (e.g. JPA, PostgreSQL).
3. Add Java code to the template.
4. Run `gradle build` to build the Spring Boot application.
5. Run `java -jar build/libs/<yourJarFileName>` to start the Spring Boot application.