#!/bin/bash
echo '# print commands to recreate soft links for rtps application'

CMD_DIR=$( cd $(dirname $0) && pwd ) # .../sbin
( cd $CMD_DIR ) || exit

APP_DIR=$( dirname $CMD_DIR )
[[ -L $APP_DIR ]] && {
    echo error: softlink in the path
    ls -l $APP_DIR
    exit 1
}

APP_NAMVER=$(basename $APP_DIR)
APP_NAME=${APP_NAMVER%%-[0-9]*}

APAR_DIR=$( cd $APP_DIR/.. ; pwd )

echo "
rm -f $APAR_DIR/$APP_NAME ; ln -s $APP_NAMVER $APAR_DIR/$APP_NAME
rm -f /etc/bda/$APP_NAME.env ; ln -s $APP_DIR/conf/$APP_NAME.env /etc/bda
"
