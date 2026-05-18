#!/bin/bash

# Download ANTLR4 complete jar if it doesn't exist
if [ ! -f "antlr-4.13.1-complete.jar" ]; then
    echo "Downloading ANTLR4..."
    curl -O https://www.antlr.org/download/antlr-4.13.1-complete.jar
fi

# Generate Java parsers from the grammar
echo "Generating parser..."
java -jar antlr-4.13.1-complete.jar -Dlanguage=Java CUDALexer.g4 CUDAParser.g4

# Compile the Java classes
echo "Compiling Java classes..."
javac -cp antlr-4.13.1-complete.jar *.java

# Run the Main test class
echo "Running the test..."
java -cp .:antlr-4.13.1-complete.jar Main
