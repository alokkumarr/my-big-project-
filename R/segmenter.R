
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
  #sobj <- set_measure(sobj, silhouette)
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
  target_df <- get_target(obj) %>%
    dplyr::mutate(index = row_number())

  for (id in ids) {
    model <- get_models(obj, id = id)[[1]]
    checkmate::assert_class(model, "ml_model")
    model <- evaluate(model, target_df, obj$measure)
    obj$models[[id]] <- model
    obj$evaluate <- rbind(obj$evaluate, model$evaluate)
  }
  obj
}
