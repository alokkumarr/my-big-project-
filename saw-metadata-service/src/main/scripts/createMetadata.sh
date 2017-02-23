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
maprcli table cf create -cfname _objects -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _es_explore -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _dl_locations -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _applications -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _system -path /main/metadata/datalake_metadata

maprcli table create -path /main/metadata/report_metadata
maprcli table cf create -cfname _source -path /main/metadata/report_metadata
maprcli table cf create -cfname _search -path /main/metadata/report_metadata
maprcli table cf create -cfname _relations -path /main/metadata/report_metadata
maprcli table cf create -cfname _elements -path /main/metadata/report_metadata
maprcli table cf create -cfname _objects -path /main/metadata/report_metadata
maprcli table cf create -cfname _dl_locations -path /main/metadata/report_metadata
maprcli table cf create -cfname _system -path /main/metadata/report_metadata

maprcli table create -path /main/metadata/report_results
maprcli table cf create -cfname _source -path /main/metadata/report_results
maprcli table cf create -cfname _search -path /main/metadata/report_results
maprcli table cf create -cfname _report -path /main/metadata/report_results
maprcli table cf create -cfname _objects -path /main/metadata/report_results
maprcli table cf create -cfname _dl_locations -path /main/metadata/report_results
maprcli table cf create -cfname _system -path /main/metadata/report_results


