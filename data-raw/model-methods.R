
# File to create model_method look up table -------------------------------
library(dplyr)
library(tidyr)


# Forecast Methods --------------------------------------------------------


# Forecast methods currently supported
forecast_methods <- data.frame(
  method =  c(
    "Arima",
    "arfima",
    "auto.arima",
    "bats",
    "ets",
    "nnetar",
    "tbats",
    "auto_fourier"
  ),
  name = c(
    "Arima",
    "Fractionally Differenced Arima",
    "Auto Arima",
    "ETS with boxcox, ARIMA errors, Trend and Seasonal",
    "Exponentially Smoothing State Space",
    "Neural Network Time Series",
    "BATS with Trigonometric Seasonal",
    "Auto Fourier"
  ),
  class = "forecast_model",
  package = c(
    "forecast",
    "forecast",
    "forecast",
    "forecast",
    "forecast",
    "forecast",
    "forecast",
    "a2modeler"
  )
)



# Segmeneter Methods ------------------------------------------------------


# Segmenter methods currently supported
segment_methods <- data.frame(
  method =  c(
    "ml_kmeans",
    "ml_bisecting_kmeans",
    "ml_lda",
    "ml_gaussian_mixture"
  ),
  name = c(
    "Spark ML K-Means",
    "Spark ML Bisecting K-Means",
    "Spark ML Latent Dirichlet Allocation",
    "Spark ML Gaussian Mixture clustering"
  ),
  class = c(rep("spark_ml", 4), rep("spark_ml_clustering", 4)),
  package = "sparklyr"
)



# All methods -------------------------------------------------------------

model_methods <- rbind(
  forecast_methods %>% dplyr::mutate(type = "forecaster"),
  segment_methods %>% dplyr::mutate(type = "segmenter")
) %>%
  nest(class) %>%
  rename(class = data) %>%
  select(type, method, name, package, class) %>%
  mutate_at(c("type", "method", "name", "package"), as.character)

save(model_methods, file = 'data/model_methods.rdata', compress = 'xz')
