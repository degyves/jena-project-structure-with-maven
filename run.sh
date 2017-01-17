#!/bin/bash
mvn exec:java -Dexec.mainClass=com.anything.playground.App -Dexec.args="$@"
