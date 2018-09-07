# Imputer Unit Tests -----------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate) 

context("imputer function unit tests")


# Function to create simulated data ---------------------------------------


sim_data <- function(n_ids, n_recs, n_iter, seed = 319) {
  n <- n_ids * n_recs
  ids <- 1:n_ids
  dates <- seq(from = Sys.Date() - 365,
               to = Sys.Date(),
               by = "day")
  cat1 <- LETTERS[1:3]
  cat2 <- LETTERS[22:26]


  do.call("rbind",
          replicate(n_iter,
                    {
                      data.frame(
                        id = sample(ids, n, replace = T),
                        date = sample(dates, n, replace = T),
                        cat1 = as.character(sample(cat1, n, replace = T,
                                                   prob = c(.6, rep(.1, 2)))),
                        cat2 = as.character(sample(cat2, n, replace = T,
                                                   prob = c(.6, rep(.1, 4)))),
                        metric1 = sample(1:7, n, replace = T),
                        metric2 = rnorm(n, mean = 50, sd = 5),
                        metric3 = sample(11:15, n, replace = T)
                      )
                    },
                    simplify = FALSE))
}


# Create Spark connection -------------------------------------------------


sc <- spark_connect(master = "local")


# Data Creation -----------------------------------------------------------



dat <- sim_data(1000, 10, 1, seed = 319) %>%
  mutate(index = row_number())

dat <- dat %>%
  mutate(date = as.Date(date))

der_dat <- dat %>%
  select(., id, date, cat1, cat2, metric1, metric2, metric3, index) %>%
  mutate(., date = as.character(date))


# Load data into Spark-date can't be directly loaded into spark -----------

dat_tbl <- copy_to(sc, der_dat, overwrite = TRUE)


# Create NULL values,as R DF has Date as DataType no need to Mutate -------

dat <- dat %>%
  mutate(
    cat1 = ifelse(row_number(id) %% 3 == 0, NA, as.character(cat1)),
    metric1 = ifelse(metric1 == 5, NA, metric1),
    metric3 = ifelse(metric3 == 13, NA, metric3)
  )


# Create NULL in Spark DF-as Date was loaded as char mutate to Date --------



dat_tbl <- dat_tbl %>%
  arrange(., id) %>%
  mutate(
    date = as.Date(date),
    cat1 = ifelse(row_number(id) %% 3 == 0, NA, as.character(cat1)),
    metric1 = ifelse(metric1 == 5, NA, metric1),
    metric3 = ifelse(metric3 == 13, NA, metric3)
  )


# Data Creation End -------------------------------------------------------

# Convert DS to char for comparision --------------------------------------

to_char_ds <- function(df) {
    mutate_all(df, as.character)
}


# Test Bed Begin ----------------------------------------------------------


# Test 1 :Use "mean" fun measure=metric1.Compare Spark and R DS -----------

spk_imp <- dat_tbl %>%
  imputer(
    .,
    group_vars = NULL,
    measure_vars = c("metric1"),
    fun = "mean"
  )


r_imp <- dat %>%
  imputer(
    .,
    group_vars = NULL,
    measure_vars = c("metric1"),
    fun = "mean"
  )


# Compare both spark and R data set with mean function --------------------

test_that("imputer mean methods consistent", {
  expect_equal(
    spk_imp %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3) ,
    r_imp %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3)
  )
  expect_equal(colnames(spk_imp), colnames(r_imp))
})




# Test 3:Use "constant" function.measure var=metric1.Compare generated DS--------

spk_imp_const <- dat_tbl %>%
  imputer(.,
          measure_vars = c("metric1","metric3"),
          fun = "constant",
          fill = 8)

r_imp_const <- dat %>%
  imputer(.,
          measure_vars = c("metric1","metric3"),
          fun = "constant",
          fill = 8)


# Compare both spark and R data set with constant function --------------------

test_that("imputer constant value replace methods consistent", {
  expect_equal(
    spk_imp_const %>%
      collect() %>%
      arrange(index) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3) ,
    r_imp_const %>%
      arrange(index) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3)
  )

  expect_equal(colnames(spk_imp_const), colnames(r_imp_const))
})


# Test 4:Use "constant" function.Measure var=cat1 -------------------------

spk_imp_const_char <- dat_tbl %>%
  imputer(
    .,
    measure_vars = c("cat1", "cat2"),
    fun = "constant",
    fill = "K"
  )

r_imp_const_char <- dat %>%
  imputer(
    .,
    measure_vars = c("cat1", "cat2"),
    fun = "constant",
    fill = "K"
  )


char_spk_imp_const_char <- to_char_ds(spk_imp_const_char)
char_r_imp_const_char <- to_char_ds(r_imp_const_char)


# Compare both spark and R data set with constant function --------------------

test_that("imputer constant value replace methods consistent", {
  expect_equal(
    char_spk_imp_const_char %>%
      arrange(index) %>%
      collect() %>%
      as.data.frame() ,
    char_r_imp_const_char %>%
      arrange(index) %>%
      collect() %>%
      as.data.frame()
  )

  expect_equal(colnames(char_spk_imp_const_char), colnames(char_r_imp_const_char))
})


# Test 5:Apply mean with no measure-All numeric col with NULL value get replaced --------

spk_imp_no_measure <- dat_tbl %>%
  imputer(., fun = "mean")

r_imp_no_measure <- dat %>%
  imputer(., fun = "mean")


# Compare both spark and R data set with mean function --------------------

test_that("imputer mean methods consistent", {
  expect_equal(
    spk_imp_no_measure %>%
      collect() %>%
      arrange(index) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3) ,
    r_imp_no_measure %>%
      arrange(index) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3)
  )
  expect_equal(colnames(spk_imp_no_measure), colnames(r_imp_no_measure))
})


# Test 6 :Mode with no measure :numeric and non-numeric values gets updated --------

spk_mode_no_measure <- dat_tbl %>%
  imputer(., fun = "mode")

r_mode_no_measure <- dat %>%
  imputer(., fun = "mode")



# Compare both spark and R data set with mode function --------------------

test_that("imputer mode methods consistent", {
  expect_equal(
    spk_mode_no_measure %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3) ,
    r_mode_no_measure %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3)
  )
  expect_equal(colnames(spk_mode_no_measure),
               colnames(r_mode_no_measure))
})


# Test 7:Apply constant fun-no measure:so both numeric- non-num get updated --------


spk_const_no_measure <- dat_tbl %>%
  imputer(., fun = "constant",
          fill = 111)

r_const_no_measure <- dat %>%
  imputer(., fun = "constant",
          fill = 111)


# Compare both spark and R data set with const function -------------------

test_that("imputer mean methods consistent", {
  expect_equal(
    spk_const_no_measure %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3) ,
    r_const_no_measure %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3)
  )
  expect_equal(colnames(spk_const_no_measure),
               colnames(r_const_no_measure))
})



# Test 8 :Test imputer with mean function with Grouped variables ----------

spk_imp_group_mean <- dat_tbl %>%
  imputer(
    .,
    group_vars = c("cat2"),
    measure_vars = c("metric1", "metric3"),
    fun = "mean"
  )

r_imp_group_mean <- dat %>%
  imputer(
    .,
    group_vars = c("cat2"),
    measure_vars = c("metric1", "metric3"),
    fun = "mean"
  )


# Compare both spark and R data set with mean function,ungroup it first--------------------

test_that("imputer mean with group-by methods consistent", {
  spk_imp_ungroup_mean <- ungroup(spk_imp_group_mean)
  r_imp_ungroup_mean <- ungroup(r_imp_group_mean)

  expect_equal(
    spk_imp_ungroup_mean %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3) ,
    r_imp_ungroup_mean %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3)
  )
  expect_equal(colnames(spk_imp_ungroup_mean),
               colnames(r_imp_ungroup_mean))
})




