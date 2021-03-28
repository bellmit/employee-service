#!/bin/bash


java -Djava.security.egd=file:/dev/./urandom -Dspring.config.location=/opt/app/application.yml -jar /opt/app/app.jar 
