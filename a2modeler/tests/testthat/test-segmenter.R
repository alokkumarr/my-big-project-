
# Segmenter Test Cases ----------------------------------------------------


library(a2modeler)
library(testthat)
library(checkmate)
library(dplyr)
library(sparklyr)

context("segmenter unit tests")
# 
# # Create Spark Connection
# spark_home_dir <- sparklyr::spark_installed_versions() %>%
#   as.data.frame() %>%
#   dplyr::filter(spark == "2.3.0") %>%
#   dplyr::pull(dir)
# sc <- spark_connect(master = "local", spark_home =spark_home_dir)
# 
# # Copy data to spark
# df <- copy_to(sc, mtcars, name = "df", overwrite = TRUE)
# 
# 
# test_that("Segmenter Constructer", {
# 
#   s1 <- segmenter(df = df, name = "test")
#   expect_class(s1, "segmenter")
# })
# 
# 
# test_that("Segmenter Add Model", {
# 
#   s1 <- segmenter(df = df, name = "test") %>%
#     add_model(pipe = NULL, method = "ml_kmeans")
# 
#   expect_equal(s1$models[[names(get_models(s1))]]$method, "ml_kmeans")
# })
# 
# 
# 
# test_that("Segmenter Add Sampling", {
# 
#   s1 <- segmenter(df = df, name = "test") %>%
#     add_holdout_samples(splits = c(.8, .2)) %>%
#     add_model(pipe = NULL,
#               method = "ml_kmeans",
#               k = 3)
#   s1_index <- get_indicies(s1)
# 
#   expect_equal(s1_index$holdout$train, 1:(floor(.8*nrow(mtcars))))
#   expect_class(s1_index$holdout$train, "integer")
# })
# 
# 
# test_that("Segmenter Train Model", {
# 
#   s1 <- segmenter(df = df, name = "test") %>%
#     add_holdout_samples(splits = c(.8, .2)) %>%
#     add_model(pipe = NULL,
#               method = "ml_kmeans",
#               k = 3,
#               uid = "kmeans") %>%
#     train_models()
# 
#   expect_class(s1$models[["kmeans"]]$performance, "data.frame")
#   expect_class(s1$models[["kmeans"]]$fit, "ml_model_clustering")
#   expect_subset("data.frame", class(s1$performance))
#   expect_equal(nrow(s1$performance), 1)
#   expect_subset("silhouette", colnames(s1$performance))
# })
# 
# 
# 
# test_that("Segmenter set final model options work as expected", {
# 
# 
#   s1 <- segmenter(df = df, name = "test_multi_model") %>%
#     add_holdout_samples(splits = c(.5, .5)) %>%
#     add_model(pipe = NULL,
#               method = "ml_kmeans",
#               k = 3,
#               desc = "model1-ml_kmeans ") %>%
#     add_model(pipe = NULL,
#               method = "ml_bisecting_kmeans",
#               k = 3,
#               desc = "model2-ml_bisecting_kmeans") %>%
#     add_model(pipe = NULL,
#               method = "ml_gaussian_mixture",
#               k = 3,
#               desc = "model4-ml_kmeans2") %>%
#     train_models()
# 
#   s1_best <- set_final_model(s1,
#                              method = "best",
#                              reevaluate = FALSE,
#                              refit = FALSE)
#   s1_man <- set_final_model(s1,
#                             method = "manual",
#                             uid = s1$models[[1]]$uid,
#                             reevaluate = FALSE,
#                             refit = FALSE)
# 
#   expect_subset("spark_model_clustering", class(s1_best$final_model))
#   expect_equal(s1_best$performance %>%
#                  top_n(1, silhouette) %>%
#                  dplyr::pull(model_uid),
#                s1_best$final_model$uid)
# 
#   expect_equal(s1_man$final_model$uid, s1$models[[1]]$uid)
# })
# 
# 
# test_that("Segmenter Predicts New Data", {
# 
#   s1 <- segmenter(df = df, name = "test") %>%
#     add_holdout_samples(splits = c(.6, .4)) %>%
#     add_model(pipe = NULL,
#               method = "ml_kmeans",
#               k = 3) %>%
#     add_model(pipe = NULL,
#               method = "ml_kmeans",
#               k = 4) %>%
#     train_models() %>%
#     set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)
# 
#   p1 <- predict(s1, data = df)
# 
#   expect_class(p1, "predictions")
# })
# 
# 
# test_that("Segmenter with param_map", {
# 
#   .k <- 3:5
#   s1 <- segmenter(df = df, name = "test") %>%
#     add_holdout_samples(splits = c(.6, .4)) %>%
#     add_model(pipe = NULL,
#               method = "ml_kmeans",
#               uid = "kmeans",
#               param_map = list(k = .k)) %>%
#     train_models() %>%
#     set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)
# 
#   expect_equal(length(s1$models$kmeans$sub_models), length(.k))
#   expect_equal(nrow(s1$models$kmeans$sub_models[[1]][[1]]$centers), .k[1])
#   expect_equal(s1$models$kmeans$sub_models[[1]][[1]]$summary$k, .k[1])
#   expect_equal(nrow(s1$models$kmeans$sub_models[[2]][[1]]$centers), .k[2])
#   expect_equal(s1$models$kmeans$sub_models[[2]][[1]]$summary$k, .k[2])
#   expect_equal(nrow(s1$models$kmeans$sub_models[[3]][[1]]$centers), .k[3])
#   expect_equal(s1$models$kmeans$sub_models[[3]][[1]]$summary$k, .k[3])
# })
# 
# 
