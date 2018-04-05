
# Measure class -----------------------------------------------------------


#' Measure Class Constructer
#'
#' @param id name of measure ex - mse
#' @param name full name of measure. ex - mean squared error
#' @param method name of measure method. method function differ based on class
#'   of input. Desigend to be accomdate spark dataframes
#' @param method_args ([list]) additional arguments to pass to method
#' @param properties measure's applicaple modeler classes
#' @param minimize logical flag that minimizing the measure is optimal
#' @param best best possible value for measure
#' @param worst worst possible value for measure
#' @param note additional notes for measure
#'
#' @note measure class designed after similiar named class in mlr package:
#'   \url{https://github.com/mlr-org/mlr/blob/master/R/Measure.R}
#'
#' @export
measure <- function(id,
                    method,
                    method_args = list(),
                    properties,
                    name = id,
                    minimize,
                    best = NULL,
                    worst = NULL,
                    note = "") {
  checkmate::assert_character(id, len = 1)
  checkmate::assert_character(method)
  checkmate::assert_list(method_args)
  checkmate::assert_subset(properties,
                           c("modeler","forecaster", "regresser", "classifier", "segmenter"))
  checkmate::assert_character(name)
  checkmate::assert_flag(minimize)

  if (is.null(best))
    best = ifelse(minimize,-Inf, Inf)
  else
    checkmate::assert_number(best)
  if (is.null(worst))
    worst = ifelse(minimize, Inf,-Inf)
  else
    checkmate::assert_number(worst)

  structure(
    list(
      id = id,
      method = method,
      method_args = method_args,
      properties = properties,
      name = name,
      minimize = minimize,
      best = best,
      worst = worst,
      note = note
    ),
    class = "measure"
  )
}


#' @export
print.measure = function(x, ...) {
  cat("Name:", x$name, "\n")
  cat("Performance measure:", x$id, "\n")
  cat("Method:", x$method, "\n")
  cat("Properties:", x$properties, "\n")
  cat("Minimize:", x$minimize, "\n")
  cat(sprintf("Best: %g; Worst: %g", x$best, x$worst), "\n")
  cat(sprintf("Arguments: %s", x$method_args), "\n")
  cat("Note:", x$note, "\n")
}


#' Set Modeler Method
#'
#' Function updates or sets measure for modeler object. Requires valid modeler
#' and measure objects
#'
#' @param obj modeler object
#' @param measure measure object
#'
#' @export
set_measure <- function(obj, measure) {
  checkmate::assert_class(obj, "modeler")
  checkmate::assert_class(measure, "measure")
  checkmate::assert_subset(class(obj), measure$properties)
  obj$measure <- measure
  obj
}


#' Get Modeler Measure
#'
#' Getter function to extract measure from modeler object
#'
#' @param obj modeler object
#'
#' @return measure object
#' @export
get_measure <- function(obj) {
  checkmate::assert_class(obj, "modeler")
  obj$measure
}


#' Match Modeler Measurement Method
#'
#' Function to match the internal modeler measure method
#'
#' @param obj modeler object
#'
#' @return measure method function
#' @export
#'
match_measure_method <- function(obj) {
  checkmate::assert_class(obj, "modeler")
  match.fun(obj$measure$method)
}


# Measures----------------------------------------------------------------



#' @export RMSE
#' @rdname measures
RMSE <- measure(id = "RMSE",
                method = "rmse",
                method_args = list("x", "predicted", "actual"),
                minimize = TRUE,
                best = 0,
                worst = Inf,
                properties = c("modeler", "regresser","forecaster"),
                name = "Root mean squared error",
                note = "The RMSE is aggregated as sqrt(mean((predicted - actual)^2))")


#' Generic rmse function
#' @export
rmse <- function(...){
  UseMethod("rmse")
}


#' @export
#' @rdname rmse
rmse.data.frame <- function(x, predicted, actual){
  checkmate::assert_choice(predicted, colnames(x))
  checkmate::assert_choice(actual, colnames(x))
  c("rmse" = sqrt(mean((x[[predicted]] - x[[actual]])^2)))
}


#' @importFrom magrittr %>%
#' @export
#' @rdname rmse
rmse.tbl_spark <- function(x, predicted, actual){
  checkmate::assert_choice(predicted, colnames(x))
  checkmate::assert_choice(actual, colnames(x))
  x %>%
    dplyr::summarise_at(predicted, funs(rmse = sqrt(mean((. - !!rlang::sym(actual))^2)))) %>%
    dplyr::collect()
}
