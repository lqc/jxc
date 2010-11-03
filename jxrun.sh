#!/bin/sh

# sciezka do biblioteki standardowej
JXLIB_PATH=jxlib/bin

CLASSPATH=$JAVA_CLASSPATH:$JXLIB_PATH
JAVA=java

$JAVA -cp $CLASSPATH:$JXLIB_PATH/:. $1