
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
                           save_fits = TRUE,
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

  df <- df %>%
    dplyr::mutate(index = 1) %>%
    dplyr::mutate(index = row_number(index))
  mobj <- modeler(df,
                  target = target,
                  type = "classifier",
                  name,
                  id,
                  version,
                  desc,
                  scientist,
                  save_fits,
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
    id = sparklyr::random_string(prefix = "pred"),
    desc = desc
  )
}
