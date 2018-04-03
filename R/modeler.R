

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


  checkmate::assert_character(target)
  checkmate::assert_character(type)
  checkmate::assert_character(name, null.ok = TRUE)
  checkmate::assert_character(id, null.ok = TRUE)
  checkmate::assert_character(version, null.ok = TRUE)
  checkmate::assert_character(desc, null.ok = TRUE)
  checkmate::assert_character(scientist, null.ok = TRUE)


  if(is.null(name)) name <- paste(target, type, sep = "-")
  if(is.null(id)) id <- sparklyr::random_string("id")
  if(is.null(version)) version <- "1.0"
  if(is.null(desc)) desc <- ""
  if(is.null(scientist)) scientist <- Sys.info()["user"]
  default_samples <- add_default_samples(df)

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
      samples = default_samples,
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


