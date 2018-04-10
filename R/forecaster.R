
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
      predicted <- predict(model,
                           data = model$pipe$output %>% dplyr::slice(index$train),
                           periods = length(index$validation),
                           level = obj$conf_levels)
      perf <- list(train = data.frame("index" = index$train,
                                      fitted = fitted),
                   validation = data.frame("index" = index$validation,
                                           predicted = predicted))
      model$performance[[i]] <- perf
      model$status <- "trained"
    }
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
    status <- status[names(status) %in% ids]
  ids <- names(status == "trained")

  eval <- get_validation_predictions(obj, ids) %>%
    dplyr::inner_join(get_target(obj) %>%
                        dplyr::mutate(index = row_number()),
                      by = "index") %>%
    dplyr::group_by(model) %>%
    dplyr::do(data.frame(
      match.fun(obj$measure$method)(.,
                                    actual = obj$target,
                                    predicted = "predicted.mean")
    )) %>%
    dplyr::ungroup() %>%
    setNames(c("model", obj$measure$method))

  obj$evaluate <- eval

  obj
}
