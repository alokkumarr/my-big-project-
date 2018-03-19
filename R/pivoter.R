

#'DataFrame Pivoter Function
#'
#'Function to pivot data from long to wide format. Simliar to reshape2::dcast
#'function. Function allows for more than one group variable and measure
#'variable.
#'
#'@param df DataFrame
#'@param id_vars vector of column names. used as the left hand side of pivot.
#'  can be one or more columns.
#'@param group_vars vector of column names. used as the right hand side of
#'  pivot. unique values are transformed into columns. can be one or more
#'  columns. should be categorical column
#'@param measure_vars vector of column names. used as the value amount in the
#'  pivot. can be one or more columns.
#'@param fun function name. supports only single function. default is sum.
#'  Current functions supported: min, max, sum, mean, variance, sd, kurtosis,
#'  skewness
#'@param sep if NULL, new column names will be taken from group variable. If
#'  not-NULL, the column names will be given by '<group_var name><sep><group_var
#'  value><sep><measure_var name>'
#'@param fill optional fill value for structural missings. defaults to value
#'  from applying fun to 0 length vector
#'
#'@return dataframe reshaped in wide format
#'
#'@examples
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
#'pivoter(dat,
#'        id_vars = "id",
#'        group_vars = c("cat1"),
#'        measure_vars = "metric1",
#'        fun = "sum",
#'        fill=0)
#'@export
pivoter <- function(df, ...) {
  UseMethod("pivoter", df)
}


#' @importFrom magrittr %>%
#' @importFrom stats as.formula setNames
#' @rdname pivoter
#' @export
pivoter.tbl_spark <- function(df,
                              id_vars,
                              group_vars,
                              measure_vars,
                              fun = "sum",
                              sep = "_",
                              fill = NULL) {
  group_by_vars <- c(id_vars, group_vars)

  # Iterate through group variables and create pivot formula
  i <- 0
  for (g in group_vars) {
    pivot_fn <-
      as.formula(paste(paste(id_vars, collapse = "+"), "~", g))

    # Iterate through measure variables and determine pivot function
    for (m in measure_vars) {
      i <- i + 1
      pivot_agg <- list(fun)
      names(pivot_agg) <- m
      if (is.null(fill)) {
        fill_var <- suppressWarnings(match.fun(fun)(c()))
      } else{
        fill_var <- fill
      }
      sub_result <-
        sparklyr::sdf_pivot(df, formula = pivot_fn, fun.aggregate = pivot_agg) %>%
        sparklyr::na.replace(fill_var)
      pvt_measure_vars <-
        setdiff(colnames(sub_result), group_by_vars)

      if (!is.null(sep)) {
        for (var in pvt_measure_vars) {
          sub_result <-
            dplyr::rename_(sub_result, .dots = setNames(var, paste(g, var, m, sep = sep)))
        }
      }

      if (i == 1) {
        result <- sub_result
      } else{
        result <- dplyr::full_join(result, sub_result, by = id_vars)
      }
    }
  }

  result
}



#' @importFrom stats as.formula setNames
#' @rdname pivoter
#' @export
pivoter.data.frame <- function(df,
                               id_vars,
                               group_vars,
                               measure_vars,
                               fun = "sum",
                               sep = "_",
                               fill = NULL) {
  group_by_vars <- c(id_vars, group_vars)

  # Iterate through group variables and create pivot formula
  i <- 0
  for (g in group_vars) {
    pivot_fn <-
      as.formula(paste(paste(id_vars, collapse = "+"), "~", g))

    # Iterate through measure variables and determine pivot function
    for (m in measure_vars) {
      i <- i + 1
      pivot_fun <- match.fun(fun)

      sub_result <-
        reshape2::dcast(
          df,
          formula = pivot_fn,
          fun.aggregate = pivot_fun,
          value.var = m,
          fill = fill
        )
      pvt_measure_vars <-
        setdiff(colnames(sub_result), group_by_vars)

      if (!is.null(sep)) {
        for (var in pvt_measure_vars) {
          sub_result <-
            dplyr::rename_(sub_result, .dots = setNames(var, paste(g, var, m, sep = sep)))
        }
      }

      if (i == 1) {
        result <- sub_result
      } else{
        result <- dplyr::full_join(result, sub_result, by = id_vars)
      }
    }
  }

  result
}
