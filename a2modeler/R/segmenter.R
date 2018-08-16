
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
                          execution_strategy = "sequential",
                          refit = TRUE,
                          save_submodels = TRUE,
                          dir = NULL,
                          ...){
  checkmate::assert_subset("tbl_spark", class(df))
  checkmate::assert_false("index" %in% colnames(df))
  
  mobj <- modeler(df,
                  target = NULL,
                  type = "segmenter",
                  name,
                  id,
                  version,
                  desc,
                  scientist,
                  execution_strategy,
                  refit,
                  save_submodels,
                  dir)
  sobj <- structure(mobj, class = c("segmenter", class(mobj)))
  sobj <- set_measure(sobj, Silhouette)
  sobj
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
  if (is.null(obj$final_model)) {
    stop("Final model not set")
  } else {
    final_model <- obj$final_model
  }
  
  # Schema Check
  schema_check <- purrr::flatten(obj$schema) %>%
    tibble::as_tibble() %>%
    tidyr::gather() %>%
    left_join(
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
      type = "segmenter",
      uid = sparklyr::random_string(prefix = "pred"),
      desc = desc
    )
}


