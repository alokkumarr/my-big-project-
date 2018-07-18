
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
    add_holdout_samples(splits = c(.5, .5)) %>%
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
      dplyr::filter(sample == "validation") %>%
      dplyr::top_n(1, -rmse) %>%
      dplyr::pull(model)
  )
})


r1 <- new_regressor(df = df, target = "mpg", name = "test") %>%
  add_default_samples() %>%
  add_model(pipe = NULL,
            method = "ml_linear_regression") %>%
  train_models() %>%
  evaluate_models() %>%
  set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)


test_that("Regressor Predicts New Data consistent with Method", {

  p1 <- predict(r1, data = df)
  r2 <- ml_linear_regression(df, formula = "mpg~.")
  p2 <- sparklyr::sdf_predict(df, r2)

  expect_class(p1, "predictions")
  expect_equal(p1$predictions %>%
                 dplyr::collect() %>%
                 dplyr::pull(predicted),
               p2 %>%
                 dplyr::collect() %>%
                 dplyr::pull(prediction))
})


test_that("Regressor Works with add_model_grid", {

  enet_parm <- c(0, .5, 1)
  lambda <- c(.1, .01, .001)

  r1 <- new_regressor(df = df, target = "mpg", name = "test") %>%
    add_default_samples() %>%
    add_model_grid(pipe = NULL,
                   method = "ml_linear_regression",
                   elastic_net_param = enet_parm,
                   reg_param = lambda) %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  param_grid <- expand.grid(elastic_net_param = enet_parm, reg_param = lambda)
  expect_equal(length(r1$models), nrow(param_grid))
  for(i in 1:nrow(param_grid)){
    expect_equal(r1$models[[i]]$method_args[[1]], param_grid[i,1])
    expect_equal(r1$models[[i]]$method_args[[2]], param_grid[i,2])
    expect_equal(r1$models[[i]]$fit$model$param_map$elastic_net_param, param_grid$elastic_net_param[i])
    expect_equal(r1$models[[i]]$fit$model$param_map$reg_param, param_grid$reg_param[i])
  }
})


test_that("Prediction Schema Check Works as expected", {

  expect_error(
    df %>%
      select(am, mpg) %>%
      predict(r1, data = .))


})

