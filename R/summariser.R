#'DataFrame Summariser function
#'
#'Aggregation function. Configuarable inputs for grouping variables, measure
#'variables and aggregation functions.
#'
#'@param df DataFrame
#'@param group_vars vector of column names. used in the group by aggregration.
#'  can be zero or more columns. set to NULL for no grouping
#'@param measure_vars vector of column names. colnames to aggregrate. can be one
#'  or more columns. Default is NULL which will calculate counts only
#'@param fun vector of function names. can be one or more columns. default is
#'  sum. Current functions supported: n_distinct, min, max, sum, mean, variance,
#'  sd, kurtosis, skewness, and percentile
#'@param ... additional parameters passed to aggregate function
#'
#'@return returns DataFrame with one row for each unique combination of grouping
#'  variables provided
#'
#'@examples
#'library(dplyr)
#'
#'
#'summariser(iris,
#'           group_vars = c("Species"),
#'           measure_vars = c("Sepal.Length", "Sepal.Width"),
#'           fun = c("sum", "mean"))
#'@export
summariser <- function(df, ...) {
  UseMethod("summariser", df)
}


#' @importFrom magrittr %>%
#' @importFrom dplyr n
#' @importFrom dplyr vars
#' @rdname summariser
#' @export
summariser.data.frame <- function(df,
                                  group_vars,
                                  measure_vars = NULL,
                                  fun = c("sum"),
                                  ...) {
  # Arguments
  args <- summariser_args(group_vars, measure_vars, fun, ...)
  group_vars <- args$group_vars
  measure_vars <- args$measure_vars
  fun <- args$fun


  # Check for measure variables provided
  if (is.null(measure_vars)) {
    message("No measure vars provided. Providing counts of records only.")
    fun <- "n_distinct"
    measure_vars <- "rn"
    df <- df %>%
      dplyr::group_by_at(.vars = group_vars) %>%
      dplyr::mutate(rn = 1:n()) %>%
      dplyr::ungroup()
  }


  # Aggregate Data
  all_vars <- c(group_vars, measure_vars)
  group_by_vars <- if (is.null(group_vars))
    vars()
  else
    group_vars
  agg <- df %>%
    dplyr::select_at(all_vars) %>%
    dplyr::group_by_at(group_by_vars) %>%
    dplyr::summarise_all(.funs = fun, ...) %>%
    dplyr::ungroup()


  # Rename measure variables
  new_measure_vars <- setdiff(colnames(agg), group_vars)
  if (length(measure_vars) < 2 | length(fun) < 2) {
    agg <-
      dplyr::rename_(agg, .dots = setNames(new_measure_vars, paste(measure_vars, fun, sep =
                                                                     "_")))
  }

  agg
}



#' @importFrom magrittr %>%
#' @importFrom dplyr vars
#' @rdname summariser
#' @export
summariser.tbl_spark <- function(df,
                                 group_vars,
                                 measure_vars = NULL,
                                 fun = c("sum"),
                                 ...) {
  # Arguments
  args <- summariser_args(group_vars, measure_vars, fun, ...)
  group_vars <- args$group_vars
  measure_vars <- args$measure_vars
  fun <- args$fun

  # Check for measure variables provided
  if (is.null(measure_vars)) {
    message("No measure vars provided. Providing counts of records only.")
    fun <- "n_distinct"
    measure_vars <- "rn"
    df <- df %>%
      dplyr::arrange_at(.vars = group_vars) %>%
      dplyr::mutate(rn = row_number())
  }


  # Aggregate Data
  all_vars <- c(group_vars, measure_vars)
  group_by_vars <- if (is.null(group_vars))
    vars()
  else
    group_vars
  agg <- df %>%
    dplyr::select_at(all_vars) %>%
    dplyr::group_by_at(group_by_vars) %>%
    dplyr::summarise_all(.funs = fun, ...) %>%
    dplyr::ungroup()


  # Rename measure variables
  if (length(measure_vars) < 2 | length(fun) < 2) {
    agg <-
      agg %>% dplyr::select_(.dots = stats::setNames(colnames(agg), c(
        group_vars, paste(measure_vars, fun, sep =
                            "_")
      )))
  }

  agg
}


#' Summariser Arguments Constructor Function
#'
#' Function to create object of summarise_args S3 class
#'
#' @inheritParams summariser
new_summariser_args <-
  function(group_vars, measure_vars, fun, ...) {
    stopifnot(is.character(group_vars) | is.null(group_vars))
    stopifnot(is.character(measure_vars) | is.null(measure_vars))
    stopifnot(is.character(fun))

    structure(list(
      group_vars = group_vars,
      measure_vars = measure_vars,
      fun = fun,
      ...
    ),
    class = "summariser_args")
  }


#' Summariser Arguments Validation Function
#'
#' Checks for valid inputs to summarise_args class
#'
#' @param x obj of class summariser_args
validate_summariser_args <- function(x) {
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
  if (! all(x$fun %in% funs)) {
    stop(
      "Supplied function not supported.\nPlease use one of following: ",
      paste(funs, collapse = ", "),
      .call = FALSE
    )
  }
  if (is.null(x$group_vars) & is.null(x$measure_vars)) {
    stop(
      "Both group_vars and measure_vars are NULL.\nNeed to supply one valid column name to one or the other\n",
      .call = FALSE
    )
  }
  x
}

#' Summariser Argument Helper Function
#'
#' Creates a valid object of summarise_args class
#'
#' Function should be used to pass nested arguments to the summarise_map, map
#' argument
#' @inheritParams summariser
#'
#' @export
#' @importFrom magrittr %>%
summariser_args <- function(group_vars, measure_vars, fun, ...) {
  new_summariser_args(group_vars, measure_vars, fun, ...) %>%
    validate_summariser_args()
}
