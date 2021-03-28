FROM adoptopenjdk:11-jre-hotspot
VOLUME /tmp
RUN apt-get update && apt-get -y install postgresql-client
ARG JAR_FILE
COPY employee-camunda-0.0.1-SNAPSHOT.jar app.jar
COPY application.yml application.yml
COPY wait-for-it.sh wait-for-it.sh
COPY entry_point.sh entry_point.sh
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
