FROM gradle:8.4 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine AS producer
WORKDIR /src
COPY --from=build /home/gradle/src/producer/build/libs/ ./
CMD ["java","-jar","producer-0.0.1-SNAPSHOT.jar"]

FROM eclipse-temurin:17-jre-alpine AS processor
WORKDIR /src
COPY --from=build /home/gradle/src/processor/build/libs/ ./
CMD ["java","-jar","processor-0.0.1-SNAPSHOT.jar"]

FROM eclipse-temurin:17-jre-alpine AS consumer
WORKDIR /src
COPY --from=build /home/gradle/src/consumer/build/libs/ ./
CMD ["java","-jar","consumer-0.0.1-SNAPSHOT.jar"]

FROM eclipse-temurin:17-jre-alpine AS manager
WORKDIR /src
COPY --from=build /home/gradle/src/manager/build/libs/ ./
CMD ["java","-jar","manager-0.0.1-SNAPSHOT.jar"]
