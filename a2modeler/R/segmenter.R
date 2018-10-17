
# Segmenter Class ---------------------------------------------------------


#' Segmenter Constructer Function
#'
#' Creates a segmenter object which inherits from modeler class
#'
#' @inheritParams modeler
#' @family use cases
#' @aliases segmenter
#' @export
segmenter <- function(df,
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
                  target = NULL,
                  type = "segmenter",
                  name,
                  uid,
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
  schema_compare <- obj$schema %>% 
    a2munge::schema_check(., get_schema(data))
  
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


