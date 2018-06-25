
# Classifier Unit Tests ---------------------------------------------------


library(a2modeler)
library(testthat)
library(checkmate)
library(dplyr)
library(sparklyr)

context("classifier unit tests")

# Create Spark Connection
spark_home_dir <- sparklyr::spark_installed_versions() %>%
  as.data.frame() %>%
  dplyr::filter(spark == "2.3.0") %>%
  dplyr::pull(dir)
sc <- spark_connect(master = "local", spark_home =spark_home_dir)

# Copy data to spark
df <- copy_to(sc, mtcars, name = "df", overwrite = TRUE)


test_that("Classifier Constructer", {

  c1 <- new_classifier(df = df, target = "am", name = "test")
  expect_class(c1, "classifier")
})


test_that("Classifier Selects Best Model", {

  test_pipe <- pipeline(expr = function(x){select(x, index, am, mpg)})

  c1 <- new_classifier(df = df, target = "am", name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = test_pipe,
              method = "ml_logistic_regression") %>%
    add_model(pipe = test_pipe,
              method = "ml_decision_tree_classifier") %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  expect_subset("spark_ml", class(c1$final_model))
  expect_subset("spark_ml_classification", class(c1$final_model))
  expect_subset(
    c1$final_model$id,
    c1$evaluate %>%
      top_n(1, auc) %>%
      pull(model)
  )
})



test_that("Classifier Predicts New Data", {

  c1 <- new_classifier(df = df, target = "am", name = "test") %>%
    add_holdout_samples(splits = c(.5, .5)) %>%
    add_model(pipe = NULL,
              method = "ml_logistic_regression") %>%
    add_model(pipe = NULL,
              method = "ml_decision_tree_classifier") %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = TRUE)

  p1 <- predict(c1, data = df)

  expect_class(p1, "predictions")
})
