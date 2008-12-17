#!/bin/bash

echo Starting Server...

java -cp build/classes/:lib/JainSipApi1.2.jar:lib/JainSipRi1.2.jar:lib/caldav4j-0.4.jar:lib/commons-codec-1.3.jar:lib/commons-collections-3.2.1.jar:lib/commons-httpclient-3.1.jar:lib/commons-lang-2.4.jar:lib/commons-logging-1.1.1.jar:lib/compiled-ical4j-connector6.jar:lib/ical4j-1.0-beta5.jar:lib/icalgatewayImpl.jar:lib/jackrabbit-webdav-1.4.jar:lib/jain-sip-api-1.2.jar:lib/jain-sip-ri-1.2.jar:lib/junit-3.8.2.jar:lib/log4j.jar:lib/mysql-connector-java-5.0.8-bin.jar:lib/slf4j-api-1.5.6.jar:lib/slf4j-simple-1.5.6.jar:lib/smack.jar:lib/smackx-debug.jar:lib/smackx-jingle.jar:lib/smackx.jar:lib/webdav.jar:lib/xerces-2.6.0.jar -jar icalpresencegateway.jar &

