FROM maven:3.8.6-openjdk-8-slim AS build-java-stage

ADD pom.xml /work/pom.xml
ADD rql-api /work/rql-api/
ADD rql-core /work/rql-core/
ADD rql-server /work/rql-server/
WORKDIR /work

RUN ["mvn", "package","-Dmaven.test.skip=true"]

FROM adoptopenjdk/openjdk8:debian-slim

RUN apt-get update && apt-get install make && apt-get install gcc -y
ADD ["shd31", "/shd31"]
RUN cd /shd31 && make

COPY --from=build-java-stage /work/rql-server/target/classes /work/target/classes
COPY --from=build-java-stage /work/rql-server/target/dependency/*.jar /work/target/dependency/

WORKDIR /work
EXPOSE 9992
ENTRYPOINT ["java", "-cp", "target/classes:target/dependency/*", "fr.ensma.lias.rql.RqlLauncher"]

LABEL maintainer="Mickael BARON and Bilal REZKELLAH"
