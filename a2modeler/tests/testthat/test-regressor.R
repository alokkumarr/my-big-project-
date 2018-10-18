
# Regressor Unit Tests ----------------------------------------------------


library(a2modeler)
library(testthat)
library(checkmate)
library(dplyr)
library(sparklyr)

context("regressor unit tests")


# Basic Tests -------------------------------------------------------------


# Create Spark Connection
# spark_home_dir <- sparklyr::spark_installed_versions() %>%
#   as.data.frame() %>%
#   dplyr::filter(spark == "2.3.0") %>%
#   dplyr::pull(dir)
sc <- spark_connect(master = "local", version = "2.3.0")


# Copy data to spark
df <- copy_to(sc, mtcars, name = "df", overwrite = TRUE)


test_that("Regressor Constructer", {

  r1 <- regressor(df = df, target = "mpg", name = "test")
  expect_class(r1, "regressor")
})


test_that("Regressor Selects Best Model", {

  test_pipe <- pipeline(expr = function(x){select(x, mpg, wt, hp)})

  r1 <- regressor(df = df, target = "mpg", name = "test") %>%
    add_holdout_samples(splits = c(.5, .5)) %>%
    add_model(pipe = test_pipe,
              method = "ml_linear_regression") %>%
    add_model(pipe = test_pipe,
              method = "ml_decision_tree_regressor") %>%
    train_models() %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  expect_subset("spark_model", class(r1$final_model))
  expect_subset("spark_model_regression", class(r1$final_model))
  expect_subset(
    r1$final_model$uid,
    r1$performance %>%
      dplyr::filter(sample == "validation") %>%
      dplyr::top_n(1, -rmse) %>%
      dplyr::pull(model_uid)
  )


  p1 <- predict(r1, data = df)
  expect_class(p1, "predictions")
  expect_error(predict(r1, data = select(df, wt)))
})


