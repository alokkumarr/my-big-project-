% Generated by roxygen2: do not edit by hand
% Please edit documentation in R/forecast-model.R, R/model.R, R/spark-ml.R,
%   R/spark-model.R
\name{get_coefs.forecast_model}
\alias{get_coefs.forecast_model}
\alias{get_coefs}
\alias{get_coefs.default}
\alias{get_coefs.ml_model_linear_regression}
\alias{get_coefs.ml_linear_regression_model}
\alias{get_coefs.ml_model_logistic_regression}
\alias{get_coefs.ml_logistic_regression_model}
\alias{get_coefs.ml_model_generalized_linear_regression}
\alias{get_coefs.spark_model}
\title{Get Coefficients from Forecast Model Object}
\usage{
\method{get_coefs}{forecast_model}(mobj)

get_coefs(mobj, ...)

\method{get_coefs}{default}(mobj, ...)

\method{get_coefs}{ml_model_linear_regression}(mobj)

\method{get_coefs}{ml_linear_regression_model}(mobj, features)

\method{get_coefs}{ml_model_logistic_regression}(mobj)

\method{get_coefs}{ml_logistic_regression_model}(mobj, features)

\method{get_coefs}{ml_model_generalized_linear_regression}(mobj)

\method{get_coefs}{spark_model}(mobj)
}
\arguments{
\item{mobj}{model object}

\item{...}{additional arguments to pass through}
}
\description{
Extracts coefficient summary from Spark-ML linear regression model

Extracts coefficient summary from Spark-ML logistic regression model

Extracts coefficient summary from Spark-ML generalized regression model
}
