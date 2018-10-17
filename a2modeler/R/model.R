


# Model Class Functions ---------------------------------------------------


#' Model Class Constructer
#' 
#' @inheritParams model
#' @param status model status. new models are givend added status
#' @param created_on model creation timestamp 
new_model <- function(pipe,
                      target,
                      method,
                      method_args, 
                      param_map,
                      desc,
                      uid,
                      status,
                      created_on
) {
  checkmate::assert_character(pipe)
  checkmate::assert_character(target, null.ok = TRUE)
  checkmate::assert_choice(method, choices = model_methods$method)
  checkmate::assert_list(method_args, null.ok = TRUE)
  checkmate::assert_list(param_map)
  checkmate::assert_character(desc)
  checkmate::assert_character(uid)
  checkmate::assert_character(status)
  checkmate::assert_posixct(created_on)
  
  .method <- method
  method_fun <- model_methods %>%
    dplyr::filter(method == .method) %>%
    dplyr::pull(package) %>%
    asNamespace() %>%
    get(.method, .)
  method_params <- setdiff(c(names(method_args), names(param_map)), "uid")
  if(length(method_params) == 0) method_params <- NULL
  checkmate::assert_function(method_fun, args = method_params)
  
  method_class <- model_methods %>%
    dplyr::filter(method == .method) %>%
    tidyr::unnest(class) %>%
    dplyr::pull(class) %>%
    as.character()
  method_package <- model_methods %>%
    dplyr::filter(method == .method) %>%
    dplyr::pull(package)
  
  structure(
    list(
      pipe = pipe,
      target = target,
      method = method,
      method_args = method_args,
      param_map = param_map,
      package = method_package,
      desc = desc,
      uid = uid,
      created_on = created_on,
      status = status
    ),
    class = c(method_class, "model")
  )
}


#' Model Class Helper
#'
#' Function to create a model object.
#'
#' Requires a pipeline object input, and a valid model method. Any method
#' package dependencies need to be loaded prior to model call
#'
#' @param pipe pipeline uid string
#' @param target column name of target variable. string input
#' @param method string input of model method
#' @param method_args required method arguments not in parameter grid
#' @param param_map list of parameter values for model tuning
#' @param desc optional model description
#' @param uid model uid
#'
#' @export
model <- function(pipe,
                  target,
                  method,
                  method_args,
                  param_map,
                  desc = NULL,
                  uid = NULL) {
  
  if (is.null(desc)) desc <- ""
  if (is.null(uid)) sparklyr::random_string("model")
  
  new_model(
    pipe = pipe,
    target = target,
    method = method,
    method_args = method_args,
    param_map = param_map,
    desc = desc,
    uid = uid,
    status = "created",
    created_on = Sys.time()
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
#' @param param_map list of parameters values to be used in model tuning
#' @param ... additional method arguments
#' @inheritParams model
#' @export
#' @return modeler object with model added
add_model <-function(obj,
                     pipe = NULL,
                     method,
                     param_map = list(),
                     ...,
                     desc = NULL,
                     uid = sparklyr::random_string("model")) {
  checkmate::assert_class(obj, "modeler")
  
  # Check Method
  type_methods <- model_methods %>%
    dplyr::filter(type == obj$type) %>%
    dplyr::pull(method) %>%
    as.character()
  checkmate::assert_choice(method, type_methods)
  
  # Define pipeline
  if(is.null(pipe))
    pipe <- pipeline()
  
  if(! pipe$uid %in% names(obj$pipelines))
    obj$pipelines[[pipe$uid]] <- pipe
  
  # Create model object
  m <- model(pipe = pipe$uid,
             target = obj$target,
             method = method,
             method_args = list(...),
             param_map = param_map,
             desc = desc,
             uid = uid)
  m$status <- "added"
  m$index_var <- obj$index_var
  obj$models[[m$uid]] <- m
  obj
}



# Model Class Generics ----------------------------------------------------


#' Train Model Generic Function
#'
#' Train a model added to a modeler object.
#'
#' Function fits model based on modeler samples, model pipeline, model method
#' and param grid.
#'
#' @param mobj model object
#' @param ... additional arguments to pass to model class train_model method
#'
#' @export
#' @return updated modeler object
train_model <- function(mobj, ...) {
  UseMethod("train_model")
}



#' Evaluate Model Generic Function
#'
#' Evaluate the accuracy of a fitted and applied model
#'
#' Function applies the measure function associated with modeler object to a
#' model predictions
#'
#' @param mobj model object
#' @param data data with actual values used to compare predictions against
#' @param measure measure object
#' @param prediction_col colname of predicted values
#' @param ... additional arguments to pass to method
#'
#' @export
#' @return updated modeler object
evaluate_model <- function(mobj, ...) {
  UseMethod("evaluate_model")
}



#' Return a Model Fit
#'
#' @param mobj model object
#' @param ... additional arguments to pass through
#' @export
get_fit <- function(mobj, ...) {
  UseMethod("get_fit", mobj)
}


#' Return the Model Coefficients
#'
#' @inheritParams get_fit
#' @export
get_coefs <- function(mobj) {
  UseMethod("get_coefs", mobj)
}


#' Get Model Forecasts
#'
#' @inheritParams get_fit
#' @export
get_forecasts <- function(mobj) {
  UseMethod("get_forecasts", mobj)
}

#' Get Model Variable Importance Metrics
#'
#' Returns variable importance from tree based models and coefficient summaries
#' from linear based models
#'
#' @param mobj model object
#' @export
get_variable_importance <- function(mobj) {
  UseMethod("get_variable_importance", mobj)
}


