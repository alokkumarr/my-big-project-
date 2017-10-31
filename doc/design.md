# Introduction

This document describes the high-level design of the application.

# Overview

The application consists of three main modules: SAW Web, SAW Services
and SAW Security.

# Executing analyses of type report

When an analysis of type report is executed by the Transport Service
using Apache Spark, the results are stored as newline-delimited JSON
in the data lake.  When results need to be read back by the Transport
Service, it reads the newline-delimited JSON file in the data lake
over the MapR-FS.  The results can then be streamed to avoid reading
the entire results into memory at the same time which might lead to
out of memory errors.

When the SAW Transport Service needs to execute report queries, it
writes a message to a queue requesting a specific report to be
executed.  There is a SAW Transport Service Executor process reading
that queue and executing the report, writing an entry into MapR-DB
when done.  The queue solution is used to provide query execution
concurrency, as multiple Spark contexts are needed.
