
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
                           dir = NULL,
                           ...){
  checkmate::assert_subset("tbl_spark", class(df))
  checkmate::assert_false("index" %in% colnames(df))

  df <- df %>%
    dplyr::mutate(index = 1) %>%
    dplyr::mutate(index = dplyr::row_number(index))
  mobj <- modeler(df,
                  target = target,
                  type = "classifier",
                  name,
                  id,
                  version,
                  desc,
                  scientist,
                  dir)
  mobj$index_var <- "index"
  mobj$index <- 1:sdf_nrow(df)
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
    dplyr::mutate(index = dplyr::row_number(index))
  final_model$pipe <- execute(data, final_model$pipe)
  preds <- predict(final_model, data = final_model$pipe$output, ...)

  new_predictions(
    predictions = preds,
    model = final_model,
    type = "classifier",
    id = sparklyr::random_string(prefix = "pred"),
    desc = desc
  )
}
