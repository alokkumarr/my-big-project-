
# Spark ML Class Methods --------------------------------------------------

#' Spark ML fit method
#' @rdname fit
#' @export
fit.spark_ml <- function(obj, data, ...) {
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


#' Spark.ml Cluster Fitted Method
#' @rdname fitted
#' @export
fitted.spark_ml <- function(obj) {
  obj$fit$model$summary$cluster %>%
    dplyr::select(prediction)
}



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
    fitted <- fitted(mobj)
    train <- mdl_df %>%
      dplyr::select(index) %>%
      sparklyr::sdf_bind_cols(fitted)
    perf <- list("train" = train)

    # Add predictions for validation, test or both
    samples <- names(index)[! sapply(index, is.null)]
    for(smpl in setdiff(samples, "train")){
      smpl_index <- index[[smpl]]
      smpl_df <- mobj$pipe$output %>%
        dplyr::filter(index %in% smpl_index)
      predicted <- predict(mobj, data = smpl_df, ...)
      smpl_preds <- smpl_df %>%
        dplyr::select(index) %>%
        sparklyr::sdf_bind_cols(predicted)
      smpl_list <- list(smpl_preds)
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
        dplyr::select(1:2)
      perf <- perf %>%
        dplyr::select_(.dots = setNames(colnames(perf), c("index", "predicted"))) %>%
        dplyr::mutate(sample = names(mobj$performance)[i]) %>%
        dplyr::mutate(indicie = names(mobj$performance[[i]])[j])
      if(i == 1 & j == 1) {
        perf_df <- perf
      } else {
        perf_df <- sparklyr::sdf_bind_rows(perf_df, perf)
      }
    }
  }

  perf_df
}



#' @rdname evaluate
#' @export
evaluate.spark_ml <- function(mobj, target_df, measure) {

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

