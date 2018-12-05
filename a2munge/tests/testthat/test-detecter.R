# Detecter Unit Tests -----------------------------------------------------



# Set Up ------------------------------------------------------------------


library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)
library(lubridate)

context("detecter function unit tests")


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
sim_tbl_anom <- sim_df_anom %>% 
  mutate_at("date", as.character) %>% 
  copy_to(sc, ., overwrite = TRUE)

# Format Spark tables
sim_tbl_anom <- mutate(sim_tbl_anom, date = to_date(date))



# Apply Detecter ----------------------------------------------------------


r_detect1 <- detecter(sim_df_anom,
                      index_var = "index",
                      group_vars = NULL,
                      measure_vars = "y",
                      frequency = 7,
                      direction = "pos",
                      alpha = 0.01,
                      max_anoms = 0.05,
                      trend_window = .75)

r_detect2 <- detecter(sim_df_anom,
                      index_var = "index",
                      group_vars = NULL,
                      measure_vars = "y",
                      frequency = 7,
                      direction = "neg",
                      alpha = 0.01,
                      max_anoms = 0.05,
                      trend_window = .75)

r_detect3 <-  detecter(sim_df_anom,
                       index_var = "index",
                       group_vars = NULL,
                       measure_vars = "y",
                       frequency = 7,
                       direction = "both",
                       alpha = 0.01,
                       max_anoms = 0.10,
                       trend_window = .75)



spk_detect1 <- sim_tbl_anom %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()

spk_detect2 <- sim_tbl_anom %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "neg",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()

spk_detect3 <- sim_tbl_anom %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "both",
           alpha = 0.01,
           max_anoms = 0.10,
           trend_window = .75) %>%
  collect()



# Tests -------------------------------------------------------------------


test_that("detecter methods consistent", {
  expect_equal(
    spk_detect1 %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_detect1 %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(
    spk_detect2 %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_detect2 %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(
    spk_detect3 %>%
      select(index, measure, value, seasonal, trend, resid, anomaly) %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_detect3 %>%
      select(index,  measure, value, seasonal, trend, resid, anomaly) %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(colnames(spk_detect1), colnames(r_detect1))
  expect_equal(colnames(spk_detect3), colnames(r_detect3))
})


test_that("detecter return correct dimensions", {
  expect_equal(nrow(r_detect1), nrow(sim_df_anom))
})


test_that("detecter identifies anomalies as expected", {

  detect_pos_indx <- r_detect1 %>% 
    filter(anomaly == 1) %>% 
    pull(index)
  
  df_pos_indx <- sim_df_anom %>% 
    filter(flag == 1) %>% 
    pull(index)
  
  detect_neg_indx <- r_detect2 %>% 
    filter(anomaly == 1) %>% 
    pull(index)
  
  df_neg_indx <- sim_df_anom %>% 
    filter(flag == -1) %>% 
    pull(index)
  
  
  detect_both_indx <- r_detect3 %>% 
    filter(anomaly == 1) %>% 
    pull(index)
  
  df_both_indx <- sim_df_anom %>% 
    filter(flag != 0) %>% 
    pull(index)
  
  
  expect_equal(detect_pos_indx, df_pos_indx)
  expect_equal(detect_neg_indx, df_neg_indx)
  expect_equal(detect_both_indx, df_both_indx)
})


test_that("detecter preserves index var date type", {

  r_detect1.1 <-  detecter(sim_df_anom,
                           index_var = "date",
                           group_vars = NULL,
                           measure_vars = "y",
                           frequency = 7,
                           direction = "both",
                           alpha = 0.01,
                           max_anoms = 0.10,
                           trend_window = .75)

  spk_detect1.1 <-  detecter(sim_tbl_anom,
                             index_var = "date",
                             group_vars = NULL,
                             measure_vars = "y",
                             frequency = 7,
                             direction = "both",
                             alpha = 0.01,
                             max_anoms = 0.10,
                             trend_window = .75)
  expect_class(r_detect1.1$date, "Date")
  expect_equal(sdf_schema(spk_detect1.1)[[1]]$type, "DateType")
})
