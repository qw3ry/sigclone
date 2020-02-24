#!/bin/sh

SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

java -jar "${SCRIPT_DIR}/../build/libs/clonedetection-1.0-SNAPSHOT.jar" detect $@
