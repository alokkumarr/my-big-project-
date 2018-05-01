

#
# # Forecast methods currently supported
# forecast_methods <- c(
#   "Arima" = "Arima",
#   "Fractionally Differenced Arima"= "arfima",
#   "Auto Arima" = "auto.arima",
#   "Auto Fourier" = "auto_fourier",
#   "ETS with boxcox, ARIMA errors, Trend and Seasonal" = "bats",
#   "Exponentially Smoothing State Space"  = "ets",
#   "Neural Network Time Series" = "nnetar",
#   "BATS with Trigonometric Seasonal" = "tbats"
# )

# Forecast methods currently supported
forecast_methods <- data.frame(
  method =  c(
    "Arima",
    "arfima",
    "auto.arima",
    "auto_fourier",
    "bats",
    "ets",
    "nnetar",
    "tbats"
  ),
  name = c(
    "Arima",
    "Fractionally Differenced Arima",
    "Auto Arima",
    "Auto Fourier",
    "ETS with boxcox, ARIMA errors, Trend and Seasonal",
    "Exponentially Smoothing State Space",
    "Neural Network Time Series",
    "BATS with Trigonometric Seasonal"
  ),
  package = c(
    "forecast",
    "forecast",
    "forecast",
    "a2modeler",
    "forecast",
    "forecast",
    "forecast",
    "forecast"
  )
)


#' @rdname fit
#' @export
fit.forecast_model <- function(obj, data, ...) {
  checkmate::assert_data_frame(data)
  checkmate::assert_choice(obj$method, as.character(forecast_methods$method))

  y <- as.numeric(data[[obj$target]])
  x_vars <- setdiff(colnames(data), c(obj$target, obj$index_var))
  if (length(x_vars) > 0) {
    xreg <- data[, x_vars, drop = FALSE]
  } else{
    xreg <- NULL
  }

  args <- modifyList(obj$method_args, list(y = y, xreg = xreg))
  pkg <- as.character(dplyr::filter(forecast_methods, method == obj$method)$package)
  fun <- get(obj$method, asNamespace(pkg))
  m <- do.call(fun, args)
  obj$fit <- m
  obj$last_updated <- Sys.time()
  obj$status <- "trained"
  obj
}


#' Forecast Model Fitted Method
#' @rdname fitted
#' @export
fitted.forecast_model <- function(obj) {
  as.numeric(fitted(obj$fit))
}


#' Forecast Prediction Method
#' @rdname predict
#' @export
predict.forecast_model <- function(obj,
                                   periods,
                                   data = NULL,
                                   level = c(80, 95)) {
  if (!is.null(data)) {
    if (nrow(data) != periods) {
      warning("number of data rows doesn't match forecast periods")
    }

    x_vars <- setdiff(colnames(data), c(obj$target, obj$index_var))
    if (length(x_vars) > 0) {
      xreg <- data[, x_vars, drop = FALSE]
    } else {
      xreg <- NULL
    }
  } else{
    xreg <- NULL
  }

  fun <- get("forecast", asNamespace("forecast"))
  f <- do.call(fun,
               modifyList(
                 obj$method_args,
                 list(
                   object = obj$fit,
                   xreg = xreg,
                   h = periods,
                   level = level
                 )
               ))
  get_forecasts(f)
}


#' @export
#' @rdname summary
summary.forecast_model <- function(mobj){
  mobj$fit
}


#' @export
#' @rdname print
print.forecast_model <- function(mobj){
  mobj$fit
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
  checkmate::assert_choice(mobj$status, c("trained", "evaluated", "selected", "final"))

  lapply(mobj$performance, function(z) {
    lapply(z, function(x) {
      x %>%
        dplyr::select(1:2) %>%
        setNames(c("index", "predicted"))
    }) %>%
      dplyr::bind_rows(.id = "sample")
  }) %>%
    dplyr::bind_rows(.id = "indicie") %>%
    dplyr::mutate(model = mobj$id)
}



#' @rdname evaluate
#' @export
evaluate.forecast_model <- function(mobj, target_df, measure) {

   mobj$evaluate <- tidy_performance(mobj) %>%
    dplyr::inner_join(target_df, by = "index") %>%
    dplyr::group_by(model, sample, indicie) %>%
    dplyr::do(data.frame(
      match.fun(measure$method)(.,
                                actual = mobj$target,
                                predicted = "predicted")
    )) %>%
    dplyr::ungroup() %>%
    setNames(c("model", "sample", "indicie", measure$method))

   mobj
}



#' @rdname train
#' @export
train.forecast_model <- function(mobj, indicies, level) {
  checkmate::assert_list(indicies)
  mobj$performance <- indicies
  for (i in seq_along(indicies)) {
    index <- indicies[[i]]
    train_index <- index$train
    checkmate::assert_subset(names(index), c("train", "validation", "test"))

    # Fit model to training sample
    mobj <- fit(mobj,
                data = mobj$pipe$output %>% dplyr::slice(train_index))
    fitted <- fitted(mobj)
    train <- data.frame("index" = index$train, "fitted" = fitted)
    perf <- list("train" = train)

    # Add predictions for validation, test or both
    samples <- names(index)[! sapply(index, is.null)]
    for(smpl in setdiff(samples, "train")){
      smpl_index <- index[[smpl]]
      predicted <- predict(mobj,
                           data = mobj$pipe$output %>% dplyr::slice(smpl_index),
                           periods = length(smpl_index),
                           level = level)
      smpl_list <- list(data.frame("index" = smpl_index, predicted))
      names(smpl_list) <- smpl
      perf <- c(perf, smpl_list)
    }

    mobj$performance[[i]] <- perf
    mobj$status <- "trained"
  }

  mobj
}
