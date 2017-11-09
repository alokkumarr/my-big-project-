# Introduction

This document describes the high-level design of the application.

# Overview

The application consists of three main modules: SAW Web, SAW Services
and SAW Security.

# SAW Services: Executing analyses of type report

Reports are executed as Spark SQL queries running on an Apache Spark
cluster.  The queried data is stored as Parquet files in the data
lake.  The report execution functionality is provided by two
components: the Transport Service and the Transport Service Executor.

The Transport Service provides an internal REST API for SAW Web to
use, including operations to execute a report.  When a report is
executed, the Transport Service writes a message requesting execution
to a message queue.  The message queue is implemented using MapR
streams.  The Transport Service Executor consumes messages from the
queue and executes queries accordingly.

Executors are run in two different modes: fast and regular.  The fast
executors read from the fast queue to which preview and onetime
executions are sent, with expectations of lower latency using
techniques such as preallocated Spark contexts.  The regular executors
read from the regular queue to which scheduled executions are sent.
Using two different queues limits the resources provided to
potentially heavy and long-running scheduled executions to avoid
blocking the more time-sensitive preview and onetime executions.

The queue approach with executors in separate processes is used due to
the limitation of having one Spark context per Java virtual machine.
The number of executors of each type is configured statically in the
SAW environment configuration and used during deployment.  The report
execution concurrency limit follows from the number of executors
configured for each type.

As a preventive measure, executors restart the Java virtual machine
after handling an execution.  This avoids building up state between
executions that can be a source of errors.

When an analysis of type report is executed by the Transport Service,
the results are stored as newline-delimited JSON in the data lake.
When results need to be read back by the Transport Service, it reads
the newline-delimited JSON file in the data lake over the MapR-FS.
The results can then be streamed to avoid reading the entire results
into memory at the same time which might lead to out of memory errors.
