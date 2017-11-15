#!/bin/bash

source ./host.sh

curl -XPOST "$HOST/dl/create/project?prj=$1"

echo
