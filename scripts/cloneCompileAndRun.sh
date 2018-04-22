#!/bin/bash

# Get sources
git clone https://github.com/TedAhlberg/AutoMataria.git

# Go to cloned directory
cd AutoMataria

# Compile all sourcecode
javac -encoding UTF-8 -d bin -sourcepath src -cp lib/mp3plugin.jar  -classpath src/ $(find . -name "*.java")

# Start the server
java -cp bin/ test.StartServer "Auto-Mataria Test Server 1" 32000 50 2 25 "Huge Map 1"
