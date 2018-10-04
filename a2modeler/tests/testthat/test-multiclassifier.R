#
# # Multi-Classifier Unit Tests ---------------------------------------------
#
#
# library(a2modeler)
# library(testthat)
# library(checkmate)
# library(dplyr)
# library(sparklyr)
#
# context("multiclassifier unit tests")
#
#
# # Basic Tests -------------------------------------------------------------
#
#
# # Create Spark Connection
# spark_home_dir <- sparklyr::spark_installed_versions() %>%
#   as.data.frame() %>%
#   dplyr::filter(spark == "2.3.0") %>%
#   dplyr::pull(dir)
# sc <- spark_connect(master = "local", spark_home = spark_home_dir)
#
# # Copy data to spark
# df <- copy_to(sc, iris, name = "df", overwrite = TRUE)
#
#
# test_that("Multi-Classifier Constructer", {
#
#   c1 <- multiclassifier(df = df, target = "Species", name = "test")
#   expect_class(c1, "multiclassifier")
# })
#
#
# test_that("Multi-Classifier Selects Best Model and Makes Predictions", {
#
#   test_pipe <- pipeline(expr = function(x){select(x, Species, Sepal_Length)})
#
#   mc1 <- multiclassifier(df = df, target = "Species", name = "test") %>%
#     add_holdout_samples(splits = c(.5, .5)) %>%
#     add_model(pipe = test_pipe,
#               method = "ml_decision_tree_classifier") %>%
#     add_model(pipe = test_pipe,
#               method = "ml_naive_bayes") %>%
#     train_models() %>%
#     set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)
#
#
#   expect_subset("spark_model", class(mc1$final_model))
#   expect_subset("spark_model_multiclassification", class(mc1$final_model))
#   expect_subset(
#     mc1$final_model$uid,
#     mc1$performance %>%
#       dplyr::filter(sample == "validation") %>%
#       dplyr::top_n(1, f1) %>%
#       dplyr::pull(model_uid)
#   )
#   expect_equal(mc1$models[[1]]$pipe, mc1$models[[2]]$pipe)
#
#   p1 <- predict(mc1, data = df)
#   expect_class(p1, "predictions")
# })
