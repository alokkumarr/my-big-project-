
#' Dataframe Expander Function
#'
#' Function to expand dataset by converting implicit missing values to explict
#' values
#'
#' @param df dataframe
#' @param group_vars optional vector column names to group data by. default is
#'   NULL. Any expansion function will be nested within the grouping vars
#' @param id_vars optional vector of colnames to expand data by. default it
#'   NULL.
#' @param fun optional expansion function. useful for expanding continuous
#'   variables with values that don't appear in the data (like missing dates).
#'   fun argument should be wrapped in `funs(...)` function and should assign
#'   the output to a valid column name  `fun = funs(name = ...)` see examples
#'   below
#' @param mode expansion mode for id_vars arguments. `nesting`` mode returns all
#'   unique combinations of id_vars seen in the data. `crossing` mode returns
#'   all combinations of the unique values of id_vars. `crossing` mode similar
#'   to `expand.grid` in base R
#' @param complete logical option to return original data `right_join` to
#'   expanded dataset. FALSE returns just expanded grid. Default is TRUE
#'
#' @seealso \url{http://tidyr.tidyverse.org/reference/expand.html} for tidyr
#'   `expand` function which is used internally by `expander`
#'
#' @return dataframe
#' @export
#' @importFrom tidyr full_seq expand crossing nesting complete
#' @importFrom dplyr funs
#'
#' @examples
#' library(tidyr)
#' library(dplyr)
#' expander(mtcars, id_vars = c('am', 'cyl'), complete = FALSE)
#' expander(mtcars, id_vars = c('am', 'cyl'), fun = funs(carb = 1:5), complete = TRUE)
#' expander(mtcars, id_vars = c('am'), fun = funs(cyl = full_seq(cyl, 1)), complete = TRUE)
expander <- function(df, group_vars, id_vars, fun, mode, complete) {
  UseMethod("expander")
}


#' @export
#' @rdname expander
expander.data.frame <- function(df,
                                group_vars = NULL,
                                id_vars = NULL,
                                fun = NULL,
                                mode = "crossing",
                                complete = TRUE) {
  df_names <- colnames(df)
  checkmate::assert_subset(group_vars, df_names, empty.ok = TRUE)
  checkmate::assert_subset(id_vars, df_names, empty.ok = TRUE)
  checkmate::assert_class(fun, "fun_list", null.ok = TRUE)
  checkmate::assert_choice(mode, c("crossing", "nesting"))
  checkmate::assert_logical(complete)

  if (!is.null(group_vars)) {
    df <- df %>% dplyr::group_by_at(group_vars)
  }
  mode_fun <- get(mode, asNamespace("tidyr"))

  if (complete) {
    results <- df %>%
      tidyr::complete(., mode_fun(!!!rlang::syms(id_vars)),!!!fun)

  } else {
    results <-  df %>%
      tidyr::expand(., mode_fun(!!!rlang::syms(id_vars)),!!!fun)
  }

  if (!is.null(group_vars)) {
    results %>% dplyr::ungroup()
  } else {
    results
  }
}


#' @export
#' @rdname expander
expander.tbl_spark <- function(df,
                               group_vars = NULL,
                               id_vars = NULL,
                               fun = NULL,
                               mode = "crossing",
                               complete = TRUE) {

  df_names <- colnames(df)
  checkmate::assert_subset(group_vars, df_names, empty.ok = TRUE)
  checkmate::assert_subset(id_vars, df_names, empty.ok = TRUE)
  checkmate::assert_class(fun, "fun_list", null.ok = TRUE)
  checkmate::assert_choice(mode, c("crossing", "nesting"))
  checkmate::assert_logical(complete)

  sdf <-  sparklyr::spark_dataframe(df)
  sc <- sparklyr::spark_connection(sdf)

  if (!is.null(group_vars)) {
    df <- df %>% dplyr::group_by_at(group_vars)
  }
  mode_fun <- get(mode, asNamespace("tidyr"))

  dfr <- df %>%
    dplyr::distinct(!!!rlang::syms(c(id_vars, names(fun)))) %>%
    collecter(sample = FALSE) %>%
    tidyr::expand(., mode_fun(!!!rlang::syms(id_vars)),!!!fun) %>%
    dplyr::ungroup()

  if (!is.null(group_vars)) {
    df <- df %>% dplyr::ungroup()
  }

  # Workaround for date class copy_to issue
  date_vars <- dfr %>%
    dplyr::summarise_all(class) %>%
    dplyr::select_if(funs(. == "Date")) %>%
    colnames()
  dfr <- dfr %>%
    dplyr::mutate_at(date_vars, as.character)


  if(complete) {
   results <- df %>%
      dplyr::left_join(
        dplyr::copy_to(sc, dfr, overwrite = TRUE),
        by = intersect(df_names, colnames(dfr))
          )
  } else {
    results <- dplyr::copy_to(sc, dfr, overwrite = TRUE)
  }

  # Workaround for date class copy_to issue
  results <- results %>%
    mutate_at(date_vars, funs(to_date(.))) %>%
    dplyr::compute("expand")

  dplyr::db_drop_table(sc, "dfr")
  results
}


