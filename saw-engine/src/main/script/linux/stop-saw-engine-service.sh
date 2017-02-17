#!/bin/sh
ps -ef | grep saw-engine | grep -v grep | awk '{print $2}' | xargs kill

echo "saw-engine process killed "

