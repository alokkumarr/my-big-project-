
#' Pipeline Class Constructer
new_pipeline <- function(expr,
                         output,
                         desc,
                         created_on,
                         last_updated,
                         runtime) {
  checkmate::assert_function(expr)
  checkmate::assert_character(desc)
  checkmate::assert_posixct(created_on)
  checkmate::assert_posixct(last_updated)

  structure(
    list(
      expr = expr,
      output = output,
      desc = desc,
      created_on  = created_on,
      last_updated = last_updated,
      runtime = runtime
    ),
    class = "pipeline"
  )
}


#' Pipeline Class Validator
valid_pipeline <- function(x){

  x
}


#' Pipeline Class Helper
#'
#' Creates a pipeline object. The expression is set by default as the identity function. This returns the
#'
#' @export
pipeline <- function(expr = identity,
                     desc = NULL){

  if(is.null(desc)) desc <- ""
  a1 <- Sys.time()
  valid_pipeline(
    new_pipeline(expr,
                 output = NULL,
                 desc,
                 created_on = a1,
                 last_updated = a1,
                 runtime = NULL)
  )
}



#' Pipeline Flow function
#'
#' Flow function executes the pipeline expression on the input and stores in
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
#' flow(mtcars, pipe)

flow <- function(x, pipe){
  UseMethod("flow")
}


#' @rdname flow
#' @export
flow.tbl_spark <- flow.data.frame <- function(x, pipe){
  checkmate::assert_class(pipe, "pipeline")
  a1 <- Sys.time()
  pipe$output <- pipe$expr(x)
  a2 <- Sys.time()
  pipe$runtime <- as.numeric(a2 - a1)
  pipe$last_updated <- a2
  pipe
}


#' @rdname flow
#' @export
flow.modeler <- function(x, pipe){
  flow(x$data)
}

