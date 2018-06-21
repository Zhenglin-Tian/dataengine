#!/bin/sh
[  -e `dirname $0`/env.sh ]    && . `dirname $0`/env.sh
if [ -f $PID_FILE ]
then
 PID=`cat $PID_FILE`
 `dirname $0`/stop.sh
  if [ $? -eq 0 ]
   then
     `dirname $0`/start.sh
      exit $?
   else
     exit 1
   fi
fi
`dirname $0`/start.sh
exit $?
