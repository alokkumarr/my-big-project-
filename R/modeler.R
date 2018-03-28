

#' @export
fit_args <- function(x, ...) {
  UseMethod("fit_args", x)
}


#' @export
fit_model <- function(x, ...) {
  UseMethod("fit_model", x)
}


#' @export
fit <- function(type, model, ...){

  fit_args(structure(model, class = type), ...) %>%
    fit_model()
}


#' @export
validate <- function(x, ...) {
  UseMethod("validate", x)
}
