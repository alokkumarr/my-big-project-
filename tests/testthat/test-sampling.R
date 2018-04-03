
# Sampling Uint Tests -----------------------------------------------------


library(testthat)
library(checkmate)
library(a2modeler)
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
