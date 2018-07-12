
# Classifier Unit Tests ---------------------------------------------------


library(a2modeler)
library(testthat)
library(checkmate)
library(dplyr)
library(sparklyr)

context("classifier unit tests")


# Basic Tests -------------------------------------------------------------


# Create Spark Connection
spark_home_dir <- sparklyr::spark_installed_versions() %>%
  as.data.frame() %>%
  dplyr::filter(spark == "2.3.0") %>%
  dplyr::pull(dir)
sc <- spark_connect(master = "local", spark_home =spark_home_dir)

# Copy data to spark
df <- copy_to(sc, mtcars, name = "df", overwrite = TRUE)

# Add Salaries
salaries_tbl <- salaries %>%
  filter(gender_guess != "U") %>%
  mutate(male = ifelse(gender_guess == "M", 1, 0)) %>%
  select(-gender_guess) %>%
  head(100) %>%
  copy_to(sc, ., name = "salaries", overwrite = TRUE)

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
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  p1 <- predict(c1, data = df)


  c2 <- ml_logistic_regression(df %>% head(16), formula = "am~.")
  p2 <- predict(c2, newdata = df)

  expect_class(p1, "predictions")
  expect_equal(p1$predictions %>%
                 collect() %>%
                 pull(predicted),
               as.numeric(p2))
})



# Advanced Tests ----------------------------------------------------------

test_that("Classifer with Multiple Methods and CV", {

  salary_pipe <- pipeline(expr = function(x) {
    x %>%
      select(index, male, base_pay, overtime_pay, benefits, police, fire, medical)
  })

  f1 <- new_classifier(
    df = salaries_tbl,
    target = "male",
    name = "Classifier-tests",
    desc = "New classifier") %>%
    add_cross_validation_samples(folds = 2) %>%
    # add_model(pipe = salary_pipe,
    #           method = "ml_logistic_regression",
    #           desc = "model1-ml_logistic_regression-Test ") %>%
    # add_model(pipe = salary_pipe,
    #           method = "ml_decision_tree_classifier",
    #           desc = "model2-ml_decision_tree_classifier-Test") %>%
    add_model(pipe = salary_pipe,
              method = "ml_multilayer_perceptron_classifier",
              layers = c(6, 4, 2),
              desc = "model3-ml_multilayer_perceptron_classifier-Test") %>%
    add_model(pipe = salary_pipe,
              method = "ml_gbt_classifier",
              desc = "model3-ml_gbt_classifier-Test") %>%
    add_model(pipe = salary_pipe,
              method = "ml_random_forest_classifier",
              desc = "model4-ml_random_forest_classifier-Test") %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(.,
                    method = "best",
                    reevaluate = FALSE,
                    refit = TRUE)

  expect_subset("spark_ml", class(f1$final_model))
  expect_subset("spark_ml_classification", class(f1$final_model))
  expect_subset(f1$final_model$id,
                f1$evaluate %>%
                  top_n(1, auc) %>%
                  pull(model))

  pred_data <- predict(f1, data = salaries_tbl)

  expect_class(pred_data, "predictions")
})




test_that("Classifier set final model options work as expected", {


  c1 <- new_classifier(df = df, target = "am", name = "test") %>%
    add_holdout_samples(splits = c(.5, .5)) %>%
    add_model(pipe = NULL,
              method = "ml_logistic_regression") %>%
    add_model(pipe = NULL,
              method = "ml_decision_tree_classifier") %>%
    train_models() %>%
    evaluate_models()

  c1_best <- set_final_model(c1, method = "best", refit = TRUE)
  c1_man <- set_final_model(c1, method = "manual", id = c1$models[[1]]$id, refit = TRUE)

  expect_subset("spark_ml", class(c1_best$final_model))
  expect_equal(get_evalutions(c1_best) %>%
                 top_n(1, auc) %>%
                 pull(model),
               c1_best$final_model$id)

  expect_equal(c1_man$final_model$id, c1$models[[1]]$id)
})




test_that("Classifier train model works on additional models", {


  c1 <- new_classifier(df = df, target = "am", name = "test") %>%
    add_holdout_samples(splits = c(.5, .5)) %>%
    add_model(pipe = NULL,
              method = "ml_logistic_regression") %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(., method = "best", refit = TRUE)

  c2 <- c1 %>%
    add_model(pipe = NULL,
              method = "ml_decision_tree_classifier") %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(., method = "best", refit = TRUE)

  expect_equal(c1$models[[1]]$fit$coefficients,
               c2$models[[1]]$fit$coefficients)

  expect_equal(c1$models[[1]]$last_updated,
               c2$models[[1]]$last_updated)
})
