

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
  
  checkmate::assert_class(data, "tbl_spark")
  checkmate::assert_class(measure, "measure")
  checkmate::assert_class(samples, "samples")
  checkmate::assert_logical(save_submodels)
  checkmate::assert_choice(execution_strategy,
                           c("sequential", "transparent", "multisession", "multicore",
                             "multiprocess", "cluster", "remote"))
  
  # Get Spark connection
  sc <- sparklyr::spark_connection(data)
  
  # Set method fun
  method_fun <- get(mobj$method, asNamespace(mobj$package))
  
  
  # Perceptron Check
  if(grepl("ml_multilayer_perceptron", mobj$method)) {
    if(is.null(mobj$method_args$layers)) {
      stop("method arguments missing layers")
    } else {
      layers <- mobj$method_args$layers
      nfeatures <- sparklyr::sdf_ncol(data) - 1
      if(layers[1] != nfeatures) {
        stop(paste("input layer size", layers[1],
                   "doesn't equal feature size", nfeatures))
      }
      if("spark_model_classification" %in% class(mobj) &
         layers[length(layers)] != 2) {
        stop(paste("output layer size", layers[length(layers)],
                   "should be 2 for classifaction models"))
      }
    }
  }
  
  
  # Create validator pipeline
  val_pipe <- sparklyr::ml_pipeline(sc) %>%
    sparklyr::ft_r_formula(paste(mobj$target, "~.")) %>%
    method_fun(uid = mobj$uid, layers = mobj$method_args$layers)
  
  
  # Set Validator
  if (samples$validation_method == "cross_validation") {
    validator_fun <- get("ml_cross_validator", asNamespace("sparklyr"))
    validator_args <- list(num_folds = samples$validation_args$folds)
  } else{
    validator_fun <- get("ml_train_validation_split", asNamespace("sparklyr"))
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
  if (length(mobj$param_map) == 0) {
    method_args <- formals(method_fun)
    param_map <- method_args[setdiff(names(method_args),  c("x", "formula", "layers"))][1]
  } else{
    param_map <- mobj$param_map
  }
  param_map <- list(param_map)
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
    dplyr::mutate(rn = 1:n()) %>% 
    tidyr::nest(!!names(.)[-c(1, length(names(.)))], .key = "param_grid") %>% 
    dplyr::rename(!!measure$method := !!names(.)[1]) %>% 
    dplyr::mutate(submodel_uid = sub_model_uids, sample = "validation") %>%
    dplyr::select(submodel_uid, sample, !!measure$method,  param_grid)
  mobj$fit <- validator$best_model
  
  mobj
}



#' @rdname evaluate_model
#' @export
evaluate_model.spark_model <- function(mobj,
                                       data,
                                       measure,
                                       prediction_col = "predicted",
                                       ...){
  
  checkmate::assert_true(! is.null(mobj$fit))
  checkmate::assert_class(data, "tbl_spark")
  checkmate::assert_class(measure, "measure")
  
  # Measure Fun
  measure_fun <- match.fun(measure$method)
  
  # Make Predictions 
  predictions <- predict(mobj, data, prediction_col) %>% 
    sparklyr::sdf_bind_cols(data %>% select(!!mobj$target))
  
  # Calculate Performance
  performance <- predictions %>%
    measure_fun(predicted = prediction_col, actual = mobj$target)
  
  mobj$test_performance <- tibble::tibble(!! measure$method := performance)
  mobj$test_predictions <- predictions
  mobj$status <- "evaluated"
  mobj$last_updated <- Sys.time()
  mobj
}


#' Summary Method for Spark Model Object
#' @export
#' @rdname summary
summary.spark_model <- function(mobj){
  print(mobj$fit)
}


#' @export
#' @rdname print
print.spark_model <- function(mobj){
  print(mobj$fit)
}
