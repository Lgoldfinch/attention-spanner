#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd "$DIR/../" || exit

function docker_up {
    echo "Starting up docker"
    docker-compose build --pull
    docker-compose -f docker-compose.yml up
}

function docker_down {
  echo "Stopping docker"
  docker-compose -f docker-compose.yml down -v
}

trap docker_down EXIT

docker_up
