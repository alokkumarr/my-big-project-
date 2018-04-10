


# Model Class Functions ---------------------------------------------------


#' Model Class Constructer
new_model <- function(pipe,
                      target,
                      method,
                      method_args,
                      class,
                      desc,
                      path,
                      id,
                      status,
                      created_on,
                      last_updated,
                      fit,
                      performance) {
  checkmate::assert_class(pipe, "pipeline")
  checkmate::assert_character(target)
  checkmate::assert_list(method_args)
  checkmate::assert_function(match.fun(method), args = names(method_args))
  checkmate::assert_choice(class, choices = c("forecast_model", "spark.ml", "h2o"))
  checkmate::assert_character(desc)
  checkmate::assert_path_for_output(path, overwrite = FALSE)
  checkmate::assert_character(id)
  checkmate::assert_choice(status,
                           c("created", "added", "trained", "evaluated", "selected"))
  checkmate::assert_posixct(created_on)
  checkmate::assert_posixct(last_updated)
  checkmate::assert_list(fit, null.ok = TRUE)
  checkmate::assert_list(performance, null.ok = TRUE)


  structure(
    list(
      pipe = pipe,
      target = target,
      method = method,
      method_args = method_args,
      desc = desc,
      path = path,
      id = id,
      created_on = created_on,
      last_updated = last_updated,
      status = status,
      fit = fit,
      performance = performance
    ),
    class = c(class, "model")
  )
}


#' Model Class Validator
valid_model <- function(x) {
  if (!all(names(x$method_args) %in% names(formals(x$method)))) {
    stop(
      "Not all method args valid. Please check following arguments.\n",
      paste(names(x$method_args)[!names(x$method_args) %in% names(formals(x$method))],
            collapse = "\n")
    )
  }

  x
}


#' Model Class Helper
#'
#' Function to create a model object.
#'
#' Requires a pipeline object input, and a valid model method. Any method
#' package dependencies need to be loaded prior to model call
#'
#' @param pipe pipeline object. default is empty pipeline which applies no
#'   data transformations
#' @param method string input of model method
#' @param ... additional arguments to pass to model method
#' @param desc optional model description
#' @param path optional file path to save model
#'
#' @export
model <- function(pipe,
                  target,
                  method,
                  ...,
                  class,
                  desc = NULL,
                  path = NULL) {
  id <- sparklyr::random_string("model")
  if (is.null(desc))
    desc <- ""
  if (is.null(path))
    path <- "./"

  valid_model(
    new_model(
      pipe = pipe,
      target = target,
      method = method,
      method_args = list(...),
      class = class,
      desc = desc,
      path = path,
      id = id,
      status = "created",
      created_on = Sys.time(),
      last_updated = Sys.time(),
      fit = NULL,
      performance = NULL
    )
  )
}




#' Add Model to Modeler Object function
#'
#' Function to add model to modeler object. More than one model can be added to
#' a modeler object.
#'
#' Function creates a new model object from inputs and then appends to modeler
#' models list
#'
#' @param obj modeler object
#' @inheritParams model
#' @export
#' @return modeler object with model added
add_model <-function(obj,
                     pipe = NULL,
                     method,
                     ...,
                     class,
                     desc = NULL,
                     path = NULL) {
  checkmate::assert_class(obj, "modeler")

  if(is.null(pipe))
    pipe <- pipeline()

  m <- model(pipe = pipe,
             target = obj$target,
             method = method,
             ...,
             class = class,
             desc = desc,
             path = path)
  m$status <- "added"
  obj$models[[m$id]] <- m
  obj
}


#' Append Model to Modeler Object function
#'
#' Function to append a valid model to a modeler object.
#'
#' Function updates the model status and appends to modeler models list
#'
#' @param obj modeler object
#' @param model model object
#' @export
#' @return modeler object with model added
append_model <- function(obj, model) {
  checkmate::assert_class(obj, "modeler")
  checkmate::assert_class(model, "model")

  model$target <- obj$target
  model$status <- "added"
  model$last_updated <- Sys.time()
  obj$models[[model$id]] <- model
  obj
}



# Model Class Generics ----------------------------------------------------


#' Fit Model Generic
#'
#' Fit model to single data sample
#'
#' @export
fit <- function(...){
  UseMethod("fit")
}


#' @export
evaluate <- function(x, ...) {
  UseMethod("evaluate", x)
}


#' Predict Model Generic
#'
#' Make model predictions
#'
#' @export
predict <- function(...){
  UseMethod("predict")
}




