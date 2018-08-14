

# spark_model Generics ----------------------------------------------------

#' Set Evaluator Function
#'
#' Function to create new evaluator object used in validator object
#'
#' @param mobj spark_model object
#' @param sc spark_connection
#' @param measure measure object
#'
#' @export
#' @return updated modeler object
set_evaluator <- function(mobj, sc, measure) {
  setMethod("set_evaluator")
}



# spark_model methods -----------------------------------------------------


#' @rdname train_model
#' @export
train_model.spark_model <- function(mobj,
                                    data,
                                    measure,
                                    samples,
                                    save_submodels,
                                    execution_strategy) {
  # Get Spark connection
  sc <- sparklyr::spark_connection(data)
  
  
  # Set method fun
  method_fun <- get(mobj$method, asNamespace("sparklyr"))
  
  
  # Create validator pipeline
  val_pipe <- sparklyr::ml_pipeline(sc) %>%
    sparklyr::ft_r_formula(paste(mobj$target, "~.")) %>%
    method_fun(uid = mobj$uid)
  
  
  # Set Validator
  if (samples$validation_method == "cross_validation") {
    validator_fun <- get("ml_cross_validator", asNamespace("sparklyr"))
    validator_args <-
      list(num_folds = samples$validation_args$folds)
  } else{
    validator_fun <-
      get("ml_train_validation_split", asNamespace("sparklyr"))
    if (samples$validation_method == "holdout") {
      validator_args <- list(train_ratio = samples$validation_args$split)
    } else {
      validator_args <- list(train_ratio = 1.0)
    }
  }
  
  
  # Set Evaluator
  measure_fun <- match.fun(measure$method)
  evaluator <- measure_fun(sc)
  
  
  # Set param_map
  if (nrow(mobj$param_grid) == 0) {
    method_args <- formals(method_fun)
    param_grid <- method_args[setdiff(names(method_args),  c("x", "formula"))][1]
  } else{
    param_grid <- mobj$param_grid %>% as.list()
  }
  param_map <- list(param_grid)
  names(param_map) <- mobj$uid
  
  
  # Set parallelism
  if (execution_strategy == "sequential") {
    cores = 1L
  } else {
    cores = future::availableCores() %>%
      as.integer()
  }
  
  
  # Set Validator args
  validator_args <- c(
    validator_args,
    list(
      x = data,
      estimator = val_pipe,
      estimator_param_maps = param_map,
      evaluator = evaluator,
      collect_sub_models = save_submodels,
      parallelism = cores,
      uid = "validator"
    )
  )
  
  
  # Execute Validator
  validator <- do.call(validator_fun, validator_args)
  
  
  # Extract Objects
  n_sub_models <- length(validator$param_map$estimatorParamMaps)
  sub_model_uids <- purrr::map_chr(1:n_sub_models, ~ sparklyr::random_string("submodel"))
  if (save_submodels) {
    sub_models <- sparklyr::ml_sub_models(validator)
    if (samples$validation_method == "cross_validation") {
      sub_models <- purrr::transpose(sub_models)
    }
    names(sub_models) <- sub_model_uids
    mobj$sub_models <- sub_models
  }
  
  mobj$performance <- sparklyr::ml_validation_metrics(validator) %>%
    tibble::as.tibble() %>%
    dplyr::rename(!!measure$method := !!names(.)[1]) %>% 
    tidyr::nest(!!names(.)[-1], .key = "param_grid") %>% 
    dplyr::mutate(submodel_uid = sub_model_uids) %>% 
    dplyr::select(submodel_uid, !!measure$method, param_grid)
  mobj$fit <- validator$best_model
  
  mobj
}








# spark-model-subclass methods --------------------------------------------

