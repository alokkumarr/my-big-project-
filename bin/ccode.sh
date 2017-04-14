#!/bin/bash
# Crypt code argument

# Command directory is our anchor
cmd_dir=$(dirname $0)

# Check lib dir is in place
( cd $cmd_dir/../lib ) || exit
# Get absolute file name
lib_dir=$( cd $cmd_dir/../lib ; pwd )

# Get absolute file names of Jar file
# Must be the only name
jar_fls=( $lib_dir/saw-security*.jar )

# Check '*' was expanded into existing file name
( <${jar_fls[0]} ) || exit

# Initialize Java args vars
jar_fnm="${jar_fls[0]}"
main_kl=com.sncr.nsso.common.util.CcodeUtil

# Run java main class
java -cp $jar_fnm $main_kl "$@"
