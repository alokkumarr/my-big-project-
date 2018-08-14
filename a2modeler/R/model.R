


# Model Class Functions ---------------------------------------------------


#' Model Class Constructer
new_model <- function(pipe,
                      target,
                      method,
                      param_grid,
                      desc,
                      uid,
                      status,
                      created_on
) {
  checkmate::assert_character(pipe)
  checkmate::assert_character(target, null.ok = TRUE)
  checkmate::assert_choice(method, choices = model_methods$method)
  checkmate::assert_data_frame(param_grid)
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
  method_args <- setdiff(colnames(param_grid), "uid")
  if(length(method_args) == 0) method_args <- NULL
  checkmate::assert_function(method_fun, args = method_args)
  
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
      param_grid = param_grid,
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
#' @param pipe_uid pipeline uid string
#' @param target column name of target variable. string input
#' @param method string input of model method
#' @param param_grid parameter grid for model tuning
#' @param desc optional model description
#' @param uid model uid
#'
#' @export
model <- function(pipe,
                  target,
                  method,
                  param_grid,
                  desc = NULL,
                  uid = NULL) {
  
  if (is.null(desc)) desc <- ""
  if (is.null(uid)) sparklyr::random_string("model")
  
  new_model(
    pipe = pipe,
    target = target,
    method = method,
    param_grid = param_grid,
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
#' @inheritParams model
#' @export
#' @return modeler object with model added
add_model <-function(obj,
                     pipe = NULL,
                     method,
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
  
  # Get Parameter Grid
  grid <- data.frame(expand.grid(list(...)))
  
  # Create model object
  m <- model(pipe = pipe$uid,
             target = obj$target,
             method = method,
             param_grid = grid,
             desc = desc,
             uid = uid)
  m$status <- "added"
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
#' @param ... additional arguments to pass on
#'
#' @export
#' @return updated modeler object
train_model <- function(mobj, ...) {
  UseMethod("train_model")
}


#' Fit Model Generic Function
#'
#' Functions execues model method on its pipeline output for given param grid
#' and sampling indicies
#'
#' Sub model fits are stored in sub-model list. sub-models given unique id. Each
#' sub-model is fit on each training sample provided
#'
#' @param mobj model object
#' @param ... additional arguments to pass to fit model function
#' @export
#' @return updated model object
fit_model <- function(mobj, ...) {
  UseMethod("fit_model")
}



#' Apply Model Generic Fuction
#'
#' Function applies model to make predictions for all samples
#'
#' Fitted values extracted for training data and predictions made for validation
#' or test samples. Predictions stored with sub-model
#'
#' @param mobj model object
#' @param ... additional arguments to pass to fit function
#'
#' @export
#' @return updated model object
apply_model <- function(mobj, ...) {
  UseMethod("apply_model")
}


#' Evaluate Model Generic Function
#'
#' Evaluate the accuracy of a fitted and applied model
#'
#' Function applies the measure function associated with modeler object to a
#' model predictions
#'
#' @param mobj model object
#' @param uids optional input for model uid. default is NULL and all trained
#'   models evaluated
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
get_coefs <- function(mobj, ...) {
  UseMethod("get_coefs", mobj)
}

#' Get Model Forecasts
#'
#' @inheritParams get_fit
#' @export
get_forecasts <- function(mobj, ...) {
  UseMethod("get_forecasts", mobj)
}


#' Return Tidy Dataset of Model Performance
#'
#' @export
tidy_performance <- function(mobj) {
  UseMethod("tidy_performance", mobj)
}



# Class Methods -----------------------------------------------------------


#' @rdname train_model
#' @export
train_model.model <- function(mobj, data, samples, save_fits, execution_strategy) {
  
  mobj %>%
    fit_model() %>%
    apply_model() %>%
    evaluate_model()
}



#' @rdname evaluate_model
#' @export
evaluate_model.model <- function(mobj, measure) {
  checkmate::assert_class(measure, "measure")
  
  mobj$evaluate <- purrr::map_df(mobj$predictions,
                                 ~purrr::map_df(.,
                                                dplyr::bind_rows,
                                                .id = "sample"),
                                 .id="indicie") %>%
    dplyr::inner_join(mobj$pipe$output %>%
                        dplyr::select_at(c(mobj$target, mobj$index_var)),
                      by = mobj$index_var) %>%
    dplyr::mutate(model = mobj$uid) %>%
    dplyr::mutate(predicted = ifelse(is.na(fitted), mean, fitted)) %>%
    dplyr::select_at(c("indicie", "sample", "model", mobj$index_var, mobj$target, "predicted")) %>%
    dplyr::group_by(model, sample, indicie) %>%
    dplyr::do(data.frame(
      match.fun(measure$method)(.,
                                actual = mobj$target,
                                predicted = "predicted")
    )) %>%
    dplyr::ungroup() %>%
    setNames(c("model", "sample", "indicie", measure$method))
  
  mobj
}


