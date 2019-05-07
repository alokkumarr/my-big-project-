#!/bin/bash

d="0000000000"
h="Header 0"
s109=$d
h10=$h
# Create CSV line
for i in $(seq 1 9); do
  s109+=,s${d//0/$i}
  h10+=,${h/0/$i}
done

(exec 1>&2
echo "# $h10"
echo "# $s109"
echo "# Line length:" ${#s109}
)
echo "
( echo $h10
  yes $s109 ) | 
head -1000001 >file_1000001.csv

"
echo 1>&2 "# Check command above run: $0 | sh"


