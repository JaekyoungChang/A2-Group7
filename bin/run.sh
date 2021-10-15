#!/bin/bash

# This script will launch the Cinco Ticket program.

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# launch from compiled class files
# java -classpath "$SCRIPT_DIR/../build" cinco.ticket.Main

# launch from jar file (specifying the entrypoint)
# java -classpath "$SCRIPT_DIR/../build/ticket.jar" cinco.ticket.Main

# launch from runnable jar file
java -jar "$SCRIPT_DIR/../build/ticket.jar"
