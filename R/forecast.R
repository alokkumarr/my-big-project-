


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
    xreg <- data[, colnames(data) != target, drop=FALSE]
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
predict.forecast_model <- function(obj,
                                   periods,
                                   data = NULL,
                                   level = c(80, 95)) {
  method_args <- obj$method_args
  target <- obj$target

  if (!is.null(data)) {
    obj$pipe <- execute(data, obj$pipe)
    data <- obj$pipe$output
    x_vars <- setdiff(colnames(data), target)
    if (length(x_vars) > 0) {
      xreg <- data[, x_vars, drop = FALSE]
    } else {
      xreg <- NULL
    }
  } else{
    xreg <- NULL
  }

  f <- do.call("forecast",
               modifyList(
                 method_args,
                 list(
                   object = obj$fit,
                   xreg = xreg,
                   h = periods,
                   level = level
                 )
               ))
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
        select(1:2) %>%
        setNames(c("index", "predicted"))
    }) %>%
      bind_rows(.id = "sample")
  }) %>%
    bind_rows(.id = "indicie") %>%
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
    checkmate::assert_subset(names(index), c("train", "validation", "test"))

    # Fit model to training sample
    mobj <- fit(mobj,
                data = mobj$pipe$output %>% dplyr::slice(index$train))
    fitted <- fitted(mobj)
    train <- data.frame("index" = index$train, "fitted" = fitted)
    perf <- list("train" = train)

    # Add predictions for validation, test or both
    samples <- names(index)[! sapply(index, is.null)]
    for(smpl in setdiff(samples, "train")){
      predicted <- predict(mobj,
                           data = mobj$pipe$output %>% dplyr::slice(index[[smpl]]),
                           periods = length(index[[smpl]]),
                           level = level)
      smpl_list <- list(data.frame("index" = index[[smpl]], predicted))
      names(smpl_list) <- smpl
      perf <- c(perf, smpl_list)
    }

    mobj$performance[[i]] <- perf
    mobj$status <- "trained"
  }

  mobj
}
