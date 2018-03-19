
#'DataFrame Standarizer Data Transform Function
#'
#'Data transformation function to standarize one or more variables. The
#'standardize function transforms values by subtracting the mean and dividing by
#'the standard deviation of all values. The function is applied columnwise only.
#'The function only applies to numerical data types.
#'
#'The standardize function transforms values to be on a standard normal
#'distribution N(0, 1) scale [-Inf, Inf].
#'
#'@param df DataFrame
#'@param group_vars optional vector of column names. Applies transformation on
#'  grouped data. Can be zero or more columns. Default set to NULL (no grouping)
#'@param measure_vars one or more variable names. vector of strings. columns to
#'  apply transformation on
#'
#'@return Transformed Dataframe with the same dimensions of input dataframe
#'@export
#'
#' @examples
#'library(dplyr)
#'standardizer(mtcars, measure_vars = c("mpg", "hp"))
#'standardizer(mtcars, group_vars = "am", measure_vars = c("mpg", "hp"))
standardizer <- function(df, ...) {
  UseMethod("standardizer", df)
}


#' @importFrom stats sd
#' @importFrom magrittr %>%
#' @rdname standardizer
#' @export
standardizer.data.frame <- function(df,
                                    group_vars = NULL,
                                    measure_vars) {
  stopifnot(all(measure_vars %in% colnames(df)))
  if (!is.null(group_vars)) {
    df <- df %>%
      dplyr::group_by_at(group_vars)
  }

  results <- df %>%
    dplyr::mutate_at(measure_vars, dplyr::funs((. - mean(.)) / sd(.)))

  if (!is.null(group_vars)) {
    results %>% dplyr::ungroup()
  } else{
    results
  }
}


#' @importFrom magrittr %>%
#' @rdname standardizer
#' @export
standardizer.tbl_spark <- function(df,
                                   group_vars = NULL,
                                   measure_vars){

  stopifnot(all(measure_vars %in% colnames(df)))
  if (!is.null(group_vars)) {
    df <- df %>%
      dplyr::group_by_at(group_vars)
  }

  results <- df %>%
    dplyr::mutate_at(measure_vars, dplyr::funs((. - mean(.)) / sd(.)))

  if (!is.null(group_vars)) {
    results %>% dplyr::ungroup()
  } else{
    results
  }
}






#'DataFrame Normalizer Data Transform Function
#'
#'Data transformation function to normalize one or more variables. The normalize
#'function transforms values by subtracting the min and dividing by the max of
#'all values. The function is applied columnwise only. The function only applies
#'to numerical data types.
#'
#'The normalize function transforms values to a [0,1] range. The underlying
#'distribution is perserved.
#'
#'@inheritParams standardizer
#'
#'@return Transformed Dataframe with the same dimensions of input dataframe
#'@export
#'
#'@examples
#'library(dplyr)
#'normalizer(mtcars, measure_vars = c("mpg", "hp"))
#'normalizer(mtcars, group_vars = "am", measure_vars = c("mpg", "hp"))
normalizer <- function(df, ...) {
  UseMethod("normalizer", df)
}



#' @importFrom magrittr %>%
#' @rdname normalizer
#' @export
normalizer.data.frame <- function(df,
                                    group_vars = NULL,
                                    measure_vars) {
  stopifnot(all(measure_vars %in% colnames(df)))
  if (!is.null(group_vars)) {
    df <- df %>%
      dplyr::group_by_at(group_vars)
  }

  results <- df %>%
    dplyr::mutate_at(measure_vars, dplyr::funs((. - min(.))/max(.)))

  if (!is.null(group_vars)) {
    results %>% dplyr::ungroup()
  } else{
    results
  }
}


#' @importFrom magrittr %>%
#' @rdname normalizer
#' @export
normalizer.tbl_spark <- function(df,
                                   group_vars = NULL,
                                   measure_vars){

  stopifnot(all(measure_vars %in% colnames(df)))
  if (!is.null(group_vars)) {
    df <- df %>%
      dplyr::group_by_at(group_vars)
  }

  results <- df %>%
    dplyr::mutate_at(measure_vars, dplyr::funs((. - min(.))/max(.)))

  if (!is.null(group_vars)) {
    results %>% dplyr::ungroup()
  } else{
    results
  }
}

