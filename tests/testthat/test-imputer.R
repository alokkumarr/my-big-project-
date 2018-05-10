# Imputer Unit Tests -----------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)
library(devtools)

context("imputer function unit tests")


# Function to create simulated data
sim_data <- function(n_ids, n_recs, n_iter, seed = 319){

  n <- n_ids * n_recs
  ids <- 1:n_ids
  dates <- seq(from=Sys.Date()-365, to=Sys.Date(), by="day")
  cat1 <- c("A", "B")
  cat2 <- c("X", "Y", "Z")


  do.call("rbind",
          replicate(n_iter,
                    {
                      data.frame(id = sample(ids, n, replace=T),
                                 date = sample(dates, n, replace = T),
                                 cat1 = as.character(sample(cat1, n, replace = T)),
                                 cat2 = as.character(sample(cat2, n, replace = T)),
                                 metric1 = sample(1:7, n, replace = T),
                                 metric2 = rnorm(n, mean=50, sd = 5),
                                 metric3 = sample(11:15, n, replace = T))
                    },
                    simplify = FALSE)
  )
}

# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

#-------------------------------------------Data Creation-------------------------------------------------------------------------------


dat <- sim_data(3,3, 1, seed = 319)%>%
mutate(index = row_number())

dat <- dat %>%
  mutate(date = as.Date(date))

der_dat <- dat %>%
  select(., id, date, cat1, cat2, metric1, metric2,metric3,index) %>%
  mutate(., date = as.character(date))

# Load data into Spark-As the date can't be directly loaded into spark with Date data type-first convert to character and load to spark once loaded ,update the data type back to Date
dat_tbl <- copy_to(sc, der_dat, overwrite = TRUE)


# Create Missing Values-As Date field is already in Date format in R data frame no need to type cast in "dat",but in dat_tbl[Spark] update it to Date format
dat <- dat %>%
  mutate(
         cat1 = ifelse(row_number(id)%%3==0, NA, as.character(cat1)),
         metric1 = ifelse(metric1 == 5, NA, metric1),
         metric3 = ifelse(metric3 == 13, NA, metric3))

dat_tbl <- dat_tbl %>%
  arrange(., id) %>%
  mutate(date = as.Date(date),
         cat1 = ifelse(row_number(id)%%3==0, NA, as.character(cat1)),
         metric1 = ifelse(metric1 == 5, NA, metric1),
         metric3 = ifelse(metric3 == 13, NA, metric3))
         

#--------------------------------------------Data Creation End --------------------------------------------------------------------------


mean(dat$metric1, na.rm = TRUE)

dat_tbl %>%
  filter(., !is.na(metric1)) %>%
  summarise(., mn_metric1 = mean(metric1))

dat_tbl %>%
  filter(., !is.na(metric3)) %>%
  summarise(., mn_metric3 = mean(metric3))


#----------------------------# Test Bed # ----------------------------------------------------------------


#Test 1 :Apply "mean" function on measure var metric1 and compare both Spark and R dataset


spk_imp <- dat_tbl %>%
  imputer(., group_vars = NULL,
         measure_vars = c("metric1"),
         fun = "mean"
         )

r_imp <- dat %>%
  imputer(., group_vars = NULL,
         measure_vars = c("metric1"),
         fun = "mean"
         )


#Compare both spark and R data set with mean function

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


#Test 2:Apply "mode" as function on measure var metric1 and compare both Spark and R dataset

Mode_value_count <- dat_tbl %>%
  filter(., !is.na(metric1)) %>%
  summariser(., group_vars = c("metric1"),
             fun = "n_distinct")

spk_imp_mode <- dat_tbl %>%
  imputer(., group_vars = NULL,
         measure_vars = c("metric1"),
         fun = "mode"
         )

r_imp_mode <- dat %>%
  imputer(., group_vars = NULL,
         measure_vars = c("metric1"),
         fun = "mode"
         )


#Compare both spark and R data set with mode function

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


#Test 3:Apply "constant" as function on measure var metric1 and compare both Spark and R dataset

spk_imp_const <- dat_tbl %>%
  imputer(., measure_vars = c("metric1"),
          fun = "constant",
         fill = 8
         )

r_imp_const <- dat %>%
  imputer(., measure_vars = c("metric1"),
          fun = "constant",
         fill = 8
         )

#Compare both spark and R data set with constant function

test_that("imputer constant value replace methods consistent", {
    expect_equal(
      spk_imp_const %>%
        collect() %>%
        arrange(id, date, cat1, cat2) %>%
        select_if(is.numeric) %>%
        as.data.frame() %>%
        round(5) ,
      r_imp_const %>%
        arrange(id, date, cat1, cat2) %>%
        select_if(is.numeric) %>%
        as.data.frame() %>%
        round(5)
    )

  expect_equal(colnames(spk_imp_const), colnames(r_imp_const))
})


#Test 4:Apply "constant" as function on measure var cat1 to check missing charecter replacement

spk_imp_const_char <- dat_tbl %>%
  imputer(., measure_vars = c("cat1"),
          fun = "constant",
          fill = "K"
  )

r_imp_const_char <- dat %>%
  imputer(., measure_vars = c("cat1"),
          fun = "constant",
          fill = "K"
  )

#Compare both spark and R data set with constant function

test_that("imputer constant value replace methods consistent", {
  expect_equal(
    spk_imp_const_char %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_imp_const_char %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  
  expect_equal(colnames(spk_imp_const_char), colnames(r_imp_const_char))
})



#Test 5:Apply mean function when no measure is mentioned :so impute should consider all numeric columns to consideration to replace the missing values 

spk_imp_no_measure <- dat_tbl %>%
  imputer(.,fun = "mean")

r_imp_no_measure <- dat %>%
  imputer(.,fun = "mean")


#Compare both spark and R data set with mean function

test_that("imputer mean methods consistent", {
  expect_equal(
    spk_imp_no_measure %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_imp_no_measure %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_imp_no_measure), colnames(r_imp_no_measure))
})


#Test 6 :Apply mode function when no measure is mentioned:so impute should consider all(both numeric and non-numeric values) columns to consideration to replace the missing values

##################################ERRORRRRRR###############################################

spk_mode_no_measure <- dat_tbl %>%
  imputer(.,fun = "mode")

r_mode_no_measure <- dat %>%
  imputer(.,fun = "mode")


#Compare both spark and R data set with mean function

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
  expect_equal(colnames(spk_mode_no_measure), colnames(r_mode_no_measure))
})


#Test 7 :Apply constant function when no measure is mentioned:so impute should consider all(both numeric and non-numeric values) columns to consideration to replace the missing values

spk_const_no_measure <- dat_tbl %>%
  imputer(.,fun = "constant",
          fill=111)

r_const_no_measure <- dat %>%
  imputer(.,fun = "constant",
          fill=111)


#Compare both spark and R data set with mean function

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
  expect_equal(colnames(spk_const_no_measure), colnames(r_const_no_measure))
})



#Test 8 :Test imputer with mean function with Grouped variables 

spk_imp_group_mean <- dat_tbl %>%
  imputer(., group_vars = c("cat2"),
          measure_vars = c("metric1","metric3"),
          fun = "mean"
  )

r_imp_group_mean <- dat %>%
  imputer(., group_vars = c("cat2"),
          measure_vars = c("metric1","metric3"),
          fun = "mean"
  )

#spk_imp_group_mean-ungroup,r_imp_group_mean it


#Compare both spark and R data set with mean function

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
  expect_equal(colnames(spk_imp_ungroup_mean), colnames(r_imp_ungroup_mean))
})

#Test 9 :Test imputer with mode function with Grouped variables 

spk_imp_group_mode <- dat_tbl %>%
  imputer(., group_vars = c("cat2"),
          measure_vars = c("metric1","metric3"),
          fun = "mode"
  )

#############ERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRR####################################################

r_imp_group_mode <- dat %>%
  imputer(., group_vars = c("cat2"),
          measure_vars = c("metric1","metric3"),
          fun = "mode"
  )


#Compare both spark and R data set with mean function

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
  expect_equal(colnames(spk_imp_ungroup_mode), colnames(r_imp_ungroup_mode))
})


#Test 10 :Test imputer with mode function with Grouped variables 

spk_imp_group_const <- dat_tbl %>%
  imputer(., group_vars = c("cat2"),
          measure_vars = c("metric1","metric3"),
          fun = "constant",
          fill=5
          
  )


r_imp_group_const <- dat %>%
  imputer(., group_vars = c("cat2"),
          measure_vars = c("metric1","metric3"),
          fun = "constant",
          fill=5
  )


#Compare both spark and R data set with mean function

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
  expect_equal(colnames(spk_imp_ungroup_const), colnames(r_imp_ungroup_const))
})


#Test 11 :Apply constant function with character when no measure is mentioned:so impute should consider all(non-numeric values) columns to consideration to replace the missing values

spk_const_char_no_measure <- dat_tbl %>%
  imputer(.,fun = "constant",
          fill="S")

r_const_char_no_measure <- dat %>%
  imputer(.,fun = "constant",
          fill="S")


#Compare both spark and R data set with mean function

test_that("imputer mean methods consistent", {
  expect_equal(
    spk_const_char_no_measure %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_const_char_no_measure %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_const_char_no_measure), colnames(r_const_char_no_measure))
})


