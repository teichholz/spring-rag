import sys
import os
import requests
from concurrent.futures import ThreadPoolExecutor


def usage():
    print("Usage: python script.py <questions> [solutions]")
    print("  questions: File containing the questions to test")
    print("  solutions: Optional file containing the solutions to the questions")
    print("  Each question / solution should be on a separate line")
    sys.exit(1)


def process_line(question, solution=None):
    """ Process a single question with optional solution """
    params = { 'message': question }
    response = requests.get("http://localhost:8080/q", params)

    # Extract response
    if response.status_code != 200:
        print(f"Error querying the server for question: {question}", file=sys.stderr)
        return None

    completion = response.json().get("completion")
    files_used = response.json().get("files")

    result = {
        'question': question,
        'response': completion,
        'Files used': files_used

    }

    if solution:
        params = {
            'answer': completion,
            'solution': solution
        }
        test_response = requests.get(f"http://localhost:8080/test", params)

        if test_response.status_code != 200:
            print(f"Error comparing the response for question: {question}", file=sys.stderr)
            return None

        score = test_response.json().get("score")
        result['score'] = score

    return result


def main():
    if len(sys.argv) < 2:
        usage()

    questions_file = sys.argv[1]
    solutions_file = sys.argv[2] if len(sys.argv) > 2 else None

    # Check if the questions file exists
    if not os.path.isfile(questions_file):
        print(f"File not found: {questions_file}")
        sys.exit(1)

    # Read the questions and solutions from the file
    with open(questions_file, 'r') as qf:
        questions = qf.read().splitlines()

    if solutions_file:
        if not os.path.isfile(solutions_file):
            print(f"File not found: {solutions_file}")
            sys.exit(1)
        with open(solutions_file, 'r') as sf:
            solutions = sf.read().splitlines()

        if len(questions) != len(solutions):
            print("Number of questions and solutions do not match.")
            sys.exit(1)

    # Process the questions in parallel
    with ThreadPoolExecutor() as executor:
        if solutions_file:
            total_score = 0
            for result in list(executor.map(process_line, questions, solutions)):
                for k, v in result.items():
                    print(f"{k}: {v}")
                print()
                total_score += result['score']

            print(f"Total Score: {total_score}")
            print(f"Average Score: {total_score / len(questions) if questions else 0}")
        else:
            for result in list(executor.map(process_line, questions)):
                for k, v in result.items():
                    print(f"{k}: {v}")
                print()

if __name__ == "__main__":
    main()
