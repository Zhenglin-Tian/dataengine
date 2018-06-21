#!/bin/sh

[  -e `dirname $0`/env.sh ]  && . `dirname $0`/env.sh

if [ ! -d "$LOG_DIR" ] ;then
    mkdir "$LOG_DIR"
    if [ $? -ne 0 ] ;then
        echo "Cannot create $LOG_DIR" >&2
        exit 1
    fi
fi

if [ -f $PID_FILE ]
then
 PID=`cat $PID_FILE`
 ps -ef | awk '{print $2}' | grep "^$PID$" >/dev/null
 if [ $? -eq 0 ]
 then
  echo "process $PID in running."
  exit 1
 fi
fi

nohup $JAVA_PATH/java $JAVA_OPTS -cp "$CONFIG_PATH:$CLASS_PATH" com.tcredit.engine.ApplicationMain &> /dev/null &
echo $! > $PID_FILE
PID=`cat $PID_FILE `
echo "process $PID is start ok."
