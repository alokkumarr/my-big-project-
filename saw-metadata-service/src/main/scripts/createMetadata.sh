#!/usr/bin/env bash
# The script creates MapR DB infrastructure for Metadata and Execution service

hadoop fs -mkdir -p /main/metadata

maprcli table create -path /main/metadata/semantic_metadata
maprcli table cf create -cfname _source -path /main/metadata/semantic_metadata
maprcli table cf create -cfname _search -path /main/metadata/semantic_metadata
maprcli table cf create -cfname _relations -path /main/metadata/semantic_metadata
maprcli table cf create -cfname _elements -path /main/metadata/semantic_metadata
maprcli table cf create -cfname _system -path /main/metadata/semantic_metadata

maprcli table create -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _source -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _search -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _relations -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _elements -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _es_explore -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _dl_locations -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _applications -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _system -path /main/metadata/datalake_metadata

maprcli table create -path /main/metadata/analysis_metadata
maprcli table cf create -cfname _source -path /main/metadata/analysis_metadata
maprcli table cf create -cfname _search -path /main/metadata/analysis_metadata
maprcli table cf create -cfname _relations -path /main/metadata/analysis_metadata
maprcli table cf create -cfname _elements -path /main/metadata/analysis_metadata
maprcli table cf create -cfname _system -path /main/metadata/analysis_metadata

maprcli table create -path /main/metadata/analysis_results
maprcli table cf create -cfname _source -path /main/metadata/analysis_results
maprcli table cf create -cfname _search -path /main/metadata/analysis_results
maprcli table cf create -cfname _objects -path /main/metadata/analysis_results
maprcli table cf create -cfname _system -path /main/metadata/analysis_results


[mapr@client101 sbin]$ maprcli table cf list -path /main/metadata/analysis_metadata
compressionperm  readperm  appendperm  writeperm  versionperm  minversions  maxversions  compression  ttl         inmemory  cfname      memoryperm
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _source     u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _search     u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _relations  u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _elements   u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _objects    u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _system     u:mapr

maprcli table cf list -path /main/metadata/datalake_metadata
[mapr@client101 ~]$ maprcli table cf list -path /main/metadata/datalake_metadata
compressionperm  readperm  appendperm  writeperm  versionperm  minversions  maxversions  compression  ttl         inmemory  cfname         memoryperm
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _source        u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _search        u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _relations     u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _elements      u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _es_explore    u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _dl_locations  u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _applications  u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _system        u:mapr

maprcli table cf list -path /main/metadata/analysis_results
compressionperm  readperm  appendperm  writeperm  versionperm  minversions  maxversions  compression  ttl         inmemory  cfname    memoryperm
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _source   u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _search   u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _objects  u:mapr
u:mapr           u:mapr    u:mapr      u:mapr     u:mapr       0            1            lz4          2147483647  false     _system   u:mapr


