

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
corr <- function(df, target_var, target_var_name, ...) {
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



#' @param table_name string input for spark table name created. Default is
#'   'corr'. Table is overwritten
#' @rdname corr
#' @export
corr.tbl_spark <- function(df,
                           target_var = NULL,
                           target_var_name = "target_variable",
                           table_name = "corr") {
  variables <- colnames(df)
  checkmate::assert_choice(target_var, variables, null.ok = TRUE)
  checkmate::assert_string(target_var_name)
  
  num_features <- length(variables)
  features_col <- sparklyr::random_string("features")
  result <- df %>%
    sparklyr::ft_vector_assembler(variables, features_col) %>%
    sparklyr::spark_dataframe() %>% 
    sparklyr::invoke_static(sparklyr::spark_connection(.),
                            "org.apache.spark.ml.stat.Correlation",
                            "corr", 
                            .,
                            features_col) %>%
    sparklyr::invoke("first") %>%
    sapply(invoke, "toArray") %>%
    matrix(nrow = num_features) %>%
    as.data.frame() %>%
    dplyr::rename(!!!rlang::set_names(paste0("V", seq_len(num_features)),
                                      variables)) %>% 
    dplyr::mutate(!!target_var_name := variables) %>% 
    dplyr::select_at(c(target_var_name, variables))
  
  if(!is.null(target_var)){
    result <- result %>%
      dplyr::filter_at(target_var_name, dplyr::any_vars(. == target_var))
  }
  
  dplyr::copy_to(spark_connection(df), result, table_name, overwrite = TRUE)
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
#'@param ... not currently implemented  
#'
#'@return Dataframe with pairwise correlation values in narrow, long format
#'@export
correlater <- function(df,
                       target_var,
                       transform,
                       output_col_names,
                       remove_diag,
                       collect,
                       ...) {
  UseMethod("correlater")
}



#' @importFrom magrittr %>%
#' @rdname correlater
#' @export
correlater.data.frame <- function(df,
                                  target_var = NULL,
                                  transform = NULL,
                                  output_col_names = c("var1", "var2", "cor"),
                                  remove_diag = FALSE,
                                  ...) {

  checkmate::assert_choice(transform,
                           c("standardize", "standardizer", "normalize", "normalizer"),
                           null.ok = TRUE)
  checkmate::assert_character(output_col_names, min.len = 3, max.len = 3)
  checkmate::assert_flag(remove_diag)

  # Select only numeric columns
  df <- df %>%
    dplyr::select_if(is.numeric)
  variables <- colnames(df)
  checkmate::assert_choice(target_var, variables, null.ok = TRUE)

  if(! is.null(transform)) {
    if (transform %in% c("standardize", "standardizer")) {
      df <- standardizer(df, measure_vars = variables)
    }
    if (transform %in% c("normalize", "normalizer")) {
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
                                 collect = TRUE,
                                 ...){

  # Select only numeric columns
  df <- df %>%
    dplyr::select_if(is.numeric)

  variables <- colnames(df)
  checkmate::assert_choice(target_var, variables, null.ok = TRUE)
  checkmate::assert_choice(transform,
                           c("standardize", "standardizer", "normalize", "normalizer"),
                           null.ok = TRUE)
  checkmate::assert_character(output_col_names, min.len = 3, max.len = 3)
  checkmate::assert_flag(remove_diag)
  checkmate::assert_flag(collect)



  if(! is.null(transform)) {
    if (transform %in% c("standardize", "standardizer")) {
      df <- standardizer(df, measure_vars = variables)
    }
    if (transform %in% c("normalize", "normalizer")) {
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


