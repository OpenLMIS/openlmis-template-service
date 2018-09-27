#!/bin/bash

set +x
set -e

function finish {
  docker-compose -f docker-compose.builder.yml down --volumes
}
trap finish EXIT

sudo rm -f .env
cp $ENV_FILE .env
if [ "$GIT_BRANCH" != "master" ]; then
    sed -i '' -e "s#^TRANSIFEX_PUSH=.*#TRANSIFEX_PUSH=false#" .env  2>/dev/null || true
fi

docker-compose -f docker-compose.builder.yml run -e BUILD_NUMBER=$BUILD_NUMBER -e GIT_BRANCH=$GIT_BRANCH builder
