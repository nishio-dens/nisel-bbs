#!/bin/sh

program=$0
while test -h $program; do
	program=`readlink $program`
done

cygwin=false
case "`uname`" in
  CYGWIN*) cygwin=true ;;
esac

BIN_DIR=`dirname $program`
OW_HOME=${OW_HOME:-$BIN_DIR/..}
LIB_DIR=$OW_HOME/lib
TARGET_DIR=$OW_HOME/target
BUILD_DIR=$OW_HOME/bin

CLASSPATH=$BUILD_DIR:$TARGET_DIR/overlayweaver.jar:$LIB_DIR/je-3.3.75.jar:$LIB_DIR/commons-cli-1.1.jar:$LIB_DIR/commons-codec-1.3.jar:$LIB_DIR/httpclient-4.0.jar:$LIB_DIR/httpcore-4.0.1.jar:$LIB_DIR/xmlrpc-common-3.1.2.jar:$LIB_DIR/xmlrpc-server-3.1.2.jar:$LIB_DIR/ws-commons-util-1.0.2.jar:$LIB_DIR/commons-cli-1.1.jar:$LIB_DIR/jetty-6.1.17.jar:$LIB_DIR/jetty-util-6.1.17.jar:$LIB_DIR/servlet-api-2.5-20081211.jar:$LIB_DIR/clink170.jar
LOGGING_CONFIG=$BIN_DIR/logging.properties
if $cygwin; then
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  LOGGING_CONFIG=`cygpath --path --windows "$LOGGING_CONFIG"`
fi
export CLASSPATH

JVM_OPTION="-Xss80k"
#JVM_OPTION="-server -Xss80k -Xmx250m"

exec java $JVM_OPTION Main "$@"
