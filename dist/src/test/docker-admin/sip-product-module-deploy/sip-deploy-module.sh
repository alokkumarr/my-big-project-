#!/bin/bash

PRODUCT_MODULE="sip-product-module"

echo ${PRODUCT_MODULE}

echo "Creating the tarball"

CWD=$(PWD)

TAR_CONTENT="${PRODUCT_MODULE}"

tar -czvf "${PRODUCT_MODULE}.tar.gz" ${TAR_CONTENT}

echo "Deploying the product module"


#!/bin/bash
#
# Deploy SIP product module
#
set -eu

if [ $# -lt 2 -o $# -gt 3 ]; then
    echo "Usage: $0 <sip-config> <sip-product-module> [<ansible-options>]"
    exit 1
fi

config=$1
module=$2
options="${3:-}"
dir=$(dirname $0)

# Extract the SIP product module to a temporary location
module_dir=$(mktemp -d)
tar -xzf $module -C $module_dir
export SIP_PRODUCT_MODULE="$module_dir"

# Include common SIP deploy library
. $dir/lib/sip-deploy-common.sh

echo "Starting product module deployment"
ansible-playbook -i $config $options $dir/lib/sip-modules/main.yml
echo "Finished produce module deployment"
