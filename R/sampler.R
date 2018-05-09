


# Sampler -----------------------------------------------------------------


#'DataFrame Sampler Generic Function
#'
#'Function to sample dataset. Supports multiple methods for both spark and R
#'data.frames
#'
#'Grouping and weighting options only supporting in base R. Fraction and head
#'sampling methods valid for both R and Spark while n and tail only valid for R
#'
#'@param df DataFrame
#'@param group_vars optional grouping variable. Allows for stratified sampling
#'  for R data.frames only. default is NULL which applies no grouping
#'@param method sampling method to apply. Valid options include "fraction" and
#'  "head" for both R and Spark and additionally "n" and "tail" for R
#'@param size size of sampling. fraction of data for "fraction" method. number
#'  of records for other methods
#'@param replace logical option to allow for record replacement. valid option
#'  for "fraction" and "n" methods
#'@param weight optional option to apply sampling weights. see
#'  \url{https://dplyr.tidyverse.org/reference/sample.html} for more details.
#'  Default is NULL for no weighting. Only valid for R data.frames
#'@param seed option numerical input to set random seed. Default is NULL which
#'  does not set the random seed and results will vary
#'
#'@return Dataframe
#'@export
sampler <- function(df,
                    group_vars = NULL,
                    method,
                    size,
                    replace,
                    weight = NULL,
                    seed) {
  UseMethod("sampler")
}


#' @rdname sampler
#' @export
sampler.data.frame <- function(df,
                               group_vars = NULL,
                               method = "fraction",
                               size = 1.0,
                               replace = FALSE,
                               weight  = NULL,
                               seed = NULL) {
  checkmate::assert_choice(method, c("head", "tail", "fraction", "n"))
  checkmate::assert_flag(replace)
  checkmate::assert_number(seed, null.ok = TRUE)

  if (!is.null(seed))
    set.seed(seed)

  if (!is.null(group_vars)) {
    checkmate::assert_subset(group_vars, colnames(df))
    df <- dplyr::group_by_at(df, group_vars)
  }

  if (method == "fraction") {
    checkmate::assert_number(size, lower = 0, upper = 1)
    results <- dplyr::sample_frac(df,
                                  size = size,
                                  replace = replace,
                                  weight = weight)
  } else if (method == "n") {
    checkmate::assert_number(size, lower = 0, upper = Inf)
    results <- dplyr::sample_n(df,
                               size = size,
                               replace = replace,
                               weight = weight)
  } else {
    checkmate::assert_number(size, lower = 0, upper = nrow(df))
    fun <- match.fun(method)
    if ("grouped_df" %in% class(df)) {
      results <- df %>% dplyr::do(fun(., size))
    } else {
      results <- fun(df, size)
    }
  }

  if (!is.null(group_vars)) {
    results <- dplyr::ungroup(results)
  }

  results
}



#' @rdname sampler
#' @export
sampler.tbl_spark <- function(df,
                              group_vars = NULL,
                              method = "fraction",
                              size = 1.0,
                              replace = FALSE,
                              weight  = NULL,
                              seed = NULL) {
  checkmate::assert_choice(method, c("head", "fraction"))
  checkmate::assert_flag(replace)
  checkmate::assert_number(seed, null.ok = TRUE)
  if (!is.null(group_vars))
    message(
      "stratified sampling for spark data.frames not currently supported.
      \ngrouping variables not applied"
    )
  if (!is.null(weight))
    message(
      "weighted sampling for spark data.frames not currently supported.
      \nweighting not applied"
    )

  if (method == "fraction") {
    checkmate::assert_number(size, lower = 0, upper = 1)
    results <- sparklyr::sdf_sample(df,
                                    fraction = size,
                                    replacement = replace,
                                    seed = seed)
  } else {
    checkmate::assert_number(size, lower = 0, upper = Inf)
    results <- head(df, size)
  }

  results
}



# Collecter ---------------------------------------------------------------




#' DataFrame Collecter Function
#'
#' Collect Spark Dataframe into local R with options to sample data prior to
#' collecting.
#'
#' If sample option selected, arguments passed to sampler function for sampling
#' prior to collecting
#'
#' @param df Spark dataframe
#' @inheritParams sampler
#' @param sample logical option to apply sampling prior to collecting data into
#'   R. Default is FALSE which applies no sampling
#'
#' @return R data.frame
#' @export
collecter <- function(df,
                      sample = FALSE,
                      method = "fraction",
                      size = 1.0,
                      replace = FALSE,
                      seed = NULL) {
  checkmate::assert_subset("tbl_spark", class(df))
  checkmate::assert_flag(sample)

  if (sample) {
    do.call("sampler", modifyList(list(df = df), list(...))) %>%
      dplyr::collect()
  } else {
    dplyr::collect(df)
  }
}
