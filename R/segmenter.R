
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


