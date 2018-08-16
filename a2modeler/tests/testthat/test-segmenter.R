
# Segmenter Test Cases ----------------------------------------------------


library(a2modeler)
library(testthat)
library(checkmate)
library(dplyr)
library(sparklyr)

context("segmenter unit tests")

# Create Spark Connection
spark_home_dir <- sparklyr::spark_installed_versions() %>%
  as.data.frame() %>%
  dplyr::filter(spark == "2.3.0") %>%
  dplyr::pull(dir)
sc <- spark_connect(master = "local", spark_home =spark_home_dir)

# Copy data to spark
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
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 3,
              uid = "kmeans") %>%
    train_models()

  expect_class(s1$models[[1]]$performance$train$train, "tbl_spark")
  expect_class(s1$models[[1]]$fit, "ml_model")
})


test_that("Segmenter Evaluate Model", {

  s1 <- new_segmenter(df = df, name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 3,
              uid = "kmeans") %>%
    train_models() %>%
    evaluate_models()

  expect_subset("data.frame", class(s1$evaluate))
  expect_equal(nrow(s1$evaluate), 1)
  expect_subset("silhouette", colnames(s1$evaluate))
})


test_that("Segmenter set final model options work as expected", {


  s1 <- new_segmenter(df = df, name = "test_multi_model") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              desc = "model1-ml_kmeans ") %>%
    add_model(pipe = NULL,
              method = "ml_bisecting_kmeans",
              desc = "model2-ml_bisecting_kmeans") %>%
    add_model(pipe = NULL,
              method = "ml_gaussian_mixture",
              desc = "model4-ml_kmeans2") %>%
    train_models() %>%
    evaluate_models()

  s1_best <- set_final_model(s1,
                             method = "best",
                             reevaluate = FALSE,
                             refit = TRUE)
  s1_man <- set_final_model(s1,
                            method = "manual",
                            id = s1$models[[1]]$id,
                            reevaluate = FALSE,
                            refit = TRUE)

  expect_subset("spark_ml", class(s1_best$final_model))
  expect_equal(get_evalutions(s1_best) %>%
                 top_n(1, silhouette) %>%
                 pull(model),
               s1_best$final_model$id)

  expect_equal(s1_man$final_model$id, s1$models[[1]]$id)
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


test_that("Segmenter with add_model_grid", {

  .k <- 3:5
  s1 <- new_segmenter(df = df, name = "test") %>%
    add_default_samples() %>%
    add_model_grid(pipe = NULL,
                   method = "ml_kmeans",
                   k = .k) %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = TRUE)

  expect_equal(length(s1$models), length(.k))
  expect_equal(s1$models[[1]]$method_args[[1]], .k[1])
  expect_equal(s1$models[[1]]$fit$summary$k, .k[1])
  expect_equal(s1$models[[2]]$method_args[[1]], .k[2])
  expect_equal(s1$models[[2]]$fit$summary$k, .k[2])
  expect_equal(s1$models[[3]]$method_args[[1]], .k[3])
  expect_equal(s1$models[[3]]$fit$summary$k, .k[3])
})


