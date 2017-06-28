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
operating system service (`/etc/cront.daily`).  It fetches analyses
with a schedule from the Analysis Service and triggers execution for
any analyses that are due for execution.

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
