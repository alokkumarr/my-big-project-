
# Regressor Unit Tests ----------------------------------------------------


library(a2modeler)
library(testthat)
library(checkmate)
library(dplyr)
library(sparklyr)

context("regressor unit tests")


# Basic Tests -------------------------------------------------------------


# Create Spark Connection
spark_home_dir <- sparklyr::spark_installed_versions() %>%
  as.data.frame() %>%
  dplyr::filter(spark == "2.3.0") %>%
  dplyr::pull(dir)
sc <- spark_connect(master = "local", spark_home =spark_home_dir)

# Copy data to spark
df <- copy_to(sc, mtcars, name = "df", overwrite = TRUE)


test_that("Regressor Constructer", {

  r1 <- new_regressor(df = df, target = "mpg", name = "test")
  expect_class(r1, "regressor")
})


test_that("Regressor Selects Best Model", {

  test_pipe <- pipeline(expr = function(x){select(x, index, mpg, wt, hp)})

  r1 <- new_regressor(df = df, target = "mpg", name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = test_pipe,
              method = "ml_linear_regression") %>%
    add_model(pipe = test_pipe,
              method = "ml_decision_tree_regressor") %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  expect_subset("spark_ml", class(r1$final_model))
  expect_subset("ml_model_regression", class(r1$final_model))
  expect_subset(
    r1$final_model$id,
    r1$evaluate %>%
      filter(sample == "validation") %>%
      top_n(1, -rmse) %>%
      pull(model)
  )
})


test_that("Regressor Predicts New Data consistent with Method", {

  r1 <- new_regressor(df = df, target = "mpg", name = "test") %>%
    add_default_samples() %>%
    add_model(pipe = NULL,
              method = "ml_linear_regression") %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  p1 <- predict(r1, data = df)


  r2 <- ml_linear_regression(df, formula = "mpg~.")
  p2 <- predict(r2, newdata = df)

  expect_class(p1, "predictions")
  expect_equal(p1$predictions %>%
                 collect() %>%
                 pull(predicted),
               as.numeric(p2))
})
