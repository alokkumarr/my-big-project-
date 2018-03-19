

#'DataFrame Correlation Function
#'
#'Function that calculates the pearson correlation for one or more pairs of
#'numerical data types.
#'
#'This function is used in the correlater function but can be used as stand
#'alone function.
#'
#'@param df DataFrame
#'@param target_var optional input to compute correlation for a single target
#'  column producing a 1xN vector of values. Default value is NULL which
#'  produces a NxN matrix of values.
#'@param target_var_name optional column name for id column. default is
#'  'target_variable'. this differs from stats::cor function which returns NxN
#'  matrix with column and row names. First column returned by corr is
#'  equivalent to rownames in cor function
#'
#'@return Dataframe of either 1xN vector or NxN martrix with pearson
#'  correlation values with additional rowname id column
#'@export
#'
#' @examples
#'library(dplyr)
#'corr(mtcars)
#'corr(mtcars, "mpg")
#'corr(mtcars, "mpg", "id_var")
corr <- function(df, ...) {
  UseMethod("corr", df)
}


#' @importFrom magrittr %>%
#' @importFrom stats cor
#' @rdname corr
#' @export
corr.data.frame <- function(df,
                            target_var = NULL,
                            target_var_name = "target_variable"){
  variables <- colnames(df)
  stopifnot(target_var %in% c(variables) | is.null(target_var))
  result <- cor(df) %>%
    as.data.frame(.) %>%
    dplyr::mutate(!!target_var_name := variables) %>%
    dplyr::select_at(c(target_var_name, variables))

  if(!is.null(target_var)){
    result %>%
      dplyr::filter_at(target_var_name, dplyr::any_vars(. == target_var))
  }else{
    result
  }
}




#' @importFrom magrittr %>%
#' @rdname corr
#' @export
corr.tbl_spark <- function(df,
                           target_var = NULL,
                           target_var_name = "target_variable") {
  variables <- colnames(df)
  stopifnot(target_var %in% c(variables) | is.null(target_var))

  if (!is.null(target_var)) {
    result <- df %>%
      dplyr::summarise_all(funs(cor(., !!rlang::sym(target_var)))) %>%
      dplyr::mutate(!!target_var_name := target_var)
  } else{
    i <- 1
    for (tv in variables) {
      sub_result <- df %>%
        dplyr::summarise_all(funs(cor(., !!rlang::sym(tv)))) %>%
        dplyr::mutate(!!target_var_name := tv)
      if (i == 1) {
        result <- sub_result
      } else{
        result <- dplyr::union_all(result, sub_result)
      }
      i <- i + 1
    }
  }

  dplyr::select_at(result, c(target_var_name, variables))
}





#'DataFrame Correlater function
#'
#'Function that calculates the pearson correlation for a dataset. Function
#'removes non-numerical columns, calculates pairwise pearson correlation values
#'for 1 or more columns, and transforms the results into long format.
#'
#'The function provides an option to transform the data prior to the correlation
#'calculation with either the standardizer or normalizer transformation
#'functions. The target_variable input allows the user to compute correlation
#'values for a single target variable, which can reduce unwanted or
#'un-neccessary computations.
#'@inheritParams corr
#'@param transform optional input to transform data prior to correlation
#'  calculation. Default is NULL (no transformation). Options are  'standardize'
#'  or 'normalize'
#'@param output_col_names colnames of output dataset. default is c("var1",
#'  "var2", "cor")
#'@param remove_diag optional binary T/F input for removing diagonal values from
#'  the correlation matrix
#'
#'@return Dataframe with pairwise correlation values in narrow, long format
#'@export
correlater <- function(df, ...) {
  UseMethod("correlater", df)
}



#' @importFrom magrittr %>%
#' @rdname correlater
#' @export
correlater.data.frame <- function(df,
                                  target_var = NULL,
                                  transform = NULL,
                                  output_col_names = c("var1", "var2", "cor"),
                                  remove_diag = FALSE) {
  # Select only numeric columns
  df <- df %>%
    dplyr::select_if(is.numeric)

  variables <- colnames(df)
  stopifnot(target_var %in% c(variables) | is.null(target_var))

  if (!is.null(transform)) {
    if (!transform %in% c("standardize", "standardizer", "normalize", "normalizer")) {
      stop(
        "transform input not a recognized function. currently only supporting 'standardize' transformation via stardardizer function or 'normalize' via normalizer."
      )
    } else if (transform %in% c("standardize", "standardizer")) {
      df <- standardizer(df, measure_vars = variables)
    } else if (transform %in% c("normalize", "normalizer")) {
      df <- normalizer(df, measure_vars = variables)
    }
  }


  result <- df %>%
    corr(target_var,
         output_col_names[1]) %>%
    melter(
      id_vars = output_col_names[1],
      measure_vars = variables,
      variable_name = output_col_names[2],
      value_name = output_col_names[3]
    )

  if (remove_diag) {
    result %>%
      dplyr::mutate_at(output_col_names[1],
                       dplyr::funs(match = ifelse(
                         . == !!rlang::sym(output_col_names[2]), 1, 0
                       ))) %>%
      dplyr::filter(match == 0) %>%
      dplyr::select(-match)
  } else{
    result
  }
}


#' @param collect logical option to collect transformed dataset into R prior to
#'   corr function. applies to Spark DataFrames only
#' @rdname correlater
#' @export
correlater.tbl_spark <- function(df,
                                 target_var = NULL,
                                 transform = NULL,
                                 output_col_names = c("var1", "var2", "cor"),
                                 remove_diag = FALSE,
                                 collect = TRUE){

  # Select only numeric columns
  df <- df %>%
    dplyr::select_if(is.numeric)

  variables <- colnames(df)
  stopifnot(target_var %in% c(variables) | is.null(target_var))

  if (!is.null(transform)) {
    if (!transform %in% c("standardize", "standardizer", "normalize", "normalizer")) {
      message(
        "transform input not a recognized function. currently only supporting 'standardize' transformation via stardardizer function or 'normalize' via normalizer. no transform applied on data"
      )
    } else if (transform %in% c("standardize", "standardizer")) {
      df <- standardizer(df, measure_vars = variables)
    } else if (transform %in% c("normalize", "normalizer")) {
      df <- normalizer(df, measure_vars = variables)
    }
  }

  if(collect){
    df <- df %>% dplyr::collect()
  }

  result <- df %>%
    corr(target_var,
         output_col_names[1]) %>%
    melter(
      id_vars = output_col_names[1],
      measure_vars = variables,
      variable_name = output_col_names[2],
      value_name = output_col_names[3]
    )

  if (remove_diag) {
    result %>%
      dplyr::mutate_at(output_col_names[1],
                       dplyr::funs(match = ifelse(
                         . == !!rlang::sym(output_col_names[2]), 1, 0
                       ))) %>%
      dplyr::filter(match == 0) %>%
      dplyr::select(-match)
  } else{
    result
  }
}


