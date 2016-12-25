#!/usr/bin/env bash
ENV=$1
if [ -z "$ENV" ]
then
  ENV="prod";
fi

#java -Dspring.profiles.active=${ENV} -Dspring.config.location=config/ -jar lib/matrix.server-*.jar

nohup java -Xms2G -Xmx3G \
           -Dspring.profiles.active=${ENV} -Dspring.config.location=config/ \
           -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=logs \
           -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=1098 \
           -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false \
           -jar lib/matrix.server-*.jar > /dev/null &
/
