#!/bin/bash

# Define your GitHub repository details
repo_owner="Lgoldfinch"
repo_name="attention-spanner"
file_path=".scalafmt.conf"
commit_sha="$1"
github_token="$2"

echo "hello"
echo "$1"


# GitHub API URL for comparing commits
api_url="https://api.github.com/repos/$repo_owner/$repo_name/compare/$commit_sha...HEAD"

# Get the file details from the GitHub API
file_details=$(curl -s -H "Authorization: token $github_token" $api_url | grep -o "\"filename\":\"$file_path\",\".*\"status\":\"[a-z]*\"" | head -1)

# Check if the file exists in the comparison result
if [ ! -z "$file_details" ]; then
    # Extract the file status using string manipulation
    file_status=$(echo "$file_details" | grep -o "\"status\":\"[a-z]*\"" | cut -d':' -f2 | tr -d '"')

    if [ "$file_status" == "modified" ]; then
        echo "true"  # File has been modified
    else
        echo "false"  # File has not been modified
    fi
else
    echo "File not found in the comparison result."
fi
