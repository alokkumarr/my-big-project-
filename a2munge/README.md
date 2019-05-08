# Introduction

a2munge is an R package to help create Advanced Analytic data pipelines on the SIP platform. The package contains a suite of [Spark](https://spark.apache.org/) compatible data preparation, transformation and analytic functions (aka munging).


# Installation and Getting Started

#### SIP Platform

a2munge is installed on the SIP platform as part of its deployment. The a2munge package can be loaded by navigating to an available RStudio port or opening an R GUI

To execute R from the terminal:
```
$ R
```

Type the following into the R console to load the a2munge package

```
# Load R Package
> library(a2munge)
```

#### Local

The R package devtools is required to install ```a2munge```. If its not already install on your local environment, run the following

```
# Ensure devtools is installed
> install.packages("devtools")
```

Finally install the a2munge package and its dependencies by typing the following into your console

```
# Use the devtools install command for the a2munge package
# this may take a min while dependencies are installed
> devtools::install("./a2munge")
```

Once completed, its recommend you close the SIP RStudio project, then type the following into the new console to load the a2modeler package into memory

```
# Load R Package
> library(a2munge)
```

#### Getting Started

To get started, check out the User Guide Vignettes by typing the following into the console and following the link that pops up

```
# In RStudio, you will see two tabs ```Console``` & ```Terminal```
# Go to Terminal
> cd a2munge

# then execute the below command
# Check out the a2munge User Guide Vignettes
> browseVignettes("a2munge")
```

#### Some Important links
 * [Basic Probability Distribution](http://www.cyclismo.org/tutorial/R/probability.html#the-normal-distribution)
 * [dplyr](https://dplyr.tidyverse.org/)
 * [tidyverse cheatsheet](https://github.com/rstudio/cheatsheets/blob/master/data-import.pdf)
 * [Tibbles](https://cran.r-project.org/web/packages/tibble/vignettes/tibble.html)
 * [DataWrangling cheatsheet](https://www.rstudio.com/wp-content/uploads/2015/02/data-wrangling-cheatsheet.pdf)
 * [Anomaly Detector](https://arxiv.org/pdf/1704.07706.pdf)

#### Noteworthy
 * [DESCRIPTION](https://stash.synchronoss.net/projects/BDA/repos/sip/browse/a2munge/DESCRIPTION) : This is the file in the ```a2munge``` module maintains the version of packages & minimum version of ```R``` required for the package.
