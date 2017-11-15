#!/bin/bash

source ./host.sh

# Create new project
curl -XPOST "$HOST/dl/create/project?prj=$1"

echo
