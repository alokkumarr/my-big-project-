
#' Forecaster Constructer Function
#'
#' Creates a forecaster object which inherits from modeler class
#'
#' @inheritParams modeler
#' @param index_var column name of index variable. index variable should be
#'   either sequential numeric, date or datetime.
#' @param units index variable unit for date or datetime variables. ex - days
#'   for date index variable.
#' @param frequency seasonaly frequency of target. Default is NULL - no
#'   seasonality
#' @param prediction_conf_levels prediction confidence levels. Default is 80 &
#'   95% CIs
#' @family use cases
#' @aliases forecaster
#' @export
new_forecaster <- function(df,
                           target,
                           index_var,
                           unit = NULL,
                           frequency = NULL,
                           prediction_conf_levels = c(80, 95),
                           name = NULL,
                           id = NULL,
                           version = NULL,
                           desc = NULL,
                           scientist = NULL,
                           dir = NULL,
                           ...){

  checkmate::assert_choice(index_var, colnames(df))
  checkmate::assert_numeric(frequency, lower = 1, null.ok = TRUE)
  checkmate::assert_numeric(prediction_conf_levels, lower = 50, upper = 100,
                            min.len = 1, max.len = 2)
  checkmate::assert_character(unit, null.ok = TRUE)

  mobj <- modeler(df,
                  target,
                  type = "forecaster",
                  name,
                  id,
                  version,
                  desc,
                  scientist,
                  dir)
  mobj$index_var <- index_var
  mobj$index <- index(df[[index_var]], unit = unit)
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
    status <- status[names(status) %in% ids]
  ids <- names(status == "added")
  indicies <- get_indicies(obj)

  for (id in ids) {
    model <- get_models(obj, ids = id)[[1]]
    checkmate::assert_class(model, "forecast_model")
    model$pipe <- execute(obj$data, model$pipe)
    model$index_var <- obj$index_var
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
    status <- status[names(status) %in% ids]
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
#' @export
predict.forecaster <- function(obj,
                               periods,
                               data = NULL,
                               level = c(80, 95),
                               desc = "") {
  final_model <- obj$final_model
  if (is.null(final_model)) {
    stop("Final model not set")
  }
  final_model$pipe <- execute(data, final_model$pipe)
  preds <- predict(final_model, periods, data = final_model$pipe$output, level)
  index_out <- extend(obj$index, length_out = periods)
  preds <- data.frame(index_out, preds)
  colnames(preds)[1] <- obj$index_var

  new_predictions(
    predictions = preds,
    model = final_model,
    type = "forecaster",
    id = sparklyr::random_string(prefix = "pred"),
    desc = desc
  )
}



# Auto Forecaster ---------------------------------------------------------


#' Auto Forecaster Function
#'
#' Creates automated forecasts for univariate time series.
#'
#' Convience wrapper to create complete forecaster pipeline
#'
#' @param df dataframe
#' @param target numeric variable to model and forecast
#' @param index_var index variable
#' @param periods number of periods to forecast
#' @param unit unit of index variable default is null
#' @param pipe pipeline object
#' @param models nested list of models. each model list should have a method and
#'   list of arguments
#' @param splits holdout splits ratios default is 80-20 train to validation
#' @param conf_levels forecast confidence levels. default is 80 and 90 percent
#'
#' @return predictions object
#' @export
#'
#' @examples
auto_forecaster <- function(df,
                            target,
                            index_var,
                            periods,
                            unit = NULL,
                            pipe = NULL,
                            models = list(list(method = "auto.arima",
                                               method_args = list()),
                                          list(method = "ets",
                                               method_args = list())),
                            splits = c(.8, .2),
                            conf_levels = c(80, 95)) {

  df_names <- colnames(df)
  checkmate::assert_data_frame(df)
  checkmate::assert_choice(index_var, df_names)
  checkmate::assert_subset(target, df_names)
  checkmate::assert_number(periods, lower = 0)
  checkmate::assert_class(pipe, "pipeline", null.ok = TRUE)
  checkmate::assert_list(models)
  checkmate::assert_numeric(splits, lower=0, upper=1, min.len = 2, max.len = 3)
  checkmate::assert_numeric(conf_levels, lower = 50, upper = 100,
                            min.len = 1, max.len = 2)

  new_forecaster(df,
                 target = target,
                 index_var = index_var,
                 unit = unit,
                 name = "auto-forecaster") %>%
    add_holdout_samples(., splits = splits) %>%
    add_models(pipe = if(is.null(pipe)) pipeline() else pipe,
               models = models) %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = TRUE) %>%
    predict(periods = periods, level = conf_levels)
}



#' @inheritParams auto_forecasts
#' @param group_vars optional column name of grouping variables. splits data and
#'   applies auto_forecaster to each group
#' @param measure_vars colname names of variables to forecast
#' @export
forecaster <- function(...){
  UseMethod("forecaster")
}



#' @export
#' @rdname forecaster
forecaster.data.frame <- function(df,
                                  index_var,
                                  group_vars = NULL,
                                  measure_vars,
                                  periods,
                                  unit = NULL,
                                  pipe = NULL,
                                  models = list(list(method = "auto.arima",
                                                     method_args = list()),
                                                list(method = "ets",
                                                     method_args = list())),
                                  splits = c(.8, .2),
                                  conf_levels = c(80, 95)) {

  df_names <- colnames(df)
  checkmate::assert_choice(index_var, df_names)
  checkmate::assert_subset(group_vars, df_names)
  checkmate::assert_subset(measure_vars, df_names)
  checkmate::assert_number(periods, lower = 0)
  checkmate::assert_list(models)
  checkmate::assert_numeric(splits, lower=0, upper=1, min.len = 2, max.len = 3)
  checkmate::assert_numeric(conf_levels, lower = 50, upper = 100,
                            min.len = 1, max.len = 2)

  if(is.null(pipe))
    pipe <- pipeline(expr = function(x) x[, "y", drop=FALSE])

  conf_levels_names <- do.call("c",
                               lapply(conf_levels,
                                      function(x)
                                        paste(c("lower", "upper"), x, sep = "")))
  df %>%
    dplyr::select_at(c(index_var, group_vars, measure_vars)) %>%
    a2munge::melter(.,
                    id_vars = c(index_var, group_vars),
                    measure_vars,
                    variable_name = "measure",
                    value_name = "y") %>%
    dplyr::group_by_at(c(group_vars, "measure")) %>%
    dplyr::do(
      auto_forecaster(.,
                      target = "y",
                      index_var = index_var,
                      periods  = periods,
                      unit = NULL,
                      pipe = pipe,
                      models = models,
                      splits = splits,
                      conf_levels = conf_levels) %>%
        .$predictions
    ) %>%
    dplyr::ungroup() %>%
    dplyr::select_at(c(index_var, group_vars, "measure", "mean", conf_levels_names))
}


#' @export
#' @rdname forecaster
forecaster.tbl_spark <- function(df,
                                 index_var,
                                 group_vars = NULL,
                                 measure_vars,
                                 periods,
                                 unit = NULL,
                                 pipe = NULL,
                                 models = list(list(method = "auto.arima",
                                                    method_args = list()),
                                               list(method = "ets",
                                                    method_args = list())),
                                 splits = c(.8, .2),
                                 conf_levels = c(80, 95)) {
  df_names <- colnames(df)
  checkmate::assert_choice(index_var, df_names)
  checkmate::assert_subset(group_vars, df_names)
  checkmate::assert_subset(measure_vars, df_names)
  checkmate::assert_number(periods, lower = 0)
  checkmate::assert_list(models)
  checkmate::assert_numeric(
    splits,
    lower = 0,
    upper = 1,
    min.len = 2,
    max.len = 3
  )
  checkmate::assert_numeric(
    conf_levels,
    lower = 50,
    upper = 100,
    min.len = 1,
    max.len = 2
  )

  conf_levels_names <- do.call("c",
                               lapply(conf_levels,
                                      function(x)
                                        paste(c("lower", "upper"), x, sep = "")))
  spk_names <- c(index_var, "mean", conf_levels_names)

  df %>%
    dplyr::select_at(c(index_var, group_vars, measure_vars)) %>%
    a2munge::melter(
      .,
      id_vars = c(index_var, group_vars),
      measure_vars,
      variable_name = "measure",
      value_name = "y"
    ) %>%
    sparklyr::spark_apply(.,
                          function(e, l) {
                            library(forecast)
                            library(a2modeler)
                            library(dplyr)
                            library(checkmate)
                            library(lubridate)

                            f1 <- new_forecaster(e[, c(l$target, l$index_var)],
                                                 target = l$target,
                                                 index_var = l$index_var,
                                                 unit = l$unit,
                                                 name = "auto-forecaster") %>%
                              add_holdout_samples(., splits = l$splits) %>%
                              add_models(.,
                                         pipe = l$pipe,
                                         models = l$models) %>%
                              train_models(.) %>%
                              evaluate_models(.) %>%
                              set_final_model(., method = "best", reevaluate = FALSE, refit = TRUE)

                            p1 <- predict(f1, periods = l$periods, level = l$conf_levels)
                            p1$predictions

                          },
                          group_by = c(group_vars, "measure"),
                          names = spk_names,
                          packages = TRUE,
                          context = {
                            l = list(
                              target = "y",
                              index_var = index_var,
                              periods = periods,
                              unit = unit,
                              pipe = pipe,
                              models = models,
                              splits = splits,
                              conf_levels = conf_levels
                            )
                          }) %>%
    dplyr::select_at(c(index_var, group_vars, "measure", "mean", conf_levels_names))
}
