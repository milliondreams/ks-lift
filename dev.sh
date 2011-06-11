#!/bin/bash
export MAVEN_OPTS="-noverify -javaagent:../jrebel/jrebel.jar -Drebel.lift_plugin=true $MAVEN_OPTS"
mvn jetty:run
