
# Classifier Class --------------------------------------------------------


#' Classifier Constructer Function
#'
#' Creates a classifier object which inherits from modeler class
#'
#' @inheritParams modeler
#' @family use cases
#' @aliases classifier
#' @export
classifier <- function(df,
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
                  uid,
                  version,
                  desc,
                  scientist,
                  execution_strategy,
                  refit,
                  save_submodels,
                  dir,
                  seed)
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
  schema_compare <- obj$schema %>% 
    purrr::keep(names(obj$schema) != obj$target) %>% 
    a2munge::schema_check(., a2munge::get_schema(data))
  
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





