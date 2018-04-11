
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

  for (id in ids) {
    model <- get_models(obj, id = id)[[1]]
    checkmate::assert_class(model, "forecast_model")
    model$pipe <- flow(obj$data, model$pipe)
    indicies <- get_indicies(obj)
    model$performance <- indicies
    for (i in seq_along(indicies)) {
      index <- indicies[[i]]
      model <- fit(model,
                   data = model$pipe$output %>% dplyr::slice(index$train))
      fitted <- fitted(model)
      train <- data.frame("index" = index$train, "fitted" = fitted)

      if(! is.null(index$validation)){
        predicted <- predict(model,
                             data = model$pipe$output %>% dplyr::slice(index$validation),
                             periods = length(index$validation),
                             level = obj$conf_levels)
        validation = data.frame("index" = index$validation, predicted)
        perf <- list("train" = train, "validation" = validation)
      }else{
        perf <- list("train" = train)
      }
      model$performance[[i]] <- perf
      model$status <- "trained"
    }
    obj$models[[id]] <- model
  }
  obj
}


#' @rdname evaluate_models
#' @export
evaluate_models.forecaster <- function(obj) {

  status <- get_models_status(obj)
  ids <- names(status == "trained")

  eval <- tidy_performance(obj) %>%
    dplyr::inner_join(get_target(obj) %>%
                        dplyr::mutate(index = row_number()),
                      by = "index") %>%
    dplyr::group_by(model, sample, indicie) %>%
    dplyr::do(data.frame(
      match.fun(obj$measure$method)(.,
                                    actual = obj$target,
                                    predicted = "predicted")
    )) %>%
    dplyr::ungroup() %>%
    setNames(c("model", "sample", "indicie", obj$measure$method))

  obj$evaluate <- eval

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
    evals <- get_evalutions(obj)
    smpl <- ifelse("validation" %in% evals$sample, "validation", "train")
    id <- evals %>%
      dplyr::filter_at("sample", dplyr::any_vars(. == smpl)) %>%
      dplyr::group_by(model) %>%
      dplyr::summarise_at(obj$measure$method, mean) %>%
      dplyr::arrange_at(obj$measure$method,
                        .funs = ifelse(obj$measure$minimize, identity, desc)) %>%
      head(1) %>%
      .$model
  }

  model <- get_models(obj, ids = id)[[1]]
  model$status <- "selected"
  obj$models[[id]] <- model

  if (reevaluate) {
    if (is.null(obj$samples$test_holdout_prct)) {
      warning("Missing Test Holdout Sample. Final Model not re-evaluated.")
    } else{
      val_index <- setdiff(1:nrow(obj$data), obj$samples$test_index)
      remodel <- fit(model,
                     data = model$pipe$output %>% dplyr::slice(val_index))
      fitted <- fitted(remodel)
      train <- data.frame("index" = val_index, "fitted" = fitted)
      predicted <- predict(
        model,
        data = model$pipe$output %>% dplyr::slice(obj$samples$test_index),
        periods = length(obj$samples$test_index),
        level = obj$conf_levels)
      test <- data.frame("index" = obj$samples$test_index, predicted)
      perf <- list("test_holdout" = list("train" = train, "test" = test))
      remodel$performance <- perf

      eval <- tidy_performance(remodel) %>%
        dplyr::inner_join(get_target(obj) %>%
                            dplyr::mutate(index = row_number()),
                          by = "index") %>%
        dplyr::group_by(model, sample, indicie) %>%
        dplyr::do(data.frame(
          match.fun(obj$measure$method)(.,
                                        actual = obj$target,
                                        predicted = "predicted")
        )) %>%
        dplyr::ungroup() %>%
        setNames(c("model", "sample", "indicie", obj$measure$method))
      obj$evaluate <- rbind(obj$evaluate, eval)
    }
  }


  if (refit) {
    final_model <- fit(model,
                       data = model$pipe$output)
    fitted <- fitted(final_model)
    train <- data.frame("index" = 1:nrow(obj$data), "fitted" = fitted)
    final_model$status <- "final"
    perf <- list("train" = train)
    final_model$performance <- perf
    obj$final_model <- final_model
  }else{
    obj$final_model <- model
  }

  obj
}
