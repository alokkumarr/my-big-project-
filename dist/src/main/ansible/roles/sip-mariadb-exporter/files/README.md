# Current mysqld exporter version is 0.11.0.
 
1. You can need to check Prometheus MySQL exporter releases page
 `https://github.com/prometheus/mysqld_exporter/releases`
 for the latest release.
2. Binary file comes in a tar file which is downloaded from above GIT location and extracted to the local machine.
 Then the binary file should be included in the saw package by copying it to the current location.
3. FYI, procedure to follow on the `local linux machine` to download the binary file and move it to the required location
   Replace <VER> with version 0.11.0(or with a latest version if we are planning to upgrade)
 a) Download the file
`wget https://github.com/prometheus/mysqld_exporter/releases/download/v<VER>/mysqld_exporter-<VER>.linux-amd64.tar.gz`
 b) Extract
`tar xvf mysqld_exporter-<VER>.linux-amd64.tar.gz`
 c) Copy the binary file to the required location 
`sudo mv  mysqld_exporter-<VER>.linux-amd64/mysqld_exporter <sip-directory>/sip/dist/src/main/ansible/roles/sip-mariadb-exporter/files/mariadb_exporter/mysqld_exporter/`
4. Once we have the binary file in the required location, ansible script will perform the next steps.
