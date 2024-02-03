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

# Get the file details from the GitHub API

gitResponse=$(curl -s -H "Authorization: token $github_token" $api_url)

fileName=$(echo $gitResponse | jq -r '.files.[].filename')
fileLength=$(echo $gitResponse | jq -r '.files' | jq 'length')
fileStatus=$(echo $gitResponse | jq -r '.files.[].status')

if [ "$fileName" == "$file_path" ] && [ "$fileStatus" == "modified" ] && [ "$fileLength" == 1 ]
then
  echo "skip build"
else
  echo "crack on"
fi