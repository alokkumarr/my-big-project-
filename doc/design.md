# Introduction

This document describes the high-level design of the application.

# Frameworks

Services are implemented using Java and the Spring Framework.  As an
exception, the SAW Transport Service is implemented in Scala and the
Play framework, but is intended to be refactored into multiple
services that use Java and the Spring Framework.

# Persistence

Services use MapR-DB for persistence wherever possible, using the OJAI
interface.  As an exception, the SAW Transport Service uses the
MapR-DB binary tables, but this is planned to be phased out.

# SAW Modules

The application consists of three main modules: SAW Web, SAW Services
and SAW Security.

# SAW Services

## SAW Report Executor

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

## SAW Gateway Service

The SAW Gateway Service acts as single entry point for all upstream
micro services. This service is spring boot based micro services. It
upholds the concerns regarding security. It acts as edge service &
authenticates every request passes by it & make it sure that it's
valid. In addition of these, it comes with the following benefits:

1. Insulates the clients from how the application is partitioned into
   micro-services

2. Insulates the clients from the problem of determining the locations
   of service instances

3. Provides the optimal API for each client

4. Reduces the number of requests/roundtrips. For example, the API
   gateway enables clients to retrieve data from multiple services
   with a single round-trip. Fewer requests also means less overhead
   and improves the user experience. An API gateway is essential for
   mobile applications.

5. Simplifies the client by moving logic for calling multiple services
   from the client to API gateway

6. Translates from a “standard” public web-friendly API protocol to
   whatever protocols are used internally

The SAW gateway pattern has some drawbacks:

1. Increased complexity - the API gateway is yet another moving part
   that must be developed, deployed and managed.

2. Increased response time due to the additional network hop through
   the API gateway - however, for most applications the cost of an
   extra roundtrip is insignificant.

Enhancements: To support ASYNC Endpoints for long running processes.

## SAW Scheduler Service

The Scheduler Service periodically triggers execution of analyses
based on their configured schedule.  The Scheduler Service is a Spring
Boot command-line application which is executed once daily by
operating system services (see `/etc/cron.daily`).  It fetches
analyses with a schedule from the Analysis Service and triggers
execution for any analyses that are due for execution.

Internally it uses the MapR-DB to keep track of analyses it has
already executed with the current time period.  It then uses this
information, plus the schedule of the analysis to calculate if it is
time for the analysis to be executed again.  The Scheduler Service
does not monitor the actual execution or its results, but only
triggers the start of execution.

The Scheduler Service uses one MapR-DB table that is named
`saw-scheduler-last-executed`.

When implementing the first version of the Scheduling Service using
the Quartz scheduler was also considered.  The needs of the Scheduling
Service are, at least for now, simpler than what the general Quartz
scheduler provides, so it was considered better to implement that
functionality self than bringing in the complexity of Quartz.  This
decision can be revisited later, if the needs come closer to what
Quartz provides.

Notes: There is currently no catchup of missed periods.  If needed,
have scheduler store the last processed period and step over each
remaining time period to catch up until the current moment.
