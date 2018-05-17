

#' Dataframe Imputer
#'
#' Function to impute missing values from either Spark or R data.frames.
#'
#' Imputer fills missing values by either calculating the average value, most
#' frequent or some user defined constant
#'
#' @param df Spark or R data.frame
#' @param group_vars optional grouping variable input. Only valid for mean and
#'   mode impute functions. default is NULL which applies no grouping. note -
#'   mode function does not supporting grouping
#' @param measure_vars optional character vector of column names to apply impute
#'   function on. default is NULL which applies impute on all columns with
#'   missing values
#' @param fun impute function. valid options are mean, mode, or constant.
#'   default is mean
#' @param ... optional additional arguments to pass to impute function. ex-
#'   constant requires a fill input
#'
#' @return imputed Spark or R data.frame
#' @export
imputer <- function(df,
                    group_vars = NULL,
                    measure_vars = NULL,
                    fun = "mean",
                    ...) {
  checkmate::assert_choice(fun, c("mean", "mode", "constant"))
  checkmate::assert_subset(group_vars, colnames(df), empty.ok = TRUE)

  if(! is.null(group_vars) & fun != "constant") {
    df <- dplyr::group_by_at(df, group_vars)
  }

  impute_fun <- match.fun(paste("impute", fun, sep="_"))
  impute_args <- modifyList(list(df = df, measure_vars = measure_vars), list(...))
  do.call("impute_fun", impute_args)
}


#' Is NA
#'
#' Function determines if column has any missing values
#' @return logical
is_na <- function(x) any(is.na(x))



#' Get NA Column names from dataframe
#'
#' Function to get the column names for any columns with missing values.
#' Function works for both Spark and R dataframes
#'
#' @param df dataframe
#'
#' @return vector of column names
#' @export
na_cols <- function(df) {
  cols <- df %>%
    dplyr::summarise_all(funs(sum(as.numeric(is.na(.)), na.rm=TRUE))) %>%
    collect()
  colnames(cols)[cols > 0]
}



# impute mean -------------------------------------------------------------



#' Impute Mean
#'
#' Function to impute missing values with the average value. Applied columwise -
#' the average value imputed is the average for by column
#'
#' @inheritParams imputer
#'
#' @return imputed Spark or R data.frame
#' @export
impute_mean <- function(df, measure_vars) {
  checkmate::assert_true(any(class(df) %in% c("data.frame", "tbl_spark")))

  df_numeric_cols <- df %>%
    dplyr::select_if(is.numeric) %>%
    colnames()
  df_na_cols <- na_cols(df)

  if(is.null(measure_vars)){
    measure_vars <- intersect(df_numeric_cols, df_na_cols)
  } else {
    checkmate::assert_subset(measure_vars, df_numeric_cols)
  }

  df %>%
    dplyr::mutate_at(measure_vars,
                     dplyr::funs(ifelse(is.na(.), mean(., na.rm = TRUE), .)))
}


# impute constant ---------------------------------------------------------




#' Impute Constant
#'
#' Function to impute missing values with constant value provided
#'
#' The fill type will determine which columns the imputation is applied on.
#' Setting fill to 5 (a numeric type) will only impute missing values for
#' numberic columns
#'
#' @inheritParams imputer
#' @param fill constant value to fill missing values with
#'
#' @return imputed Spark or R data.frame
#' @export
impute_constant <- function(df,
                            measure_vars = NULL,
                            fill = 0) {
  checkmate::assert_true(any(class(df) %in% c("data.frame", "tbl_spark")))

  df_numeric_cols <- df %>%
    dplyr::select_if(is.numeric) %>%
    colnames()
  df_na_cols <- na_cols(df)
  numeric_cols <- intersect(df_numeric_cols, df_na_cols)
  char_cols <-
    intersect(setdiff(colnames(df), df_numeric_cols), df_na_cols)

  if (is.null(measure_vars)) {
    measure_vars <- df_na_cols
  }

  fill_type <- class(fill)
  if (fill_type %in% c("numeric", "integer")) {
    measure_vars <- intersect(numeric_cols, measure_vars)
  } else {
    measure_vars <- intersect(char_cols, measure_vars)
  }

  impute_constant_at(df, measure_vars, fill)
}



#' Impute Numeric Constant At
#'
#' Function imputes numeric constants for specific columns
impute_constant_at <- function(df, measure_vars, fill) {
  dplyr::mutate_at(df, measure_vars, funs(ifelse(is.na(.), fill, .)))
}



# impute mode -------------------------------------------------------------


#' Impute Mode
#'
#' Function to impute missing values with the most frequent values (mode). Applied columwise -
#' the most frequent value imputed is the by column
#'
#' impute mode does not support grouping currently
#'
#' @inheritParams imputer
#'
#' @return imputed Spark or R data.frame
#' @export
impute_mode <- function(df, measure_vars = NULL) {
  checkmate::assert_true(any(class(df) %in% c("data.frame", "tbl_spark")))
  checkmate::assert_character(group_vars(df), len = 0)
  df_na_cols <- na_cols(df)

  if(is.null(measure_vars)) {
    measure_vars <- df_na_cols
  } else {
    measure_vars <- intersect(measure_vars, df_na_cols)
  }

  for(mv in measure_vars) {
    fill <- df %>%
      dplyr::count(!! rlang::sym(mv), sort=TRUE) %>%
      dplyr::filter(! is.na(!! rlang::sym(mv))) %>%
      dplyr::filter(n == max(n, na.rm = TRUE)) %>%
      dplyr::collect() %>%
      dplyr::slice(1) %>%
      dplyr::select_at(mv)

    df <- impute_constant_at(df, mv, fill[[1]])
  }
  df
}


