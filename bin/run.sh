#!/bin/bash

# This script will launch the Cinco Ticket program.

# move to base director
cd "$(pwd)/.."

# launch from compiled class files
# java -classpath "$(pwd)/build" cinco.ticket.Main

# launch from jar file (specifying the entrypoint)
# java -classpath "$(pwd)/build/ticket.jar" cinco.ticket.Main

# launch from runnable jar file
java -jar "$(pwd)/build/ticket.jar"
