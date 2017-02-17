#!/bin/sh
ps -ef | grep saw-published | grep -v grep | awk '{print $2}' | xargs kill

echo "saw-published process killed"


