
# Segmenter Class ---------------------------------------------------------


#' Segmenter Constructer Function
#'
#' Creates a segmenter object which inherits from modeler class
#'
#' @inheritParams modeler
#' @family use cases
#' @aliases segmenter
#' @export
new_segmenter <- function(df,
                          name = NULL,
                          id = NULL,
                          version = NULL,
                          desc = NULL,
                          scientist = NULL,
                          dir = NULL,
                          ...){
  checkmate::assert_subset("tbl_spark", class(df))
  checkmate::assert_false("index" %in% colnames(df))

  df <- df %>%
    dplyr::mutate(index = 1) %>%
    dplyr::mutate(index = row_number(index))
  mobj <- modeler(df,
                  target = NULL,
                  type = "segmenter",
                  name,
                  id,
                  version,
                  desc,
                  scientist,
                  dir)
  mobj$index_var <- "index"
  mobj$index <- 1:sdf_nrow(df)
  sobj <- structure(mobj, class = c("segmenter", class(mobj)))
  sobj <- set_measure(sobj, Silhouette)
  sobj
}


#' @rdname train_models
#' @export
train_models.segmenter <- function(obj, ids = NULL) {
  checkmate::assert_character(ids, null.ok = TRUE)

  status <- get_models_status(obj)
  if (!is.null(ids))
    status <- status[names(status) %in% ids]
  ids <- names(status == "added")
  indicies <- get_indicies(obj)

  for (id in ids) {
    model <- get_models(obj, ids = id)[[1]]
    model$pipe <- execute(obj$data, model$pipe)
    model$index_var <- obj$index_var
    model <- train(model, indicies)
    obj$models[[id]] <- model
  }
  obj
}


#' @rdname evaluate_models
#' @export
evaluate_models.segmenter <- function(obj, ids = NULL) {
  checkmate::assert_character(ids, null.ok = TRUE)

  status <- get_models_status(obj)
  if (!is.null(ids))
    status <- status[names(status) %in% ids]
  ids <- names(status == "trained")

  for (id in ids) {
    model <- get_models(obj, id = id)[[1]]
    model <- evaluate(model, obj$measure)
    obj$models[[id]] <- model
    obj$evaluate <- rbind(obj$evaluate, model$evaluate)
  }
  obj
}



#' @rdname set_final_model
#' @export
set_final_model.segmenter <- function(obj,
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
                            list("train" = setdiff(obj$index, obj$samples$test_index),
                                 "test"  = obj$samples$test_index))
      remodel <- train(model, val_indicie) %>%
        evaluate(., obj$measure)
      obj$evaluate <- rbind(obj$evaluate, remodel$evaluate)
    }
  }

  if (refit) {
    refit_indicie <- list("train" = list("train" = obj$index))
    final_model <- train(model, refit_indicie)
    obj$final_model <- final_model
  }else{
    obj$final_model <- model
  }

  obj
}



#' Segmenter Predict Method
#'
#' Method makes predictions using segmenter object's final model
#' @rdname predict
#' @export
predict.segmenter <- function(obj,
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
  final_model$pipe <- execute(data, final_model$pipe)
  preds <- predict(final_model, data = final_model$pipe$output, ...)

  new_predictions(
    predictions = preds,
    model = final_model,
    type = "segmenter",
    id = sparklyr::random_string(prefix = "pred"),
    desc = desc
  )
}


