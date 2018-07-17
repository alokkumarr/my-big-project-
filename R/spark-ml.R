
# Spark ML Class Methods --------------------------------------------------

#' Spark ML fit method
#' @rdname fit
#' @export
fit.spark_ml <- function(mobj, data) {
  checkmate::assert_class(data, "tbl_spark")

  x_vars <- setdiff(colnames(data), c(mobj$target, mobj$index_var))
  fn <- as.formula(paste(mobj$target, "~", paste(x_vars, collapse = "+")))
  args <- modifyList(mobj$method_args, list(x = data, formula = fn))
  fun <- get(mobj$method, asNamespace(mobj$package))
  m <- do.call(fun, args)
  mobj$fit <- m
  mobj$last_updated <- Sys.time()
  mobj$status <- "trained"
  mobj
}


#' Fitted Method for Spark-ML Object
#' @rdname fitted
#' @export
fitted.spark_ml <- function(obj) {
  if(! is.null(obj$fit$model$summary)){
    obj$fit$model$summary$predictions %>%
      dplyr::select(index, predicted = prediction, features)
  }else{
    predict(obj, obj$pipe$output)
  }
}


#' Fitted Method for Spark-ML Decision Tree Classification Model
#' @rdname fitted
#' @export
fitted.ml_decision_tree_classification_model <- function(obj) {
  sparklyr::sdf_predict(mobj$fit, mobj$pipe$output) %>%
    dplyr::select(index, predicted = prediction, features)
}


#' Predict Method for Spark-ML Object
#' @rdname predict
#' @export
predict.spark_ml <- function(obj, data, ...) {
  checkmate::assert_class(data, "tbl_spark")
  sparklyr::sdf_predict(data, obj$fit, ...) %>%
    select(index, predicted = prediction)
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



#' Evaluate Method for Spark-ML Binary Classification Object
#' @rdname evaluate
#' @export
evaluate.spark_ml_classification <- function(mobj, measure) {

  mobj$evaluate <- purrr::map(
    mobj$performance,
    ~ purrr::map(., ~ inner_join(., mobj$pipe$output %>%
                                   select_at(
                                     c(mobj$target, mobj$index_var)
                                   ),
                                 by = mobj$index_var))) %>%
    purrr::map_df(.,
                  ~ purrr::map_df(
                    .,
                    match.fun(measure$method),
                    predicted = "predicted",
                    actual = mobj$target
                  ),
                  .id = "indicie") %>%
    tidyr::gather(key = "sample", value = "value",-indicie) %>%
    dplyr::rename_(.dots = setNames("value", measure$method)) %>%
    dplyr::mutate(model = mobj$id) %>%
    dplyr::select_at(c("model", "sample", "indicie", measure$method))

  mobj
}


#' Evaluate Method for Spark-ML Regression Object
#' @rdname evaluate
#' @export
evaluate.ml_model_regression <- function(mobj, measure) {

  mobj$evaluate <- purrr::map(
    mobj$performance,
    ~ purrr::map(., ~ inner_join(.,
                                 mobj$pipe$output %>%
                                   select_at(c(mobj$target, mobj$index_var)),
                                 by = mobj$index_var))) %>%
    purrr::map_df(.,
                  ~ purrr::map_df(
                    .,
                    match.fun(measure$method),
                    predicted = "predicted",
                    actual = mobj$target
                  ),
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


