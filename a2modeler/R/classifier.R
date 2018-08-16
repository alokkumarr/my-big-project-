
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
                           execution_strategy = "sequential",
                           refit = TRUE,
                           save_submodels = TRUE,
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
  
  mobj <- modeler(df,
                  target = target,
                  type = "classifier",
                  name,
                  id,
                  version,
                  desc,
                  scientist,
                  execution_strategy,
                  refit,
                  save_submodels,
                  dir)
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
      type = "classifier",
      uid = sparklyr::random_string(prefix = "pred"),
      desc = desc
    )
}





