

#' Dataframe Imputer
#'
#' Function to impute missing values from either Spark or R data.frames.
#'
#' Imputer fills missing values by either calculating the average value, most
#' frequent or some user defined constant
#'
#' @param df Spark or R data.frame
#' @param group_vars optional grouping variable input. Only valid for mean and
#'   mode impute functions. default is NULL which applies no grouping
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

  if (!is.null(group_vars) & fun != "constant") {
    df <- group_by_at(df, group_vars)
  }

  impute_fun <- match.fun(paste("impute", fun, sep = "_"))
  impute_args <- modifyList(list(df = df, measure_vars = measure_vars), list(...))
  results <- do.call("impute_fun", impute_args)

  if (!is.null(group_vars) & fun != "constant") {
    results <- dplyr::ungroup(results)
  }

  results
}


#' Is NA
#'
#' Function determines if column has any missing values
#' @return logical
is_na <- function(x) any(is.na(x))


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
impute_mean <- function(df, measure_vars = NULL) {
  checkmate::assert_true(any(class(df) %in% c("data.frame", "tbl_spark")))

  if(is.null(measure_vars)){
    measure_vars <- df %>%
      select_if(is_na) %>%
      dplyr::select_if(is.numeric) %>%
      colnames()
  }

  impute_mean_at(df, measure_vars)
}


#' Impute Mean At
#'
#' Internal impute mean function
impute_mean_at <- function(df, measure_vars) {
  checkmate::assert_true(any(class(df) %in% c("data.frame", "tbl_spark")))
  numeric_vars <- df %>%
    dplyr::select_if(is.numeric) %>%
    colnames()

  checkmate::assert_subset(measure_vars, numeric_vars)

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
  if (is.null(measure_vars)) {
    measure_vars <- df %>%
      select_if(is_na) %>%
      colnames()
  }
  fill_type <- class(fill)
  numeric_vars <- df %>%
    dplyr::select_at(measure_vars) %>%
    dplyr::select_if(is.numeric) %>%
    colnames()

  if (fill_type %in% c("numeric", "integer")) {
    if (length(numeric_vars) == 0)
      message("numeric fill provided but no numeric measure variables provided")
    impute_constant_at_num(df, numeric_vars, fill)
  } else {
    char_vars <- setdiff(measure_vars, numeric_vars)
    if (length(char_vars) == 0)
      message("character fill provided but no character measure variables provided")
    impute_constant_at_chr(df, char_vars, fill)
  }
}


#' Impute Numeric Constant At
#'
#' Function imputes numeric constants for numeric columns
impute_constant_at_num <- function(df, measure_vars, fill) {
  checkmate::assert_subset(measure_vars, colnames(df))
  checkmate::assert_number(fill)
  df %>%
    dplyr::mutate_at(measure_vars, funs(ifelse(is.na(.), fill, .)))
}


#' Impute Character Constant At
#'
#' Function imputes character constants for numeric columns
impute_constant_at_chr <- function(df, measure_vars, fill) {
  checkmate::assert_subset(measure_vars, colnames(df))
  checkmate::assert_character(fill)

  df %>%
    dplyr::mutate_at(measure_vars, funs(ifelse(is.na(.), fill, .)))
}


#
# impute_constant_at_chr <- function(df, measure_vars, fill) {
#   UseMethod("impute_constant_at_chr")
# }
#
# impute_constant_at_chr.data.frame <- function(df, measure_vars, fill) {
#   checkmate::assert_subset(measure_vars, colnames(df))
#   checkmate::assert_character(fill)
#
#   df %>%
#     dplyr::mutate_at(measure_vars, funs(ifelse(is.na(.), fill, .)))
# }
#
#
# impute_constant_at_chr.tbl_spark <- function(df, measure_vars, fill) {
#   checkmate::assert_subset(measure_vars, colnames(df))
#   checkmate::assert_character(fill)
#
#   df %>%
#     dplyr::mutate_at(measure_vars, funs(ifelse(. == "NA", fill, .)))
# }




# impute mode -------------------------------------------------------------


#' Impute Mode
#'
#' Function to impute missing values with the most frequent values (mode). Applied columwise -
#' the most frequent value imputed is the by column
#'
#' @inheritParams imputer
#'
#' @return imputed Spark or R data.frame
#' @export
impute_mode <- function(df, measure_vars = NULL) {
  checkmate::assert_true(any(class(df) %in% c("data.frame", "tbl_spark")))
  if(is.null(measure_vars)) {
    measure_vars <- df %>%
      select_if(is_na) %>%
      colnames()
  }

  impute_mode_at(df, measure_vars)
}


#' Impute Mode At
#'
#' Internal function to impute Mode at specific columns
impute_mode_at <- function(df, measure_vars) {
  checkmate::assert_true(any(class(df) %in% c("data.frame", "tbl_spark")))
  checkmate::assert_subset(measure_vars, colnames(df))
  for(mv in measure_vars) {
    fill <- df %>%
      count(!! rlang::sym(mv), sort=TRUE) %>%
      na.omit() %>%
      head(1) %>%
      select_at(mv) %>%
      collect()
    df <- impute_constant(df, mv, fill[[1]])
  }
  df
}


