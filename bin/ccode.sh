#!/bin/bash
# Crypt code argument
[[ $DEBUG ]] && set -vx

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

# Initialize Java args
jar_fnm="${jar_fls[0]}"
[[ $(uname -s) = CYGWIN* ]] && jar_fnm=$(cygpath -w "$jar_fnm")
main_kl=com.sncr.nsso.common.util.Ccode

# Run java main class
java -cp $jar_fnm $main_kl "$@"
exit $?
