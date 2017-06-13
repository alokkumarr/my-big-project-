#!/usr/bin/env bash
# The script creates MapR DB infrastructure for Metadata and Execution service

hadoop fs -mkdir -p /main/metadata

maprcli table create -path /main/metadata/semantic_metadata
maprcli table cf create -cfname _system -path /main/metadata/semantic_metadata
maprcli table cf create -cfname _search -path /main/metadata/semantic_metadata
maprcli table cf create -cfname _source -path /main/metadata/semantic_metadata
maprcli table cf create -cfname _relations -path /main/metadata/semantic_metadata

maprcli table create -path /main/metadata/ui_metadata
maprcli table cf create -cfname _system -path /main/metadata/ui_metadata
maprcli table cf create -cfname _search -path /main/metadata/ui_metadata
maprcli table cf create -cfname _source -path /main/metadata/ui_metadata

maprcli table create -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _system -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _search -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _source -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _dl_locations -path /main/metadata/datalake_metadata
maprcli table cf create -cfname _parent_relations -path /main/metadata/datalake_metadata

maprcli table create -path /main/metadata/analysis_metadata
maprcli table cf create -cfname _system -path /main/metadata/analysis_metadata
maprcli table cf create -cfname _search -path /main/metadata/analysis_metadata
maprcli table cf create -cfname _source -path /main/metadata/analysis_metadata
maprcli table cf create -cfname _relations -path /main/metadata/analysis_metadata

maprcli table create -path /main/metadata/analysis_results
maprcli table cf create -cfname _system -path /main/metadata/analysis_results
maprcli table cf create -cfname _search -path /main/metadata/analysis_results
maprcli table cf create -cfname _source -path /main/metadata/analysis_results
maprcli table cf create -cfname _objects -path /main/metadata/analysis_results

maprcli table create -path /main/metadata/sys_uniqness_registry
maprcli table cf create -cfname _uni_datalake -path /main/metadata/sys_uniqness_registry
maprcli table cf create -cfname _uni_ui -path /main/metadata/sys_uniqness_registry
maprcli table cf create -cfname _uni_semantic -path /main/metadata/sys_uniqness_registry
maprcli table cf create -cfname _uni_analysis -path /main/metadata/sys_uniqness_registry



maprcli table info -path /main/metadata/sys_uniqness_registry
maprcli table info -path /main/metadata/semantic_metadata
maprcli table info -path /main/metadata/ui_metadata
maprcli table info -path /main/metadata/analysis_metadata
maprcli table info -path /main/metadata/datalake_metadata
maprcli table info -path /main/metadata/analysis_results
