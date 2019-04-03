# A2 (Advance Analytics)
This is the home of the Advance Analytics: the foundation of statistic modeling & feature engineering. Collectively the A2 module and the family of A2 module is often referred to simply as "Advance Analytics or Advance Insight Module".

A2 provides everything required beyond  ```(Extract, Transform & Load)``` for creating deeper insight of your data for a wide range of scenarios.


# Project Standard
This project is governed by the [SIP Team](https://confluence.synchronoss.net:8443/display/BDA/Project+Standards). By participating, you are expected to uphold this project standards.

# Features

  - It provides all necessary data wrangling transformation method as a part of configuration.
  - It provides a framework for efficiently and reliabily building statistical models.
  - It provides a unified API which works both on RDataFrame & SparkDataFrame
  - It provides an integration module to integrate & execute on sip execution environment.

 # Access to Binaries
For access to artifacts or a distribution zip, see the [SIP Bamboo Page](https://bamboo.synchronoss.net:8443/browse/BDA-BDASAW). Once you are in bamboo page, click on latest build number & scroll down, there you can download ```A2 Package```


# Reporting issues
A2 module uses [JIRA to track issue](https://jira.synchronoss.net:8443/jira/secure/RapidBoard.jspa?rapidView=13385&projectKey=SIP). If you want to raise an issue, please follow the recommendations below:

- Before you log a bug, please search the issue tracker to see if someone has already reported the problem.

- If the issue doesn’t already exist, [create a new issue](https://jira.synchronoss.net:8443/jira/secure/RapidBoard.jspa?rapidView=13385&projectKey=SIP).

- Please provide as much information as possible with the issue report, we like to know the version of SIP that you are using, as well as your Operating System and JVM version.

- If you need to paste code, or include a stack trace use Markdown ``` ``` escapes before and after your text.

- If possible try to create a test-case or project that replicates the issue. You can submit sample projects as pull-requests against the SIP stash project.


### Tech
A2 uses a number of open source projects to work properly:

* [R](https://www.r-project.org/) - R is a free software environment for statistical computing and graphics
* [Apache Spark](https://spark.apache.org/) - is a unified analytics engine for large-scale data processing
* [sparklyr](https://spark.rstudio.com/) - R interface for Apache Spark
* [Spark MLlib](http://spark.apache.org/docs/latest/mllib-guide.html) : Use Spark’s distributed machine learning library from R
* [Docker](https://www.docker.com/) : a tool designed to make it easier to create, deploy, and run applications by using containers
* [Maven](https://maven.apache.org/) : software project management and comprehension tool.
* [SIP](https://stash.synchronoss.net/projects/BDA/repos/sip/browse) : A2 uses execution SIP environment to run & visualize it.

# Modules

* [A2Modeler](https://stash.synchronoss.net/projects/BDA/repos/sip/browse/a2modeler) : provides a framework for efficiently and reliably building statistical models

* [A2Munge](https://stash.synchronoss.net/projects/BDA/repos/sip/browse/a2munge) : contains a suite of [Spark](https://spark.apache.org/) compatible data preparation, transformation and analytic functions

* [A2SipR](https://stash.synchronoss.net/projects/BDA/repos/sip/browse/a2sipr): contains the integration service layer to interact with [SIP](https://stash.synchronoss.net/projects/BDA/repos/sip/browse) execution environment.



# Installation and Getting Started
### Installation

A2Module requires [R](https://www.r-project.org/) R version >= 3.5.0 required to run.
Install the dependencies and devDependencies and start the server.
##### CentOS

```sh
sudo yum install R
```

##### MacOS

```sh
sudo brew install R
```


##### RStudio

 * [RStudio](http://rstudio.org/download/desktop) : Download the RStudio appropriate binary (64-Bit)
 * (optional) Install [XQuartz](https://www.xquartz.org/) by clicking on the link and download and install the dmg file

To execute R from the terminal:
```
$ R
```

##### Importing project into RStudio

> Note :If you haven't done so already, the first step is create a checkout the SIP repository. See this [guide](https://support.rstudio.com/hc/en-us/articles/200532077-Version-Control-with-Git-and-SVN) for details.

Once a SIP project connected to the Stash repository is made, open the project. You should have a fresh RStudio enviornment with the working directory set to your local repository.

> In RStudio, go to ```File Menu``` and Open SIP Projects, RStudio may ask you to install all dependent packages or select ```Tools Menu``` then select ```Install Packages```

The R package devtools is required to install A2 modules. If its not already install on your local environment, run the following command RStudion Terminal

``` sh

$ install.packages("devtools")

```

Finally install the all packages and its dependencies by typing the following into your RStudio Terminal

```
# this may take a min while dependencies are installed
$ install.packages(“sparklyr”)
$ devtools::install("./a2munge")
$ devtools::install("./a2modeler")

```

##### Running Test Suits from RStudio
If you want to run the test suits for the installed packages then type the following command into your RStudio Terminal

```
# this may take a min while dependencies are installed
$ devtools::test("./a2munge")
$ devtools::test("./a2modeler")

```

#### Building for A2 Module source

##### Maven
> TODO
##### build
> TODO
##### Docker
> TODO
##### rbuild
> TODO
##### rpreinstall
> TODO


# Build from Source
See the [Build from Source](https://stash.synchronoss.net/projects/BDA/repos/sip/browse/doc) Wiki page

# Stay in Touch
SIP team members on [#sip-team-b](https://synchronoss.slack.com/messages/G8QTCE6UD) and releases are announced via our [news feed](https://confluence.synchronoss.net:8443/display/BDA/SIP+Releases)

# License
The A2 Module is released under SIP License.
