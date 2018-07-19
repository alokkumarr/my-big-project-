
# Forecaster Unit Tests ---------------------------------------------------

library(a2modeler)
library(testthat)
library(checkmate)
library(forecast)
library(dplyr)
library(lubridate)
library(sparklyr)

context("forecaster unit tests")

# Create simulated dataset
n <- 200
dat1 <- data.frame(index = 1:n,
                   y = as.numeric(arima.sim(n = n,
                                            list(order = c(1,0,0), ar = 0.7),
                                            rand.gen = function(n, ...) rt(n, df = 2))))

# Create Spark Connection
spark_home_dir <- sparklyr::spark_installed_versions() %>%
  as.data.frame() %>%
  dplyr::filter(spark == "2.3.0") %>%
  dplyr::pull(dir)

sc <- spark_connect(master = "local", spark_home = spark_home_dir)


test_that("No holdout sampling test case", {

  f1 <- new_forecaster(df = dat1,
                       target = "y",
                       index_var = "index",
                       name = "test") %>%
    add_model(pipe = NULL, method = "auto.arima") %>%
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

  f2 <- new_forecaster(df = dat1,
                       target = "y",
                       index_var = "index",
                       name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = NULL, method = "auto.arima") %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  f2_preds <- predict(f2, periods = 10, level = c(80, 95))

  expect_class(f2, "forecaster")
  expect_class(f2$final_model, "forecast_model")
  expect_equal(f2$models[[names(get_models(f2))]]$fit, f2$final_model$fit)
  expect_data_frame(get_evalutions(f2), nrow=2)
  expect_data_frame(f2_preds$predictions, nrow=10)
})



test_that("Multiple Model test case", {

  f3 <- new_forecaster(df = dat1,
                       target = "y",
                       index_var = "index",
                       name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = NULL, method = "auto.arima") %>%
    add_model(pipe = NULL, method = "ets") %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = TRUE)

  f3_preds <- predict(f3, periods = 10, level = c(80, 95))

  expect_class(f3, "forecaster")
  expect_class(f3$final_model, "forecast_model")
  expect_equal(f3$final_model$id,
               get_evalutions(f3) %>%
                 dplyr::filter(sample == "validation") %>%
                 dplyr::top_n(1, -rmse) %>%
                 .$model)
  expect_data_frame(get_evalutions(f3), nrow=4)
  expect_data_frame(f3_preds$predictions, nrow=10)
})



test_that("Multiple Model with Test Holdout test case", {

  f4 <- new_forecaster(df = dat1,
                   target = "y",
                   index_var = "index",
                   name = "test") %>%
    add_holdout_samples(splits = c(.6, .2, .2)) %>%
    add_model(pipe = NULL, method = "auto.arima") %>%
    add_model(pipe = NULL, method = "ets") %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = TRUE, refit = TRUE)

  f4_preds <- predict(f4, periods = 10, level = c(80, 95))

  expect_class(f4, "forecaster")
  expect_class(f4$final_model, "forecast_model")
  expect_equal(f4$final_model$id,
               get_evalutions(f4) %>%
                 dplyr::filter(sample == "validation") %>%
                 dplyr::top_n(1, -rmse) %>%
                 .$model)
  expect_data_frame(get_evalutions(f4), nrow=6)
  expect_data_frame(f4_preds$predictions, nrow=10)
})



test_that("Multiple Model with Manual set final model method test case", {

  f5 <- new_forecaster(df = dat1,
                   target = "y",
                   index_var = "index",
                   name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = NULL, method = "auto.arima") %>%
    add_model(pipe = NULL, method = "ets") %>%
    train_models(.) %>%
    evaluate_models(.)

  f5 <- set_final_model(f5, method = "manual", id = names(get_models(f5))[2],
                        reevaluate = FALSE, refit = TRUE)
  f5_preds <- predict(f5, periods = 10, level = c(80, 95))

  expect_class(f5, "forecaster")
  expect_class(f5$final_model, "forecast_model")
  expect_equal(f5$final_model$id,names(get_models(f5))[2])
  expect_data_frame(get_evalutions(f5), nrow=4)
  expect_data_frame(f5_preds$predictions, nrow=10)
})



test_that("Covariate test case", {

  dat2 <- data.frame(dat1, x = rnorm(n))

  f6 <- new_forecaster(df = dat2,
                   target = "y",
                   index_var = "index",
                   name = "test") %>%
    add_model(pipe = pipeline(), method = "auto.arima") %>%
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



test_that("Prediction index test case", {

  f7 <- new_forecaster(df = dat1,
                   target = "y",
                   index_var = "index",
                   name = "test") %>%
    add_model(pipe = NULL, method = "auto.arima") %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE) %>%
    predict(periods = 10)

  dat3 <- dat1 %>% mutate(index = seq(Sys.Date()-days(n-1), Sys.Date(), by="day"))

  f8 <- new_forecaster(df = dat3,
                       target = "y",
                       index_var = "index",
                       unit = "days",
                       name = "test") %>%
    add_model(pipe = NULL, method = "auto.arima") %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE) %>%
    predict(periods = 10)

  expect_data_frame(f7$predictions, nrow=10)
  expect_data_frame(f8$predictions, nrow=10)
  expect_equal(f7$predictions$index, seq(n+1, n+10, by=1))
  expect_class(f8$predictions$index, "Date")
  expect_equal(f8$predictions$index, Sys.Date()+days(1:10))

})



test_that("Pipeline transformation test case", {

  box_cox_pipe <- pipeline(expr = function(x) {
    bcl <- BoxCox.lambda(x[["y"]])
    x %>% mutate(y = BoxCox(y, bcl))
  })

  f9 <- new_forecaster(df = dat1,
                   target = "y",
                   index_var = "index",
                   name = "test") %>%
    add_model(pipe = box_cox_pipe,
              method = "auto.arima") %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  f9_preds <- predict(f9, 10)
  expect_data_frame(f9_preds$predictions, nrow=10)
  expect_equal(f9$final_model$pipe$output$y, BoxCox(dat1$y, BoxCox.lambda(dat1$y)))
})



test_that("Time Slice Sampling test case", {

  f10 <- new_forecaster(df = dat1,
                    target = "y",
                    index_var = "index",
                    name = "test") %>%
    add_time_slice_samples(., width = 190, horizon = 1, skip = 0) %>%
    add_model(pipe = pipeline(),
              method = "auto.arima") %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  expect_data_frame(get_evalutions(f10), nrow=20)
})




test_that("Add Models test case", {

  f11 <- new_forecaster(df = dat1,
                    target = "y",
                    index_var = "index",
                    name = "test") %>%
    add_holdout_samples(., splits = c(.8, .2)) %>%
    add_models(.,
               pipe = pipeline(),
               models = list(
                 list(method = "auto.arima", method_args = list()),
                 list(method = "ets", method_args = list()))) %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  expect_equal(length(f11$models), 2)
  expect_data_frame(get_evalutions(f11), nrow=4)
})



test_that("Auto Forecaster data.frame test case", {

  af1 <- auto_forecaster(dat1,
                         target = "y",
                         index_var = "index",
                         periods = 10,
                         unit = NULL,
                         models = list(
                           list(method = "auto.arima", method_args = list()),
                           list(method = "ets", method_args = list())))
  expect_data_frame(af1$predictions, nrow=10)

  dat3 <- rbind(dat1 %>% mutate(group = "A"),
                dat1 %>% mutate(group = "B"))

  af2 <- forecaster(dat3,
                    index_var = "index",
                    group_vars = "group",
                    measure_vars = c("y"),
                    periods = 10,
                    unit = NULL,
                    pipe = NULL,
                    models = list(
                      list(method = "auto.arima", method_args = list()),
                      list(method = "ets", method_args = list())))
  expect_data_frame(af2, nrow=20)
})



test_that("Spark Forecaster test case", {


  dat3 <- rbind(dat1 %>% mutate(group = "A"),
                dat1 %>% mutate(group = "B"))

  # Load data into Spark
  dat3_tbl <- copy_to(sc, dat3, overwrite = TRUE)

  r_f1 <- forecaster(dat3,
                     index_var = "index",
                     group_vars = "group",
                     measure_vars = c("y"),
                     periods = 10,
                     unit = NULL,
                     pipe = NULL,
                     models = list(
                       list(method = "auto.arima", method_args = list()),
                       list(method = "ets", method_args = list()))
                     )

  spk_f1 <- forecaster(dat3_tbl,
                       index_var = "index",
                       group_vars = "group",
                       measure_vars = c("y"),
                       periods = 10,
                       unit = NULL,
                       pipe = NULL,
                       models = list(
                         list(method = "auto.arima", method_args = list()),
                         list(method = "ets", method_args = list()))
                       )

  expect_equal(
    spk_f1 %>%
      arrange(group, measure, index) %>%
      select_if(is.numeric) %>%
      collect() %>%
      round(5) ,
    r_f1 %>%
      arrange(group, measure, index) %>%
      select_if(is.numeric) %>%
      round(5)
  )

})


test_that("Schema Check works as expected", {

  dat2 <- data.frame(dat1, x1 = rnorm(n), x2 = rnorm(n))

  f12 <- new_forecaster(df = dat2,
                       target = "y",
                       index_var = "index",
                       name = "test") %>%
    add_model(pipe = pipeline(), method = "auto.arima") %>%
    train_models(.) %>%
    evaluate_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  expect_error(predict(f12, periods = 10, data = data.frame(x1=rnorm(10))))

  p12 <- predict(f12, periods = 10, data = data.frame(x1=rnorm(10), x2=rnom(10)))
  expect_class(p12, "predictions")
})
