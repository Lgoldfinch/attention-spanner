#!/bin/bash

# Define your GitHub repository details
repo_owner="Lgoldfinch"
repo_name="attention-spanner"
file_path=".scalafmt.conf"
commit_sha="$1"
github_token="$2"

# GitHub API URL for comparing commits
#api_url="https://api.github.com/repos/$repo_owner/$repo_name/compare/$commit_sha...HEAD"
api_url="https://api.github.com/repos/Lgoldfinch/attention-spanner/commits/$commit_sha"

#echo "$response"

# Get the file details from the GitHub API

#echo "$commit_compare_result"
gitResponse=$(curl -s -H "Authorization: token $github_token" $api_url)

fileName=$(echo $gitResponse | jq -r '.files.[].filename')
fileLength=$(echo $gitResponse | jq -r '.files' | jq 'length')
fileStatus=$(echo $gitResponse | jq -r '.files.[].status')

echo "$fileName"
echo "$file_path"
echo "$fileLength"
echo "$fileStatus"
#res=$(curl -s -H "Authorization: token $github_token" $api_url)

if [ "$fileName" == "$file_path" ] && [ "$fileStatus" == "modified" ] && [ "$fileLength" == 1 ]
then
  echo hello
fi

#echo "$res"

#getCommitUrl=$(commit_compare_result | grep -o )



#| grep -o "\"filename\":\"$file_path\",\".*\"status\":\"[a-z]*\"" | head -1)

#echo "$commit_compare_result" | grep -o
#echo
# Check if the file exists in the comparison result
#if [ ! -z "$file_details" ]; then
#     Extract the file status using string manipulation
#    file_status=$(echo "$file_details" | grep -o "\"status\":\"[a-z]*\"" | cut -d':' -f2 | tr -d '"')
#
#    if [ "$file_status" == "modified" ]; then
#        echo "true"  # File has been modified
#    else
#        echo "false"  # File has not been modified
#    fi
#else
#    echo "File not found in the comparison result."
#fi
