% Generated by roxygen2: do not edit by hand
% Please edit documentation in R/helpers.R
\name{schema}
\alias{schema}
\alias{schema.data.frame}
\alias{schema.tbl_spark}
\title{Dataset Schema Function}
\usage{
schema(df)

\method{schema}{data.frame}(df)

\method{schema}{tbl_spark}(df)
}
\arguments{
\item{df}{dataframe}
}
\value{
nested list with element for each column in dataset
}
\description{
Function to extract dataset schema. Returns a nested list with an element for
each column in the dataset. Output is similiar to sparklyr::sdf_schema.
}
\examples{
schema(mtcars)
}
