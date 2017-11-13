# Synchronoss - XDF 
### XDF stands for 'Xtreme Data Framework' or 'x' as in y=mx+b :)


***
The Synchronoss XDF provides a comprehensive programming and configuration model on top of Apache Spark & Hadoop ecosystem. A key element of XDF is infrastructural support at the application level: XDF focuses on the "plumbing" of Extract, Tranform, Load, Enrichment & Aggregation applications so that teams can focus on application-level business logic, without unnecessary ties to specific deployment environments. It makes easy to solve common Razorsight's 4V's problem (Volume, Variety, Velocity & Veracity) such as data ingestion and export, real-time analytics, data munging and batch workflow orchestration.  XDF will simplify the process of creating real-world big data solutions.
***

## Downloading Artifacts

Maven Repository is on the way..........................

## Project Directory Structure


The intention behind this project directory structure to provide flexibity during our release process.
Each & every module can be released separately.  

## Documentation

See our current reference [documentation](http://vm-bes:8090/display/TEC/Big+Data+Framework?src=contextnavchildmode)

## Issue Tracking
Report issues via the [XDF JIRA](http://vm-jira:8080/secure/RapidBoard.jspa?rapidView=571&view=planning&selectedIssue=BDF-234). Think you've found a bug? Please consider submitting it

## Build from source

The XDF uses a maven-based build system. In the instructions below, `mvn clean install -DskipTest=true` is invoked from the root of the source tree and serves as a cross-platform.

Be sure that your JAVA_HOME environment variable points to the jdk1.7.0 folder extracted from the JDK download & M2_HOME environment variable to the maven-3.x.x. extracted from the [Maven download](http://maven.apache.org/download.cgi)

### Prerequisites

[Gitlab](http://git-scm.com/downloads) , [Java 7 update x](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) and [Apache Spark 1.1.x with prebuild Hadoop version 2.4.x](https://spark.apache.org/downloads.html).

***
### Checking out from the source 
```
git@vm-git.razorsight.com:bigdatapoc/bigdataframework.git

```

### Build

`mvn clean install -DskipTest=true:` This command will build the whole and push the respective jars to your local repository


### Staying in touch

BigDataFramework : <BigDataFramework@razorsight.com>

***
## Licence 

Copyright ? 2014 Razorsight Corp. All Rights Reserved.


***
## FAQ's

Use-cases what we have considered are as follows:

1. Got to build request for whole bdf components? Then use the below command
		mvn clean install -Doutput=D:\\Spark -Pbdf

2. Got a build request for only parsing component. Then use the below command
		mvn clean install -Doutput=D:\\Spark -Pparser
        
3. Got a build request for only loader component. Then use the below command on the source tree /razorsight-loader
		mvn clean install -Doutput=[destination folder]
        
4. Got a build request for specific customer like vmla. Then use the below command on the source tree /razorsight-apps/razorsight-vmla-app/

		mvn clean install -Doutput=D:\\Spark -Dcustomer=vmla -Ddeployenv=uat
		
##Release History

|Date|Updates|
|----|-------|
|01-29-2015|new codebase structure updated |
|02-05-2015|Documentation updated |




