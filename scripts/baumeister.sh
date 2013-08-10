#!/bin/bash
#
# Usage:
#
# baumeister [args]	# Runs the script passing it arguments
#

# prerequisites: JAVA_HOME must be set

# Set $BAUMEISTER_HOME to the directory where the started script resides
# This way it is possible to have more then one Baumeister version installed
pushd . > /dev/null
BAUMEISTER_HOME="${BASH_SOURCE[0]}";
if ([ -h "${BAUMEISTER_HOME}" ]) then
	while([ -h "${BAUMEISTER_HOME}" ]) do cd `dirname "$BAUMEISTER_HOME"`; SCRIPT_PATH=`readlink "${BAUMEISTER_HOME}"`; done
fi
cd `dirname ${BAUMEISTER_HOME}` > /dev/null
BAUMEISTER_HOME=`pwd`;
popd  > /dev/null
export BAUMEISTER_HOME

# build classpath with all the jars in $BAUMEISTER_HOME/lib
# TODO add plugin libs when plugins are installable
OLD_IFS=$IFS
BAUMEISTER_CLASSPATH=$(JARS=("$BAUMEISTER_HOME"/lib/*.jar); IFS=:; echo "${JARS[*]}")
IFS=$OLD_IFS

echo `pwd`
#echo ${BAUMEISTER_CLASSPATH}

# Start Baumeister
if [ $# -eq 0 ]; then 
  java -server -Xmx512m -cp ${BAUMEISTER_CLASSPATH} baumeister.process
else
  java -server -Xmx512m -cp ${BAUMEISTER_CLASSPATH} baumeister.process $@	# $1 -- "$@"
fi
