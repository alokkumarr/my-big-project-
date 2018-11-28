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

Open RStudio and type the following into the console pane

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
