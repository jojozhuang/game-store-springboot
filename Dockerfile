# Compile and run spring boot application in docker
# Build image: docker build -t game-store-springboot .
# Run container: docker run --name game-store-springboot -e DATABASE_URL='postgresql://gamestorepostgresql_militarymy:5b72892690897f1fba9c6d0d369370e854d932dd@i90-xy.h.filess.io:5434/gamestorepostgresql_militarymy' -p 8080:8080 -d game-store-springboot
FROM sapmachine:17.0.16 as builder

WORKDIR /spring-boot-app

#COPY build.gradle settings.gradle gradlew ./
#COPY ./gradle ./gradle
COPY ./ ./

RUN ./gradlew build -x check --stacktrace

# Stage 2, based on openjdk 11, to have only the compiled app, ready for production

#FROM sapmachine:11.0.17

## From ‘builder’ stage copy over jar file
#COPY --from=builder /spring-boot-app/build/libs/game-store-springboot-0.0.1-SNAPSHOT.jar game-store-springboot-0.0.1-SNAPSHOT.jar
#ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/game-store-springboot-0.0.1-SNAPSHOT.jar"]

# It seems multistage docker file doesn't work, so have to define docker file with single stage
ENTRYPOINT ["java","-Dspring.profiles.active=render","-jar","/spring-boot-app/build/libs/game-store-springboot-0.0.1-SNAPSHOT.jar"]