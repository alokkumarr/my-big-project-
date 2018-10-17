
# Regression Class --------------------------------------------------------


#' Regressor Constructer Function
#'
#' Creates a regressor object which inherits from modeler class
#'
#' @inheritParams modeler
#' @family use cases
#' @aliases regression
#' @export
regressor <- function(df,
                      target,
                      name = NULL,
                      uid = NULL,
                      version = NULL,
                      desc = NULL,
                      scientist = NULL,
                      execution_strategy = "sequential",
                      refit = TRUE,
                      save_submodels = TRUE,
                      dir = NULL,
                      seed = 319,
                      ...){
  checkmate::assert_subset("tbl_spark", class(df))
  checkmate::assert_false("index" %in% colnames(df))
  
  mobj <- modeler(df,
                  target = target,
                  type = "regressor",
                  name,
                  uid,
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
  if (is.null(obj$final_model)) {
    stop("Final model not set")
  } else {
    final_model <- obj$final_model
  }
  
  # Schema Check
  schema_compare <- obj$schema %>% 
    purrr::keep(names(obj$schema) != obj$target) %>% 
    a2munge::schema_check(., a2munge::get_schema(data))
  
  pipe <- obj$pipelines[[final_model$pipe]] %>% 
    execute(data, .) 
  predict(final_model, data = pipe$output, ...) %>% 
    new_predictions(
      predictions = .,
      model = final_model,
      type = "regressor",
      uid = sparklyr::random_string(prefix = "pred"),
      desc = desc
    )
}
