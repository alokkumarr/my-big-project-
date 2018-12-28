#!/bin/bash
CDIR=$(cd $(dirname $0); pwd -P)

RUBY=ruby
command -v $RUBY &>/dev/null || {
    RUBY=/opt/chef/embedded/bin/ruby
}

$RUBY $CDIR/gen-n-send.rb "$@"

exit
