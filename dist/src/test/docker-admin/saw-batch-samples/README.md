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

Notes:
In docker, we have not mounted mapRFS. So only NFS directory will work to test the route
