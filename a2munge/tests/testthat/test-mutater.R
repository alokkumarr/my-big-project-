
# Mutater Unit Tests ------------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)

context("mutater function unit tests")


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
sim_tbl <- mutate_at(sim_df, "date", as.character) %>% 
  copy_to(sc, ., name = "df", overwrite = TRUE) %>%
  mutate(date = to_date(date))

r_mtr <- sim_df %>%
  mutater(order_vars = c("id", 'date'),
          group_vars = c("cat1", "cat2"),
          measure_vars = c("metric1", "metric2"),
          fun = "cumsum")

spk_mtr <- sim_tbl %>%
  mutater(order_vars = c("id", 'date'),
          group_vars = c("cat1", "cat2"),
          measure_vars = c("metric1", "metric2"),
          fun = "cumsum")


test_that("mutater methods consistent", {
  expect_equal(
    spk_mtr %>%
      collect() %>%
      arrange(index) %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_mtr %>%
      arrange(index) %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(colnames(spk_mtr), colnames(r_mtr))
})



test_that("mutater returns correct dimensions", {
  expect_equal(sdf_nrow(spk_mtr), nrow(sim_df))
  expect_equal(nrow(r_mtr), nrow(sim_df))
})


test_that("custom expression works as expected", {
  spk_mtr1 <- sim_tbl %>%
    mutater(measure_vars = "metric1", fun = funs(add2 = .+2))
  r_mtr1 <- sim_df %>%
    mutater(measure_vars = "metric1", fun = funs(add2 = .+2))

  expect_equal(colnames(spk_mtr1), colnames(r_mtr1))
  expect_equal(r_mtr1[["metric1_add2"]], r_mtr1[["metric1"]]+2)
  expect_equal(sdf_nrow(spk_mtr1), nrow(r_mtr1))
  expect_equal(spk_mtr1 %>%
                 collect() %>%
                 arrange(index) %>%
                 select_if(is.numeric),
               r_mtr1 %>%
                 arrange(index) %>%
                 select_if(is.numeric))
})
