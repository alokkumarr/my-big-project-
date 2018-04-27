#!/usr/bin/env bash
## Environment specific xdf-rest http address,
## must be adjusted for every environment

BIN_DIR=$( cd $(dirname $0)/../bin && pwd -P )
: ${BIN_DIR:?no value}

xdf_info() {
  $BIN_DIR/xdf_info ${1:-:}
}
xdf_info || exit

PORT=${PORT:-$(xdf_info http.port)}
: ${PORT:?no value}

#HOST=http://mapr01.bda.poc.velocity-va.sncrcorp.net:14006
HOST=http://$(hostname -f):$PORT
