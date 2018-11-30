
# Lagger Unit Tests -------------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)

context("lagger function unit tests")


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
sim_tbl <- mutate_at(sim_df, "date", as.character) %>% 
  copy_to(sc, ., name = "df", overwrite = TRUE) %>%
  mutate(date = to_date(date))

spk_lag <- sim_tbl %>%
  lagger(order_vars = c('date'),
         group_vars = c("id"),
         measure_vars = c("metric1", "metric2"),
         lags = 1:3)

r_lag <- sim_df %>%
  lagger(order_vars = c('date'),
         group_vars = c("id"),
         measure_vars = c("metric1", "metric2"),
         lags = 1:3)


test_that("lagger methods consistent", {
  expect_equal(
    spk_lag %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_lag %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_lag), colnames(r_lag))
})



test_that("lagger returns correct dimensions", {
  expect_equal(sdf_nrow(spk_lag), nrow(sim_df))
  expect_equal(nrow(r_lag), nrow(sim_df))
})


test_that("lag values are correct",{
  r_lag101 <- r_lag %>% filter(id == 101)
  expect_equal(r_lag101[["metric1"]][-nrow(r_lag101)],
               r_lag101[["metric1_lag1"]][-1])
  expect_equal(r_lag101[["metric1"]][1:10],
               r_lag101[["metric1_lag3"]][4:13])
})
