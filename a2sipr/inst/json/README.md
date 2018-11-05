Introduction
============

Advanced Analytics Components will be used in conjunction with XDF
components to provide a full suite of Analytics. 

Objective
=========

> The objective of this document is to deploy the below
> 1. a2sipr package
> 2. a2modeler package
> 3. a2munge package
> 4. a2charter package
> 5. Package installer R scripts
> 6. All required R packages for a2 applications
> 7. AA components & sample config/shell scripts

Installation
============

> Create RPM using the command
> $ ./mk_mvn.sh
> The above command creates RPM with R package installation method set as zip.
> This is to allow R package installation on a closed network cluster. 

> If R packages installation has to be done offline, run command as below.
> $ ./mk_mvn.sh offline clean package
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

7.  R 3.5.0 & above

8.  /dfs/opt/aa-r-fw/libraries path has to created & all the default R libraries should be present

9.  docopt & devtools package has to be installed as pre-requisite on the above path

10.  Open $(R RHOME)/etc/Renviron file on the mapr environment which has R installed & make below change.
     R_LIBS_SITE parameter has to be edited to include the path mentioned in point "8". 
	 The location of the file is usually /usr/lib64/R/etc/Renviron. 
     R_LIBS_SITE=${R_LIBS_SITE-'/usr/local/lib/R/site-library:/usr/local/lib/R/library:/usr/lib64/R/library:/usr/share/R/library:/dfs/opt/aa-r-fw/libraries'}

>Copy to mapr test box

>$ scp target/rpm/bda-aa-sb-poc/RPMS/noarch/a2modules* mapr:rtmapr103:/var/depo \# Change to proper server

>Install and run application on target mapr box
>List installed a2modules

>$ rpm -qa | grep a2modules

>Remove if installed

>$ sudo rpm -e a2modules