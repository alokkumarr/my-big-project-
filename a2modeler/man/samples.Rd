% Generated by roxygen2: do not edit by hand
% Please edit documentation in R/sampling.R
\name{samples}
\alias{samples}
\title{Sample Class Helper function}
\usage{
samples(validation_method, validation_args, test_holdout_prct,
  test_holdout_method, downsample_prct, train_indicies,
  validation_indicies, indicies_names, test_index)
}
\arguments{
\item{validation_method}{name of validation method. examples are holdout,
cross-validation, etc...}

\item{validation_args}{list of arguments to pass to validation method}

\item{test_holdout_prct}{percent of total data records to use for test
dataset}

\item{downsample_prct}{percent of total data to downsample and use for
analysis}

\item{train_indicies}{list of numeric train indicies. each index contains
numeric vector pertaining to row numbers to use for model fitting}

\item{validation_indicies}{list of numeric validation indicies. each index contains
numeric vector pertaining to row numbers to use for model validation}

\item{indicies_names}{character vector of indicies names}

\item{test_index}{numeric index with records to use for model testing}
}
\value{
samples object
}
\description{
Creates a valid object of samples class
}
