#!/bin/bash

# This script will compile the Cinco Ticket program to class files 
# and then build a runnable JAR file from those class files.

# move to base director
cd "$(pwd)/.."

# compile class files
find "$(pwd)/src/cinco/ticket" -type f > "$(pwd)/files.txt"
mkdir "$(pwd)/build"
javac -d "$(pwd)/build" @"$(pwd)/files.txt"
rm -f "$(pwd)/files.txt"

# build jar
jar -cvfe "$(pwd)/build/ticket.jar" "cinco.ticket.Main" -C "$(pwd)/build" .

# clean up class files
# rm -rf "$(pwd)/build/cinco"
