
# Classifier Class --------------------------------------------------------


#' Classifier Constructer Function
#'
#' Creates a classifier object which inherits from modeler class
#'
#' @inheritParams modeler
#' @family use cases
#' @aliases classifier
#' @export
new_classifier <- function(df,
                           target,
                           name = NULL,
                           id = NULL,
                           version = NULL,
                           desc = NULL,
                           scientist = NULL,
                           execution_strategy = "sequential",
                           refit = TRUE,
                           save_submodels = TRUE,
                           dir = NULL,
                           ...){
  checkmate::assert_subset("tbl_spark", class(df))
  checkmate::assert_false("index" %in% colnames(df))
  
  # Target Check
  target_dims <- df %>%
    dplyr::distinct_(target) %>%
    sparklyr::sdf_nrow()
  
  if(target_dims != 2) {
    stop(paste("target not a binary distribution.\n  Has", target_dims, "unique values"))
  }
  
  mobj <- modeler(df,
                  target = target,
                  type = "classifier",
                  name,
                  id,
                  version,
                  desc,
                  scientist,
                  execution_strategy,
                  refit,
                  save_submodels,
                  dir)
  mobj <- structure(mobj, class = c("classifier", class(mobj)))
  mobj <- set_measure(mobj, AUC)
  mobj
}


#' Classifier Predict Method
#'
#' Method makes predictions using classifier object's final model
#' @rdname predict
#' @export
predict.classifier <- function(obj,
                               data = NULL,
                               desc = "",
                               ...) {
  final_model <- obj$final_model
  if (is.null(final_model)) {
    stop("Final model not set")
  }
  data <- data %>%
    dplyr::mutate(index = 1) %>%
    dplyr::mutate(index = row_number(index))
  
  schema <- obj$schema[! names(obj$schema) %in% c(obj$target)]
  schema_check <- all.equal(get_schema(data), obj$schema)
  if(schema_check[1] != TRUE) {
    stop(paste("New Data shema check failed:\n", schema_check))
  }
  
  final_model$pipe <- execute(data, final_model$pipe)
  preds <- predict(final_model, data = final_model$pipe$output, ...)
  
  new_predictions(
    predictions = preds,
    model = final_model,
    type = "classifier",
    uid = sparklyr::random_string(prefix = "pred"),
    desc = desc
  )
}



#' @rdname set_final_model
#' @export
set_final_model.classifier <- function(obj,
                                       method,
                                       uid = NULL,
                                       reevaluate = TRUE,
                                       refit = TRUE) {
  checkmate::assert_choice(method, c("manual", "best"))
  checkmate::assert_character(uid, null.ok = TRUE)
  checkmate::assert_flag(reevaluate)
  checkmate::assert_flag(refit)
  
  if (!is.null(uid))
    checkmate::assert_choice(uid, names(obj$models))
  if (method == "manual" & is.null(uid))
    stop("final model not selected: uid not provided for manual method")
  
  if (method == "best") {
    uid <- get_best_model(obj)$uid
  }
  
  if (reevaluate) {
    if (is.null(obj$samples$test_holdout_prct)) {
      warning("Missing Test Holdout Sample. Final Model not re-evaluated.")
    } else{
      
      # Make Predictions on Test Data Set
      test_predictions <- obj$pipelines[[obj$models[[uid]]$pipe]]$output %>%
        dplyr::mutate(index = 1, index = row_number(index)) %>%
        dplyr::filter(index %in% obj$samples$test_index) %>%
        dplyr::select(-index) %>% 
        sparklyr::ml_predict(obj$models[[uid]]$fit, .)
      
      performance <- obj2$models[[1]]$performance %>%
        dplyr::filter(sample == "validation") %>%
        dplyr::mutate(sample = "test", model = uid)
      obj$performance <- rbind(obj$performance, performance)
    }
  }
  obj$models[[uid]] <- model
  model$status <- "selected"
  model$last_updated <- Sys.time()
  
  if (refit) {
    
    # Retrain Data
    obj$models[[uid]] <- train_model(mobj = obj$models[[uid]],
                                     data = train_data,
                                     measure = obj$measure,
                                     samples = obj$samples,
                                     save_submodels = obj$save_submodels,
                                     execution_strategy = obj$execution_strategy)
    
    obj$final_model <- obj2$models[[1]]
    obj$final_model$uid <- model$uid
    obj$final_model$fit <- obj2$models[[1]]$fits$default
  }else{
    obj$final_model <- model
    obj$final_model$fit <- model$fits[[1]]
  }
  
  obj$final_model$fits <- NULL
  obj$final_model$pipe <- obj$pipelines[[obj$final_model$pipe]]
  obj$final_model$status <- "final"
  obj$final_model$last_updated <- Sys.time()
  
  obj
}