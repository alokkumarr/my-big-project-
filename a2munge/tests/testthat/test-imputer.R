# Imputer Unit Tests -----------------------------------------------------

library(a2munge)
library(testthat)
library(dplyr)
library(sparklyr)
library(checkmate) 

context("imputer function unit tests")


# Function to create simulated data ---------------------------------------


sim_data <- function(n_ids, n_recs, n_iter, seed = 319) {
  set.seed(seed)
  
  n <- n_ids * n_recs
  ids <- 1:n_ids
  
  d1 <- as.Date("2018-01-01")
  dates <- seq(from = d1 - 365, to = d1, by = "day")
  
  cat1 <- c("A", "B")
  p1 <- c(.75, .25)
  
  cat2 <- c("X", "Y", "Z")
  p2 <- c(.6, .3, .1)
  
  
  do.call("rbind",
          replicate(n_iter,
                    {
                      data.frame(
                        id = sample(ids, n, replace = T),
                        date = sample(dates, n, replace = T),
                        cat1 = as.character(sample(cat1, n, prob = p1, replace = T)),
                        cat2 = as.character(sample(cat2, n, prob = p2, replace = T)),
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


# Simulate Data
dat <- sim_data(1000, 10, 1, seed = 319) %>%
  mutate(index = row_number(),
         date = as.character(date))

# Copy to Spark
dat_tbl <- copy_to(sc, dat, overwrite = TRUE)


# Create NULL values,as R DF has Date as DataType no need to Mutate -------
dat <- dat %>%
  arrange(id) %>% 
  mutate(
    date = as.Date(date),
    cat1 = ifelse(row_number(id) %% 3 == 0, NA, as.character(cat1)),
    metric1 = ifelse(metric1 == 5, NA, metric1),
    metric3 = ifelse(metric3 == 13, NA, metric3)
  )


# Create NULL in Spark DF-as Date was loaded as char mutate to Date --------
dat_tbl <- dat_tbl %>%
  arrange(., id) %>%
  mutate(
    date = to_date(date),
    cat1 = ifelse(row_number(id) %% 3 == 0, NA, as.character(cat1)),
    metric1 = ifelse(metric1 == 5, NA, metric1),
    metric3 = ifelse(metric3 == 13, NA, metric3)
  )


to_char_ds <- function(df) {
  df %>%
    mutate_all(., as.character)
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


# Test 2:Apply "mode" function.Measure var=cat1.Compare both DS --------

Mode_value_count <- dat_tbl %>%
  filter(., !is.na(metric1)) %>%
  summariser(., group_vars = c("cat1"),
             fun = "n_distinct")

spk_imp_mode <- dat_tbl %>%
  imputer(
    .,
    group_vars = NULL,
    measure_vars = c("cat1"),
    fun = "mode"
  )

r_imp_mode <- dat %>%
  imputer(
    .,
    group_vars = NULL,
    measure_vars = c("cat1"),
    fun = "mode"
  )


# Compare both spark and R data set with mode function --------------------

test_that("imputer mode methods consistent", {
  expect_equal(
    spk_imp_mode %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3) ,
    r_imp_mode %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3)
  )
  expect_equal(colnames(spk_imp_mode), colnames(r_imp_mode))
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



# Test 8 :Test imputer with mean function with Grouped variables ----------

spk_imp_group_mean <- dat_tbl %>%
  imputer(
    .,
    group_vars = c("cat2"),
    measure_vars = c("metric2"),
    fun = "mean"
  )

r_imp_group_mean <- dat %>%
  imputer(
    .,
    group_vars = c("cat2"),
    measure_vars = c("metric2"),
    fun = "mean"
  )


# Compare both spark and R data set with mean function,group it first--------------------

test_that("imputer mean with group-by methods consistent", {
  
  expect_equal(
    spk_imp_group_mean %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3) ,
    r_imp_group_mean %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3)
  )
  expect_equal(colnames(spk_imp_group_mean),
               colnames(r_imp_group_mean))
})




# Test 10 :Constant function with Grouped vars for multi Int measures ---------

spk_imp_group_const <- dat_tbl %>%
  imputer(
    .,
    group_vars = c("cat2"),
    measure_vars = c("metric1", "metric3"),
    fun = "constant",
    fill = 5
  )


r_imp_group_const <- dat %>%
  imputer(
    .,
    group_vars = c("cat2"),
    measure_vars = c("metric1", "metric3"),
    fun = "constant",
    fill = 5
  )


# Compare both spark and R data set with const function -------------------

test_that("imputer mean with group-by methods consistent", {
  
  expect_equal(
    spk_imp_group_const %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3) ,
    r_imp_group_const %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(3)
  )
  expect_equal(colnames(spk_imp_group_const),
               colnames(r_imp_group_const))
})


# Test 11 :Constant fun with char.No measure:non-numeric value gets updated --------

spk_const_char_no_measure <- dat_tbl %>%
  imputer(., fun = "constant", fill = "S")

r_const_char_no_measure <- dat %>%
  imputer(., fun = "constant", fill = "S")

spk_const_char_no_measure_to_char <- to_char_ds(spk_const_char_no_measure)

r_const_char_no_measure_to_char <- to_char_ds(r_const_char_no_measure)


#Compare both spark and R data set with mean function

test_that("imputer mean methods consistent", {
  expect_equal(
    spk_const_char_no_measure_to_char %>%
      collect() %>%
      arrange(index) %>%
      as.data.frame() ,
    r_const_char_no_measure_to_char %>%
      arrange(index) %>%
      as.data.frame()
  )
  expect_equal(colnames(spk_const_char_no_measure_to_char),
               colnames(r_const_char_no_measure_to_char))
})