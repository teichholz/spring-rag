#!/bin/bash

# Check if a file is provided as an argument
if [ $# -eq 0 ]; then
    echo "Please provide a file name as an argument."
    exit 1
fi

# File to read
file="$1"

# Check if the file exists
if [ ! -f "$file" ]; then
    echo "File not found: $file"
    exit 1
fi

if [ ! command -v jq &> /dev/null ]; then
    echo "jq is not installed. Please install jq."
    exit 1
fi

if [ ! command -v parallel &> /dev/null ]; then
    echo "parallel is not installed. Please install parallel."
    exit 1
fi

process_line() {
    local line="$1"

    # URL encode the question
    encoded_question=$(printf '%s' "$line" | jq -sRr @uri)

    # Make the GET request
    response=$(curl -s "http://localhost:8080/q?message=$encoded_question")

    # Print the question and response
    echo "Question: $line"
    echo "Response:" $(echo $response | jq -r '.completion')
    echo "Files used:" $(echo $response | jq -r '.files')
    echo "-------------------"
}

export -f process_line

parallel process_line :::: "$file"