

#' Fit Auto Fouier Arima model to univariate time series
#'
#' The auto_fourier_arima function is a wrapper function around the auto.arima
#' function in the forecast package. The wrapper adds fourier harmonic
#' regressors to model seasonal factors. This function fits multiple fourier
#' frequencies and selects the model with the best fit as measured by min aic.
#' The case of no harmonic regressors is also considered
#'
#'
#' @inheritParams forecast::auto.arima
#' @param x Optional a vector or matrix of extrenal regressors. Must have same
#'   number of rows as y
#' @param frequency numeric input for seasonal frequency of y - univarite time
#'   series. Default is NULL for no seasonality
#' @param ic Fit metric to select best model. Options are 'aic', 'aicc',
#'   'loglik', or 'bic'. Default is 'aicc'
#'
#' @return returns a list with best ARIMA model according AIC including harmonic
#'   regressors and K number of harmonic regressor terms
#' @export
#'
#' @examples
#' library(forecast)
#' auto_fourier(austres, frequency = 4)
auto_fourier <- function(y,
                         xreg = NULL,
                         frequency = NULL,
                         ic = "aicc",
                         ...) {
  stopifnot(ic %in% c('aic', 'aicc', 'loglik', 'bic'))

  if (!is.null(frequency) & frequency < 2) {
    stop("frequency input should be >= 2")
  }

  if (is.null(frequency)) {
    K_seq <- 0
  } else{
    K_seq <-
      floor(seq(0, frequency / 2, length.out = round(log2(frequency))))
  }

  models <- list()
  for (k in K_seq) {
    if (k == 0) {
      m <- try(forecast::auto.arima(y, ic = ic))
    } else{
      x_reg <-
        cbind(xreg, forecast::fourier(ts(y, frequency = frequency), K = k))
      m <- try(forecast::auto.arima(y, xreg = x_reg, ic = ic, ...))
    }
    if (class(m)[1] == "try-error") {
      models <- c(models, list(list(aic = Inf)))
    } else if (m$sigma == Inf) {
      models <- c(models, list(list(aic = Inf)))
    } else{
      models <- c(models, list(m))
    }
  }
  error <- lapply(models, function(x)
    x[[ic]])

  structure(c(models[[which.min(error)]],
              K = K_seq[which.min(error)]),
            class = c("auto_fourier", "Arima"))
}



#' Forecast Auto Fourier Arima model for univariate time series
#'
#' Function to provide forecasts from an auto_fourier_arima model. The
#' auto_fourier_model adds harmonic regressors to the auto_arima function from
#' the forecast function
#'
#' @inheritParams forecast::auto.arima
#' @param x Optional a vector or matrix of extrenal regressors. Must correspond
#'   to forecast period and be the same length as the forecast
#' @param frequency seasonal frequency of y - univarite time series
#'
#' @return An object of class 'forecast'
#' @export
#'
#' @examples
#' library(forecast)
#' fit <- auto_fourier(austres, frequency = 4)
#' forecast(fit, h=12, frequency=12)
forecast.auto_fourier <- function(object,
                                  h = 10,
                                  xreg = NULL,
                                  frequency,
                                  level = c(80, 95),
                                  ...){

  stopifnot("Arima" %in% class(object))

  K <- as.numeric(object$K)
  y <- as.numeric(object$x)

  if(K == 0){
    x_reg <- NULL
  }else{
    x_reg <- cbind(xreg, forecast::fourier(ts(y, frequency=frequency), h=h, K=K))
  }

  arimaobj <- structure(object, class = "Arima")

  forecast::forecast(arimaobj, h, xreg = x_reg, level = level, ...)
}

