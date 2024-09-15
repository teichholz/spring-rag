#!/bin/bash

usage() {
    echo "Usage: $0 <questions> [solutions]"
    echo "  questions: File containing the questions to test"
    echo "  solutions: Optional file containing the solutions to the questions"
    echo "  Each question / solution should be on a separate line"
    exit 1
}

# Check if a questions is provided as an argument
if [ $# -eq 0 ]; then
  usage
fi

# Files to read
questions="$1"
solutions="$2"

# Check if the questions exists
if [ ! -f "$questions" ]; then
    echo "File not found: $questions"
    exit 1
fi

if ! command -v jq &> /dev/null ; then
    echo "jq is not installed. Please install jq."
    exit 1
fi

if ! command -v parallel &> /dev/null ; then
    echo "parallel is not installed. Please install parallel."
    exit 1
fi

process_line() {
    local question="$1"
    local solution="$2"

    # URL encode the question
    encoded_question=$(echo "$question" | jq -sRr @uri)
    response=$(curl -s "http://localhost:8080/q?message=$encoded_question")

    # Print the question and response to stderr (this is a hack, to be able to return the score via stdout and pipe it to awk)
    echo "Question: $question" >&2
    echo "Response: $(echo "$response" | jq -r '.completion')" >&2
    echo "Files used: $(echo "$response" | jq -r '.files')" >&2

    if [ ! -z "$solution" ]; then
      # URL encode the question
      encoded_response=$(echo "$response" | jq -r '.completion' | jq -sRr @uri)
      encoded_solution=$(echo "$solution" | jq -sRr @uri)
      response=$(curl -s "http://localhost:8080/test?answer=$encoded_response&solution=$encoded_solution")
      score=$(echo "$response" | jq -r '.score')
      echo "Score: $score" >&2
      echo $score
    fi

    echo "" >&2
}

# Export the function so it can be used in parallel
export -f process_line

if [ ! -z "$solutions" ]; then
  # + is used to pass in the lines of the argument files as a pairwise tuple and not as a cross product
  total_score=$(parallel --keep-order process_line :::: "+$questions" "+$solutions" | awk '{sum += $1} END {print sum}')
  echo "Total Score: $total_score"
  lines=$(wc -l < "$questions")
  echo "Average Score: $((total_score / lines))"
else
  parallel --keep-order process_line :::: "$questions"
fi
