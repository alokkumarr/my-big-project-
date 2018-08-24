
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
                          uid = NULL,
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
  schema_check <- purrr::flatten(obj$schema) %>%
    tibble::as_tibble() %>%
    tidyr::gather() %>%
    dplyr::left_join(
      purrr::flatten(get_schema(data)) %>%
        tibble::as_tibble() %>%
        tidyr::gather(),
      by = "key") %>% 
    dplyr::filter(value.x != value.y | is.na(value.y))
  
  if(nrow(schema_check) > 0) {
    stop(paste("New Data schema check failed:",
               "\n     columns missing:",
               schema_check %>%
                 dplyr::filter(is.na(value.y)) %>%
                 dplyr::pull(key) %>% 
                 paste(collapse = ", "),
               "\n     columns with miss-matching type:",
               schema_check %>%
                 dplyr::filter(!is.na(value.y)) %>%
                 dplyr::pull(key) %>% 
                 paste(collapse = ", ")),
         .call=FALSE)
  }
  
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