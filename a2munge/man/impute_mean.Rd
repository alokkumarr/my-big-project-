% Generated by roxygen2: do not edit by hand
% Please edit documentation in R/imputer.R
\name{impute_mean}
\alias{impute_mean}
\title{Impute Mean}
\usage{
impute_mean(df, measure_vars)
}
\arguments{
\item{df}{Spark or R data.frame}

\item{measure_vars}{optional character vector of column names to apply impute
function on. default is NULL which applies impute on all columns with
missing values}
}
\value{
imputed Spark or R data.frame
}
\description{
Function to impute missing values with the average value. Applied columwise -
the average value imputed is the average for by column
}
