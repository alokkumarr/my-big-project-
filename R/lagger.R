
#' DataFrame Lagger function
#'
#' Function that iteratively adds lagged values of specified column to a
#' dataframe
#'
#' Iteratively calls mutater for each lag value provided
#'
#'
#' @param df DataFrame
#' @param order_vars required vector of columns to arrange data by
#' @param group_vars optional grouping variables
#' @param measure_vars required vector of columns name to applied lag
#'   transformations to
#' @param lags number of lag period. accepts more than one value as vector
#'
#' @return spark dataframe with lag values appended
#' @export
#'
#' @examples
#' library(dplyr)
#'
#'
#' # Create toy dataset
#' set.seed(319)
#' id_vars <- seq(101, 200, by=1)
#' dates <- seq(from=Sys.Date()-365, to=Sys.Date(), by="day")
#' cat1 <- c("A", "B")
#' cat2 <- c("X", "Y", "Z")
#'
#' dat <- data.frame()
#' for(id in id_vars){
#'   n <- floor(runif(1)*100)
#'   d <- data.frame(id = id,
#'                  date = sample(dates, n, replace = T),
#'                  cat1 = sample(cat1, n, replace = T),
#'                  cat2 = sample(cat2, n, replace = T),
#'                  metric1 = sample(1:5, n, replace = T),
#'                  metric2 = rnorm(n, mean=50, sd = 5))
#'  dat <- rbind(dat, d)
#' }
#'
#' df %>%
#'   lagger(order_vars = c("date"),
#'          group_vars = "id",
#'          mutate_vars = c("metric1"),
#'          lags = c(1, 3, 5))
lagger <- function(df, ...) {
  UseMethod("lagger", df)
}




#' @rdname lagger
lagger.data.frame <- function(df,
                              order_vars,
                              group_vars = NULL,
                              measure_vars,
                              lags = 1L) {
  stopifnot(!is.null(order_vars))

  for (l in lags) {
    c1 <- colnames(df)
    df <- mutater(df,
                  order_vars,
                  group_vars,
                  measure_vars,
                  fun = "lag",
                  n = l)

    new_mutate_vars <- setdiff(colnames(df), c1)
    for (var in new_mutate_vars) {
      new_name <- paste0(var, l)
      df <- dplyr::rename_(df, .dots = stats::setNames(var, new_name))
    }
  }

  df
}



#' @rdname lagger
lagger.tbl_spark <- function(df,
                             order_vars,
                             group_vars = NULL,
                             measure_vars,
                             lags = 1L) {
  stopifnot(!is.null(order_vars))

  for (l in lags) {
    c1 <- colnames(df)
    df <- mutater(df,
                  order_vars,
                  group_vars,
                  measure_vars,
                  fun = "lag",
                  n = l)

    new_vars <- setdiff(colnames(df), c1)
    new_names <- paste0(new_vars, l)
    df <- dplyr::select_(df,
                         .dots = stats::setNames(colnames(df),
                                                 c(c1, new_names)))

  }

  df
}
