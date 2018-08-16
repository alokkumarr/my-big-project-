#' 
#' # Forecast Model Class Methods --------------------------------------------
#' 
#' 
#' 
#' 
#' #' @rdname fit
#' #' @export
#' fit.forecast_model <- function(mobj, data, ...) {
#'   checkmate::assert_data_frame(data)
#' 
#'   y <- as.numeric(data[[mobj$target]])
#'   x_vars <- setdiff(colnames(data), c(mobj$target, mobj$index_var))
#'   if (length(x_vars) > 0) {
#'     xreg <- data[, x_vars, drop = FALSE]
#'   } else{
#'     xreg <- NULL
#'   }
#' 
#'   args <- modifyList(mobj$method_args, list(y = y, xreg = xreg))
#'   fun <- get(mobj$method, asNamespace(mobj$package))
#'   m <- do.call(fun, args)
#'   mobj$fit <- m
#'   mobj$last_updated <- Sys.time()
#'   mobj$status <- "trained"
#'   mobj
#' }
#' 
#' 
#' #' Forecast Model Fitted Method
#' #'
#' #' Extracts fitted values from train step
#' #'
#' #' @rdname fitted
#' #' @export
#' fitted.forecast_model <- function(mobj) {
#'   as.numeric(fitted(mobj$fit))
#' }
#' 
#' 
#' #' Forecast Prediction Method
#' #' @rdname predict
#' #' @export
#' predict.forecast_model <- function(obj,
#'                                    periods,
#'                                    data = NULL,
#'                                    level = c(80, 95)) {
#'   if (!is.null(data)) {
#'     if (nrow(data) != periods) {
#'       warning("number of data rows doesn't match forecast periods")
#'     }
#' 
#'     x_vars <- setdiff(colnames(data), c(obj$target, obj$index_var))
#'     if (length(x_vars) > 0) {
#'       xreg <- data[, x_vars, drop = FALSE]
#'     } else {
#'       xreg <- NULL
#'     }
#'   } else{
#'     xreg <- NULL
#'   }
#' 
#'   fun <- get("forecast", asNamespace("forecast"))
#'   f <- do.call(fun,
#'                modifyList(
#'                  obj$method_args,
#'                  list(
#'                    object = obj$fit,
#'                    xreg = xreg,
#'                    h = periods,
#'                    level = level
#'                  )
#'                ))
#'   get_forecasts(f)
#' }
#' 
#' 
#' #' @export
#' #' @rdname summary
#' summary.forecast_model <- function(mobj){
#'   mobj$fit
#' }
#' 
#' 
#' #' @export
#' #' @rdname print
#' print.forecast_model <- function(mobj){
#'   mobj$fit
#' }
#' 
#' 
#' 
#' #' Get Forecasts from forecast object
#' #'
#' #' @rdname get_forecasts
#' #' @export
#' #' @return data.frame with forecats from forecast model
#' get_forecasts.forecast <- function(mobj){
#' 
#'   df <- as.data.frame(mobj)
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
#' #' Convert Forecast object to data.frame
#' #'
#' #' @param mobj forecast model object
#' #' @rdname as_data_frame
#' #' @export
#' as_data_frame.forecast <- function(mobj){
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
#' 
#' 
#' #' Get Coefficients from Forecast Model Object
#' #'
#' #' @rdname get_coefs
#' #' @export
#' get_coefs.forecast_model <- function(mobj){
#'   coef(mobj$fit)
#' }
#' 
#' 
#' 
#' #' @rdname train
#' #' @export
#' train.forecast_model <- function(mobj, indicies, level) {
#'   checkmate::assert_list(indicies)
#'   mobj$performance <- indicies
#'   for (i in seq_along(indicies)) {
#'     index <- indicies[[i]]
#'     train_index <- index$train
#'     checkmate::assert_subset(names(index), c("train", "validation", "test"))
#' 
#'     # Fit model to training sample
#'     mdf <- mobj$pipe$output %>%
#'       dplyr::slice(train_index)
#'     mobj <- fit(mobj, data = mdf)
#'     fitted <- fitted(mobj)
#'     train <- mdf %>%
#'       dplyr::select_at(mobj$index_var) %>%
#'       dplyr::mutate(fitted = fitted)
#'     perf <- list("train" = train)
#' 
#'     # Add predictions for validation, test or both
#'     samples <- names(index)[! sapply(index, is.null)]
#'     for(smpl in setdiff(samples, "train")){
#'       smpl_index <- index[[smpl]]
#'       sdf <- mobj$pipe$output %>%
#'         dplyr::slice(smpl_index)
#'       predicted <- predict(mobj,
#'                            data = sdf,
#'                            periods = length(smpl_index),
#'                            level = level)
#'       smpl_list <- sdf %>%
#'         dplyr::select_at(mobj$index_var) %>%
#'         dplyr::bind_cols(predicted) %>%
#'         list()
#'       names(smpl_list) <- smpl
#'       perf <- c(perf, smpl_list)
#'     }
#' 
#'     mobj$performance[[i]] <- perf
#'     mobj$status <- "trained"
#'   }
#' 
#'   mobj
#' }
