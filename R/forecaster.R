
#' @export
forecastr <- function(df, ...) {
  UseMethod("forecastr", df)
}



#' @rdname forecaster
#' @export
forecastr.data.frame <- function(df,
                                 index_var = NULL,
                                 y_var = NULL,
                                 x_vars = NULL,
                                 frequency = NULL,
                                 models = "auto.arima",
                                 auto_transform = FALSE,
                                 n_periods = 7,
                                 level = c(80, 95),
                                 validation = NULL,
                                 validation_metric = c("RMSE"),
                                 validation_benchmark_methods = c("meanf", "snaive"),
                                 validation_split = .2,
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




# Forecast algos currently supported
forecast_algos <- c(
  "Arima" = "Arima",
  "Fractionally Differenced Arima"= "arfima",
  "Auto Arima" = "auto.arima",
  "Auto Fourier" = "auto_fourier",
  "ETS with boxcox, ARIMA errors, Trend and Seasonal" = "bats",
  "Exponentially Smoothing State Space"  = "ets",
  "Neural Network Time Series" = "nnetar",
  "BATS with Trigonometric Seasonal" = "tbats"
)


#' Forecast Method Constructer function
new_forecaster_method <- function(algo,
                                  df,
                                  index_var,
                                  y_var,
                                  x_vars,
                                  boxcox,
                                  name,
                                  ...){

  stopifnot(is.character(algo))
  stopifnot(is.data.frame(df))
  stopifnot(is.null(index_var) | is.character(index_var))
  stopifnot(is.character(y_var))
  stopifnot(is.null(x_vars) | all(is.character(x_vars)))
  stopifnot(is.logical(boxcox))
  stopifnot(is.null(name) | is.character(name))

  if(is.null(name)) name <- y_var

  structure(
    list(
      algo = algo,
      df = df,
      index_var = index_var,
      y_var = y_var,
      x_vars = x_vars,
      boxcox = boxcox,
      name = name,
      ...
    ),
    class = "forecaster_method")
}


#' Forecast Fit Arguments Validation function
valid_forecaster_fit_args <- function(x){

  if (! x$algo %in% forecast_algos) {
    stop("algorithm supplied not supported.  \nPlease use one of following: ",
         paste(forecast_algos , collapse = ", "),
         "\nsee forecast R package documentation for details")
  }

  if (!all(x$y_var %in% colnames(x$df))) {
    stop("y_var not a valid colname name")
  }

  if (!is.null(x$x_vars) & (!all(x$x_vars %in% colnames(x$df)))) {
    stop("Not all x_vars are valid column names")
  }

  if (!is.null(x$x_vars) & (any(x$x_vars %in% c(x$index_var, x$measure_var)))) {
    stop("x_vars can not be either the index or measure variables")
  }

  if(x$algo == 'ets' & (! is.null(x$x_vars))) {
    message("ets algorithm does not support covariates. dropping x_vars from model")
    x$x_vars <- NULL
  }

  if(class(try(is.function(get(x$algo)), silent = TRUE)) == "try-error"){
    stop("model not found in current envirnoment. make sure all required packages loaded")
  }

  x
}


#' Forecaster Fit Arguments Helper function
#'
#' @rdname fit_args
#' @export
fit_args.forecaster <- function(algo = "auto.arima",
                                df,
                                index_var,
                                y_var,
                                x_vars = NULL,
                                boxcox = FALSE,
                                name = NULL,
                                ...){
  valid_forecaster_fit_args(
    new_forecaster_fit_args(algo = algo,
                            df = df,
                            index_var = index_var,
                            y_var = y_var,
                            x_vars = x_vars,
                            boxcox = boxcox,
                            name = name,
                            ...)
  )
}



#' Forecast Fit Model function
#'
#' @param obj forecast fit argument object. created with fit_args method.
#'
#' @return forecaster model object
#'
#' @rdname fit_model
#' @export
fit_model.forecaster_fit_args <- function(obj) {
  y <- as.numeric(obj$df[[obj$y_var]])
  name <- deparse(substitute(obj$df))

  if (is.null(obj$x_vars)) {
    xreg <- NULL
  } else{
    xreg <- obj$df[, obj$x_vars, drop = FALSE]
  }

  if (is.null(obj$index_var)) {
    index <- 1:length(y)
  } else{
    index <- obj$df[[obj$index_var]]
  }

  if (obj$boxcox) {
    lambda <- forecast::BoxCox.lambda(y)
  } else{
    lambda <- NULL
  }

  dots <- obj[setdiff(names(obj), c("df", "index_var", "y_var", "x_vars", "algo", "boxcox", "name"))]
  model_args <- modifyList(Filter(Negate(is.null), list(y = y, xreg = xreg, lambda = lambda)),
                           dots)
  model <- do.call(obj$algo, model_args)
  model_obj <- list(
    model = model,
    meta_data = list(
      index = index,
      index_var = obj$index_var,
      y_var = obj$y_var,
      x_vars = obj$x_vars,
      algo = obj$algo,
      boxcox = obj$boxcox,
      name = obj$name
    )
  )

  structure(model_obj, class = c('forecast_model', class(model)))
}


#' @rdname summary
summary.forecast_model <- function(mobj){
  mobj$model
}


#' @rdname print
print.forecast_model <- function(mobj){
  mobj$model
}



#' Get Coefficients from Forecast Model Object
#'
#' @param mobj forecast model object as a result of fit function
#' @rdname get_coefs
#' @export
get_coefs.forecast_model <- function(mobj){
  coef(mobj$model)
}


#' Get Fitted Values from Forecast Model Object
#'
#' @param mobj forecast model object as a result of fit function
#' @rdname get_fit
#' @export
get_fit.forecast_model <- function(mobj){

  index <- mobj$meta_data$index
  ydf <- data.frame(
    index = index,
    y = as.numeric(mobj$model$x),
    fitted = as.numeric(mobj$model$fitted),
    residuals = as.numeric(mobj$model$residuals)
  )
  colnames(ydf)[1:2] <- c(mobj$meta_data$index_var, mobj$meta_data$y_var)

  ydf
}



#' Predict Forecast method
#'
#' Method to make forecast predictions using a forecaster model object
#'
#' @param mobj forecast model object as a result of fit function
#' @rdname predict
#' @export
predict.forecast_model <- function(mobj, ...) {
  dots <- list(...)
  object <- mobj$model
  fobj <- do.call("forecast", modifyList(list(object = object), dots))

  index <- mobj$meta_data$index
  findex <- seq(from = max(index) + 1, length.out = dots$h, by = 1)
  fdf <- cbind(findex, get_forecasts(fobj))
  colnames(fdf)[1] <- mobj$meta_data$index_var
  ydf <- data.frame(
    index = index,
    y = as.numeric(mobj$model$x),
    fitted = as.numeric(mobj$model$fitted),
    residuals = as.numeric(mobj$model$residuals)
  )
  colnames(ydf)[1:2] <- c(mobj$meta_data$index_var, mobj$meta_data$y_var)

  structure(
    modifyList(
      mobj$meta_data,
      list(
        method = fobj$method,
        level = fobj$level,
        made_at = Sys.time(),
        forecasts = fdf,
        fit = ydf
      )
    ),
    class = "forecast_predict")
}


#' @export
get_forecasts <- function(x, ...) {
  UseMethod("get_forecasts", x)
}


#' Function to Get Forecasts from forecast prediction
#' @param pobj forecast prediction object as a result of call to predict function
#' @rdname get_forecasts
#' @return data.frame with forecasts
get_forecasts.forecast_predict <- function(pobj){
  pobj$forecasts
}


#' Function to Get Predictions from forecast prediction
#' @param pobj forecast prediction object as a result of call to predict function
#' @rdname get_predictions
#' @return data.frame with forecast predictions
get_predictions.forecast_predict <- function(pobj){
  pobj$forecasts
}


#' Function to Get Fit from forecast prediction
#' @param pobj forecast prediction object as a result of call to predict function
#' @rdname get_fit
#' @return data.frame with forecast model fitted values
get_fit.forecast_predict <- function(pobj){
  pobj$fit
}


#' Get Forecasts from forecast object
#'
#' @param fobj forecast object
#' @rdname get_forecasts
#' @export
#' @return data.frame with forecats from forecast model
get_forecasts.forecast <- function(fobj){

  df <- as.data.frame(fobj)
  cns <- tolower(colnames(df))
  cns <- gsub("lo", "lower", cns)
  cns <- gsub("hi", "upper", cns)
  cns <- gsub(" ", "", cns)
  cns[1] <- "mean"
  colnames(df) <- cns
  df
}



#' Convert Forecast object to data.frame
#'
#' @param fobj forecast object
#' @rdname as_data_frame
#' @export
as_data_frame.forecast <- function(fobj){

  df <- as.data.frame(fobj)
  cns <- tolower(colnames(df))
  cns <- gsub("lo", "lower", cns)
  cns <- gsub("hi", "upper", cns)
  cns <- gsub(" ", "", cns)
  cns[1] <- "mean"
  cns <- paste("forecast", sep="_")
  colnames(df) <- cns
  df
}

