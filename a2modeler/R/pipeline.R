
new_pipeline <- function(expr,
                         output,
                         desc,
                         uid,
                         created_on,
                         runtime) {
  checkmate::assert_function(expr)
  checkmate::assert_character(desc)
  checkmate::assert_character(uid)
  checkmate::assert_posixct(created_on)
  
  structure(
    list(
      expr = expr,
      output = output,
      desc = desc,
      uid = uid,
      created_on  = created_on,
      runtime = runtime
    ),
    class = "pipeline"
  )
}



#' Pipeline Class Helper
#'
#' Creates a pipeline object. The expression is set by default as the identity
#' function. This returns the
#'
#' @param expr pipeline function. default is identity function
#' @param desc optional description input
#' @param uid A character string used to uniquely identify the pipeline
#'
#' @export
pipeline <- function(expr = identity,
                     desc = NULL,
                     uid = sparklyr::random_string("pipe")) {
  if (is.null(desc))
    desc <- ""
  a1 <- Sys.time()
  new_pipeline(
    expr,
    output = NULL,
    desc,
    uid,
    created_on = a1,
    runtime = NULL
  )
  
}


#' @rdname print
#' @export
print.pipeline <- function(pipe, ...) {
  cat("Pipeline:", pipe$uid, "---------------- \n\n")
  cat("Description:\n", pipe$desc, "\n\n")
  cat("Expression:\n")
  print(pipe$expr)
  cat("\nCreated at:", as.character(pipe$created_on), "\n")
  cat("Runtime:", ifelse(is.null(pipe$runtime),
                         "< not run yet > \n\n",
                         paste(round(pipe$runtime, 2), "seconds\n\n")))
  cat("Sample Output:\n")
  print(head(pipe$output))
}



#' Pipeline Execute function
#'
#' Execute function executes the pipeline expression on the input and stores in
#' output location
#'
#' @param x input object to apply pipeline expression on. Can either be
#'   data.frame, spark dataframe or modeler object
#' @param pipe pipeline object
#'
#' @export
#' @return updated pipeline object with output
#' @examples
#'
#' pipe <- pipeline(expr = function(e) mean(e$mpg))
#' execute(mtcars, pipe)
execute <- function(x, pipe){
  UseMethod("execute")
}


#' @rdname execute
#' @export
execute.data.frame <- function(x, pipe){
  checkmate::assert_class(pipe, "pipeline")
  a1 <- Sys.time()
  pipe$output <- pipe$expr(x)
  a2 <- Sys.time()
  pipe$runtime <- as.numeric(a2 - a1)
  pipe
}


#' @rdname execute
#' @export
execute.tbl_spark <-  function(x, pipe){
  checkmate::assert_class(pipe, "pipeline")
  a1 <- Sys.time()
  pipe$output <- pipe$expr(x)
  a2 <- Sys.time()
  pipe$runtime <- as.numeric(a2 - a1)
  pipe
}


#' @rdname execute
#' @export
execute.NULL <- function(x, pipe){
  checkmate::assert_class(pipe, "pipeline")
  a1 <- Sys.time()
  pipe$output <- NULL
  a2 <- Sys.time()
  pipe$runtime <- as.numeric(a2 - a1)
  pipe
}



#' @rdname execute
#' @export
execute.modeler <- function(x, pipe){
  execute(x$data, pipe)
}


#' Pipeline Flow function
#'
#' Flow function executes the pipeline expression on the input and returns the
#' data output only
#'
#' @param x input object to apply pipeline expression on. Can either be
#'   data.frame, spark dataframe or modeler object
#' @param pipe pipeline object
#'
#' @export
#' @return data output resulting from pipeline execution
#' @examples
#'
#' pipe <- pipeline(expr = function(e) mean(e$mpg))
#' flow(mtcars, pipe)
flow <- function(x, pipe){
  checkmate::assert_class(pipe, "pipeline")
  pipe$expr(x)
}



#' Pipeline Test function
#'
#' Test function applies pipeline expression on sample of data only.
#'
#' @param x input object to apply pipeline expression on. Can either be
#'   data.frame, spark dataframe or modeler object
#' @param pipe pipeline object
#' @param n number of records to take from head of input. default is 100
#'
#' @export
#' @return data output resulting from sampled pipeline execution
#' @examples
#'
#' pipe <- pipeline(expr = function(e) mean(e$mpg))
#' test(mtcars, pipe, 10)
test <- function(x, pipe, n){
  UseMethod("test")
}


#' @export
#' @rdname test
test.modeler <- function(x, pipe, n = 100){
  flow(head(x$data, n), pipe)
}


#' @export
#' @rdname test
test.tbl_spark <- function(x, pipe, n = 100){
  flow(head(x, n), pipe)
}


#' @export
#' @rdname test
test.data.frame <- function(x, pipe, n = 100){
  flow(head(x, n), pipe)
}




#' Pipeline Clean function
#'
#' Clean function removes any stored output data from a pipeline
#'
#' Function useful for cleaning up and reducing size of modeler object
#'
#' @param pipe pipeline object
#'
#' @export
#' @return updated pipeline object with data output removed
#' @examples
#'
#' pipe <- pipeline(expr = function(e) mean(e$mpg))
#' pipe <- execute(mtcars, pipe)
#' clean_pipe <- clean(pipe)
#' clean_pipe$output
clean <- function(pipe) {
  checkmate::assert_class(pipe, "pipeline")
  pipe$output <- NULL
  pipe
}


#' Flush Pipelines function
#'
#' Function to clean pipelines for one or more models in modeler object
#'
#' @param obj modeler object
#' @param uids one or more model ids. default is null which cleans all pipes
#'
#' @return updated modeler object
#' @export
clean_pipes <- function(obj, uids = NULL) {
  checkmate::assert_class(obj, "modeler")
  checkmate::assert_character(uids, null.ok = TRUE)
  
  if(is.null(uids)) {
    uids <- get_models(obj)
  }
  
  for(id in ids) {
    model <- get_models(obj, uids = uid)[[1]]
    model$pipe <- clean(model$pipe)
    obj$models[[uid]] <- model
  }
  
  obj
}
