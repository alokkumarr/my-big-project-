
# Segmenter Test Cases ----------------------------------------------------


library(a2modeler)
library(testthat)
library(checkmate)
library(dplyr)
library(sparklyr)

context("segmenter unit tests")

sc <- spark_connect(master = "local")
df <- copy_to(sc, mtcars, name = "df", overwrite = TRUE)


test_that("Segmenter Constructer", {

  s1 <- new_segmenter(df = df, name = "test")
  expect_class(s1, "segmenter")
})



test_that("Segmenter Add Model", {

  s1 <- new_segmenter(df = df, name = "test") %>%
    add_model(pipe = NULL, method = "ml_kmeans")

  expect_equal(s1$models[[names(get_models(s1))]]$method, "ml_kmeans")
})



test_that("Segmenter Add Sampling", {

  s1 <- new_segmenter(df = df, name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = NULL, method = "ml_kmeans")
  s1_index <- get_indicies(s1)

  expect_equal(s1_index$holdout$train, 1:(floor(.8*nrow(mtcars))))
  expect_class(s1_index$holdout$train, "integer")
})


test_that("Segmenter Train Model", {

  s1 <- new_segmenter(df = df, name = "test") %>%
    add_default_samples() %>%
    add_model(pipe = NULL, method = "ml_kmeans") %>%
    train_models()

  expect_class(s1$models[[1]]$performance$train$train, "tbl_spark")
  expect_class(s1$models[[1]]$fit, "ml_model")
})



test_that("Segmenter Evaluate Model", {

  s1 <- new_segmenter(df = df, name = "test") %>%
    add_default_samples() %>%
    add_model(pipe = NULL, method = "ml_kmeans") %>%
    train_models() %>%
    evaluate_models()

  expect_class(s1$models[[1]]$performance$train$train, "tbl_spark")
  expect_class(s1$models[[1]]$fit, "ml_model")
})
