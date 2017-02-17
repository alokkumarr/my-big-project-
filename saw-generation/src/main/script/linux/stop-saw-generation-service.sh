#!/bin/sh
ps -ef | grep saw-generation | grep -v grep | awk '{print $2}' | xargs kill

echo "saw-generation process killed"

