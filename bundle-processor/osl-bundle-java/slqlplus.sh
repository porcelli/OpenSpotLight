#!/bin/sh
mvn -o dependency:build-classpath -Dmdep.outputFile=.tmp-classpath
java -Xms512M -Xmx1024M -XX:MaxPermSize=128M -cp `cat .tmp-classpath`:target/osl-bundle-java-0.7-SNAPSHOT.jar org.openspotlight.graph.query.console.SLQLPlus
