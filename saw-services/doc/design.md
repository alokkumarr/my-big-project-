# Introduction

This document describes the high-level design and guidelines for the
services.

# Frameworks

Services should be implemented using Java and the Spring Framework.

# Persistence

Services should use MapR-DB for persistence wherever possible.  The
preferred interface is OJAI.

# SAW Scheduler Service

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

# SAW Gateway Service

The SAW Gateway Service acts as single entry point for all upstream micro
services. This service is spring boot based micro services. It upholds the
concerns regarding security. It acts as edge service & authenticates every
request passes by it & make it sure that it's valid. In addition of these, it
comes with the following benefits:

1. Insulates the clients from how the application is partitioned into
   micro-services

2. Insulates the clients from the problem of determining the locations of
   service instances

3. Provides the optimal API for each client

4. Reduces the number of requests/roundtrips. For example, the API gateway
   enables clients to retrieve data from multiple   services with a single
   round-trip. Fewer requests also means less overhead and improves the user
   experience. An API gateway is essential for mobile applications.

5. Simplifies the client by moving logic for calling multiple services from
   the client to API gateway

6. Translates from a “standard” public web-friendly API protocol to whatever
   protocols are used internally

The SAW gateway pattern has some drawbacks:

1. Increased complexity - the API gateway is yet another moving part that
   must be developed, deployed and managed.

2. Increased response time due to the additional network hop through the
   API gateway - however, for most applications the cost of an extra
   roundtrip is insignificant.

# Enhancements
 To support ASYNC Endpoints for long running processes.
