
# Spark ML Class Methods --------------------------------------------------

#' Spark ML fit method
#' @rdname fit
#' @export
fit.spark_ml <- function(obj, data) {
  checkmate::assert_class(data, "tbl_spark")

  x_vars <- setdiff(colnames(data), c(obj$target, obj$index_var))
  fn <- as.formula(paste(obj$target, "~", paste(x_vars, collapse = "+")))
  args <- modifyList(obj$method_args, list(x = data, formula = fn))
  fun <- get(obj$method, asNamespace(obj$package))
  m <- do.call(fun, args)
  obj$fit <- m
  obj$last_updated <- Sys.time()
  obj$status <- "trained"
  obj
}


#' Fitted Method for Spark-ML Object
#' @rdname fitted
#' @export
fitted.spark_ml <- function(obj) {
  obj$fit$model$summary$predictions %>%
    dplyr::select(index, predicted = prediction, features)
}


#' Predict Method for Spark-ML Object
#' @rdname predict
#' @export
predict.spark_ml <- function(obj, data, ...) {
  checkmate::assert_class(data, "tbl_spark")
  sparklyr::sdf_predict(data, obj$fit, ...) %>%
    select(index, predicted = prediction, features)
}


#' Train Method for Spark-ML Object
#' @rdname train
#' @export
train.spark_ml <- function(mobj, indicies, ...) {
  checkmate::assert_list(indicies)
  mobj$performance <- indicies
  for (i in seq_along(indicies)) {
    index <- indicies[[i]]
    checkmate::assert_subset(names(index), c("train", "validation", "test"))

    # Fit model to training sample
    mdl_df <- mobj$pipe$output %>%
      dplyr::filter(index %in% index$train)
    mobj <- fit(mobj, data = mdl_df)
    train <- fitted(mobj)
    perf <- list("train" = train)

    # Add predictions for validation, test or both
    samples <- names(index)[! sapply(index, is.null)]
    for(smpl in setdiff(samples, "train")){
      smpl_index <- index[[smpl]]
      smpl_df <- mobj$pipe$output %>%
        dplyr::filter(index %in% smpl_index)
      predicted <- predict(mobj, data = smpl_df, ...)
      smpl_list <- list(predicted)
      names(smpl_list) <- smpl
      perf <- c(perf, smpl_list)
    }

    mobj$performance[[i]] <- perf
    mobj$status <- "trained"
  }

  mobj
}


#' Tidy Forecast Model Performance Object
#
#' @param mobj forecast model object as a result of fit function
#' @rdname get_performance
#' @export
tidy_performance.spark_ml <- function(mobj) {
  checkmate::assert_choice(mobj$status, c("trained", "evaluated", "selected", "final"))

  for(i in seq_along(mobj$performance)) {
    for(j in seq_along(mobj$performance[[i]])) {
      perf <- mobj$performance[[i]][[j]] %>%
        dplyr::select(1:3)
      perf <- perf %>%
        dplyr::select_(.dots = setNames(colnames(perf), c("index", "predicted", "features"))) %>%
        dplyr::mutate(sample = names(mobj$performance)[i]) %>%
        dplyr::mutate(indicie = names(mobj$performance[[i]])[j]) %>%
        dplyr::mutate(model = mobj$id)
      if(i == 1 & j == 1) {
        perf_df <- perf
      } else {
        perf_df <- sparklyr::sdf_bind_rows(perf_df, perf)
      }
    }
  }

  perf_df
}


#' Evaluate Method for Spark-ML Clustering Object
#' @rdname evaluate
#' @export
evaluate.spark_ml_clustering <- function(mobj, measure) {

  mobj$evaluate <- purrr::map_df(mobj$performance,
             ~purrr::map_df(., match.fun(measure$method)),
             .id = "indicie") %>%
    tidyr::gather(key = "sample", value = "value", -indicie) %>%
    dplyr::rename_(.dots = setNames("value", measure$method)) %>%
    dplyr::mutate(model = mobj$id) %>%
    dplyr::select_at(c("model", "sample", "indicie", measure$method))

  mobj
}


#' Summary Method for Spark-ML Object
#' @export
#' @rdname summary
summary.spark_ml <- function(mobj){
  mobj$fit
}


#' @export
#' @rdname print
print.spark_ml <- function(mobj){
  mobj$fit
}



