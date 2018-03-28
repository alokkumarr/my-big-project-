
#'DataFrame Mutater Function
#'
#'Appends additional calculated columns to a dataframe. Allows for grouping and
#'ordering calculations. Additional arguments to the can be passed directly to
#'transfomation function.
#'
#'@param df DataFrame
#'@param order_vars optional vector of column names to arrange data by. can be
#'  one or more columns. default is NULL. order matters - arranges left to
#'  right. supports decreasing ordering - see examples.
#'@param group_vars optional vector of column names to group data by. can be one
#'  or more columns. default is NULL
#'@param measure_vars vector of column names to apply functional transformation
#'  to. can be one or more columns
#'@param fun transformation function. accepts either fun name string or a
#'  expression wrapped in funs() call. see examples for example of using custom
#'  function with funs()
#'@param ... additional arguments to pass to the transformation function
#'
#'@return DataFrame with additional calculated columns appended
#'@export
#'
#' @examples
#'library(dplyr)
#'
#'
#'# Create toy dataset
#'set.seed(319)
#'id_vars <- seq(101, 200, by=1)
#'dates <- seq(from=Sys.Date()-365, to=Sys.Date(), by="day")
#'cat1 <- c("A", "B")
#'cat2 <- c("X", "Y", "Z")
#'
#'dat <- data.frame()
#'for(id in id_vars){
#'   n <- floor(runif(1)*100)
#'   d <- data.frame(id = id,
#'                  date = sample(dates, n, replace = TRUE),
#'                  cat1 = sample(cat1, n, replace = TRUE),
#'                  cat2 = sample(cat2, n, replace = TRUE),
#'                  metric1 = sample(1:5, n, replace = TRUE),
#'                  metric2 = rnorm(n, mean=50, sd = 5))
#'  dat <- rbind(dat, d)
#'}
#'
#' # Mutater
#' dat %>% mutater(
#'                 order_vars = c("date"),
#'                 group_vars = c("id"),
#'                 measure_vars = "metric1",
#'                 fun = "cumsum")
#' dat %>% mutater(measure_vars = "metric1",
#'                 fun = funs(add2 = .+2))
mutater <- function(df, ...) {
  UseMethod("mutater", df)
}



#' @importFrom magrittr %>%
#' @rdname mutater
#' @export
mutater.data.frame <- function(df,
                               order_vars = NULL,
                               group_vars = NULL,
                               measure_vars,
                               fun,
                               ...) {
  if (!is.null(group_vars)) {
    df <- df %>% dplyr::group_by_at(group_vars)
  }

  if (!is.null(order_vars)) {
    df <- df %>% dplyr::arrange_at(order_vars)
  }


  if (class(fun) != "fun_list") {
    .fun <- fun
    fname <- names(.fun) <- fun
  } else{
    .fun <- fun
    fname <- names(.fun)
  }

  results <- df %>%
    dplyr::mutate_at(.vars = measure_vars, .funs = .fun, ...)

  if (!is.null(group_vars)) {
    results <- results %>% dplyr::ungroup()
  }

  # Rename measure variables
  new_measure_vars <- setdiff(colnames(results), colnames(df))
  if (length(measure_vars) < 2) {
    results <-
      dplyr::rename_(results, .dots = setNames(new_measure_vars, paste(measure_vars, fname, sep =
                                                                         "_")))
  }

  results
}




#' @importFrom magrittr %>%
#' @rdname mutater
#' @export
mutater.tbl_spark <- function(df,
                              order_vars = NULL,
                              group_vars = NULL,
                              measure_vars,
                              fun,
                              ...) {
  if (!is.null(group_vars)) {
    df <- df %>% dplyr::group_by_at(group_vars)
  }

  if (!is.null(order_vars)) {
    df <- df %>% dplyr::arrange_at(order_vars)
  }

  if (class(fun) != "fun_list") {
    .fun <- fun
    fname <- names(.fun) <- fun
  } else{
    .fun <- fun
    fname <- names(.fun)
  }

  results <- df %>%
    dplyr::mutate_at(.vars = measure_vars, .funs = .fun, ...)

  if (!is.null(group_vars)) {
    results <- results %>% dplyr::ungroup()
  }

  if (length(measure_vars) < 2) {
    results <-
      results %>% dplyr::select_(.dots = stats::setNames(colnames(results),
                                                         c(
                                                           colnames(df),
                                                           paste(measure_vars, fname, sep =
                                                                   "_")
                                                         )))
  }

  results
}
