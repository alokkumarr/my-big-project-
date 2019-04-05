# A2SipR (A2 Integration)

a2SipR is R package which provides to execute a2 packages on top of SIP execution environment. In addition it provides a way to set up the a2 packages & related packages to SIP environment in ```offline & online``` mode

# Features
  - It provides a way to access the dataset or make dataset available for a2 package.
  - It provides a component template to extend & use the a2 package in SIP execution environment.
  - It opens up a path to add A2 capability as a part of descriptive analytics pipepline

# Objective
The objective is to deploy the below in offline mode
> internet is not available on the destination machine or environment

* a2sipr package
* a2modeler package
* a2munge package
* a2charter package
* Package installer R scripts
* All required R packages for a2 applications
* AA components & sample config/shell scripts

# Technology
a2SipR uses a number of open source projects to work properly:

* [R](https://www.r-project.org/) - R is a free software environment for statistical computing and graphics
* [Apache Spark](https://spark.apache.org/) - is a unified analytics engine for large-scale data processing
* [sparklyr](https://spark.rstudio.com/) - R interface for Apache Spark
* [Maven](https://maven.apache.org/) : software project management and comprehension tool.
* [SIP](https://stash.synchronoss.net/projects/BDA/repos/sip/browse) : A2 uses execution SIP environment to run & visualize it.

# Folder Structure
#### man
> It holds the documentation related to SIP execution integration API which enables a2 modules to run on SIP execution environment.
#### exec
> It holds the R script used to in the process offline installation and sample of how correlator & detector integrated using the template
#### inst
> inst/json : It holds json configuration template files related to integration of a2 specific packages like correlator, detector etc. etc.

>inst/sh: It holds the script template, following the template any component in the a2 modules can be integrated with existing pipeline of basic insight pipeline & can be a part of Shell action of Oozio script.
Note: all packages only accepts data in parquet format

#### R
> It holds the R script which provides an API to access the datasets from SIP execution environment which will be used as a part of a2 pipeline.

# Installation and Getting Started

#### Installation of a2SipR & a2 package in online mode
Create RPM using the command
~~~sh
 $ ./mk_mvn.sh
~~~
 The above command creates R installation package for Online network by default.

#### Installation of a2SipR & a2 package in offline mode
Create RPM using the command

 If R packages installation has to be done offline, run command as below.

```
 $ ./mk_mvn.sh offline clean package
```
 Similarly, other parameter values such as version, product name etc. can be altered at the time of RPM creation by passing parameter values as mentioned above.

 Once you have the ```a2sipR``` RPM, place it in the machine where you want to install a2sipr package. Make sure the target machine has the following capabilities:

 ###### Required Softwares:

 > While installing packages offline in distributed environment, the libraries has to be downloaded into location & it has to be accessible from all nodes.

* [Python 2.7](https://www.python.org/download/releases/2.7/)
* [Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [CentOS 7](https://wiki.centos.org/Download)
* [Python MySqlDb](https://pypi.org/project/MySQL-python/)
* [Spark 2.2.1 & above](https://spark.apache.org/docs/2.2.1/)
* [R 3.5.1](https://cran.r-project.org/)


> ```/../opt/aa-r-fw/libraries``` path has to created & all the below default R libraries should be present. These libraries can be copied from ```/usr/lib64/R/library path.```

###### Libraries
* KernSmooth
* MASS
* Matrix
* base
* boot
* class
* cluster
* codetools
* compiler
* datasets
* foreign
* grDevices
* graphics
* grid
* lattice
* methods
* mgcv
* nlme
* nnet
* parallel
* rpart
* spatial
* splines
* stats
* stats4
* survival
* tcltk
* tools
* translations
* utils

docopt & devtools package has to be installed as pre-requisite on the above path. These are not included in the default libraries list. Hence, these have to be installed manually by downloading the associated tar files from R repository

* devtools - 2.0.1
* docopt - 0.6.1

Installation of tar file is handled using the below commands

* install.packages(pkgs = "devtools_2.0.1.tar.gz", lib = "/dfs/opt/aa-r-fw/libraries", repos = NULL)
* install.packages(pkgs = "docopt_0.6.1.tar.gz", lib = "/dfs/opt/aa-r-fw/libraries", repos = NULL)

 Open $(R RHOME)/etc/R environment file on the mapr environment which has R installed & make below change.

* R_LIBS_SITE parameter has to be edited to include the path mentioned in point "8".
	The location of the file is usually /usr/lib64/R/etc/Renviron.
* R_LIBS_SITE=${R_LIBS_SITE-'/usr/local/lib/R/site-library:/usr/local/lib/R/library:/usr/lib64/R/library:/usr/share/R/library:/dfs/opt/aa-r-fw/libraries'}

Below dependencies should be installed.

```
$ yum -q -y install openssl-devel libcurl-devel libxml2-devel libX11-devel libpng-devel mesa-libGLU-devel
```

Below command should be executed to ensure pkg-config is updated properly. This will avoid R package installation issues

```
$ pkg-config --libs libxml-2.0
```
Copy to mapr test box
~~~
$ scp target/rpm/bda-aa-sb-poc/RPMS/noarch/a2modules* {server_address}:/var/depo
~~~
Install and run application on target mapr box. List installed a2modules
~~~
$ rpm -qa | grep a2modules
~~~
Remove if installed
~~~
$ sudo rpm -e a2modules
~~~

# Few Noteworthy

* [DESCRIPTION](https://stash.synchronoss.net/projects/BDA/repos/sip/browse/a2sipr/DESCRIPTION) : This is the file in the ```a2sipR``` module maintains the version of packages & minimum version of ```R``` required for the package
