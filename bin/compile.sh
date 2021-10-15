#!/bin/bash

# This script will compile the Cinco Ticket program to class files 
# and then build a runnable JAR file from those class files.

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# compile class files
find "$SCRIPT_DIR/../src/cinco/ticket" -type f > "$SCRIPT_DIR/files.txt"
mkdir "$SCRIPT_DIR/../build"
javac -d "$SCRIPT_DIR/../build" @"$SCRIPT_DIR/files.txt"
rm -f "$SCRIPT_DIR/files.txt"

# build jar
jar -cvfe "$SCRIPT_DIR/../build/ticket.jar" "cinco.ticket.Main" -C "$SCRIPT_DIR/../build" .

# clean up class files
# rm -rf "$SCRIPT_DIR/../build/cinco"
