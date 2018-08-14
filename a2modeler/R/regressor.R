
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
                          execution_strategy = "sequential",
                          refit = TRUE,
                          save_submodels = TRUE,
                          dir = NULL,
                          ...){
  checkmate::assert_subset("tbl_spark", class(df))
  checkmate::assert_false("index" %in% colnames(df))
  
  mobj <- modeler(df,
                  target = target,
                  type = "regressor",
                  name,
                  id,
                  version,
                  desc,
                  scientist,
                  execution_strategy,
                  refit,
                  save_submodels,
                  dir)
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
    type = "regressor",
    uid = sparklyr::random_string(prefix = "pred"),
    desc = desc
  )
}
