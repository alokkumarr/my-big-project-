

#' New Modeler Object Constructer function
new_modeler <- function(df,
                        target,
                        type,
                        name,
                        id,
                        version,
                        desc,
                        scientist,
                        ...){


  assert_character(target)
  assert_character(type)
  assert_character(name, null.ok = TRUE)
  assert_character(id, null.ok = TRUE)
  assert_character(version, null.ok = TRUE)
  assert_character(desc, null.ok = TRUE)
  assert_character(scientist, null.ok = TRUE)


  if(is.null(name)) name <- paste(target, type, sep = "-")
  if(is.null(id)) id <- sparklyr::random_string("id")
  if(is.null(version)) version <- "1.0"
  if(is.null(desc)) desc <- ""
  if(is.null(scientist)) scientist <- Sys.info()["user"]

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
                    ...){



  valid_modeler(
    new_modeler(
      df,
      target,
      type,
      name,
      id,
      version,
      desc,
      scientist,
      ...)
  )
}


#' @export
set_sampling <- function(...){
  UseMethod("set_sampling", ...)
}



add_validation_sample.modeler <- function(obj, method, ...){

  vobj <- validation_sample(method, ...)
  assert_class(vobj, "validation_sample")
  obj$validation_sample <- vobj
}


add_test_sample.modeler <- function(obj, method, ...){

  tobj <- test_sample(method, ...)
  assert_class(tobj, "test_sample")
  obj$test_sample <- tobj
}




#' Set Modeler Sampling Methods
#'
#' Function sets internal Modeler object sampling methods. Function creates sample indicies but not
#' full samples. Modeler object sampling slots are updated
#'
#'
#' @param obj modeler object
#' @param validation_method sampling method to apply to create training and validation datasets
#' @param validation_args list of additional arguments to pass to validation sampling method
#' @param test_method sampling method to apply to create test dataset only. default is 'none'
#' @param test_args list of additional arguments to pass to test sampling method
#'
#' @rdname set_sampling
#' @export
set_sampling.modeler <- function(obj,
                                 validation_method,
                                 validation_args,
                                 test_method,
                                 test_args) {

  assert_class(obj, c("modeler"))
  assert_choice(validation_method, c("holdout", "rsample", "none"))
  assert_list(validation_args)
  assert_choice(test_method, c("holdout", "rsample", "none"))
  assert_list(test_args)

  # Create sampling object

  mobj$validation_method <- validation_method
  mboj$valition_method_args <- validation_args
  #mobj$


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


