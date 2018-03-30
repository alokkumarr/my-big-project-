
#' New Modeler Object Constructer function
new_modeler <- function(type,
                        target,
                        methods,
                        partition_ratios,
                        evaluation_metric,
                        selection_criteria,
                        baseline,
                        baseline_method,
                        project,
                        version,
                        developer,
                        desc,
                        ...){

  stopifnot(is.character(type))
  stopifnot(is.data.frame(target) | is.numeric(target))
  stopifnot(is.null(methods) | class(methods) == "list")
  stopifnot(is.null(methods) | all(sapply(methods, class) == "method"))
  stopifnot(is.numeric(partion_ratios))
  stopifnot(is.character(selection_criteria))
  stopifnot(is.null(baseline) | is.numeric(baseline))
  stopifnot(is.null(baseline) | all(sapply(baseline, class) == "character"))
  stopifnot(is.null(project) | is.character(project))
  stopifnot(is.null(version) | is.character(version))
  stopifnot(is.null(project) | is.character(project))
  stopifnot(is.null(developer) | is.character(developer))
  stopifnot(is.null(desc) | is.character(desc))

  if(is.null(developer)) developer <-Sys.info()["user"]


  structure(
    list(
      type,
      target,
      methods,
      partition_ratios,
      evaluation_metric,
      selection_criteria,
      baseline,
      baseline_models,
      project,
      created_on = Sys.time(),
      version,
      developer,
      desc,
      models,
      fit_evaluation,
      ...
    ),
    class = "modeler")
}


#' Modeler Validation function
valid_modeler <- function(x){

  if (! type %in% c("forecaster", "segmenter", "regresser", "classifier")) {
    stop("modeler type supplied not supported. Please use one of following: ",
         "\n* forecaster",
         "\n* segmenter",
         "\n* regresser",
         "\n* classifier")
  }

  if(length(partition_ratios) > 3 | length(partition_ratios) < 2){
    stop("too many or too few data partitions provided. Data should be partioned into 2 or 3 splits",
         "\nEx- partion_rations = c(.60, .20, .20) partions the data into 3 splits (60%, 20%, 20%)",
         "\nThe first partition is used for fitting modes. the second for validation and the optional third for testing")
  }

  x
}






#' @export
fit_args <- function(x, ...) {
  UseMethod("fit_args", x)
}


#' @export
fit_model <- function(x, ...) {
  UseMethod("fit_model", x)
}


#' @export
fit <- function(type, algo, ...) {
  fit_model(fit_args(structure(algo, class = type), ...))
}


#' @export
validate <- function(x, ...) {
  UseMethod("validate", x)
}


#' @export
get_predictions <- function(x, ...) {
  UseMethod("get_predictions", x)
}


#' @export
get_fit <- function(x, ...) {
  UseMethod("get_fit", x)
}


#' @export
get_coefs <- function(x, ...) {
  UseMethod("get_coefs", x)
}


