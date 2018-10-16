
# Classifier Unit Tests ---------------------------------------------------


library(a2modeler)
library(testthat)
library(checkmate)
library(dplyr)
library(sparklyr)

context("classifier unit tests")


# Basic Tests -------------------------------------------------------------


# Create Spark Connection
# spark_home_dir <- sparklyr::spark_installed_versions() %>%
#   as.data.frame() %>%
#   dplyr::filter(spark == "2.3.0") %>%
#   dplyr::pull(dir)
sc <- spark_connect(master = "local")

# Copy data to spark
df <- copy_to(sc, mtcars, name = "df", overwrite = TRUE)


test_that("Classifier Constructer", {

  c1 <- classifier(df = df, target = "am", name = "test")
  expect_class(c1, "classifier")

  expect_error(classifier(df = df, target = "mpg", name = "test"))
})


test_that("Classifier Selects Best Model and Makes Predictions", {

  test_pipe <- pipeline(expr = function(x){select(x, am, mpg)})

  c1 <- classifier(df = df, target = "am", name = "test") %>%
    add_holdout_samples(splits = c(.5, .5)) %>%
    add_model(pipe = test_pipe,
              method = "ml_logistic_regression") %>%
    add_model(pipe = test_pipe,
              method = "ml_decision_tree_classifier") %>%
    train_models() %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)


  expect_subset("spark_model", class(c1$final_model))
  expect_subset("spark_model_classification", class(c1$final_model))
  expect_subset(
    c1$final_model$uid,
    c1$performance %>%
      dplyr::filter(sample == "validation") %>%
      dplyr::top_n(1, auc) %>%
      dplyr::pull(model_uid)
  )
  expect_equal(c1$models[[1]]$pipe, c1$models[[2]]$pipe)

  p1 <- predict(c1, data = df)
  expect_class(p1, "predictions")
})






# Advanced Tests ----------------------------------------------------------


test_that("Classifer with Multiple Methods, CV sampling, without saving submodels", {

  test_pipe <- pipeline(expr = function(x) {
    x %>%
      select(am, mpg, cyl, wt, hp, vs)
  })

  c1 <- classifier(df = df, target = "am", name = "test", save_submodels = TRUE) %>%
    add_cross_validation_samples(folds = 2) %>%
    add_model(pipe = test_pipe,
              method = "ml_multilayer_perceptron_classifier",
              layers = c(5, 3, 2),
              desc = "model3-ml_multilayer_perceptron_classifier-Test") %>%
    add_model(pipe = test_pipe,
              method = "ml_gbt_classifier",
              desc = "model3-ml_gbt_classifier-Test") %>%
    add_model(pipe = test_pipe,
              method = "ml_random_forest_classifier",
              desc = "model4-ml_random_forest_classifier-Test") %>%
    train_models() %>%
    set_final_model(.,
                    method = "best",
                    reevaluate = FALSE,
                    refit = FALSE)

  expect_subset("spark_model", class(c1$final_model))
  expect_subset("spark_model_classification", class(c1$final_model))
  expect_subset(c1$final_model$uid,
                c1$performance %>%
                  dplyr::filter(sample == "validation") %>%
                  dplyr::top_n(1, auc) %>%
                  dplyr::pull(model_uid)
                )

  pred_data <- predict(c1, data = df)
  expect_class(pred_data, "predictions")
})




test_that("Classifier set final model options work as expected", {

  test_pipe <- pipeline(expr = function(x) {
    x %>%
      select(am, mpg, cyl)
  })

  c1 <- classifier(df = df, target = "am", name = "test") %>%
    add_holdout_samples(splits = c(.6, .4)) %>%
    add_model(pipe = test_pipe,
              method = "ml_logistic_regression",
              uid = "logistic") %>%
    add_model(pipe = test_pipe,
              method = "ml_decision_tree_classifier",
              uid = "tree") %>%
    train_models()

  c1_best <- set_final_model(c1,
                             method = "best",
                             reevaluate = FALSE,
                             refit = FALSE)
  c1_man <- set_final_model(c1,
                            method = "manual",
                            uid = c1$models[["tree"]]$uid,
                            reevaluate = FALSE,
                            refit = FALSE)

  expect_subset("spark_model", class(c1_best$final_model))
  expect_equal(c1$performance %>%
                 dplyr::filter(sample == "validation") %>%
                 dplyr::top_n(1, auc) %>%
                 dplyr::pull(model_uid),
               c1_best$final_model$uid)
  expect_equal(c1_man$final_model$uid, c1$models[["tree"]]$uid)
})




test_that("Classifier set_final_model works on additional models", {

  test_pipe <- pipeline(expr = function(x){select(x, am, mpg)})

  c1 <- classifier(df = df, target = "am", name = "test") %>%
    add_holdout_samples(splits = c(.5, .5)) %>%
    add_model(pipe = test_pipe,
              method = "ml_decision_tree_classifier",
              uid = "tree") %>%
    train_models() %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  c2 <- c1 %>%
    add_model(pipe = test_pipe,
              method = "ml_logistic_regression",
              uid = "logistic") %>%
    train_models() %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  expect_equal(c1$final_model$uid, "tree")
  expect_equal(nrow(c2$performance), 2)
  expect_equal(c2$final_model$uid, "logistic")
})



test_that("Classifier with parallel execution, test holdout and param_map", {

  test_pipe <- pipeline(expr = function(x){select(x, am, mpg)})

  c1 <- classifier(df = df,
                       target = "am",
                       name = "test",
                       save_submodels = FALSE,
                       execution_strategy = "multisession") %>%
    add_holdout_samples(splits = c(.5, .3, .2)) %>%
    add_model(pipe = test_pipe,
              method = "ml_logistic_regression",
              param_map = list(reg_param = c(0, 0.01),
                               elastic_net_param = c(0, 0.01)),
              uid = "logistic") %>%
    add_model(pipe = test_pipe,
              method = "ml_decision_tree_classifier",
              param_map = list(max_depth = c(2, 5)),
              uid = "tree") %>%
    train_models() %>%
    set_final_model(., method = "best", reevaluate = TRUE, refit = TRUE)

  expect_equal(nrow(c1$performance), 6)
  expect_true(!is.null(c1$final_model$test_performance))
  expect_true(!is.null(c1$final_model$test_predictions))
})


test_that("The same seed value produces similar samples", {

 inv_logit <- function(x, min = 0, max = 1) {
   p <- exp(x)/(1 + exp(x))
   p <- ifelse(is.na(p) & !is.na(x), 1, p)
   p * (max - min) + min
 }

  d1 <- data.frame(x = rnorm(100)) %>%
    mutate(y = -1 + 2 * x + rnorm(100, 0, sd = .5),
           y_prob = inv_logit(y),
           y_bin = rbinom(100, 1, y_prob)) %>%
    copy_to(sc, ., name = "d1", overwrite = TRUE)

  test_pipe <- pipeline(expr = function(df){select(df, y_bin, x)})

  c1 <- classifier(df = d1, target = "y_bin", name = "test", seed = 1) %>%
    add_holdout_samples(splits = c(.5, .5)) %>%
    add_model(pipe = test_pipe,
              method ="ml_logistic_regression",
              uid = "logistic") %>%
    train_models()

  c2 <- classifier(df = d1, target = "y_bin", name = "test", seed = 1) %>%
    add_holdout_samples(splits = c(.5, .5)) %>%
    add_model(pipe = test_pipe,
              method ="ml_logistic_regression",
              uid = "logistic") %>%
    train_models()

  c3 <- classifier(df = d1, target = "y_bin", name = "test", seed = 101) %>%
    add_holdout_samples(splits = c(.5, .5)) %>%
    add_model(pipe = test_pipe,
              method ="ml_logistic_regression",
              uid = "logistic") %>%
    train_models()

  expect_equal(c1$performance$auc, c2$performance$auc)
  expect_failure(expect_equal(c1$performance$auc, c3$performance$auc))
})