
#'@export
roller <- function(df, ...) {
  UseMethod("roller", df)
}


roller.data.frame <- function(df,
                              order_vars,
                              group_vars,
                              measure_vars,
                              fun,
                              width,
                              by,
                              ...){

}


roller.tbl_spark <- function(df,
                             order_vars,
                             group_vars,
                             measure_vars,
                             fun,
                             width,
                             by,
                             ...){

  if (length(fun) == 0) {
    stop("no function argument provided")
  }



}



#' Roller Arguments Constructor function
#'
#' Creates new object of class roller_args
#'
#' @inheritParams roller
#'
#' @return roller_args object
#' @export
new_roller_args <- function(order_vars,
                            group_vars,
                            measure_vars,
                            fun,
                            width,
                            by,
                            ...) {
  stopifnot(is.character(order_vars) | is.null(order_vars))
  stopifnot(is.character(group_vars) | is.null(group_vars))
  stopifnot(is.character(measure_vars) | is.null(measure_vars))
  stopifnot(is.character(fun))

  structure(list(
    order_vars = order_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = fun,
    ...
  ),
  class = "roller_args")
}



#' Roller Arguments Validation Function
#'
#' Checks for valid inputs to roller_args class
#'
#' @param x obj of class summariser_args
validate_roller_args <- function(x) {
  funs <- c(
    "n_distinct",
    "min",
    "max",
    "sum",
    "mean",
    "variance",
    "sd",
    "kurtosis",
    "skewness",
    "percentile"
  )
  if (!all(x$fun %in% funs)) {
    stop(
      "Supplied function not supported.\nPlease use one of following: ",
      paste(funs, collapse = ", "),
      .call = FALSE
    )
  }
  if (is.null(x$measure_vars)) {
    stop(
      "Measure_vars not specified.\nNeed to supply one valid column name to apply function to\n",
      .call = FALSE
    )
  }
  if (is.null(order_vars)) {
    message(
      "Order var not specified. Rolling function applied on the dataframe in its current order"
    )
  }
  if (by < 1) {
    stop("by input should be >= 1", .call = FALSE)
  }
  if (width < 1) {
    stop("width should be >= 1", .call = FALSE)
  }

  x
}



#' Roller Argument Helper Function
#'
#' Creates a valid object of roller_args class
#'
#' Function should be used in roller internals
#' @inheritParams roller
#'
#' @export
#' @importFrom magrittr %>%
summariser_args <- function(order_vars,
                            group_vars,
                            measure_vars,
                            fun,
                            width,
                            by,
                            ...) {
  new_roller_args(order_vars,
                  group_vars,
                  measure_vars,
                  fun,
                  width,
                  by,
                  ...) %>%
    validate_roller_args()
}
