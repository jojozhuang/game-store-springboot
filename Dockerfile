# Compile and run spring boot application in docker
# Build image: docker build -t game-store-springboot .
# Run container: docker run --name game-store-springboot -e DATABASE_URL='postgres://game_store_postgresql_user:kUaWp4S4GojSpsNUFtBGN7SAHywj8Izb@dpg-cf23rkirrk0bppaej6gg-a.oregon-postgres.render.com:5432/game_store_postgresql' -p 8080:8080 -d game-store-springboot
FROM sapmachine:11.0.17 as builder

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
ENTRYPOINT ["java","-Dspring.profiles.active=render","-jar","/spring-boot-app/build/libs/game-store-springboot-0.0.1-SNAPSHOT.jar"]