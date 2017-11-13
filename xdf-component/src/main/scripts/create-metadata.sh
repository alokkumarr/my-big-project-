#!/bin/bash

hadoop fs -mkdir /data/bda/datalake/.metadata

create /data/bda/datalake/.metadata/datasets
create /data/bda/datalake/.metadata/transformations
create /data/bda/datalake/.metadata/datapods
create /data/bda/datalake/.metadata/datasegments


### SRUX


hadoop fs -mkdir /data/bda/.metadata

create /data/bda/.metadata/datasets
create /data/bda/.metadata/transformations
create /data/bda/.metadata/datapods
create /data/bda/.metadata/datasegments
create /data/bda/.metadata/auditlog
