# Introduction

a2munge is an R package to help create Advanced Analytic data pipelines on the SIP platrofrm. The package contains a suite of [Spark](https://spark.apache.org/) compatible data preparation, transformation and analytic functions (aka munging).


# Installation

### SIP Platform

a2munge is installed on the SIP platform as part of its deployment. The a2munge package can be loaded by navigating to an available RStudio port or opening an R GUI

To execute R from the terminal:
```
$ R
```

Type the following into the R console to load the a2munge package

```
> # Load R Package
> library(a2munge)
```


### Local

Clone the SIP Git repository to your local environment. R version >= 3.5.0 required and can be downloaded here: [R](https://www.r-project.org/). RStudio is strongly recommended, the open source version can be downloaded here [RStudio](https://www.rstudio.com/products/rstudio/download/). 

If you haven't done so already, the first step is create a RStudio project for the SIP repository. See this [guide](https://support.rstudio.com/hc/en-us/articles/200532077-Version-Control-with-Git-and-SVN) for details.

Once a SIP project connected to the Stash repository is made, open the project. You should have a fresh RStudio enviornment with the working directory set to your local repository. 

The R package devtools is required to install a2munge. If its not already install on your local environment, run the following

```
# Ensure devtools is installed 
install.packages("devtools")
```

Finally install the a2munge package and its dependencies by typing the following into your console

```
# Use the devtools install command for the a2munge package
#    this may take a min while dependencies are installed
devtools::install("./a2munge")
```

Once completed, its recommend you close the SIP RStudio project, then type the following into the new console to load the a2modeler package into memory

```
> # Load R Package
> library(a2munge)
```

# Getting Started

To get started, check out the User Guide vinette by typing the following into the console and following the link that pops up

```
> # Check out the a2munge User Guide Vinette
> browseVignettes("a2munge")
```
