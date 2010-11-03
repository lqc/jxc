#!/bin/sh

set -a

# sciezka do jar'a z Cup'em 0.11a
CUP_PATH=java-cup-11a-runtime.jar

# sciezka do Jasmin 
export JASMIN_PATH=jasmin.jar

# sciezka do biblioteki standardowej
JXLIB_PATH=jxlib/bin
JXC_PATH=jxc/bin

CLASSPATH=$CUP_PATH:$JASMIN_PATH:$JXLIB_PATH:$JXC_PATH
JAVA=java

$JAVA -cp $CLASSPATH org.lqc.jxc.JxCompiler $1