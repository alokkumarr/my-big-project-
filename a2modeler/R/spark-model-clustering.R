
# spark_model_clustering methods --------------------------------------



#' @rdname train_model
#' @importFrom stats as.formula
#' @export
train_model.spark_model_clustering <- function(mobj,
                                               data,
                                               measure,
                                               samples,
                                               save_submodels,
                                               execution_strategy = "sequential",
                                               ...) {

  checkmate::assert_class(data, "tbl_spark")
  checkmate::assert_class(measure, "measure")
  checkmate::assert_class(samples, "samples")
  checkmate::assert_logical(save_submodels)

  # Set model formula & function
  fn <- as.formula(paste("~", paste(colnames(data), collapse = "+")))
  fun <- get(mobj$method, asNamespace(mobj$package))

  # Loop through param_map
  param_grid <- data.frame(expand.grid(c(mobj$method_args, mobj$param_map)))

  # Fit submodel to train data for each sample
  for(rn in 1:nrow(param_grid)) {

    # Submodel_uid
    submodel_uid <- sparklyr::random_string("submodel")

    # Params
    params <- as.list(param_grid[rn, , drop=FALSE])

    # Get Indicies
    indicies <- get_indicies(samples)

    performance <- tibble::tibble()

    for (i in seq_along(indicies)) {
      index <- indicies[[i]]
      checkmate::assert_subset(names(index), c("train", "validation", "test"))

      # Fit submodel to training sample
      train_smpl <- data %>%
        dplyr::mutate(rn = 1) %>% 
        dplyr::mutate(rn = row_number(rn)) %>% 
        dplyr::filter(rn %in% index$train) %>%
        dplyr::select(-rn)

      args <- c(list(x = train_smpl, formula = fn, uid = submodel_uid), params)
      submodel <- do.call(fun, args)

      mobj$sub_models[[submodel_uid]][[i]] <- submodel

      if (!is.null(index$validation)) {

        # Predict each validation sample
        val_smpl <- data %>%
          dplyr::mutate(rn = 1) %>% 
          dplyr::mutate(rn = row_number(rn)) %>% 
          dplyr::filter(rn %in% index$validation) %>%
          dplyr::select(-rn)

        predictions <- sparklyr::ml_predict(submodel, val_smpl) %>%
          dplyr::rename(predicted = prediction)

        # evalulate performance
        perf <- match.fun(measure$method)(predictions, "predicted") %>%
          tibble::as.tibble() %>%
          dplyr::mutate(submodel_uid = submodel_uid,
                        index = names(indicies[i]),
                        sample = "validation") %>%
          dplyr::rename(!!measure$method := !!names(.)[1]) %>%
          dplyr::select(submodel_uid, index, sample, !!measure$method)
        performance <- rbind(performance, perf)
      }
    }

    # Calculate Final Performance
    mobj$performance <- rbind(
      mobj$performance,
      performance %>%
        dplyr::group_by(submodel_uid, sample) %>%
        dplyr::summarise_at(measure$method, mean) %>%
        dplyr::mutate(param_grid = params)
    )
  }

  # select best model
  best_submodel <- mobj$performance %>%
    dplyr::arrange_at(measure$method,
                      .funs = ifelse(measure$minimize, identity, dplyr::desc)) %>%
    head(1)

  # Refit on full sample
  params <- best_submodel %>%
    tidyr::unnest(param_grid)
  args <- c(list(x = data, formula = fn, uid = best_submodel$submodel_uid), params)
  mobj$fit <- do.call(fun, args)

  # optionally delete submodels
  if(! save_submodels) mobj$sub_models <- NULL

  mobj
}



#' Predict Method for Spark-Model Clustering Object
#' @rdname predict
#' @export
predict.spark_model_clustering <- function(mobj,
                                           data,
                                           prediction_col = "predicted",
                                           ...) {
  checkmate::assert_class(data, "tbl_spark")
  sparklyr::ml_predict(mobj$fit, data,  ...) %>%
    dplyr::rename(!!prediction_col := prediction) %>%
    dplyr::select(!!prediction_col, features)
}



#' Evaluate Method for Spark-Model Clustering Object
#' @rdname evaluate_model
#' @export
evaluate_model.spark_ml_clustering <- function(mobj, measure, ...) {

  mobj$evaluate <- purrr::map_df(mobj$performance,
                                 ~purrr::map_df(., match.fun(measure$method)),
                                 .id = "indicie") %>%
    tidyr::gather(key = "sample", value = "value", -indicie) %>%
    dplyr::rename_(.dots = setNames("value", measure$method)) %>%
    dplyr::mutate(model = mobj$id) %>%
    dplyr::select_at(c("model", "sample", "indicie", measure$method))

  mobj
}
