


# Model Class Functions ---------------------------------------------------


#' Model Class Constructer
new_model <- function(pipe,
                      target,
                      method,
                      method_args,
                      desc,
                      path,
                      id,
                      status,
                      created_on,
                      fit,
                      performance) {
  checkmate::assert_class(pipe, "pipeline")
  checkmate::assert_character(target, null.ok = TRUE)
  checkmate::assert_choice(method, choices = model_methods$method)
  checkmate::assert_list(method_args)
  checkmate::assert_character(desc)
  checkmate::assert_path_for_output(path, overwrite = FALSE)
  checkmate::assert_character(id)
  checkmate::assert_choice(status,
                           c("created", "added", "trained", "evaluated", "selected", "final"))
  checkmate::assert_posixct(created_on)
  checkmate::assert_list(fit, null.ok = TRUE)
  checkmate::assert_list(performance, null.ok = TRUE)

  .method <- method
  method_fun <- model_methods %>%
    dplyr::filter(method == .method) %>%
    dplyr::pull(package) %>%
    asNamespace() %>%
    get(method, .)
  checkmate::assert_function(method_fun, args = names(method_args))

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
      package = method_package,
      desc = desc,
      path = path,
      id = id,
      created_on = created_on,
      status = status,
      fit = fit,
      performance = performance
    ),
    class = c(method_class, "model")
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
      desc = desc,
      path = path,
      id = id,
      status = "created",
      created_on = Sys.time(),
      fit = NULL,
      performance = NULL
    )
  )
}




#' Add Model Grid to Modeler Object function
#'
#' Function to add grid of multiple models to modeler object. Vectors of
#' parameter values can be provided to this function and grid of models added to
#' modeler object
#'
#' Function creates a new model object from inputs and then appends to modeler
#' models list
#'
#' @param obj modeler object
#' @inheritParams model
#' @export
#' @return modeler object with model added
add_model_grid <-function(obj,
                          pipe = NULL,
                          method,
                          ...,
                          desc = NULL,
                          path = NULL) {
  checkmate::assert_class(obj, "modeler")

  type_methods <- model_methods %>%
    dplyr::filter(type == obj$type) %>%
    dplyr::pull(method) %>%
    as.character()
  checkmate::assert_choice(method, type_methods)

  if(is.null(pipe))
    pipe <- pipeline()

  # Args
  args <- expand.grid(list(...))

  if(nrow(args) > 0) {

    for(i in 1:nrow(args)) {
      arg_list <- as.list(args[i,])
      model_desc <- paste(desc, paste(paste(names(arg_list), arg_list, sep="="), collapse="; "), sep=": ")
      model_args <- c(list(pipe = pipe,
                           target = obj$target,
                           method = method,
                           desc = model_desc,
                           path = path),
                      arg_list)
      m <- do.call("model", model_args)
      m$status <- "added"
      obj$models[[m$id]] <- m
    }
  }else{
    m <- model(pipe = pipe,
               target = obj$target,
               method = method,
               desc = desc,
               path = path)
    m$status <- "added"
    obj$models[[m$id]] <- m
  }

  obj
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
                     path = NULL) {
  checkmate::assert_class(obj, "modeler")

  type_methods <- model_methods %>%
    dplyr::filter(type == obj$type) %>%
    dplyr::pull(method) %>%
    as.character()
  checkmate::assert_choice(method, type_methods)

  if(is.null(pipe))
    pipe <- pipeline()

  m <- model(pipe = pipe,
             target = obj$target,
             method = method,
             ...,
             desc = desc,
             path = path)
  m$status <- "added"
  obj$models[[m$id]] <- m
  obj
}



#' Add Multiple Models to Modeler Object function
#'
#' Function to add model to modeler object. More than one model can be added to
#' a modeler object.
#'
#' Function creates a new model object from inputs and then appends to modeler
#' models list
#'
#' @param obj modeler object
#' @param pipe pipeline object
#' @param models list with models method and list of arguments in each element
#'
#' @export
#' @return modeler object with models added
add_models <- function(obj,
                       pipe = NULL,
                       models) {
  checkmate::assert_class(obj, "modeler")
  checkmate::assert_class(models, "list")

  if(is.null(pipe))
    pipe <- pipeline()

  for(i in 1:length(models)) {

    model_args <- modifyList(
      list(pipe = pipe,
           target = obj$target,
           method = models[[i]]$method,
           desc = NULL,
           path = NULL),
      models[[i]]$method_args)
    m <- do.call("model", model_args)
    m$status <- "added"
    obj$models[[m$id]] <- m
    obj
  }

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


#' Train Model Generic
#'
#' Train single model to indicies provided
#'
#' Fits Model and makes predictions for any validation or test indicies
#' provided. Adds fitted values and predictions to model's performance values
#'
#' @return updated model object
#' @export
train <- function(...) {
  UseMethod("train")
}


#' Evaluate Model Generic
#'
#' Function to evaluate the predictive performance of a model
#'
#' @param mobj model object
#' @param target_df dataframe with target and index variables
#' @param measure measure object
#'
#' @return returns evaluted model object
#' @export
evaluate <- function(mobj, measure) {
  UseMethod("evaluate")
}


#' @rdname evaluate
#' @export
evaluate.model <- function(mobj, measure) {
  checkmate::assert_class(measure, "measure")

  mobj$evaluate <- purrr::map_df(mobj$performance,
                                 ~purrr::map_df(.,
                                                bind_rows,
                                                .id = "sample"),
                                 .id="indicie") %>%
    dplyr::inner_join(mobj$pipe$output %>%
                        select_at(c(mobj$target, mobj$index_var)),
                      by = mobj$index_var) %>%
    dplyr::mutate(model = mobj$id) %>%
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



#' Return a Model Fit
#'
#' @export
get_fit <- function(x, ...) {
  UseMethod("get_fit", x)
}

#' Return the Model Coefficients
#'
#' @export
get_coefs <- function(x, ...) {
  UseMethod("get_coefs", x)
}

#' Get Model Forecasts
#'
#' @export
get_forecasts <- function(x, ...) {
  UseMethod("get_forecasts", x)
}


#' Return Tidy Dataset of Model Performance
#'
#' @export
tidy_performance <- function(obj) {
  UseMethod("tidy_performance")
}


# Class Methods -----------------------------------------------------------

#' @export
#' @rdname get_target
get_target.model <- function(obj) {

  obj$pipe$output %>%
    select_at(obj$target) %>%
    mutate(index = 1:n())

}
