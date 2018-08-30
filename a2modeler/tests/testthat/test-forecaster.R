
# Forecaster Unit Tests ---------------------------------------------------

library(a2modeler)
library(testthat)
library(checkmate)
library(forecast)
library(dplyr)
library(lubridate)
library(sparklyr)
library(foreach)

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
    add_model(pipe = NULL, method = "auto.arima", uid = "auto_arima") %>%
    train_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  f1_preds <- predict(f1, periods = 10)

  expect_class(f1, "forecaster")
  expect_class(f1$final_model, "forecast_model")
  expect_equal(f1$models[[names(get_models(f1))]]$fit, f1$final_model$fit)
  expect_data_frame(f1$performance, nrow=1)
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
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  f2_preds <- predict(f2, periods = 10)

  expect_class(f2, "forecaster")
  expect_class(f2$final_model, "forecast_model")
  expect_equal(f2$models[[names(get_models(f2))]]$fit, f2$final_model$fit)
  expect_data_frame(f2$performance, nrow=1)
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
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  f3_preds <- predict(f3, periods = 10)

  expect_class(f3, "forecaster")
  expect_class(f3$final_model, "forecast_model")
  expect_equal(f3$final_model$uid,
               f3$performance %>%
                 dplyr::filter(sample == "validation") %>%
                 dplyr::top_n(1, -rmse) %>%
                 .$model_uid)
  expect_data_frame(f3$performance, nrow=2)
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
    set_final_model(., method = "best", reevaluate = TRUE, refit = FALSE)

  f4_preds <- predict(f4, periods = 10)

  expect_class(f4, "forecaster")
  expect_class(f4$final_model, "forecast_model")
  expect_equal(f4$final_model$uid,
               f4$performance %>%
                 dplyr::filter(sample == "validation") %>%
                 dplyr::top_n(1, -rmse) %>%
                 .$model)
  expect_data_frame(f4$performance, nrow=2)
  expect_data_frame(f4$final_model$test_performance, nrow=1)
  expect_data_frame(f4_preds$predictions, nrow=10)
})



test_that("Multiple Model with Manual set final model method test case", {

  f5 <- new_forecaster(df = dat1,
                   target = "y",
                   index_var = "index",
                   name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = NULL, method = "auto.arima", uid = "auto.arima") %>%
    add_model(pipe = NULL, method = "ets", uid = "ets") %>%
    train_models(.) 

  f5 <- set_final_model(f5, method = "manual", uid = "ets",
                        reevaluate = FALSE, refit = FALSE)
  f5_preds <- predict(f5, periods = 10)

  expect_class(f5, "forecaster")
  expect_class(f5$final_model, "forecast_model")
  expect_equal(f5$final_model$uid, "ets")
  expect_data_frame(f5$performance, nrow=2)
  expect_data_frame(f5_preds$predictions, nrow=10)
})



test_that("Covariate test case", {
  
  dat2 <- data.frame(dat1, x = rnorm(n))
  
  f6 <- new_forecaster(df = dat2,
                       target = "y",
                       index_var = "index",
                       name = "test") %>%
    add_model(pipe = NULL, method = "auto.arima") %>%
    train_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)
  
  f6_preds <- predict(f6, periods = 10, data = data.frame(x=rnorm(10)))
  
  expect_class(f6, "forecaster")
  expect_class(f6$final_model, "forecast_model")
  expect_subset("x", names(coef(f6$final_model$fit)))
  expect_data_frame(f6$performance, nrow=1)
  expect_class(f6_preds, "predictions")
  expect_data_frame(f6_preds$predictions, nrow=10)
})



test_that("Prediction index test case", {
  
  dat3 <- dat1 %>% 
    mutate(index = seq(Sys.Date()-days(n-1), Sys.Date(), by="day"))

  f8 <- new_forecaster(df = dat3,
                       target = "y",
                       index_var = "index",
                       unit = "days",
                       name = "test") %>%
    add_model(pipe = NULL, method = "auto.arima") %>%
    train_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE) %>%
    predict(periods = 10)

  expect_data_frame(f8$predictions, nrow=10)
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
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)
  
  f9_preds <- predict(f9, periods = 10)
  expect_data_frame(f9_preds$predictions, nrow=10)
  expect_equal(f9$pipelines[[1]]$output$y,
               BoxCox(dat1$y, BoxCox.lambda(dat1$y)))
})



test_that("Time Slice Sampling test case", {

  f10 <- new_forecaster(df = dat1,
                    target = "y",
                    index_var = "index",
                    name = "test") %>%
    add_time_slice_samples(., width = 190, horizon = 1, skip = 0) %>%
    add_model(pipe = pipeline(),
              method = "auto.arima",
              uid = "auto.arima") %>%
    train_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  expect_data_frame(f10$performance, nrow=1)
  expect_class(f10$models$auto.arima$sub_models[[1]]$slice190, "Arima")
  expect_number(length(f10$models$auto.arima$sub_models[[1]]), 10)
})



test_that("Auto Forecaster with parallel execution test case", {

  af1 <- auto_forecaster(dat1,
                         target = "y",
                         index_var = "index",
                         periods = 10,
                         unit = NULL,
                         models = list(
                           list(method = "auto.arima"),
                           list(method = "ets")),
                         execution_strategy = "multisession")
  expect_data_frame(af1$predictions, nrow=10)


  af2 <- auto_forecaster(dat1,
                         target = "y",
                         index_var = "index",
                         periods = 10,
                         unit = NULL,
                         models = list(
                           list(method = "auto.arima"),
                           list(method = "ets")),
                         execution_strategy = "sequential")
  expect_data_frame(af2$predictions, nrow=10)
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
                       list(method = "auto.arima"),
                       list(method = "ets")))

  spk_f1 <- forecaster(dat3_tbl,
                       index_var = "index",
                       group_vars = "group",
                       measure_vars = c("y"),
                       periods = 10,
                       unit = NULL,
                       pipe = NULL,
                       models = list(
                         list(method = "auto.arima"),
                         list(method = "ets")))

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
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)

  expect_error(predict(f12, periods = 10, data = data.frame(x1=rnorm(10))))

  p12 <- predict(f12, periods = 10, data = data.frame(x1=rnorm(10), x2=rnorm(10)))
  expect_class(p12, "predictions")
})




test_that("Deploy Function works as expected", {

  f12 <- new_forecaster(df = dat1,
                       target = "y",
                       index_var = "index",
                       name = "test") %>%
    add_holdout_samples(splits = c(.6, .2, .2)) %>%
    add_model(pipe = NULL, method = "auto.arima") %>%
    add_model(pipe = NULL, method = "ets") %>%
    train_models(.) %>%
    set_final_model(., method = "best", reevaluate = TRUE, refit = TRUE)

  temp_path <- paste(tempdir(), "test.rds", sep="/")
  deploy(f12, path = temp_path)
  expect_file_exists(temp_path)

  f13 <- readRDS(temp_path)
  expect_equal(f12$created_on, f13$created_on)
  expect_equal(f12$name, f13$name)
  expect_equal(f12$final_model$uid, f13$final_model$uid)

  deploy(f12, path = temp_path, lighten = TRUE)
  f13 <- readRDS(temp_path)
  expect_null(f13$models[[1]]$fit)
  expect_null(f13$models[[2]]$fit)

  p13 <- predict(f13, periods = 7)
  expect_class(p13, "predictions")
})



test_that("Parallel Execution Strategy Test Case", {
  
  f15 <- new_forecaster(df = dat1,
                        target = "y",
                        index_var = "index",
                        name = "test",
                        execution_strategy = "multisession") %>%
    add_time_slice_samples(., width = 190, horizon = 1, skip = 0) %>%
    add_model(method = "auto.arima", uid = "auto.arima") %>%
    add_model(method = "ets", uid = "ets") %>%
    train_models(.) %>%
    set_final_model(., method = "best", reevaluate = FALSE, refit = FALSE)
  
  expect_data_frame(f15$performance, nrow=2)
  expect_class(f15$models$auto.arima$sub_models[[1]]$slice190, "Arima")
  expect_number(length(f15$models$auto.arima$sub_models[[1]]), 10)
})


