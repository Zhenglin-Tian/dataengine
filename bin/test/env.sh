#!/usr/bin/env bash
APP="${project.artifactId}"
LOG_BASE="/home/poc/logs"
LOG_DIR="$LOG_BASE/$APP"
export PID_FILE="$LOG_DIR/$APP.pid"
export LANG="zh_CN.UTF-8"
export LC_ALL="zh_CN.UTF-8"

export JAVA_HOME=${jdk_version}
export JAVA_PATH=$JAVA_HOME/bin
export JAVA_OPTS="-Xms2048m -Xmx2048m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:$LOG_DIR/gc.log"
#CONFIG_PATH=$(cd "$(dirname "$0")/../config";pwd)
LIB_PATH=$(cd "$(dirname "$0")/../lib";pwd)

# 组装 CLASSPATH
ALL_JARS=$(ls "$LIB_PATH")
CLASS_PATH=""
for jar in $ALL_JARS
do
    CLASS_PATH="$CLASS_PATH""$LIB_PATH""/""$jar"":"
done
CLASS_PATH=`echo $CLASS_PATH | sed -r 's/^(.*):$/\1/g'`

