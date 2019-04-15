# You may need to check Prometheus MySQL exporter releases page
# https://github.com/prometheus/mysqld_exporter/releases 
# for the latest release. 

# Current version is 0.11.0. 
# Binary file comes in a tar file which is downloaded from above GIT location and extracted to the local machine.
# Then the binary file should be included in the saw package by copying it to the current location.

# FYI, procedure to follow on the local linux machine to get the binary file and copy it to the required location
# Replace <VER> with version 0.11.0(or with a latest version if we are planning to upgrade)
# wget https://github.com/prometheus/mysqld_exporter/releases/download/v<VER>/mysqld_exporter-<VER>.linux-amd64.tar.gz
# tar xvf mysqld_exporter-<VER>.linux-amd64.tar.gz
# sudo mv  mysqld_exporter-<VER>.linux-amd64/mysqld_exporter dist/src/main/ansible/roles/sip-mariadb-exporter/files/mariadb_exporter/mysqld_exporter
# Once we have the binary file in the required location, ansible script will perform the next steps.
