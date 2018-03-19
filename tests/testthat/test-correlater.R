
# correlater unit tests ---------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)

context("correlater function unit tests")


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
mtcars_tbl <- copy_to(sc, mtcars, overwrite = TRUE)


# corr function
r_corr_all <- mtcars %>% corr(.)
r_corr_mpg <- mtcars %>% corr(., "mpg")
spk_corr_all <- mtcars_tbl %>% corr(.) %>% collect() %>% as.data.frame()
spk_corr_mpg <- mtcars %>% corr(., "mpg") %>% collect() %>% as.data.frame()


test_that("cor methods consistent", {
  expect_equal( r_corr_all, spk_corr_all)
  expect_equal( r_corr_mpg, spk_corr_mpg)
  expect_equal(colnames( r_corr_all), colnames(spk_corr_all))
})


test_that("cor returns correct dimensions", {
  expect_equal(nrow(r_corr_all), ncol(mtcars))
  expect_equal(ncol(r_corr_all), ncol(mtcars)+1)
})


# correlater function
r_correlater <- mtcars %>%
  correlater(.) %>%
  arrange(var1)
r_correlater_std <- mtcars %>%
  correlater(., transform = "standardize") %>%
  arrange(var1)
r_correlater_nrm <- mtcars %>%
  correlater(., transform = "normalize") %>%
  arrange(var1)
spk_correlater <- mtcars_tbl %>%
  correlater(., collect = FALSE) %>%
  collect() %>%
  as.data.frame() %>%
  arrange(var1)
spk_correlater_std <- mtcars %>%
  correlater(., transform = "standardize") %>%
  collect() %>%
  as.data.frame() %>%
  arrange(var1)
spk_correlater_nrm <- mtcars %>%
  correlater(., transform = "normalize") %>%
  collect() %>%
  as.data.frame() %>%
  arrange(var1)
spk_correlater_coll <- mtcars_tbl %>%
  correlater(., collect = TRUE) %>%
  collect() %>%
  as.data.frame() %>%
  arrange(var1)


test_that("correlater methods consistent", {
  expect_equal( r_correlater, spk_correlater)
  expect_equal( spk_correlater, spk_correlater_coll)
  expect_equal( r_correlater_std, spk_correlater_std)
  expect_equal( r_correlater_nrm, spk_correlater_nrm)
  expect_equal(colnames( r_correlater), colnames(spk_correlater))
})


test_that("correlater returns correct dimensions", {
  expect_equal(nrow(r_correlater), ncol(mtcars)^2)
  expect_equal(ncol(r_correlater), 3)
})


test_that("correlater inputs work as expected", {
  expect_error(mtcars %>% correlater(., transform = "gobbler"))
})

