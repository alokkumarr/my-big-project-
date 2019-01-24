#
# Common utilities for SIP deploy commands
#

yum_install() {
    sudo yum -q -y install $*
}

# Install deployment tools, if not already installed
if ! command -v ansible-playbook > /dev/null; then
    echo "Installing deployment tools"
    yum_install ansible
fi

# Workaround: Ansible version 2.4 has deprecated using the "include"
# directive for importing playbooks, so it has been changed to
# "import_playbook".  However, there are still some environments on
# older Ansible versions, so include a workaround for them by
# modifying "site.yml" to use the old approach.  Remove when all
# environments have upgraded to Ansible 2.4 or newer.
set -o pipefail
if rpm -q ansible | grep -E "ansible-2.(2|3)" > /dev/null; then
    sed -i -e "s/^- import_playbook:/- include:/" $dir/site.yml
fi

# Send deployment logs also to system log to preserve history of
# deployments (unless invoked from the development environment
# deployment service, which already directs the deployment command log
# output to the journal)
if [ "${service:-}" != "1" ]; then
    exec > >(tee >(systemd-cat -t sip-deploy)) 2>&1
fi

# Ensure that SIP environment configuration file contains the expected
# roles, as they are required for certain configurations to render
# properly.  They can however be left empty with no hosts if that role
# is not going to be installed.  Previous versions of SIP did not have
# all current roles, so older environments might not have all of these
# roles in their configuration files.
ensure_role() {
    role=$1
    if ! grep -e "^\[$role\]$" $config; then
        echo "Error: Configuration file must contain role \"[$role]\""
        echo "(But the role can be left empty without hosts)"
        exit 1
    fi
}
ensure_role sip-rtis
ensure_role saw-security-arbitrator
