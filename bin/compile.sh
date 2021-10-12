#!/bin/bash

# compile class files
find "$(pwd)/src/cinco/ticket" -type f > "$(pwd)/files.txt"
mkdir "$(pwd)/build"
javac -d "$(pwd)/build" @"$(pwd)/files.txt"
rm -f "$(pwd)/files.txt"

# build jar
jar -cvfe "ticket.jar" "cinco.ticket.Main" -C "$(pwd)/build" .
rm -rf "$(pwd)/build"
