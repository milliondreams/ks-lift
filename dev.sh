#!/bin/bash
export MAVEN_OPTS=-noverify -javaagent:../jrebel/jrebel.jar $MAVEN_OPTS 
mvn jetty:run
