
#' Forecaster Constructer Function
#'
#' Creates a forecaster object which inherits from modeler class
#'
#' @inheritParams modeler
#' @param frequency seasonaly frequency of target. Default is NULL - no seasonality
#' @param prediction_conf_levels prediction confidence levels. Default is 80 & 95% CIs
#' @family use cases
#' @aliases forecaster
#' @export
forecaster <- function(df,
                       target,
                       frequency = NULL,
                       prediction_conf_levels = c(80, 95),
                       name = NULL,
                       id = NULL,
                       version = NULL,
                       desc = NULL,
                       scientist = NULL,
                       dir = NULL,
                       ...){

  mobj <- modeler(df,
                  target,
                  type = "forecaster",
                  name,
                  id,
                  version,
                  desc,
                  scientist,
                  dir)
  mobj$frequency <- frequency
  mobj$conf_levels <- prediction_conf_levels
  fobj <- structure(mobj, class = c("forecaster", class(mobj)))
  fobj <- set_measure(fobj, RMSE)
  fobj
}



# Forecaster Class Methods ------------------------------------------------



#' @rdname train_models
#' @export
train_models.forecaster <- function(obj, ids = NULL) {
  checkmate::assert_character(ids, null.ok = TRUE)

  status <- get_models_status(obj)
  if (!is.null(ids))
    status <- status[names(status) %in% id]
  ids <- names(status == "added")
  indicies <- get_indicies(obj)

  for (id in ids) {
    model <- get_models(obj, id = id)[[1]]
    checkmate::assert_class(model, "forecast_model")
    model$pipe <- execute(obj$data, model$pipe)
    model <- train(model, indicies, level = obj$conf_levels)
    obj$models[[id]] <- model
  }
  obj
}



#' @rdname evaluate_models
#' @export
evaluate_models.forecaster <- function(obj, ids = NULL) {
  checkmate::assert_character(ids, null.ok = TRUE)

  status <- get_models_status(obj)
  if (!is.null(ids))
    status <- status[names(status) %in% id]
  ids <- names(status == "trained")
  target_df <- get_target(obj) %>% dplyr::mutate(index = row_number())

  for (id in ids) {
    model <- get_models(obj, id = id)[[1]]
    checkmate::assert_class(model, "forecast_model")
    model <- evaluate(model, target_df, obj$measure)
    obj$models[[id]] <- model
    obj$evaluate <- rbind(obj$evaluate, model$evaluate)
  }
  obj
}



#' @rdname set_final_model
#' @export
set_final_model.forecaster <- function(obj,
                                       method,
                                       id = NULL,
                                       reevaluate = TRUE,
                                       refit = TRUE) {
  checkmate::assert_choice(method, c("manual", "best"))
  checkmate::assert_character(id, null.ok = TRUE)
  checkmate::assert_flag(reevaluate)
  checkmate::assert_flag(refit)
  if (!is.null(id))
    checkmate::assert_choice(id, names(get_models(obj)))
  if (method == "manual" & is.null(id))
    stop("final model not selected: id not provided for manual method")

  if (method == "best") {
    model <- get_best_model(obj)
    id <- model$id
  }else{
    model <- get_models(obj, ids = id)[[1]]
  }

  model$status <- "selected"
  obj$models[[id]] <- model

  if (reevaluate) {
    if (is.null(obj$samples$test_holdout_prct)) {
      warning("Missing Test Holdout Sample. Final Model not re-evaluated.")
    } else{
      val_indicie <- list("test_holdout" =
                            list("train" = setdiff(1:nrow(obj$data), obj$samples$test_index),
                                 "test"  = obj$samples$test_index))
      remodel <- train(model, val_indicie, obj$conf_levels)
      target_df <- get_target(obj) %>% dplyr::mutate(index = row_number())
      remodel <- evaluate(remodel, target_df, obj$measure)
      obj$evaluate <- rbind(obj$evaluate, remodel$evaluate)
    }
  }

  if (refit) {
    refit_indicie <- list("train" = list("train" = 1:nrow(obj$data)))
    final_model <- train(model, refit_indicie, level = obj$conf_level)
    obj$final_model <- final_model
  }else{
    obj$final_model <- model
  }

  obj
}



#' Forecaster Prediction Method
#'
#' Method makes predictions for Forecaster's final model
#' @rdname predict
predict.forecaster <- function(obj, periods, data = NULL, level = c(80, 95)) {

  if(is.null(obj$final_model)){
    stop("Final model not set")
  }

  predict(obj$final_model, periods, data, level)
}
