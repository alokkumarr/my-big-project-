


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
fit.forecast_model <- function(obj, method, method_args, target, data, ...){
  checkmate::assert_choice(method, forecast_methods)

  y <- as.numeric(data[[target]])
  if(ncol(data) > 1){
    xreg <- data[, -target, drop=FALSE]
  }else{
    xreg <- NULL
  }

  args <- modifyList(method_args, list(y = y, xreg = xreg))
  model <- do.call(method, args)
  structure(model, class = c('forecast_model', class(model)))
}

#' @rdname fitted
#' @export
fitted.forecast_model



#'
#' #' Forecast Fit Arguments Validation function
#' valid_forecaster_fit_args <- function(x){
#'
#'   if (! x$algo %in% forecast_algos) {
#'     stop("algorithm supplied not supported.  \nPlease use one of following: ",
#'          paste(forecast_algos , collapse = ", "),
#'          "\nsee forecast R package documentation for details")
#'   }
#'
#'   if (!all(x$y_var %in% colnames(x$df))) {
#'     stop("y_var not a valid colname name")
#'   }
#'
#'   if (!is.null(x$x_vars) & (!all(x$x_vars %in% colnames(x$df)))) {
#'     stop("Not all x_vars are valid column names")
#'   }
#'
#'   if (!is.null(x$x_vars) & (any(x$x_vars %in% c(x$index_var, x$measure_var)))) {
#'     stop("x_vars can not be either the index or measure variables")
#'   }
#'
#'   if(x$algo == 'ets' & (! is.null(x$x_vars))) {
#'     message("ets algorithm does not support covariates. dropping x_vars from model")
#'     x$x_vars <- NULL
#'   }
#'
#'   if(class(try(is.function(get(x$algo)), silent = TRUE)) == "try-error"){
#'     stop("model not found in current envirnoment. make sure all required packages loaded")
#'   }
#'
#'   x
#' }
#'
#'
#'
#'
#' #' Forecast Fit Model function
#' #'
#' #' @param obj forecast fit argument object. created with fit_args method.
#' #'
#' #' @return forecaster model object
#' #'
#' #' @rdname fit_model
#' #' @export
#' fit_model.forecaster_fit_args <- function(obj) {
#'   y <- as.numeric(obj$df[[obj$y_var]])
#'   name <- deparse(substitute(obj$df))
#'
#'   if (is.null(obj$x_vars)) {
#'     xreg <- NULL
#'   } else{
#'     xreg <- obj$df[, obj$x_vars, drop = FALSE]
#'   }
#'
#'   if (is.null(obj$index_var)) {
#'     index <- 1:length(y)
#'   } else{
#'     index <- obj$df[[obj$index_var]]
#'   }
#'
#'   if (obj$boxcox) {
#'     lambda <- forecast::BoxCox.lambda(y)
#'   } else{
#'     lambda <- NULL
#'   }
#'
#'   dots <- obj[setdiff(names(obj), c("df", "index_var", "y_var", "x_vars", "algo", "boxcox", "name"))]
#'   model_args <- modifyList(Filter(Negate(is.null), list(y = y, xreg = xreg, lambda = lambda)),
#'                            dots)
#'   model <- do.call(obj$algo, model_args)
#'   model_obj <- list(
#'     model = model,
#'     meta_data = list(
#'       index = index,
#'       index_var = obj$index_var,
#'       y_var = obj$y_var,
#'       x_vars = obj$x_vars,
#'       algo = obj$algo,
#'       boxcox = obj$boxcox,
#'       name = obj$name
#'     )
#'   )
#'
#'   structure(model_obj, class = c('forecast_model', class(model)))
#' }
#'
#'
#'
#'
#'
#'
#' #' Forecaster Fit Arguments Helper function
#' #'
#' #' @rdname fit_args
#' #' @export
#' fit_args.forecaster <- function(algo = "auto.arima",
#'                                 df,
#'                                 index_var,
#'                                 y_var,
#'                                 x_vars = NULL,
#'                                 boxcox = FALSE,
#'                                 name = NULL,
#'                                 ...){
#'   valid_forecaster_fit_args(
#'     new_forecaster_fit_args(algo = algo,
#'                             df = df,
#'                             index_var = index_var,
#'                             y_var = y_var,
#'                             x_vars = x_vars,
#'                             boxcox = boxcox,
#'                             name = name,
#'                             ...)
#'   )
#' }
#'
#'
#'
#' #' Forecast Fit Model function
#' #'
#' #' @param obj forecast fit argument object. created with fit_args method.
#' #'
#' #' @return forecaster model object
#' #'
#' #' @rdname fit_model
#' #' @export
#' fit_model.forecaster_fit_args <- function(obj) {
#'   y <- as.numeric(obj$df[[obj$y_var]])
#'   name <- deparse(substitute(obj$df))
#'
#'   if (is.null(obj$x_vars)) {
#'     xreg <- NULL
#'   } else{
#'     xreg <- obj$df[, obj$x_vars, drop = FALSE]
#'   }
#'
#'   if (is.null(obj$index_var)) {
#'     index <- 1:length(y)
#'   } else{
#'     index <- obj$df[[obj$index_var]]
#'   }
#'
#'   if (obj$boxcox) {
#'     lambda <- forecast::BoxCox.lambda(y)
#'   } else{
#'     lambda <- NULL
#'   }
#'
#'   dots <- obj[setdiff(names(obj), c("df", "index_var", "y_var", "x_vars", "algo", "boxcox", "name"))]
#'   model_args <- modifyList(Filter(Negate(is.null), list(y = y, xreg = xreg, lambda = lambda)),
#'                            dots)
#'   model <- do.call(obj$algo, model_args)
#'   model_obj <- list(
#'     model = model,
#'     meta_data = list(
#'       index = index,
#'       index_var = obj$index_var,
#'       y_var = obj$y_var,
#'       x_vars = obj$x_vars,
#'       algo = obj$algo,
#'       boxcox = obj$boxcox,
#'       name = obj$name
#'     )
#'   )
#'
#'   structure(model_obj, class = c('forecast_model', class(model)))
#' }
#'
#'
#' #' @rdname summary
#' summary.forecast_model <- function(mobj){
#'   mobj$model
#' }
#'
#'
#' #' @rdname print
#' print.forecast_model <- function(mobj){
#'   mobj$model
#' }
#'
#'
#'
#' #' Get Coefficients from Forecast Model Object
#' #'
#' #' @param mobj forecast model object as a result of fit function
#' #' @rdname get_coefs
#' #' @export
#' get_coefs.forecast_model <- function(mobj){
#'   coef(mobj$model)
#' }
#'
#'
#' #' Get Fitted Values from Forecast Model Object
#' #'
#' #' @param mobj forecast model object as a result of fit function
#' #' @rdname get_fit
#' #' @export
#' get_fit.forecast_model <- function(mobj){
#'
#'   index <- mobj$meta_data$index
#'   ydf <- data.frame(
#'     index = index,
#'     y = as.numeric(mobj$model$x),
#'     fitted = as.numeric(mobj$model$fitted),
#'     residuals = as.numeric(mobj$model$residuals)
#'   )
#'   colnames(ydf)[1:2] <- c(mobj$meta_data$index_var, mobj$meta_data$y_var)
#'
#'   ydf
#' }
#'
#'
#'
#' #' Predict Forecast method
#' #'
#' #' Method to make forecast predictions using a forecaster model object
#' #'
#' #' @param mobj forecast model object as a result of fit function
#' #' @rdname predict
#' #' @export
#' predict.forecast_model <- function(mobj, ...) {
#'   dots <- list(...)
#'   object <- mobj$model
#'   fobj <- do.call("forecast", modifyList(list(object = object), dots))
#'
#'   index <- mobj$meta_data$index
#'   findex <- seq(from = max(index) + 1, length.out = dots$h, by = 1)
#'   fdf <- cbind(findex, get_forecasts(fobj))
#'   colnames(fdf)[1] <- mobj$meta_data$index_var
#'   ydf <- data.frame(
#'     index = index,
#'     y = as.numeric(mobj$model$x),
#'     fitted = as.numeric(mobj$model$fitted),
#'     residuals = as.numeric(mobj$model$residuals)
#'   )
#'   colnames(ydf)[1:2] <- c(mobj$meta_data$index_var, mobj$meta_data$y_var)
#'
#'   structure(
#'     modifyList(
#'       mobj$meta_data,
#'       list(
#'         method = fobj$method,
#'         level = fobj$level,
#'         made_at = Sys.time(),
#'         forecasts = fdf,
#'         fit = ydf
#'       )
#'     ),
#'     class = "forecast_predict")
#' }
#'
#'
#' #' @export
#' get_forecasts <- function(x, ...) {
#'   UseMethod("get_forecasts", x)
#' }
#'
#'
#' #' Function to Get Forecasts from forecast prediction
#' #' @param pobj forecast prediction object as a result of call to predict function
#' #' @rdname get_forecasts
#' #' @return data.frame with forecasts
#' get_forecasts.forecast_predict <- function(pobj){
#'   pobj$forecasts
#' }
#'
#'
#' #' Function to Get Predictions from forecast prediction
#' #' @param pobj forecast prediction object as a result of call to predict function
#' #' @rdname get_predictions
#' #' @return data.frame with forecast predictions
#' get_predictions.forecast_predict <- function(pobj){
#'   pobj$forecasts
#' }
#'
#'
#' #' Function to Get Fit from forecast prediction
#' #' @param pobj forecast prediction object as a result of call to predict function
#' #' @rdname get_fit
#' #' @return data.frame with forecast model fitted values
#' get_fit.forecast_predict <- function(pobj){
#'   pobj$fit
#' }
#'
#'
#' #' Get Forecasts from forecast object
#' #'
#' #' @param fobj forecast object
#' #' @rdname get_forecasts
#' #' @export
#' #' @return data.frame with forecats from forecast model
#' get_forecasts.forecast <- function(fobj){
#'
#'   df <- as.data.frame(fobj)
#'   cns <- tolower(colnames(df))
#'   cns <- gsub("lo", "lower", cns)
#'   cns <- gsub("hi", "upper", cns)
#'   cns <- gsub(" ", "", cns)
#'   cns[1] <- "mean"
#'   colnames(df) <- cns
#'   df
#' }
#'
#'
#'
#' #' Convert Forecast object to data.frame
#' #'
#' #' @param fobj forecast object
#' #' @rdname as_data_frame
#' #' @export
#' as_data_frame.forecast <- function(fobj){
#'
#'   df <- as.data.frame(fobj)
#'   cns <- tolower(colnames(df))
#'   cns <- gsub("lo", "lower", cns)
#'   cns <- gsub("hi", "upper", cns)
#'   cns <- gsub(" ", "", cns)
#'   cns[1] <- "mean"
#'   cns <- paste("forecast", sep="_")
#'   colnames(df) <- cns
#'   df
#' }
#'
