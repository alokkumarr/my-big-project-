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
> The above command creates R installation package for Online network by default. 

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

5.  Spark 2.2.1 & above

6.  R 3.5.1

7.  /dfs/opt/aa-r-fw/libraries path has to created & all the below default R libraries should be present. These can be copied from 
    /usr/lib64/R/library path. 
    1. KernSmooth
	2. MASS
	3. Matrix
	4. base
	5. boot
	6. class
	7. cluster
	8. codetools
	9. compiler
	10. datasets
	11. foreign
	12. grDevices
	13. graphics
	14. grid
	15. lattice
	16. methods
	17. mgcv
	18. nlme
	19. nnet
	20. parallel
	21. rpart
	22. spatial
	23. splines
	24. stats
	25. stats4
	26. survival
	27. tcltk
	28. tools
	29. translations
	30. utils

8.  docopt & devtools package has to be installed as pre-requisite on the above path. These are not included in 
    the default libraries list. Hence, these have to be installed manually by downloading the associated tar files from R repository
	1. devtools - 2.0.1
	2. docopt - 0.6.1
	
	The R repository usually used is https://cran.r-project.org/src/contrib/. 
	
	Installation of tar file is handled using the below commands
	1. install.packages(pkgs = "devtools_2.0.1.tar.gz", lib = "/dfs/opt/aa-r-fw/libraries", repos = NULL)
	2. install.packages(pkgs = "docopt_0.6.1.tar.gz", lib = "/dfs/opt/aa-r-fw/libraries", repos = NULL)

9. Open $(R RHOME)/etc/Renviron file on the mapr environment which has R installed & make below change.
    R_LIBS_SITE parameter has to be edited to include the path mentioned in point "8". 
	The location of the file is usually /usr/lib64/R/etc/Renviron. 
    R_LIBS_SITE=${R_LIBS_SITE-'/usr/local/lib/R/site-library:/usr/local/lib/R/library:/usr/lib64/R/library:/usr/share/R/library:/dfs/opt/aa-r-fw/libraries'}
	 
10. The below dependencies should be installed. 
    RUN yum -q -y install openssl-devel libcurl-devel libxml2-devel libX11-devel libpng-devel mesa-libGLU-devel
	
11. The below command should be executed to ensure pkg-config is updated properly. This will avoid R package installation issues
    pkg-config --libs libxml-2.0

>Copy to mapr test box

>$ scp target/rpm/bda-aa-sb-poc/RPMS/noarch/a2modules* mapr:rtmapr103:/var/depo \# Change to proper server

>Install and run application on target mapr box
>List installed a2modules

>$ rpm -qa | grep a2modules

>Remove if installed

>$ sudo rpm -e a2modules