

# Segmenter Test Cases ----------------------------------------------------


library(a2modeler)
library(testthat)
library(checkmate)
library(dplyr)
library(sparklyr)

context("segmenter unit tests")

# Create Spark Connection
spark_home_dir <- sparklyr::spark_installed_versions() %>%
  as.data.frame() %>%
  dplyr::filter(spark == "2.3.0") %>%
  dplyr::pull(dir)
sc <- spark_connect(master = "local", spark_home = spark_home_dir)

# Copy data to spark
df <- copy_to(sc, mtcars, name = "df", overwrite = TRUE)

n <- 200
df_forecast <- data.frame(index = 1:n,
                   y = as.numeric(arima.sim(n = n,
                                            list(order = c(1,0,0), ar = 0.7),
                                            rand.gen = function(n, ...) rt(n, df = 2))))




test_that("Segmenter Constructer", {
  s1 <- new_segmenter(df = df, name = "test")
  expect_class(s1, "segmenter")
})


test_that("Segmenter Add Model for method -ml-kmeans", {
  s1 <- new_segmenter(df = df, name = "test") %>%
    add_model(pipe = NULL, method = "ml_kmeans")

  expect_equal(s1$models[[names(get_models(s1))]]$method, "ml_kmeans")
})


test_that("Segmenter Add Model for method -ml_bisecting_kmeans", {
  s1 <- new_segmenter(df = df, name = "test") %>%
    add_model(pipe = NULL, method = "ml_bisecting_kmeans")

  expect_equal(s1$models[[names(get_models(s1))]]$method, "ml_bisecting_kmeans")
})


test_that("Segmenter Add Model for method -ml_lda", {
  s1 <- new_segmenter(df = df, name = "test") %>%
    add_model(pipe = NULL, method = "ml_lda")

  expect_equal(s1$models[[names(get_models(s1))]]$method, "ml_lda")
})

test_that("Segmenter Add Model for method -ml_gaussian_mixture", {
  s1 <- new_segmenter(df = df, name = "test") %>%
    add_model(pipe = NULL, method = "ml_gaussian_mixture")

  expect_equal(s1$models[[names(get_models(s1))]]$method, "ml_gaussian_mixture")
})



test_that("Segmenter Add Sampling", {
  s1 <- new_segmenter(df = df, name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 3)
  s1_index <- get_indicies(s1)

  expect_equal(s1_index$holdout$train, 1:(floor(.8 * nrow(mtcars))))
  expect_class(s1_index$holdout$train, "integer")
})


test_that("Segmenter Train Model", {
  s1 <- new_segmenter(df = df, name = "test") %>%
    add_default_samples() %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 3) %>%
    train_models()

  expect_class(s1$models[[1]]$performance$train$train, "tbl_spark")
  expect_class(s1$models[[1]]$fit, "ml_model")
})


test_that("Segmenter Evaluate Model", {
  s1 <- new_segmenter(df = df, name = "test") %>%
    add_default_samples() %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 3) %>%
    train_models() %>%
    evaluate_models()

  expect_subset("data.frame", class(s1$evaluate))
  expect_equal(nrow(s1$evaluate), 1)
  expect_subset("silhouette", colnames(s1$evaluate))
})


test_that("Segmenter Selects Best Model", {
  s1 <- new_segmenter(df = df, name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 3) %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 4) %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(.,
                    method = "best",
                    reevaluate = FALSE,
                    refit = FALSE)

  expect_subset("spark_ml", class(s1$final_model))
  expect_equal(s1$evaluate %>%
                 top_n(1, silhouette) %>%
                 pull(model) %>%
                 head(1),
               s1$final_model$id)
})



test_that("Segmenter Predicts New Data", {
  s1 <- new_segmenter(df = df, name = "test") %>%
    add_holdout_samples(splits = c(.8, .2)) %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 3) %>%
    add_model(pipe = NULL,
              method = "ml_kmeans",
              k = 4) %>%
    train_models() %>%
    evaluate_models() %>%
    set_final_model(.,
                    method = "best",
                    reevaluate = FALSE,
                    refit = TRUE)

  p1 <- predict(s1, data = df)

  expect_class(p1, "predictions")
})

# Add modeler class and validate ------------------------------------------


test_that("Segmenter Selects Best Model", {
  mobj <- modeler(
    df = df,
    target = NULL,
    type = "segmenter",
    name = "test_segment1"
  )

  expect_class(mobj, "modeler")

})


# Add Multiple models -----------------------------------------------------


f1 <- new_segmenter(df = df, name = "test_multi_model") %>%
  add_model(pipe = NULL,
            method = "ml_kmeans",
            desc = "model1-ml_kmeans ") %>%
  add_model(pipe = NULL,
            method = "ml_bisecting_kmeans",
            desc = "model2-ml_bisecting_kmeans") %>%
  add_model(pipe = NULL,
            method = "ml_lda",
            desc = "model2-ml_lda") %>%
  add_model(pipe = NULL,
            method = "ml_kmeans",
            desc = "model4-ml_kmeans2")



?new_forecaster
f1_forcast <- new_forecaster(df=df_forecast,
                     target = "y",
                     index_var = "index",
                     name = "demo",
                     desc = "Demostration of Forecaster functionality")




RMSE
add_mod <- f1 %>%
set_measure(RMSE)

add_mod_training <-  train_models(add_mod,ids=NULL)

add_mod_eval <- evaluate_models(add_mod)

get_evalutions(add_mod) %>%
  dplyr::filter(sample== "validation") %>%
  group_by()%>%
  summarise_at("rmse",mean)

f1 <- add_mod %>%
  set_final_model(method = "best", refit = TRUE)


#####set_final_model for forecaster models-----------------------------------------------

f1 <- f1 %>% add_time_slice_samples(width = 199,
                                horizon = 1,
                                skip = 9)

class(f1)

f1 <- f1 %>% set_measure(RMSE)

f1$measure

# Add Models
f1_forcast <- f1_forcast %>%
  add_model(pipe = NULL, method = "auto.arima") %>%
  add_model(pipe = NULL, method = "ets")
print(f1)

f1$models
f1 <- f1 %>% set_final_model(method = "best", refit = TRUE)
