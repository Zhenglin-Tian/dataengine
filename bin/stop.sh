#!/bin/sh
[  -e `dirname $0`/env.sh ]    && . `dirname $0`/env.sh
PID=`cat $PID_FILE`
kill $PID
n=10
i=1
while [ $n -gt $i ]
do 
 ps -ef | awk '{print $2}' | grep "^$PID" >/dev/null
 if [ $? -eq 0 ]
 then
   sleep 1
   let i+=1
 else
   echo "stop process $PID succeed."
   exit
 fi
done
echo "stop process $PID failed."
