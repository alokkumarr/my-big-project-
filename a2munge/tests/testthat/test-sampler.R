# Sampler Unit Tests -----------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)

context("Sampler function unit tests")


# Function to create simulated data
sim_data <- function(n_ids, n_recs, n_iter, seed = 319) {
  set.seed(seed)
  n <- n_ids * n_recs
  ids <- 1:n_ids
  dates <- seq(from = Sys.Date() - 365,
               to = Sys.Date(),
               by = "day")
  cat1 <- c("A", "B")
  cat2 <- c("X", "Y", "Z")

  do.call("rbind",
          replicate(n_iter,
                    {
                      data.frame(
                        id = sample(ids, n, replace = T),
                        date = sample(dates, n, replace = T),
                        cat1 = as.character(sample(cat1, n, replace = T)),
                        cat2 = as.character(sample(cat2, n, replace = T)),
                        metric1 = sample(1:5, n, replace = T),
                        metric2 = rnorm(n, mean = 50, sd = 5)
                      )
                    },
                    simplify = FALSE))
}

dat <- sim_data(10, 100, 1, seed = 319)


dat <- dat %>%
  mutate(date = as.Date(date))

# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

der_dat <- dat %>%
  select(., id, date, cat1, cat2, metric1, metric2) %>%
  mutate(., date = as.character(date))

# Load data into Spark
dat_tbl <- copy_to(sc, der_dat, overwrite = TRUE)

dat_tbl <- dat_tbl %>%
  mutate(date = as.Date(date))



# # Test Bed  -------------------------------------------------------------

#Test 1: Make sure the sample data has count according to size ------------

R_data <- sampler(
  dat,
  group_vars = NULL,
  method = "fraction",
  size = 0.5,
  replace = FALSE,
  weight = NULL,
  seed = NULL
)

R_data_count <- n_distinct(R_data)

count_data_r <- as.integer(n_distinct(dat) * 0.5) + 50

test_that("Sampler R DF is 50% of total rows plus 10% cushion or less", {
  expect_lte(R_data_count, count_data_r)
})


# Test 2:Make sure R DF sample is subset data of R DF ---------------------

diff_val_R <- setdiff(R_data, dat)

test_that("Sampler R DF subset is derived from main set", {
  expect_equal(n_distinct(diff_val_R), 0)
})


#Test 3:Test sampler for Spark data frame sample ------------------------

Sprk_data <- sampler(
  dat_tbl,
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
  expect_lte(count_data_spark, nrow(dat))
})



# Test 5: Make sure head returns correct set of rows ----------------------

R_head_data <- sampler(
  dat,
  group_vars = NULL,
  method = "head",
  size = 5,
  replace = FALSE,
  weight = NULL,
  seed = NULL
)

top_rows <- head(dat, 5)

test_that("Sampler head methods consistent", {
  expect_equal(R_head_data, top_rows)
  expect_equal(colnames(R_head_data), colnames(top_rows))
})


# Test 6:Compare the spark and R DF with head method ----------------------

Spark_head_data <-
  sampler(
    dat_tbl,
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


# Test 7:Check if the sample data is subset of dat ------------------------

size_R_DF <- sampler(
  dat,
  group_vars = NULL,
  method = "n",
  size = 5,
  replace = FALSE,
  weight = NULL,
  seed = NULL
)

R_sample_n <- setdiff(size_R_DF, dat)

test_that("Sampler for N records- R DF subset is derived from main set", {
  expect_equal(n_distinct(R_sample_n), 0)
})


# Test 8:Check if the tail give bottom N records --------------------------

n <- 10
R_tail_data <- sampler(
  dat,
  group_vars = NULL,
  method = "tail",
  size = n,
  replace = FALSE,
  weight = NULL,
  seed = NULL
)


tail_rows <- tail(dat, n)


test_that("Sampler tail methods consistent", {
  expect_equal(R_tail_data, tail_rows)
  expect_equal(colnames(R_tail_data), colnames(tail_rows))
})


# Test 9:Collecter with fraction method functionality test  ---------------

spark_coll_frac_data <- collecter(
  dat_tbl,
  sample = TRUE,
  method = "fraction",
  size = 0.2,
  replace = FALSE,
  seed = NULL)

Int_data <- sdf_nrow(dat_tbl)
count_collect_data_R <- nrow(spark_coll_frac_data) 

test_that("Spark-data collect set is less than total count", {
  expect_lte(count_collect_data_R, Int_data)
})


# Test 10:Collecter with head method  test  -------------------------------

spark_coll_head_data <- collecter(
  dat_tbl,
  sample = TRUE,
  method = "head",
  size = 6,
  replace = FALSE,
  seed = NULL
)

actual_head_data <- head(dat_tbl, 6)

test_that("Spark-data collect for head method", {
  expect_equal(spark_coll_head_data, actual_head_data)
})
