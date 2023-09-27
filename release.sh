#!/bin/sh

# This script is used to release Magma on GitLab CI

# Token argument is required
# Sha argument is required


TOKEN=$1
PROJECTID=$2
SHA=$3

VERSION=$(cat variables.env)

release-cli --server-url https://git.magmafoundation.org --private-token=$TOKEN --project-id=$PROJECTID create --name "Magma $VERSION" --description "Magma Release" --tag-name "$SHA" --ref "$SHA" --assets-link "{\"url\":\"https://git.magmafoundation.org/api/v4/projects/140/packages/maven/org/magmafoundation/Magma/$VERSION/Magma-$VERSION-server.jar\",\"name\":\"Magma-$VERSION-server.jar\"}"