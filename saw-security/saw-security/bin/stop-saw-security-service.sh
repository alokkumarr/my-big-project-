#!/bin/sh
ps -ef | grep saw-security | grep -v grep | awk '{print $2}' | xargs kill
echo "saw-security process killed"