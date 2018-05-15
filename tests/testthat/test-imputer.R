# Imputer Unit Tests -----------------------------------------------------

library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)
library(checkmate) # should add to testing - package has many useful expect_* functions

context("imputer function unit tests")


# Function to create simulated data ---------------------------------------


sim_data <- function(n_ids, n_recs, n_iter, seed = 319) {
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



dat <- sim_data(3, 3, 1, seed = 319) %>%
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
  char_df <- df %>%
    mutate(
      id= as.character(id),
      date = as.character(date),
      cat1 = as.character(cat1),
      cat2 = as.character(cat2),
      metric1 = as.character(metric1),
      metric2 = as.character(metric2),
      metric3 = as.character(metric3),
      index = as.character(index)
      
    )
  
  return(char_df)
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
      round(5) ,
    r_imp %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_imp), colnames(r_imp))
})


# Test 2:Apply "mode" function.Measure var=metric1.Compare both DS --------

Mode_value_count <- dat_tbl %>%
  filter(., !is.na(metric1)) %>%
  summariser(., group_vars = c("metric1"),
             fun = "n_distinct")

spk_imp_mode <- dat_tbl %>%
  imputer(
    .,
    group_vars = NULL,
    measure_vars = c("metric1"),
    fun = "mode"
  )

r_imp_mode <- dat %>%
  imputer(
    .,
    group_vars = NULL,
    measure_vars = c("metric1"),
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
      round(5) ,
    r_imp_mode %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
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
      round(5) ,
    r_imp_const %>%
      arrange(index) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
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
      round(5) ,
    r_imp_no_measure %>%
      arrange(index) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_imp_no_measure), colnames(r_imp_no_measure))
})


# Test 6 :Mode with no measure :numeric and non-numeric values gets updated --------

##################################ERRORRRRRR###############################################

spk_mode_no_measure <- dat_tbl %>%
  imputer(., fun = "mode")

r_mode_no_measure <- dat %>%
  imputer(., fun = "mode")



# Compare both spark and R data set with mode function --------------------

test_that("imputer mean methods consistent", {
  expect_equal(
    spk_mode_no_measure %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_mode_no_measure %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
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
      round(5) ,
    r_const_no_measure %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
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
      round(5) ,
    r_imp_ungroup_mean %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_imp_ungroup_mean),
               colnames(r_imp_ungroup_mean))
})




# Test 9 :Mode function and Grouped variables -----------------------------

###Note:mode can't support grouping variables at this time

spk_imp_group_mode <- dat_tbl %>%
  imputer(.,
          #group_vars = c("cat2"),
          measure_vars = c("metric1", "metric3"),
          fun = "mode")

#############ERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRR####################################################

r_imp_group_mode <- dat %>%
  imputer(.,
          #group_vars = c("cat2"),
          measure_vars = c("metric1", "metric3"),
          fun = "mode")


# Compare both spark and R data set with mode function --------------------

test_that("imputer mean with group-by methods consistent", {
  spk_imp_ungroup_mode <- ungroup(spk_imp_group_mode)
  r_imp_ungroup_mode <- ungroup(r_imp_group_mode)
  
  expect_equal(
    spk_imp_ungroup_mode %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_imp_ungroup_mode %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_imp_ungroup_mode),
               colnames(r_imp_ungroup_mode))
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
  spk_imp_ungroup_const <- ungroup(spk_imp_group_const)
  r_imp_ungroup_const <- ungroup(r_imp_group_const)
  
  expect_equal(
    spk_imp_ungroup_const %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_imp_ungroup_const %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_imp_ungroup_const),
               colnames(r_imp_ungroup_const))
})


# Test 11 :Constant fun with char.No measure:non-numeric value gets updated --------

spk_const_char_no_measure <- dat_tbl %>%
  imputer(., fun = "constant",
          fill = "S")

r_const_char_no_measure <- dat %>%
  imputer(., fun = "constant",
          fill = "S")

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


# Test 12 :impute_mean fun.Measure=metric1,no group vars -----------------------

spk_imp_mean <- dat_tbl %>%
  impute_mean(.,
              measure_vars = c("metric1"))

r_imp_mean <- dat %>%
  impute_mean(.,
              measure_vars = c("metric1"))


# Compare both spark and R data set with impute_mean function --------------------

test_that("imputer mean methods consistent", {
  expect_equal(
    spk_imp_mean %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_imp_mean %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_imp_mean), colnames(r_imp_mean))
})



# Test 13 :impute_mean function with no measure vars ----------------------

spk_imp_mean_no_measure <- dat_tbl %>%
  impute_mean(.,
              measure_vars = NULL)

r_imp_mean_no_measure <- dat %>%
  impute_mean(.,
              measure_vars = NULL)




# Compare both spark and R data set with impute_mean function -------------

test_that("imputer mean methods consistent", {
  expect_equal(
    spk_imp_mean_no_measure %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_imp_mean_no_measure %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_imp_mean_no_measure),
               colnames(r_imp_mean_no_measure))
})



# Test 14 :impute_mode function with measure var=metric1 ------------------

spk_imp_mode <- dat_tbl %>%
  impute_mode(.,
              measure_vars = c("metric1"))

r_imp_mode <- dat %>%
  impute_mode(.,
              measure_vars = c("metric1"))



# Compare both spark and R data set with impute_mode function -------------

test_that("imputer mean methods consistent", {
  expect_equal(
    spk_imp_mean %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_imp_mean %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_imp_mean), colnames(r_imp_mean))
})


# Test 15 :impute_mode function with no-measure values --------------------

spk_imp_mode_no_measure <- dat_tbl %>%
  impute_mode(.,
              measure_vars = NULL) %>%
  arrange(index)

r_imp_mode_no_measure <- dat %>%
  impute_mode(.,
              measure_vars = NULL) %>%
  arrange(index)



# Compare both spark and R data set with impute_mode function -------------

test_that("imputer mean methods consistent", {
  expect_equal(
    spk_imp_mode_no_measure %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_imp_mode_no_measure %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_imp_mode_no_measure),
               colnames(r_imp_mode_no_measure))
})



# Test 16 :impute_constant function:measure=Metric1 -----------------------

spk_imp_constant <- dat_tbl %>%
  impute_constant(.,
                  measure_vars = c("metric1"),
                  fill = 0)

r_imp_constant <- dat %>%
  impute_constant(.,
                  measure_vars = c("metric1"),
                  fill = 0)


# Compare both spark and R data set with impute_constant function ---------

test_that("imputer mean methods consistent", {
  expect_equal(
    spk_imp_constant %>%
      collect() %>%
      arrange(index) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_imp_constant %>%
      arrange(index) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_imp_constant), colnames(r_imp_constant))
})


# Test 17 :impute_constant function with no-measure values ----------------

spk_imp_const_no_measure <- dat_tbl %>%
  impute_constant(.,
                  measure_vars = NULL,
                  fill = 0)

r_imp_const_no_measure <- dat %>%
  impute_constant(.,
                  measure_vars = NULL,
                  fill = 0)


# Compare both spark and R data set with impute_constant function ---------

test_that("imputer mean methods consistent", {
  expect_equal(
    spk_imp_const_no_measure %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_imp_const_no_measure %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_imp_const_no_measure),
               colnames(r_imp_const_no_measure))
})


# Test 18 :impute_constant function with no-measure values with char --------

spk_imp_const_char_no_measure <- dat_tbl %>%
  impute_constant(.,
                  measure_vars = NULL,
                  fill = "M")

r_imp_const_char_no_measure <- dat %>%
  impute_constant(.,
                  measure_vars = NULL,
                  fill = "M")


# Compare both spark and R data set with impute_constant function ---------

spk_imp_const_no_measure_to_char <- to_char_ds(spk_imp_const_char_no_measure) 
r_imp_const_no_measure_to_char <- to_char_ds(r_imp_const_char_no_measure)


# Compare both spark and R data set with constant function --------------------

test_that("imputer constant value replace methods consistent", {
  expect_equal(
    spk_imp_const_no_measure_to_char %>%
      arrange(index) %>%
      collect() %>%
      as.data.frame() ,
    r_imp_const_no_measure_to_char %>%
      arrange(index) %>%
      collect() %>%
      as.data.frame()
  )
  
  expect_equal(colnames(spk_imp_const_no_measure_to_char), colnames(r_imp_const_no_measure_to_char))
})
