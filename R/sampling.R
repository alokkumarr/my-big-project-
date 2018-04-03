


# Samples Object ----------------------------------------------------------

new_samples <- function(train, validation, test)





# Sampling Objects --------------------------------------------------------

#' @export
add_validation_sample <- function(...){
  UseMethod("add_validation_sample", ...)
}

new_validation_sample <- function(method, ...){

  assert_character(method, null.ok = TRUE)

  structure(
    list(method = method, ...),
    class = "validation_sample"
  )
}

valid_validation_sample <- function(x){

  assert_choice(x$method, choices = c("holdout", "rsample", "cv"), null.ok = TRUE)
  x
}


validation_sample <- function(method, ...){
  valid_validation_sample(
    new_validation_sample(method, ...)
  )
}


#' @export
add_test_sample <- function(...){
  UseMethod("add_validation_sample", ...)
}


new_test_sample <- function(method, ...){

  assert_character(method, null.ok = TRUE)

  structure(
    list(method = method, ...),
    class = "test_sample"
  )
}

valid_test_sample <- function(x){

  assert_choice(x$method, choices = c("holdout", "rsample"), null.ok = TRUE)
  x
}

test_sample <- function(method, ...){
  valid_test_sample(
    new_test_sample(method, ...)
  )
}


set_validation_sample <- function(...){
  UseMethod("set_validation_sample", ...)
}


set_test_sample <- function(...){
  UseMethod("set_test_sample", ...)
}



#
# new_sampling <- function(validation_method,
#                          validation_args,
#                          test_method,
#                          test_args){
#
#   structure(
#     list(
#       validation = validation_sample(validation_method, validation_args),
#       test = test_sample(test_method, test_args)
#     )
#     , class = "sampling"
#   )
# }
#
# valid_sampling <- function(x){
#   x
# }
#
# sampling <- function(){
#   valid_sampling(
#     new_sampling()
#   )
# }

# Sampling Functions ------------------------------------------------------


#' Holdout Sampling function
#'
#' Function creates two row indicies from a dataset based on split parameter.
#' Indicies used to create data samples
#'
#' @param split numeric input for percentage of rows in head index
#' @export
holdout <- function(x, ...) {
  UseMethod("holdout", x)
}


#' @inheritParams holdout
#' @param df data.frame
#' @rdname holdout
#' @export
holdout.data.frame <- function(df, split){

  assert_numeric(split, lower = 0, upper= 1)

  n <- nrow(df)
  h <- floor(n * split)
  list(head = 1:h, tail = (h+1):n)
}


#' @inheritParams holdout
#' @param x numeric vector
#' @rdname holdout
#' @export
holdout.numeric <- function(x, split){

  assert_number(split, lower = 0, upper= 1)

  n <- length(x)
  h <- floor(n * split)
  list(head = 1:h, tail = (h+1):n)
}


#' @inheritParams holdout
#' @param df spark dataframe
#' @rdname holdout
#' @export
holdout.tbl_spark <- function(df, split){

  assert_numeric(split, lower = 0, upper= 1)

  n <- sdf_nrow(df)
  h <- floor(n * split)
  list(head = 1:h, tail = (h+1):n)
}


#' Random Sample function
#'
#' Function creates a random sample index from a dataset. Index can be used to
#' create data samples
#'
#' @param amount percent of data to randomly sample
#' @param seed optional input for setting random seed
#' @export
rsample <- function(x, ...) {
  UseMethod("rsample", x)
}


#' @inheritParams rsample
#' @rdname rsample
#' @export
rsample.data.frame <- function(df, amount, seed = NULL){

  assert_number(amount, lower = 0, upper= 1)
  assert_number(seed, lower = 0, null.ok = TRUE)

  set.seed(seed)
  n <- nrow(df)
  s <- floor(n*amount)
  index <- sample(1:n, s, replace = FALSE)
  list(sample = index)
}

#' @inheritParams rsample
#' @rdname rsample
#' @export
rsample.numeric <- function(x, amount, seed = NULL){

  assert_number(amount, lower = 0, upper= 1)
  assert_number(seed, lower = 0, null.ok = TRUE)

  set.seed(seed)
  n <- length(x)
  s <- floor(n*amount)
  index <- sample(1:n, s, replace = FALSE)
  list(sample = index)
}


#' @inheritParams rsample
#' @rdname rsample
#' @export
rsample.tbl_spark <- function(df, amount, seed = NULL){

  assert_number(amount, lower = 0, upper= 1)
  assert_number(seed, lower = 0, null.ok = TRUE)

  set.seed(seed)
  n <- sdf_nrow(df)
  s <- floor(n*amount)
  index <- sample(1:n, s, replace = FALSE)
  list(sample = index)
}
