

#' Predictions Class Constructor
new_predictions <- function(predictions,
                            model,
                            type,
                            uid,
                            desc) {
  checkmate::assert_true(any(class(predictions) %in% c("data.frame", "tbl_spark")))
  checkmate::assert_class(model, "model")
  checkmate::assert_character(uid)
  checkmate::assert_character(desc)
  checkmate::assert_choice(type,
                           c("forecaster", "segmenter", "regressor",
                             "classifier", "multiclassifier"))

  structure(
    list(
      predictions = predictions,
      model = model,
      type = type,
      uid = uid,
      desc = desc,
      created_on = Sys.time()
    ),
    class = "predictions"
  )
}


# Print Function for Predictions Class Object
#' @export
print.predictions <- function(obj, ...) {
  cat("---------------------------- \n")
  cat(obj$type, "predictions \n")
  cat("---------------------------- \n\n")
  cat("model uid           :", obj$model$uid, "\n")
  cat("model method       :", obj$model$method, "\n")
  cat("created on         :", as.character(obj$created_on), "\n")
  cat("sample predictions :", "\n")
  print(head(obj$predictions))
}
