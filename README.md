# Game Store Springboot
RESTful API for Game Store, developed with Spring Boot in Java.

# Function
Six RESTful APIs for the Game Store web application.

API                       | Description         | URL
--------------------------|---------------------|--------------
GET /api/products         | Get all products    | GET [http://localhost:8080/api/products](http://localhost:8080/api/products)
GET /api/products/{id}    | Get a product by ID | GET [http://localhost:8080/api/products/1](http://localhost:8080/api/products/1)
POST /api/products        | Add a new product   | POST [http://localhost:8080/api/products](http://localhost:8080/api/products)
PUT /api/products/{id}    | Update a product    | PUT [http://localhost:8080/api/products/1](http://localhost:8080/api/products/1)
DELETE /api/products/{id} | Delete a product    | DELETE [http://localhost:8080/api/products/1](http://localhost:8080/api/products/1)
POST /api/upload          | Upload an image     | POST [http://localhost:8080/api/upload](http://localhost:8080/api/upload)

# Setup Locally
```bash
git clone https://github.com/jojozhuang/game-store-springboot.git
```
Build and bootRun.

Access http://localhost:8080/api/products in web browser or PostMan, you should get the following JSON as response.
```json
[  
   {  
      "id":3,
      "productName":"Wireless Controller",
      "price":19.99,
      "image":"http://localhost:8080/images/controller.jpg"
   },
   {  
      "id":2,
      "productName":"Wii",
      "price":269.0,
      "image":"http://localhost:8080/images/wii.jpg"
   },
   {  
      "id":1,
      "productName":"Xbox 360",
      "price":299.0,
      "image":"http://localhost:8080/images/xbox360.jpg"
   }
]
```

Change base url of images. Edit `application.properties` file, set `spring.profiles.active` to `prod`.
```shell
spring.profiles.active=prod
```
Then, restart the app. In addition, you can also run the app in production mode with `java -jar`. First, package the app.
```shell
mvn package
```
Then, launch the application with `java -jar` and append `--spring.profiles.active=prod`.
```shell
java -jar target/restful-spring-boot-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Postgresql Database

Start Postgresql in docker.

```sh
docker run --name game-store-postgresql \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=pw \
  -e POSTGRES_DB=game-store \
  -p 5432:5432 \
  -d postgres
```

# Deploy to Heroku
Add `Config Vars` to enable production mode in heroku. Go to https://dashboard.heroku.com/apps/game-store-springboot/settings, add key value as follows.
- KEY: SPRING_PROFILES_ACTIVE
- VALUE: prod

The available RESTful API on Heroku is
* baseURL: https://game-store-springboot.herokuapp.com/

For example, API https://game-store-springboot.herokuapp.com/api/products returns all products.

# Deploy to Render

Create configuration file `application-render.yml` for render deployment. Add `render` profile group in `application.yml`.

Create `Dockerfile` with `sapmachine` image, set `render` in `ENTRYPOINT`.

Log into https://dashboard.render.com/ and find the service `game-store-springboot`, or directly go to https://dashboard.render.com/web/srv-cf26g9qrrk0bppao97t0.

Go to `Settings` to edit configurations.

The available RESTful API on Render is
* baseURL: https://game-store-springboot.onrender.com

For example, API https://game-store-springboot.onrender.com/api/products returns all products.

UI Apps:
- https://game-store-angular.onrender.com/products
- https://game-store-angular.netlify.app/products

# Deploy to CF

## Create App on CF
Build
```shell
./gradlew build
```
Create manifest.yml, deploy with jre 11.
```yaml
applications:
  - name: game-store-springboot
    memory: 800MB
    path: build/libs/game-store-springboot-0.0.1-SNAPSHOT.jar
    buildpacks:
      - https://github.com/cloudfoundry/java-buildpack.git
    env:
      # see https://github.tools.sap/cloud-curriculum/materials/blob/main/cloud-platforms/cloud-foundry/java-memory-allocation.md
      JBP_CONFIG_OPEN_JDK_JRE: '{ jre: { version: 11.+}, memory_calculator: { stack_threads: 200 }}'
      MALLOC_ARENA_MAX: 4
```
Push to cf with random route.
```shell
cf push --random-route
```

## Add Database Service Dependency
Create postgresql database service on cf.
```shell
cf create-service postgresql-db trial game-store-db
```
Check cf service.
```shell
cf services
```
Update manifest.yml by adding database service.
```yaml
applications:
  - name: game-store-springboot
    memory: 800MB
    path: build/libs/game-store-springboot-0.0.1-SNAPSHOT.jar
    buildpacks:
      - https://github.com/cloudfoundry/java-buildpack.git
    env:
      # see https://github.tools.sap/cloud-curriculum/materials/blob/main/cloud-platforms/cloud-foundry/java-memory-allocation.md
      JBP_CONFIG_OPEN_JDK_JRE: '{ jre: { version: 11.+}, memory_calculator: { stack_threads: 200 }}'
      MALLOC_ARENA_MAX: 4
    services:
      - game-store-db
```
Push to cf.
```shell
cf push
```
Bind service
```shell
cf bind-service game-store-springboot game-store-db
```
Ensure your env variable changes take effect.
```shell
cf restage game-store-springboot
```

## Add Logging(not implemented)

https://github.com/SAP/cf-java-logging-support

Use `cf-java-logging-support` to enhance log messages with metadata extracted from HTTP headers of incoming requests.
```xml
    <properties>
        <cf-logging-version>3.3.0</cf-logging-version>
    </properties>
```

```xml
    <dependency>
        <groupId>com.sap.hcp.cf.logging</groupId>
        <artifactId>cf-java-logging-support-logback</artifactId>
        <version>${cf-logging-version}</version>
    </dependency>
```

Forward the Correlation ID obtained from `LogContext.getCorrelationId()` in your restTemplate for outgoing requests.
```java
@Bean
public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getInterceptors().add((request, body, execution) -> {
      request.getHeaders().set(HttpHeaders.CORRELATION_ID.getName(), LogContext.getCorrelationId());
      return execution.execute(request, body);
    });
    return restTemplate;
}
```

Test locally. Trigger any api, you should see the `correlation_id` attribute in the log.
```json
"correlation_id":"8720dd36-bc3b-40ce-8783-dbacbad23da1"
```
This id is from the `X-CorrelationID` attribute of the http request header. You can find it in postman.

Push to cf, Build.
```shell
mvn package
```
Push to cf.
```shell
cf push
```
Create an instance of the `application-logs` service with the name `app-logs`.
```shell
cf create-service application-logs lite app-logs
```
Bind the logging service to app `bulletinboard-ads`.
```shell
cf bind-service bulletinboard-ads app-logs
```
Ensure your env variable changes take effect.
```shell
cf restage bulletinboard-ads
```
Test
Call api in postman. Note down the value of `x-correlationid` or `vcap_request_id` in header, eg. "6aa0528c-b1c6-4de1-4c6a-3c6af618b4ff".
```shell
curl --location --request GET 'https://game-store-springboot-chatty-vicuna-nj.cfapps.us10.hana.ondemand.com/api/products'
```
Then open https://logs.cf.us10.hana.ondemand.com in your browser. You may need to sign in, but afterwards you should be seeing a Kibana dashboard.

You should see `correlation_id` in the latest log. And its value is "6aa0528c-b1c6-4de1-4c6a-3c6af618b4ff".

## Test

```shell
curl --location --request GET 'https://game-store-springboot-chatty-vicuna-nj.cfapps.us10.hana.ondemand.com/api/products'
```

## Troubleshooting
Inspect the environment of your app.
```shell
cf env game-store-springboot
```
Check the logs.
```shell
cf logs game-store-springboot --recent
```

## Delete
Remove binding
```shell
cf unbind-service  game-store-springboot  game-store-db
```
Delete service
```shell
cf delete-service  game-store-db
```
Recreate database service
```shell
cf create-service postgresql-db trial game-store-db
```
```shell
cf bind-service game-store-springboot game-store-db
```
Restage app
```shell
cf restage game-store-springboot
```

# Portfolio
Read portfolio [Game Store(Angular)](https://jojozhuang.github.io/project/game-store-angular) or [Game Store(React)](http://jojozhuang.github.io/project/game-store-react) to learn how these RESTful APIs are consumed by Angular and React applications.

# Tutorial
Read tutorial [Building RESTful API with Spring Boot](https://jojozhuang.github.io/tutorial/building-restful-api-with-spring-boot) to learn how this RESTful API is built.

# Run sonarqube locally
```shell
./gradlew build sonarqube --info
```
- Run with `--stacktrace` option to get the stack trace.
- Run with `--debug` option to get more log output.
- Run with `--scan` to get full insights.

