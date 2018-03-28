

#'DataFrame Roller Function
#'
#'Function appends rolling calculated fields to dataframe. Allows for grouping
#'and ordering calculations.
#'
#'
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
#'@param width size of rolling window. Window is aligned right and to current
#'  row only
#'@param ... additional arguments to pass to the transformation function
#'
#'@return DataFrame with additional calculated columns appended
#'@export
#'
#' @examples
#' library(dplyr)
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
#'d1 <- dat %>% roller(.,
#' order_vars = "date",
#' group_vars = c("id", "cat1", "cat2"),
#' measure_vars = c("metric1"),
#' fun = "mean",
#' width=5)
roller <- function(df, ...) {
  UseMethod("roller", df)
}


#'@param by sequence that the rolling calculation should be applied to. Ex - by
#'  of 2 would compute the rolling calculation for every other record. Only
#'  enabled for roller.data.frame method. Default is 1.
#'@param partial logical argument if partial windows should be calculated. Only
#'  enabled for roller.data.frame. Default is TRUE.
#'@rdname roller
#'@export
roller.data.frame <- function(df,
                              order_vars,
                              group_vars = NULL,
                              measure_vars,
                              fun,
                              width,
                              by = 1,
                              partial = TRUE,
                              ...) {
  args <- roller_args(order_vars,
                      group_vars,
                      measure_vars,
                      fun,
                      width,
                      by,
                      partial,
                      ...)


  order_vars <- args$order_vars
  group_vars <- args$group_vars
  measure_vars <- args$measure_vars
  fun <- args$fun
  width <- args$width
  by <- args$by
  partial <- args$partial


  .fun <- fun
  fname <- names(.fun) <- fun

  if (!is.null(group_vars)) {
    df <- df %>% dplyr::group_by_at(group_vars)
  }

  if (!is.null(order_vars)) {
    df <- df %>% dplyr::arrange_at(order_vars)
  }

  df2 <- df %>%
    dplyr::mutate_at(
      measure_vars,
      dplyr::funs(var = zoo::rollapply),
      width = width,
      by = by,
      align = "right",
      FUN = .fun,
      fill = NA,
      partial = partial,
      ...
    )


  if (!is.null(group_vars)) {
    df2 <- df2 %>% dplyr::ungroup()
  }

  # Rename new measure variables
  new_measure_vars <- setdiff(colnames(df2), colnames(df))
  df2 <- dplyr::rename_(df2, .dots = setNames(new_measure_vars,
                                              paste(
                                                measure_vars, paste0(width, "w"), fname, sep = "_"
                                              )))

  df2
}



#' @rdname roller
#' @export
roller.tbl_spark <- function(df,
                             order_vars,
                             group_vars = NULL,
                             measure_vars,
                             fun,
                             width,
                             ...) {

  stopifnot("tbl_spark" %in% class(df))
  sdf <-  sparklyr::spark_dataframe(df)
  sc <- sparklyr::spark_connection(sdf)
  sdf %>% sparklyr::invoke("createOrReplaceTempView", "df")
  new_tbl_name <- "df_roll"

  args <- roller_args(
    order_vars = order_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = fun,
    width = width,
    by = 1,
    partial = FALSE,
    ...
  )

  order_vars <- args$order_vars
  group_vars <- args$group_vars
  measure_vars <- args$measure_vars
  fun <- args$fun
  width <- args$width

  query <- paste("CREATE OR REPLACE TEMP VIEW", new_tbl_name, "as SELECT",
                 paste(colnames(df), collapse=", "))
  for(var in measure_vars){
    query <- paste0(query, ", ", sql_over_translator(var, fun, group_vars, order_vars, width))
  }
  query <- paste(query, "FROM df")

  DBI::dbSendQuery(sc, query)
  dplyr::tbl(sc, new_tbl_name)
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
                            partial,
                            ...) {
  stopifnot(is.character(order_vars) | is.null(order_vars))
  stopifnot(is.character(group_vars) | is.null(group_vars))
  stopifnot(is.character(measure_vars) | is.null(measure_vars))
  stopifnot(is.character(fun))
  stopifnot(is.numeric(by))
  stopifnot(is.numeric(width))
  stopifnot(is.logical(partial))

  structure(
    list(
      order_vars = order_vars,
      group_vars = group_vars,
      measure_vars = measure_vars,
      fun = fun,
      width = width,
      by = by,
      partial = partial,
      ...
    ),
    class = "roller_args"
  )
}



#' Roller Arguments Validation Function
#'
#' Checks for valid inputs to roller_args class
#'
#' @param x obj of class roller_args
validate_roller_args <- function(x) {
  funs <- c(
    "n_distinct",
    "min",
    "max",
    "sum",
    "mean",
    "var",
    "variance",
    "sd",
    "stddev",
    "kurtosis",
    "skewness"
  )
  if (!all(x$fun %in% funs)) {
    stop(
      "Supplied function not supported.\nPlease use one of following: ",
      paste(funs, collapse = ", ")
    )
  }
  if (is.null(x$measure_vars)) {
    stop(
      "Measure_vars not specified.\nNeed to supply one valid column name to apply function to\n",
      .call = FALSE
    )
  }
  if (is.null(x$order_vars)) {
    message(
      "Order var not specified. Rolling function applied on the dataframe in its current order"
    )
  }
  if (x$by < 1) {
    stop("by input should be >= 1", .call = FALSE)
  }
  if (x$width < 1) {
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
roller_args <- function(order_vars,
                        group_vars,
                        measure_vars,
                        fun,
                        width,
                        by,
                        partial,
                        ...) {
  new_roller_args(order_vars,
                  group_vars,
                  measure_vars,
                  fun,
                  width,
                  by,
                  partial,
                  ...) %>%
    validate_roller_args()
}



#' Aggregation Function SQL Translation
#'
#' Helper function to translate native r function to spark sql syntax
sql_fun_translator <- function(fun) {
  ifelse(fun == "mean", "avg",
         ifelse(fun == "sd", "stddev",
                ifelse(fun == "var", "variance", fun)))
}


#' OVER SQL Translation funcion
#'
#' Helper function to create OVER sql statement
sql_over_translator <- function(var,
                                fun,
                                group_vars,
                                order_vars,
                                width,
                                new_name = NULL) {
  paste0(
    sql_fun_translator(fun),
    "(",
    var,
    ") OVER (",
    ifelse(is.null(group_vars), "", paste(
      "PARTITION BY", paste0(group_vars, collapse = ", ")
    )),
    ifelse(is.null(order_vars), "", paste(
      " ORDER BY", paste0(order_vars, collapse = ", ")
    )),
    " ROWS BETWEEN ",
    width - 1,
    " PRECEDING and CURRENT ROW)",
    " AS ",
    ifelse(
      is.null(new_name),
      paste(var, paste0(width, "w"), fun, sep = "_"),
      new_name
    )
  )
}





#
# roller.tbl_spark_dep <- function(df,
#                              order_vars,
#                              group_vars,
#                              measure_vars,
#                              fun,
#                              width,
#                              by,
#                              partial = FALSE,
#                              mode = "summarise",
#                              ...) {
#   args <- roller_args(
#     order_vars = order_vars,
#     group_vars = group_vars,
#     measure_vars = measure_vars,
#     fun = fun,
#     width = width,
#     by = by,
#     partial = partial,
#     mode = mode,
#     ...
#   )
#
#   # Define index variables
#   indx_var <- paste(order_vars[1], "rn", sep = "_")
#   indx_grp_var <- paste(indx_var, "grp", sep = "_")
#
#   # Add row numbers
#   df2 <- df %>%
#     mutater(
#       .,
#       order_vars = args$order_vars,
#       group_vars = args$group_vars,
#       measure_vars = args$order_vars[1],
#       funs(rn = row_number())
#     )
#
#   # Define groupings based on by parameter
#   df3 <- df2 %>%
#     mutater(.,
#             measure_vars = indx_var,
#             fun = funs(grp = args$by * ceil(. / args$by))) %>%
#     # create lags for the width
#     lagger(
#       .,
#       order_vars = args$order_vars[1],
#       group_vars = args$group_vars,
#       measure_vars = indx_var,
#       lags = 1:(args$width - 1)
#     ) %>%
#     # filter to max order var for each group
#     dplyr::group_by_at(indx_grp_var) %>%
#     dplyr::filter_at(indx_var, any_vars(. == max(.))) %>%
#     dplyr::filter_at(indx_var, any_vars(. >= ifelse(partial, 1, args$width))) %>%
#     # convert lag values to long format
#     melter(
#       .,
#       id_vars = c(args$group_vars, indx_grp_var),
#       measure_vars = c(indx_var, paste(indx_var, paste0(
#         "lag", 1:(args$width - 1)
#       ), sep =
#         "_")),
#       value_name = indx_var
#     ) %>%
#     # join back to df and summarise
#     dplyr::inner_join(.,
#                       df2,
#                       by = c(args$group_vars, indx_var)) %>%
#     summariser(
#       .,
#       group_vars = c(args$group_vars, indx_grp_var),
#       measure_vars = args$measure_vars,
#       fun = args$fun
#     )
#
#   # Rename index group varible
#   df3 <- df3 %>%
#     dplyr::select_(.dots = stats::setNames(colnames(df3),
#                                            c(
#                                              args$group_vars,
#                                              indx_var,
#                                              paste(args$measure_vars,
#                                                    args$fun,
#                                                    sep = "_")
#                                            )))
#
#   # Format output
#   if (mode == "mutate") {
#     df4 <- df3 %>%
#       dplyr::right_join(df2,
#                         by = c(args$group_vars, indx_var)) %>%
#       dplyr::select_at(c(
#         args$order_vars,
#         args$group_vars,
#         args$measure_vars,
#         paste(args$measure_vars, args$fun, sep = "_")
#       ))
#   }
#   if (mode == "summarise") {
#     df4 <- df3 %>%
#       dplyr::inner_join(df2,
#                         by = c(args$group_vars, indx_var)) %>%
#       dplyr::select_at(c(
#         args$order_vars,
#         args$group_vars,
#         paste(args$measure_vars, args$fun, sep = "_")
#       ))
#   }
#
#   df4
# }
