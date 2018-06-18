
# Segmenter Test Cases ----------------------------------------------------


library(a2modeler)
library(testthat)
library(checkmate)
library(dplyr)
library(sparklyr)

context("segmenter unit tests")

spark_home_set(path = "C:\\Users\\chaa0001\\AppData\\Local\\spark\\spark-2.3.0-bin-hadoop2.7")
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
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 3)
  s1_index <- get_indicies(s1)

  expect_equal(s1_index$holdout$train, 1:(floor(.8*nrow(mtcars))))
  expect_class(s1_index$holdout$train, "integer")
})


test_that("Segmenter Train Model", {

  s1 <- new_segmenter(df = df, name = "test") %>%
    add_default_samples() %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 3) %>%
    train_models()

  expect_class(s1$models[[1]]$performance$train$train, "tbl_spark")
  expect_class(s1$models[[1]]$fit, "ml_model")
})


test_that("Segmenter Evaluate Model", {

  s1 <- new_segmenter(df = df, name = "test") %>%
    add_default_samples() %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 3) %>%
    train_models() %>%
    evaluate_models()

  expect_subset("data.frame", class(s1$evaluate))
  expect_equal(nrow(s1$evaluate), 1)
  expact_subset("silhouette", colnames(s1$evaluate))
})


test_that("Segmenter Selects Best Model", {

  s1 <- new_segmenter(df = df, name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 3) %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 4) %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  expect_subset("spark_ml", class(s1$final_model))
  expect_equal(
    s1$evaluate %>%
      top_n(1, silhouette) %>%
      pull(model),
    s1$final_model$id
  )
})



test_that("Segmenter Predicts New Data", {

  s1 <- new_segmenter(df = df, name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 3) %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 4) %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = TRUE)

  p1 <- predict(s1, data = df)

  expect_class(p1, "predictions")
})
