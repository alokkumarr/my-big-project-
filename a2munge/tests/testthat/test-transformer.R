
# transformers unit tests -------------------------------------------------



library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)
library(zoo)

context("correlater function unit tests")


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
mtcars_tbl <- copy_to(sc, mtcars, overwrite = TRUE)



# standardize function
r_std <- mtcars %>%
  standardizer(., measure_vars = c("mpg", "hp")) %>%
  select_at(colnames(mtcars))

r_std_am <- mtcars %>%
  standardizer(.,
               group_vars = "am",
               measure_vars = c("mpg", "hp")) %>%
  as.data.frame() %>%
  select_at(colnames(mtcars)) %>%
  arrange(disp)

spk_std <- mtcars_tbl %>%
  standardizer(., measure_vars = c("mpg", "hp")) %>%
  collect() %>%
  as.data.frame() %>%
  select_at(colnames(mtcars))

spk_std_am <- mtcars_tbl %>%
  standardizer(.,
               group_vars = "am",
               measure_vars = c("mpg", "hp")) %>%
  collect() %>%
  as.data.frame() %>%
  select_at(colnames(mtcars)) %>%
  arrange(disp)


test_that("standarizer methods consistent", {
  expect_equal( r_std, spk_std)
  expect_equal( r_std_am, spk_std_am)
  expect_equal(colnames( r_std), colnames(spk_std))
})


test_that("standarizer returns correct dimensions", {
  expect_equal(nrow(r_std), nrow(mtcars))
  expect_equal(ncol(r_std), ncol(r_std))
})


test_that("standarizer returns correct result", {
  expect_equal(r_std$mpg, (mtcars$mpg - mean(mtcars$mpg))/sd(mtcars$mpg))
})


# normalize function
r_nrm <- mtcars %>%
  normalizer(., measure_vars = c("mpg", "hp")) %>%
  select_at(colnames(mtcars))

r_nrm_am <- mtcars %>%
  normalizer(.,
               group_vars = "am",
               measure_vars = c("mpg", "hp")) %>%
  as.data.frame() %>%
  select_at(colnames(mtcars)) %>%
  arrange(disp)

spk_nrm <- mtcars_tbl %>%
  normalizer(., measure_vars = c("mpg", "hp")) %>%
  select_at(colnames(mtcars)) %>%
  collect() %>%
  as.data.frame()

spk_nrm_am <- mtcars_tbl %>%
  normalizer(.,
               group_vars = "am",
               measure_vars = c("mpg", "hp")) %>%
  as.data.frame() %>%
  select_at(colnames(mtcars)) %>%
  arrange(disp)


test_that("normalizer methods consistent", {
  expect_equal( r_nrm, spk_nrm)
  expect_equal( r_nrm_am, spk_nrm_am)
  expect_equal(colnames( r_nrm), colnames(spk_nrm))
})


test_that("normalizer returns correct dimensions", {
  expect_equal(nrow(r_nrm), nrow(mtcars))
  expect_equal(ncol(r_nrm), ncol(r_nrm))
})


test_that("normalizer returns correct result", {
  expect_equal(r_nrm$mpg, (mtcars$mpg - min(mtcars$mpg))/max(mtcars$mpg))
})
