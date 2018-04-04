

# Modeler Class Functions -------------------------------------------------



#' New Modeler Object Constructer function
new_modeler <- function(df,
                        target,
                        type,
                        name,
                        id,
                        version,
                        desc,
                        scientist,
                        dir,
                        samples,
                        models,
                        ...){


  checkmate::assert_character(target)
  checkmate::assert_character(type)
  checkmate::assert_character(name)
  checkmate::assert_character(id)
  checkmate::assert_character(version)
  checkmate::assert_character(desc)
  checkmate::assert_character(scientist)
  checkmate::test_path_for_output(dir)
  checkmate::assert_class(samples, "samples")
  checkmate::assert_list(models)

  structure(
    list(
      type = type,
      name = name,
      id = id,
      version = version,
      desc = desc,
      scientist = scientist,
      created_on = Sys.time(),
      data = df,
      target = target,
      dir = dir,
      samples = samples,
      models,
      ...
    ),
    class = "modeler")
}


#' Modeler Validation function
valid_modeler <- function(x){

  if (! x$type %in% c("forecaster", "segmenter", "regresser", "classifier")) {
    stop("modeler type supplied not supported. Please use one of following: ",
         "\n* forecaster",
         "\n* segmenter",
         "\n* regresser",
         "\n* classifier")
  }

  if(! class(x$data) %in% c("data.frame", "tbl_spark")){
    stop("df input class not supported. modeler supports only data.frame and tbl_spark objects")
  }

  x
}


#' Modeler Helper Function
modeler <- function(df,
                    target,
                    type,
                    name = NULL,
                    id = NULL,
                    version = NULL,
                    desc = NULL,
                    scientist = NULL,
                    dir = NULL,
                    ...){

  if(is.null(name)) name <- paste(target, type, sep = "-")
  if(is.null(id)) id <- sparklyr::random_string("id")
  if(is.null(version)) version <- "1.0"
  if(is.null(desc)) desc <- ""
  if(is.null(scientist)) scientist <- Sys.info()["user"]
  if(is.null(dir)) dir <- "./"
  default_samples <- add_default_samples(df)
  empty_models <- list()

  valid_modeler(
    new_modeler(
      df = df,
      target = target,
      type = type,
      name = name,
      id = id,
      version = version,
      desc = desc,
      scientist = scientist,
      dir = dir,
      samples = default_samples,
      models = empty_models,
      ...)
  )
}




# Modeler Class Generics --------------------------------------------------



#' Train Model Generic
train_models <- function(obj, model, ...) {
  UseMethod("train_models")
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


