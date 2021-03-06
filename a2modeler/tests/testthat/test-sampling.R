
# Sampling Uint Tests -----------------------------------------------------


library(a2modeler)
library(testthat)
library(checkmate)
library(sparklyr)
library(dplyr)

context("sampling class unit tests")



test_that("holdout sampling functions work as expected", {
  n <- 100
  x <- 1:n
  df <- data.frame(x = x)
  splits1 <- c(.6, .4)
  s1x <- add_holdout_samples(x, splits1)
  s1df <- add_holdout_samples(df, splits1)

  splits2 <- c(.6, .2, .2)
  s2x <- add_holdout_samples(x, splits2)
  s2df <- add_holdout_samples(df, splits2)

  expect_equal(s1x, s1df)
  expect_equal(s2x, s2df)
  expect_equal(get_train_samples(s1x), get_train_samples(s2x))
  expect_equal(length(get_train_samples(s1x)$holdout), n * splits1[1])
  expect_equal(length(get_validation_samples(s1x)$holdout), n * splits1[2])
  expect_equal(length(get_test_samples(s2x)), n * splits2[3])
  expect_null(get_test_samples(s1x))
})




test_that("resampling functions work as expected", {
  n <- 100
  x <- 1:n
  df <- data.frame(x = x)
  amt <- .5
  num <- 5
  seed = 319
  thp <- .5

  s1x <- add_resample_samples(x, number = num, amount = amt, seed = seed)
  s1df <- add_resample_samples(df, number = num, amount = amt, seed = seed)

  s2x <- add_resample_samples(x, number = num, amount = amt, test_holdout_prct = thp, seed = seed)
  s2df <- add_resample_samples(x, number = num, amount = amt, test_holdout_prct = thp, seed = seed)

  expect_equal(s1x, s1df)
  expect_equal(s2x, s2df)
  expect_equal(length(get_train_samples(s1x)), num)
  expect_equal(length(get_train_samples(s1x)[[1]]), n*amt)
  expect_equal(length(get_train_samples(s1x)[[1]]), length(get_validation_samples(s1x)[[1]]))
  expect_equal(length(get_train_samples(s1x)[[1]]), length(get_train_samples(s1x)[[2]]))
  expect_null(get_test_samples(s1x))
  expect_equal(length(get_train_samples(s2x)[[1]]), n*amt*thp)
  expect_equal(length(get_validation_samples(s2x)[[1]]), n*amt*thp)
})




test_that("time_slices functions work as expected", {
  n <- 10
  x <- 1:n
  df <- data.frame(x = x)
  width <- 5
  horizon <- 1
  skip <- 0

  s1x <- add_time_slice_samples(x, width, horizon, skip)
  s1df <- add_time_slice_samples(df, width, horizon, skip)

  s2x <- add_time_slice_samples(x, width, horizon, skip)
  s2df <- add_time_slice_samples(df, width, horizon, skip)

  s3x <- add_time_slice_samples(x, width, horizon, skip, fixed_width=FALSE)
  s3df <- add_time_slice_samples(x, width, horizon, skip, fixed_width=FALSE)

  expect_equal(s1x, s1df)
  expect_equal(s2x, s2df)
  expect_equal(s3x, s3df)
  expect_equal(length(get_train_samples(s1x)), n-width)
  expect_equal(length(get_train_samples(s1x)[[1]]), width)
  expect_equal(length(get_validation_samples(s1x)[[1]]), horizon)
  expect_equal(length(get_train_samples(s1x)[[1]]), length(get_train_samples(s1x)[[2]]))
  expect_null(get_test_samples(s1x))
  expect_true(all(unlist(lapply(s3x$train_indicies, function(x) x[1])) == 1))
})





test_that("cross_validation functions work as expected", {
  n <- 20
  x <- 1:n
  df <- data.frame(x = x)
  folds <- 4

  s1x <- add_cross_validation_samples(x, folds, test_holdout_prct = NULL)
  s1df <- add_cross_validation_samples(df, folds, test_holdout_prct = NULL)


  expect_equal(s1x, s1df)
  expect_equal(length(get_train_samples(s1x)), folds)
  expect_equal(length(get_train_samples(s1x)[[1]]), n - n/folds)
  expect_equal(length(get_train_samples(s1x)[[1]]), length(get_train_samples(s1x)[[2]]))
  expect_null(get_test_samples(s1x))

  n <- 25
  x <- 1:n
  df <- data.frame(x = x)
  folds <- 5

  s2x <- add_cross_validation_samples(x, folds, test_holdout_prct = .2, seed = 319)
  s2df <- add_cross_validation_samples(df, folds, test_holdout_prct = .2, seed = 319)


  expect_equal(s2x, s2df)
  expect_equal(length(get_train_samples(s2x)), folds)
  expect_equal(length(get_train_samples(s2x)[[1]]), (n-n *.2) - (n- n *.2)/folds)
  expect_equal(length(get_train_samples(s2x)[[1]]), length(get_train_samples(s2x)[[2]]))
  expect_equal(length(get_test_samples(s2x)), n*.2)

})
