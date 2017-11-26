#! /bin/bash
#
# Sets the version of the current software at multiple places.
# Call it like this:
# set-version.sh 1.0.0-SNAPSHOT
#
# (c) 2017 Stephan Fuhrmann

NEWVERSION=$1

if [ "x${NEWVERSION}" = "x" ]; then
	echo "Please give a version as a parameter, for example 0.1.4-SNAPSHOT"
	exit 10
fi

echo "- pom.xml"
mvn versions:set -DnewVersion=${NEWVERSION} || exit
rm -f pom.xml.versionsBackup

