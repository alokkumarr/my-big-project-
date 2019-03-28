Testing SIP Batch Ingestion

1. Create a channel using below information
  a) username `root` password `root`
  b) hostname `sip-admin`
  c) port no `22`

2. Create a route using below information
  a) source location for large files `/root/saw-batch-samples/large`
  b) under large file you can copy files from system using below commands:
        `docker-machine scp *.csv machine-name:/tmp`
        `docker cp /tmp/*.csv sip-admin:/root/saw-batch-samples/large`
  c) source location for small files `/root/saw-batch-samples/small`
        `same thing can be done for the small files`
  d) destination can `/tmp/data`

3. Setting the limit of open file descriptors concurrently in source system,
   here in case of docker, is sip-admin docker instance
    a) use `root user`
    b) go to `/etc/security/limits.d`
    c) create a file `90-sip-batch-ingestion.conf`
    d) set the below attribute as follows
    `sip-admin           soft    nofile          20240
     sip-admin           hard    nofile          63536`
4. then open `/etc/pam.d/login`  & set `session required pam_limits.so`      

Notes:
a) In docker, we have not mounted mapRFS. So only NFS directory will work to test the route
b) while testing large number concurrent session in the source system you may find the below
   commands handy which are as follows:
      `sysctl fs.file-nr`
      `lsof | grep sipadmin | wc -l`
      `lsof -r 2 -u sipadmin`
      `ulimit -u`
