% Generated by roxygen2: do not edit by hand
% Please edit documentation in R/auto_fourier.R
\name{forecast.auto_fourier}
\alias{forecast.auto_fourier}
\title{Forecast Auto Fourier Arima model for univariate time series}
\usage{
\method{forecast}{auto_fourier}(object, h = 10, xreg = NULL, frequency,
  level = c(80, 95), ...)
}
\arguments{
\item{object}{auto_fourier object forecast model}

\item{h}{forecast horizon length}

\item{xreg}{Optional a vector or matrix of extrenal regressors. Must
correspond to forecast period and be the same length as the forecast}

\item{frequency}{seasonal frequency of y - univarite time series}

\item{level}{vector of numerical confidence levels. Ex - 80 is a 80 percent
confidence level}

\item{...}{additional arguments to pass to forecast function}
}
\value{
An object of class 'forecast'
}
\description{
Function to provide forecasts from an auto_fourier_arima model. The
auto_fourier_model adds harmonic regressors to the auto_arima function from
the forecast function
}
\examples{
library(forecast)
fit <- auto_fourier(austres, frequency = 4)
forecast(fit, h=12, frequency=4)
}
