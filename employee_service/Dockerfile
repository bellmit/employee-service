FROM adoptopenjdk:11-jre-hotspot
RUN mkdir /opt/app
RUN apt-get update && apt-get -y install postgresql-client
COPY employee-management-0.0.1-SNAPSHOT.jar /opt/app/app.jar
COPY application.yml /opt/app/application.yml
COPY log4j2.xml /opt/app/log4j2.xml
COPY wait-for-it.sh wait-for-it.sh
COPY entry_point.sh entry_point.sh
