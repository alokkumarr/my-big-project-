




#'DataFrame Melter function
#'
#'Data munging function that reshapes wide data into long format. Similiar to
#'reshape2::melt
#'
#'@param df dataframe
#'@param id_vars vector of string column names. Can be zero or more. Default is
#'  NULL. Id variables are retained in wide form in the functional result
#'@param measure_vars vector of string column names. Requires a valid column
#'  name. Accepts one or more. Measure variables are converted to long form
#'@param variable_name optional column name for resulting column of measure
#'  variable names. Default is 'variable'
#'@param value_name option column name for resulting column of measure varaible
#'  values
#'@param type measure_vars type. Used in spark.dataframe method only. default is
#'  DOUBLE
#'
#'@examples
#'library(dplyr)
#'library(sparklyr)
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
#'# Create Spark Connection and read in some data
#'sc <- sparklyr::spark_connect(master="local")
#'
#'# Load data into Spark
#'dat_tbl <- copy_to(sc, dat, overwrite = TRUE)
#'
#'spk_long <- melter(dat_tbl,
#'                   id_vars = c("id", "date", "cat1", "cat2"),
#'                   measure_vars = c("metric1", "metric2"),
#'                   variable_name = "metric",
#'                   value_name = "value")
#'spk_long
#'r_long <- melter(dat,
#'                 id_vars = c("id", "date", "cat1", "cat2"),
#'                 measure_vars = c("metric1", "metric2"),
#'                 variable_name = "metric",
#'                 value_name = "value")
#'spk_long %>% collect() == r_long
#'@export
melter <- function(df, ...) {
  UseMethod("melter", df)
}


#' @importFrom magrittr %>%
#' @rdname melter
#' @export
melter.data.frame <- function(df,
                              id_vars = NULL,
                              measure_vars,
                              variable_name = "variable",
                              value_name = "value") {
  reshape2::melt(
    df,
    id.vars = id_vars,
    measure.vars = measure_vars,
    variable.name = variable_name,
    value.name = value_name
  ) %>%
    dplyr::mutate_at(variable_name, as.character)
}


#' @importFrom magrittr %>%
#' @rdname melter
#' @export
melter.tbl_spark <- function(df,
                             id_vars = NULL,
                             measure_vars,
                             variable_name = "variable",
                             value_name = "value",
                             type = "DOUBLE") {
  # Helper Function
  str_fun <- function(s) {
    paste(paste0("'", s, "'"),
          paste("CAST(", s, "AS", toupper(type), ")"),
          sep = ", ")
  }

  # Create stack expression
  stck_expr <- paste0(
    "stack(",
    length(measure_vars),
    ", ",
    str_fun(measure_vars) %>% paste(collapse = ","),
    ") as (",
    paste(variable_name, value_name, sep = ", "),
    ")"
  )

  # Normalize passed object to DataFrame
  sdf <- sparklyr::spark_dataframe(df)

  # get the underlying connections
  sc <- sparklyr::spark_connection(sdf)

  # Transform to Long DataFrame
  sdf %>%
    sparklyr::invoke("selectExpr", c(as.list(id_vars), stck_expr)) %>%
    sparklyr::invoke("registerTempTable", "long")
  dplyr::tbl(sc, "long")
}
