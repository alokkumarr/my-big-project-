
# Regression Class --------------------------------------------------------


#' Regressor Constructer Function
#'
#' Creates a regressor object which inherits from modeler class
#'
#' @inheritParams modeler
#' @family use cases
#' @aliases regression
#' @export
new_regressor <- function(df,
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
                  type = "regressor",
                  name,
                  id,
                  version,
                  desc,
                  scientist,
                  dir)
  mobj$index_var <- "index"
  mobj$index <- 1:sdf_nrow(df)
  mobj <- structure(mobj, class = c("regressor", class(mobj)))
  mobj <- set_measure(mobj, RMSE)
  mobj
}


#' Regressor Predict Method
#'
#' Method makes predictions using regressor object's final model
#' @rdname predict
#' @export
predict.regressor <- function(obj,
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
    type = "regressor",
    id = sparklyr::random_string(prefix = "pred"),
    desc = desc
  )
}
