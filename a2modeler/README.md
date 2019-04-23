# Introduction

a2modeler is an R package that provides a framework for efficiently and reliably building statistical models

# Installation and Getting Started

#### SIP Platform

a2modeler is installed on the SIP platform as part of its deployment. The a2modeler package can be loaded by navigating to an available RStudio port or opening an R GUI

To execute R from the terminal:
```
$ R
```

Type the following into the R console to load the a2modeler package

```
# Load R Package
> library('a2modeler')
```

#### Local

The R package devtools is required to install ```a2modeler```. If its not already install on your local environment, run the following

```
# Ensure devtools is installed
> install.packages("devtools")
```

Finally, install the a2modeler package and its dependencies by typing the following into your console

```
# Use the devtools install command for the a2modeler package
# this may take a min while dependencies are installed
> devtools::install("./a2modeler")
```

Once completed, its recommend you close the SIP RStudio project, then you should be able to type the following into the console to load the a2modeler package into memory

```
# Load R Package
> library('a2modeler')
```

To get started, check out the User Guide Vignettes by typing the following into the console and following the link that pops up

```
# In RStudio, you will see two tabs ```Console``` & ```Terminal```
# Go to Terminal
> cd a2modeler

# then execute the below command
# Check out the a2munge User Guide Vignettes
> browseVignettes("a2modeler")
```

#### Some Important links
 * [Spark MLlib](https://spark.rstudio.com/mlib/)
 * [One-hot encoding](https://en.wikipedia.org/wiki/One-hot)
 * [Root Mean Square](https://en.wikipedia.org/wiki/Root_mean_square)
 * [Classification & Regression](https://spark.apache.org/docs/latest/ml-classification-regression.html)

#### Noteworthy
 * [DESCRIPTION](https://stash.synchronoss.net/projects/BDA/repos/sip/browse/a2modeler/DESCRIPTION) : This is the file in the ```a2modeler``` module maintains the version of packages & minimum version of ```R``` required for the package.
