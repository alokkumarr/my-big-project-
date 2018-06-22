
#' Pipeline Class Constructer
new_pipeline <- function(expr,
                         output,
                         desc,
                         created_on,
                         runtime) {
  checkmate::assert_function(expr)
  checkmate::assert_character(desc)
  checkmate::assert_posixct(created_on)

  structure(
    list(
      expr = expr,
      output = output,
      desc = desc,
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
#' @export
pipeline <- function(expr = identity,
                     desc = NULL) {
  if (is.null(desc))
    desc <- ""
  a1 <- Sys.time()
  new_pipeline(
    expr,
    output = NULL,
    desc,
    created_on = a1,
    runtime = NULL
  )

}


#' @rdname print
#' @export
print.pipeline <- function(pipe) {
  cat("Pipeline Description:\n", pipe$desc, "\n")
  cat("Expression:\n")
  print(pipe$expr)
  cat("\nCreated at:", as.character(pipe$created_on), "\n")
  cat("Runtime:", ifelse(is.null(pipe$runtime),
                         "< not run yet > \n",
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




#' Pipeline Flush function
#'
#' Flush function removes any stored output data from a pipeline
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
#' clean_pipe <- flush(pipe)
#' clean_pipe$output
flush <- function(pipe) {
  checkmate::check_class(pipe, "pipeline")
  pipe$output <- NULL
  pipe
}


#' Flush Pipelines function
#'
#' Function to flush pipelines for one or more models in modeler object
#'
#' @param obj modeler object
#' @param ids one or more model ids. default is null which flushes all pipes
#'
#' @return updated modeler object
#' @export
flush_pipes <- function(obj, ids = NULL) {
  checkmate::check_class(obj, "modeler")
  check_character(ids, null.ok = TRUE)

  if(is.null(ids)) {
    ids <- get_models(obj)
  }

  for(id in ids) {
    model <- get_models(obj, ids = id)[[1]]
    model$pipe <- flush(model$pipe)
    obj$models[[id]] <- model
  }

  obj
}
