#!/bin/bash
# Build and run MPSS
cd "$(dirname "$0")"
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out @sources.txt && echo "Build successful." && java -cp out ui.MainFrame
