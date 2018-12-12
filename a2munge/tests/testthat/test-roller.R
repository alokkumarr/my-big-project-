
# Roller Unit Tests -------------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)
library(zoo)

context("roller function unit tests")


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
sim_tbl <- mutate_at(sim_df, "date", as.character) %>% 
  copy_to(sc, ., name = "df", overwrite = TRUE) %>%
  mutate(date = to_date(date))


r_roll <- sim_df %>%
  summariser(.,
             group_vars = c("date"),
             measure_vars = c("metric1", "metric2"),
             fun = c("sum")) %>%
  roller(.,
         order_vars = "date",
         group_vars = NULL,
         measure_vars = c("metric1_sum", "metric2_sum"),
         fun = "mean",
         width = 5) %>%
  arrange(date)


spk_roll <- sim_tbl %>%
  summariser(.,
             group_vars = c("date"),
             measure_vars = c("metric1", "metric2"),
             fun = c("sum")) %>%
  roller(.,
         order_vars = "date",
         group_vars = NULL,
         measure_vars = c("metric1_sum", "metric2_sum"),
         fun = "mean",
         width = 5) %>%
  collect() %>%
  arrange(date)



test_that("roller methods consistent", {
  expect_equal(
    spk_roll %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_roll %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(colnames(spk_roll), colnames(r_roll))
})


test_that("roller returns correct dimensions", {
  expect_equal(nrow(spk_roll), length(unique(sim_df$date)))
})

