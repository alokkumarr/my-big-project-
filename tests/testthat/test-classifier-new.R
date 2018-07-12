# new_classifier Uint Tests -----------------------------------------------------

library(testthat)
library(checkmate)
library(a2modeler)
library(sparklyr)
library(dplyr)

context("classifier unit tests")

# Create Spark Connection
spark_home_dir <- sparklyr::spark_installed_versions() %>%
  as.data.frame() %>%
  dplyr::filter(spark == "2.3.0") %>%
  dplyr::pull(dir)

sc <- spark_connect(master = "local", spark_home = spark_home_dir)


larg_DS <-
  read.csv(file = "C:/Git_Workspace/a2modeler/tests/testthat/Salaries-small_set.csv", header =
             TRUE, sep = ",")

int_data <-
  larg_DS %>% select (.,
                      Id,
                      BasePay,
                      OvertimePay,
                      OtherPay,
                      TotalPay,
                      TotalPayBenefits,
                      MaritalStatus)


# Copy data to spark
spk_df <- copy_to(sc, int_data, name = "df", overwrite = TRUE)

# Test 1:Test case for new_classifier with NULL Pipe ------------------------


test_that("Classifier constructor New Data", {
  f1_class <-
    new_classifier(
      df = spk_df,
      target = "MaritalStatus",
      name = "Classifier-constructer",
      desc = "New classifier"
    ) %>%
    add_model(pipe = NULL,
              method = "ml_logistic_regression",
              desc = "model1-ml_logistic_regression-Test ") %>%
    add_model(pipe = NULL,
              method = "ml_decision_tree_classifier",
              desc = "model2-ml_decision_tree_classifier-Test") %>%
    add_model(pipe = NULL,
              method = "ml_gbt_classifier",
              desc = "model4-ml_gbt_classifier-Test") %>%
    add_model(pipe = NULL,
              method = "ml_random_forest_classifier",
              desc = "model4-ml_random_forest_classifier-Test") %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(.,
                    method = "best",
                    reevaluate = FALSE,
                    refit = FALSE)

  expect_subset("spark_ml", class(f1_class$final_model))
  expect_subset("spark_ml_classification", class(f1_class$final_model))
  expect_subset(f1_class$final_model$id,
                f1_class$evaluate %>%
                  top_n(1, auc) %>%
                  pull(model))

  pred_data <- predict(f1_class, data = spk_df)

  expect_class(pred_data, "predictions")

})




# Test 2:With Pipe value --------------------------------------------------


test_pipe <-
  pipeline(
    expr = function(x) {
      select(x, index, MaritalStatus, BasePay)
    }
  )

test_that("Classifier constructor with Pipe New Data", {
  f1_class <-
    new_classifier(
      df = spk_df,
      target = "MaritalStatus",
      name = "Classifier-constructer",
      desc = "New classifier"
    ) %>%
    add_model(pipe = test_pipe,
              method = "ml_logistic_regression",
              desc = "model1-ml_logistic_regression-Test ") %>%
    add_model(pipe = test_pipe,
              method = "ml_decision_tree_classifier",
              desc = "model2-ml_decision_tree_classifier-Test") %>%
    add_model(pipe = test_pipe,
              method = "ml_gbt_classifier",
              desc = "model4-ml_gbt_classifier-Test") %>%
    add_model(pipe = test_pipe,
              method = "ml_random_forest_classifier",
              desc = "model4-ml_random_forest_classifier-Test") %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(.,
                    method = "best",
                    reevaluate = FALSE,
                    refit = FALSE)

  expect_subset("spark_ml", class(f1_class$final_model))
  expect_subset("spark_ml_classification", class(f1_class$final_model))
  expect_subset(f1_class$final_model$id,
                f1_class$evaluate %>%
                  top_n(1, auc) %>%
                  pull(model))

  pred_data <- predict(f1_class, data = spk_df)

  expect_class(pred_data, "predictions")

})


# Test 3: Fit method in classifier

test_pipe <-
  pipeline(
    expr = function(x) {
      select(x, index, MaritalStatus, BasePay)
    }
  )

test_that("Classifier-Fit data", {
  f1_class <-
    new_classifier(
      df = spk_df,
      target = "MaritalStatus",
      name = "Classifier-constructer",
      desc = "New classifier"
    ) %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = test_pipe,
              method = "ml_logistic_regression",
              desc = "model1-ml_logistic_regression-Test ") %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(.,
                    method = "best",
                    reevaluate = FALSE,
                    refit = FALSE)

  f2_preds <- predict(f1_class, data = spk_df)

  expect_equal(f1_class$models[[names(get_models(f1_class))]]$fit, f1_class$final_model$fit)

})
