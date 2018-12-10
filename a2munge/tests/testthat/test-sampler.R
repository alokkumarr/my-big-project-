# Sampler Unit Tests -----------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)

context("Sampler function unit tests")


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
sim_tbl <- mutate_at(sim_df, "date", as.character) %>% 
  copy_to(sc, ., name = "df", overwrite = TRUE) %>%
  mutate(date = to_date(date))



#Test 1: Make sure the sample data has count according to size ------------

R_data <- sampler(sim_df,
                  group_vars = NULL,
                  method = "fraction",
                  size = 0.5,
                  replace = FALSE,
                  weight = NULL,
                  seed = NULL)

R_data_count <- n_distinct(R_data)

count_data_r <- as.integer(n_distinct(sim_df) * 0.5) + 50

test_that("Sampler R DF is 50% of total rows plus 10% cushion or less", {
  expect_lte(R_data_count, count_data_r)
})


# Test 2:Make sure R DF sample is subset data of R DF ---------------------

diff_val_R <- setdiff(R_data, sim_df)

test_that("Sampler R DF subset is derived from main set", {
  expect_equal(n_distinct(diff_val_R), 0)
})


#Test 3:Test sampler for Spark data frame sample ------------------------

Sprk_data <- sampler(
  sim_tbl,
  group_vars = NULL,
  method = "fraction",
  size = 0.5,
  replace = FALSE,
  weight = NULL,
  seed = NULL
)

count_data_spark <- Sprk_data %>%
  count() %>%
  collect() %>% 
  pull()


test_that("Spark-data sample set is less than total count", {
  expect_lte(count_data_spark, nrow(sim_df))
})



# Test 5: Make sure head returns correct set of rows ----------------------

R_head_data <- sampler(
  sim_df,
  group_vars = NULL,
  method = "head",
  size = 5,
  replace = FALSE,
  weight = NULL,
  seed = NULL
)

top_rows <- head(sim_df, 5)

test_that("Sampler head methods consistent", {
  expect_equal(R_head_data, top_rows)
  expect_equal(colnames(R_head_data), colnames(top_rows))
})


# Test 6:Compare the spark and R DF with head method ----------------------

Spark_head_data <-
  sampler(
    sim_tbl,
    group_vars = NULL,
    method = "head",
    size = 5,
    replace = FALSE,
    weight = NULL,
    seed = NULL
  )


test_that("Sampler head methods consistent between R and Saprk", {
  expect_equal(
    R_head_data %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5),
    Spark_head_data %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(R_head_data), colnames(Spark_head_data))
})


# Test 7:Check if the sample data is subset of sim_df ------------------------

size_R_DF <- sampler(
  sim_df,
  group_vars = NULL,
  method = "n",
  size = 5,
  replace = FALSE,
  weight = NULL,
  seed = NULL
)

R_sample_n <- setdiff(size_R_DF, sim_df)

test_that("Sampler for N records- R DF subset is derived from main set", {
  expect_equal(n_distinct(R_sample_n), 0)
})


# Test 8:Check if the tail give bottom N records --------------------------

n <- 10
R_tail_data <- sampler(
  sim_df,
  group_vars = NULL,
  method = "tail",
  size = n,
  replace = FALSE,
  weight = NULL,
  seed = NULL
)


tail_rows <- tail(sim_df, n)


test_that("Sampler tail methods consistent", {
  expect_equal(R_tail_data, tail_rows)
  expect_equal(colnames(R_tail_data), colnames(tail_rows))
})


# Test 9:Collecter with fraction method functionality test  ---------------

spark_coll_frac_data <- collecter(
  sim_tbl,
  sample = TRUE,
  method = "fraction",
  size = 0.2,
  replace = FALSE,
  seed = NULL)

Int_data <- sdf_nrow(sim_tbl)
count_collect_data_R <- nrow(spark_coll_frac_data) 

test_that("Spark-data collect set is less than total count", {
  expect_lte(count_collect_data_R, Int_data)
})


# Test 10:Collecter with head method  test  -------------------------------

spark_coll_head_data <- collecter(
  sim_tbl,
  sample = TRUE,
  method = "head",
  size = 6,
  replace = FALSE,
  seed = NULL
)

actual_head_data <- head(sim_tbl, 6)

test_that("Spark-data collect for head method", {
  expect_equal(spark_coll_head_data, actual_head_data)
})
