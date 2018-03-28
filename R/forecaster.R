
#' @export
forecastr <- function(df, ...) {
  UseMethod("forecastr", df)
}



#' @rdname forecaster
#' @export
forecastr.data.frame <- function(df,
                                 index_var = NULL,
                                 measure_var = NULL,
                                 x_vars = NULL,
                                 frequency,
                                 models = "auto.arima",
                                 periods,
                                 level = c(80, 95),
                                 auto_transform = FALSE,
                                 validation = NULL,
                                 validation_metric = c("RMSE"),
                                 benchmark_methods = c("meanf", "snaive"),
                                 ...) {
  if (is.null(index_var))
    index_var <- colnames(df)[1]
  if (is.null(measure_var))
    measure_var <- colnames(df)[2]

  if (!is.null(x_vars) & (!all(x_vars %in% colnames(df)))) {
    stop("Not all x_vars are valid colname names")
  }

  if (any(x_vars %in% c(index_var, measure_var))) {
    stop("x_vars can not be either the index or measure variables")
  }

  if (frequency < 1 | is.null(frequency)) {
    stop(
      "frequency must be positive. see https://robjhyndman.com/hyndsight/seasonal-periods/ for guidance"
    )
  }

  if (is.null(level) |
      is.na(level) | any(level < 0) | any(level >= 100)) {
    stop("valid level required. suggest c(80, 95) for 80 and 95 percent Intervals")
  }

  if (any(level < .8)) {
    message(paste(level, "level low than usual. consider a minimal level of 80"))
  }

  if (any(
    !models %in% c(
      "Arima",
      "arfima",
      "auto.arima",
      "auto_fourier",
      "bats",
      "ets",
      "holt",
      "hw",
      "nnetar",
      "ses",
      "tbats"
    )
  )) {
    stop("models supplied not supported. see forecast R package documentation for details")
  }

  if (length(models) > 1 & is.null(validation)) {
    stop("more than one model provided - validation required")
  }

  if (!is.null(validation) &  (validation <= 0 | validation >= 1.0)) {
    stop("invalid, validation input. needs to be between (0,1).\nrecommend .1 or .2")
  }

  if (!is.null(validation) & validation > .5) {
    message(
      "a large percentage of the data is being used for validation. consider using less.\nrecommend .1 or .2"
    )
  }

  if (any(benchmark_methods %in% c("rwf", "meanf", "snaive", "naive"))) {
    stop(
      "one of bechmark methods not supported. please use at least one of following:\n'rwf', 'meanf', 'snaive', 'naive'"
    )
  }

  dots <- list(...)

  if(auto_transform) {
    lambda <- forecast::BoxCox.lambda(df[[measure_var]])
  }else{
    lambda <- NULL
  }
  dots <- modifyList(dots, list(lambda = lambda))

  if(! is.null(validation)){
    train_n <- floor((1 - validation) * nrow(df))
    train <- df[1:train_n,]
    validation <- df[(train_n+1):nrow(df),]

  }


}





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
auto_fourier <- function(y, xreg = NULL, frequency = NULL, ic = "aicc", ...){

  stopifnot(ic %in% c('aic', 'aicc', 'loglik', 'bic'))

  if(! is.null(frequency) & frequency < 2){
    stop("frequency input should be >= 2")
  }

  if(is.null(frequency)){
    K_seq <- 0
  }else{
    K_seq <- floor(seq(0, frequency/2, length.out = round(log2(frequency))))
  }

  models <- list()
  for(k in K_seq){
    if(k == 0){
      m <- try(forecast::auto.arima(y, ic = ic))
    }else{
      x_reg <- cbind(xreg, forecast::fourier(ts(y, frequency = frequency), K=k))
      m <- try(forecast::auto.arima(y, xreg = x_reg, ic = ic, ...))
    }
    if(class(m)[1] == "try-error"){
      models <- c(models, list(list(aic = Inf)))
    }else if(m$sigma == Inf) {
      models <- c(models, list(list(aic = Inf)))
    }else{
      models <- c(models, list(m))
    }
  }
  error <- lapply(models, function(x) x[[ic]])

  structure(models[[which.min(error)]], class = c("auto_fourier", "Arima"))
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
forecast.auto_fourier <- function(object, h = 10, xreg = NULL, frequency, level = c(80, 95), ...){

  stopifnot("Arima" %in% class(object))

  K <- sum(grepl("S\\d-\\d", coef(object)))
  y <- as.numeric(object$x)

  if(K == 0){
    x_reg <- xreg
  }else{
    x_reg <- cbind(xreg, forecast::fourier(ts(y, frequency=frequency), h=h, K=K))
  }

  arimaobj <- structure(object, class = "Arima")

  forecast::forecast(arimaobj, h, xreg = x_reg, level = level, ...)
}


#' Convert Forecast object to data.frame
#'
#' @rdname as_data_frame
#' @export
as_data_frame.forecast <- function(object){

  df <- as.data.frame(object)
  cns <- tolower(colnames(df))
  cns <- gsub("lo", "lower", cns)
  cns <- gsub("hi", "upper", cns)
  cns <- gsub(" ", "", cns)
  cns[1] <- "forecast"
  cns[-1] <- paste("forecast", cns[-1], sep="_")
  colnames(df) <- cns
  df
}



#' Forecast Fit Arguments Constructer function
new_forecast_fit_args <- function(model,
                                  df,
                                  index_var,
                                  y_var,
                                  x_vars,
                                  boxcox,
                                  ...){

  stopifnot(is.character(model))
  stopifnot(is.data.frame(df))
  stopifnot(is.character(index_var))
  stopifnot(is.character(y_var))
  stopifnot(is.null(x_vars) | all(is.character(x_vars)))
  stopifnot(is.logical(boxcox))


  structure(
    list(
      model = model,
      df = df,
      index_var = index_var,
      y_var = y_var,
      x_vars = x_vars,
      boxcox = boxcox,
      ...
    ),
    class = "forecast_fit_args")
}


forecast_funs <- c(
  "Arima",
  "arfima",
  "auto.arima",
  "auto_fourier",
  "bats",
  "ets",
  "nnetar",
  "tbats"
)


#' Forecast Fit Arguments Validation function
valid_forecast_fit_args <- function(x){

  if (! x$model %in% forecast_funs) {
    stop("model supplied not supported.  \nPlease use one of following: ",
         paste(forecast_funs , collapse = ", "),
         "\nsee forecast R package documentation for details")
  }

  if (!all(x$y_var %in% colnames(x$df))) {
    stop("y_var not a valid colname name")
  }

  if (!is.null(x$x_vars) & (!all(x$x_vars %in% colnames(x$df)))) {
    stop("Not all x_vars are valid colname names")
  }

  if (!is.null(x$x_vars) & (any(x$x_vars %in% c(x$index_var, x$measure_var)))) {
    stop("x_vars can not be either the index or measure variables")
  }

  if(x$model == 'ets' & (! is.null(x$x_vars))) {
    message("ets model does not support covariates. dropping x_vars from model")
    x$x_vars <- NULL
  }

  x
}


#' Forecast Fit Arguments Helper function
#'
#' @rdname fit_args
#' @export
fit_args.forecast <- function(model = "auto.arima",
                              df,
                              index_var,
                              y_var,
                              x_vars = NULL,
                              boxcox = FALSE,
                              ...){
  valid_forecast_fit_args(
    new_forecast_fit_args(model = model,
                          df = df,
                          index_var = index_var,
                          y_var = y_var,
                          x_vars = x_vars,
                          boxcox = boxcox,
                          ...)
  )
}


#' Forecast Fit Model function
#' @rdname fit_model
#' @export
fit_model.forecast_fit_args <- function(obj){

  y <- as.numeric(obj$df[[obj$y_var]])
  if(is.null(obj$x_vars)){
    xreg <- NULL
  }else{
    xreg <- obj$df[, obj$x_vars]
  }

  if(obj$boxcox) {
    lambda <- forecast::BoxCox.lambda(y)
  }else{
    lambda <- NULL
  }

  dots <- obj[setdiff(names(obj), c("df", "index_var", "y_var", "x_vars", "model", "boxcox"))]
  model_args <- modifyList(Filter(Negate(is.null),list(y=y, xreg=xreg, lambda=lambda)), dots)
  do.call(obj$model, model_args)
}




