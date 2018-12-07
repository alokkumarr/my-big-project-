
# Regressor Unit Tests ----------------------------------------------------


library(a2modeler)
library(testthat)
library(checkmate)
library(dplyr)
library(sparklyr)

context("regressor unit tests")


# Basic Tests -------------------------------------------------------------


# Create Spark Connection
sc <- spark_connect(master = "local")

# Copy data to spark
df <- copy_to(sc, mtcars, name = "df", overwrite = TRUE)


# Create Spark datasets 
df1 <- iris %>%
  sample_frac(size = .5) %>%
  copy_to(sc, ., name = "df1", overwrite = TRUE)

df2 <- iris %>%
  sample_frac(size = .5) %>%
  copy_to(sc, ., name = "df2", overwrite = TRUE)


test_that("Regressor Constructer", {

  r1 <- regressor(df = df1, target = "Petal_Width", name = "test")
  expect_class(r1, "regressor")
})



test_pipe <- pipeline(expr = function(df) {
  select(df, Petal_Width, Sepal_Length, Petal_Length)
}, uid = "test-pipe")

r1 <- regressor(df = df1, target = "Petal_Width", name = "test") %>%
  add_holdout_samples(splits = c(.5, .5)) %>%
  add_model(pipe = test_pipe,
            method = "ml_linear_regression",
            uid = "lm") %>%
  add_model(pipe = test_pipe,
            method = "ml_decision_tree_regressor",
            uid = "tree") %>%
  train_models() %>%
  set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

test_that("Regressor Selects Best Model", {

  expect_subset("spark_model", class(r1$final_model))
  expect_subset("spark_model_regression", class(r1$final_model))
  expect_subset(
    r1$final_model$uid,
    r1$performance %>%
      dplyr::filter(sample == "validation") %>%
      dplyr::top_n(1, -rmse) %>%
      dplyr::pull(model_uid)
  )


  p1 <- predict(r1, data = df1)
  expect_class(p1, "predictions")
  expect_error(predict(r1, data = select(df1, Petal_width)))
})


test_that("Refit Option works as expected", {


  r2 <- refit(r1, df2, append = TRUE)

  expect_gte(sdf_nrow(r2$data), sdf_nrow(r1$data))
  expect_equivalent(r1$final_model$fit$stages[[2]]$param_map,
                    r2$final_model$fit$stages[[2]]$param_map)
  expect_equivalent(r1$final_model$uid, r2$final_model$uid)
})


test_that("get_variable_importance method works as expected", {
  
  lm_coefs <- get_variable_importance(r1$models$lm)
  expect_data_frame(lm_coefs,
                    ncols = 5,
                    types = c("character", rep("double", 4)),
                    nrows = ncol(r1$pipelines$`test-pipe`$output))
  
  tree_imp <- get_variable_importance(r1$models$tree)
  expect_data_frame(tree_imp,
                    ncols = 2,
                    types = c("character", "double"),
                    nrows = ncol(r1$pipelines$`test-pipe`$output) - 1)
})
