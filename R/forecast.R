


# Forecast methods currently supported
forecast_methods <- c(
  "Arima" = "Arima",
  "Fractionally Differenced Arima"= "arfima",
  "Auto Arima" = "auto.arima",
  "Auto Fourier" = "auto_fourier",
  "ETS with boxcox, ARIMA errors, Trend and Seasonal" = "bats",
  "Exponentially Smoothing State Space"  = "ets",
  "Neural Network Time Series" = "nnetar",
  "BATS with Trigonometric Seasonal" = "tbats"
)



#' @rdname fit
#' @export
fit.forecast_model <- function(obj, data, ...){

  method <- obj$method
  method_args <- obj$method_args
  target <- obj$target

  checkmate::assert_data_frame(data)
  checkmate::assert_choice(method, forecast_methods)

  y <- as.numeric(data[[target]])
  if(ncol(data) > 1){
    xreg <- data[, -target, drop=FALSE]
  }else{
    xreg <- NULL
  }

  args <- modifyList(method_args, list(y = y, xreg = xreg))
  m <- do.call(method, args)
  #obj$fit <- structure(m, class = c("forecast_fit", class(m)))
  obj$fit <- m
  obj$last_updated <- Sys.time()
  obj$status <- "trained"
  obj
}



#' Forecast Model Fitted Method
#' @rdname fitted
fitted.forecast_model <- function(obj) {
  as.numeric(fitted(obj$fit))
}


#' Forecast Prediction Method
#' @rdname predict
predict.forecast_model <- function(obj, periods, data, level) {

  method_args <- obj$method_args
  target <- obj$target
  y <- as.numeric(data[[target]])
  if(ncol(data) > 1){
    xreg <- data[, -target, drop=FALSE]
  }else{
    xreg <- NULL
  }

  f <- do.call("forecast",
               modifyList(method_args,
                          list(object = obj$fit, xreg = xreg, h = periods,
                               level = level)))
  get_forecasts(f)
}



#' @rdname summary
summary.forecast_model <- function(mobj){
  mobj$fit
}


#' @rdname print
print.forecast_model <- function(mobj){
  mobj$fit
}



#' @export
get_forecasts <- function(x, ...) {
  UseMethod("get_forecasts", x)
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



#' Get Coefficients from Forecast Model Object
#'
#' @param mobj forecast model object as a result of fit function
#' @rdname get_coefs
#' @export
get_coefs.forecast_model <- function(mobj){
  coef(mobj$fit)
}



#' Tidy Forecast Model Performance Object
#
#' @param mobj forecast model object as a result of fit function
#' @rdname get_performance
#' @export
tidy_performance.forecast_model <- function(mobj) {
  checkmate::assert_choice(mobj$status, c("trained", "evaluated", "selected"))

  perf <- data.frame()

  for (i in seq_along(mobj$performance)) {
    indicie <- names(mobj$performance)[i]

    train <- mobj$performance[[i]]$train %>%
      setNames(c("index", "predicted")) %>%
      dplyr::mutate(sample = "train")

    if (!is.null(mobj$performance[[i]]$validation)) {
      validation <- mobj$performance[[i]]$validation %>%
        dplyr::select(index, mean) %>%
        setNames(c("index", "predicted")) %>%
        dplyr::mutate(sample = "validation")

    } else {
      validation <- NULL
    }

    if (!is.null(mobj$performance[[i]]$test)) {
      test <- mobj$performance[[i]]$test %>%
        dplyr::select(index, mean) %>%
        setNames(c("index", "predicted")) %>%
        dplyr::mutate(sample = "test")

    } else {
      test <- NULL
    }

    smpl_perf <- rbind(train, validation , test)
    perf <- rbind(perf,
                  smpl_perf %>%
                    dplyr::mutate(model = mobj$id,
                                  indicie = indicie))
  }

  perf
}



