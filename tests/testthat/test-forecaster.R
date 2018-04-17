
# Forecaster Unit Tests ---------------------------------------------------


library(testthat)
library(checkmate)
library(a2modeler)
library(forecast)
library(dplyr)

context("forecaster class unit tests")


n <- 200
dat1 <- data.frame(index = 1:n,
                   y = as.numeric(arima.sim(n = n,
                                            list(order = c(1,0,0), ar = 0.7),
                                            rand.gen = function(n, ...) rt(n, df = 2))))


test_that("No holdout sampling test case", {

  f1 <- forecaster(df = dat1,
                   target = "y",
                   name = "test") %>%
    add_model(pipe = pipeline(expr = function(x) x %>% select(y)),
              method = "auto.arima",
              class = "forecast_model") %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  f1_preds <- predict(f1, 10)

  expect_class(f1, "forecaster")
  expect_class(f1$final_model, "forecast_model")
  expect_equal(f1$models[[names(get_models(f1))]]$fit, f1$final_model$fit)
  expect_data_frame(get_evalutions(f1), nrow=1)
  expect_data_frame(f1_preds$predictions, nrow=10)
})




test_that("Validation only holdout sampling test case", {

  f2 <- forecaster(df = dat1,
                   target = "y",
                   name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = pipeline(expr = function(x) x %>% select(y)),
              method = "auto.arima",
              class = "forecast_model") %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  f2_preds <- predict(f2, 10)

  expect_class(f2, "forecaster")
  expect_class(f2$final_model, "forecast_model")
  expect_equal(f2$models[[names(get_models(f2))]]$fit, f2$final_model$fit)
  expect_data_frame(get_evalutions(f2), nrow=2)
  expect_data_frame(f2_preds$predictions, nrow=10)
})



test_that("Multiple Model test case", {

  f3 <- forecaster(df = dat1,
                   target = "y",
                   name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = pipeline(expr = function(x) x %>% select(y)),
              method = "auto.arima",
              class = "forecast_model") %>%
    add_model(pipe = pipeline(expr = function(x) x %>% select(y)),
              method = "ets",
              class = "forecast_model") %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = TRUE)

  f3_preds <- predict(f3, 10)

  expect_class(f3, "forecaster")
  expect_class(f3$final_model, "forecast_model")
  expect_equal(f3$final_model$id,
               get_evalutions(f3) %>%
                 filter(sample == "validation") %>%
                 top_n(1, -rmse) %>%
                 .$model)
  expect_data_frame(get_evalutions(f3), nrow=4)
  expect_data_frame(f3_preds$predictions, nrow=10)
})



test_that("Multiple Model with Test Holdout test case", {

  f4 <- forecaster(df = dat1,
                   target = "y",
                   name = "test") %>%
    add_holdout_samples(splits = c(.6, .2, .2)) %>%
    add_model(pipe = pipeline(expr = function(x) x %>% select(y)),
              method = "auto.arima",
              class = "forecast_model") %>%
    add_model(pipe = pipeline(expr = function(x) x %>% select(y)),
              method = "ets",
              class = "forecast_model") %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = TRUE, refit = TRUE)

  f4_preds <- predict(f4, 10)

  expect_class(f4, "forecaster")
  expect_class(f4$final_model, "forecast_model")
  expect_equal(f4$final_model$id,
               get_evalutions(f4) %>%
                 filter(sample == "validation") %>%
                 top_n(1, -rmse) %>%
                 .$model)
  expect_data_frame(get_evalutions(f4), nrow=6)
  expect_data_frame(f4_preds$predictions, nrow=10)
})



test_that("Multiple Model with Manual set final model method test case", {

  f5 <- forecaster(df = dat1,
                   target = "y",
                   name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = pipeline(expr = function(x) x %>% select(y)),
              method = "auto.arima",
              class = "forecast_model") %>%
    add_model(pipe = pipeline(expr = function(x) x %>% select(y)),
              method = "ets",
              class = "forecast_model") %>%
    train_models(.) %>%
    evaluate_models(.)

  f5 <- set_final_model(f5, method = "manual", id = names(get_models(f5))[2],
                        reevaluate = FALSE, refit = TRUE)

  f5_preds <- predict(f5, 10)


  expect_class(f5, "forecaster")
  expect_class(f5$final_model, "forecast_model")
  expect_equal(f5$final_model$id,names(get_models(f5))[2])
  expect_data_frame(get_evalutions(f5), nrow=4)
  expect_data_frame(f5_preds$predictions, nrow=10)
})



test_that("Covariate test case", {

  dat2 <- data.frame(y = dat1$y, x = rnorm(n))

  f6 <- forecaster(df = dat2,
                   target = "y",
                   name = "test") %>%
    add_model(pipe = pipeline(),
              method = "auto.arima",
              class = "forecast_model") %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  f6_preds <- predict(f6, periods = 10, data = data.frame(x=rnorm(10)))

  expect_class(f6, "forecaster")
  expect_class(f6$final_model, "forecast_model")
  expect_equal(f6$models[[names(get_models(f6))]]$fit, f6$final_model$fit)
  expect_data_frame(get_evalutions(f6), nrow=1)
  expect_class(f6_preds, "predictions")
  expect_data_frame(f6_preds$predictions, nrow=10)
})


