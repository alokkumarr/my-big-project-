Introduction
============

Advanced Analytics Components will be used in conjunction with XDF
components to provide a full suite of Analytics. 

Objective
=========

> The objective of this document is to deploy the below
> 1. a2sipr package
> 2. Package installer R scripts
> 3. All required R packages for a2 applications
> 4. AA components & sample config/shell scripts

Installation
============

> Create RPM using the command
> $ ./mk_mvn.sh
> The above command creates RPM with R package installation method set as zip.
> This is to allow R package installation on a closed network cluster. 

> If R packages installation has to be done online, run command as below.
> $ ./mk_mvn.sh -Dpkg.inst.switch=online
> Similarly, other parameter values such as version, product name etc
> can be altered at the time of RPM creation by passing parameter values as 
> mentioned above
> Once you have the RPM, place it in the machine where you want to
> install a2sipr package. Make sure the target machine has the following
> capabilities:
>
> Software Pre-requisites:

1.  Linux (CentOS) Operating System

2.  Python

3.  Java 1.8.x

4.  Python MySQLdb (module)

5.  Elastic Search Version 5.x

6.  Spark 2.1.0

7.  R 3.4.2 & above

>Copy to mapr test box

>$ scp target/rpm/bda-aa-sb-poc/RPMS/noarch/bda-a2sipr* mapr:rtmapr103:/var/depo \# Change to proper server

>Install and run application on target mapr box
>List installed bda-a2sipr

>$ rpm -qa | grep bda-a2sipr

>Remove if installed

>$ sudo rpm -e bda-a2sipr