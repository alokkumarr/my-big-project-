
# Test Helpers Script -----------------------------------------------------


library(a2munge)
library(checkmate)
library(testthat)
library(sparklyr)
library(dplyr)

context("helper functions unit tests")


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
mtcars_tbl <- copy_to(sc, mtcars, overwrite = TRUE)



# Unit Tests -------------------------------------------------------------


test_that("schema methods are consistent", {

  r_schema <- schema(mtcars)
  spk_schema <- schema(mtcars_tbl)

  expect_equal( length(r_schema), length(spk_schema))
  expect_equal( colnames(r_schema), colnames(spk_schema))
  expect_equal(names(r_schema)[[1]], names(spk_schema)[[1]])
})


test_that("get_schema works as expected", {
  
  r_schema <- get_schema(mtcars)
  expect_list(r_schema)
  expect_equal(names(r_schema), colnames(mtcars))
})


test_that("schema_check works as expected", {
  
  x_schema <- get_schema(mtcars)
  y_schema <- get_schema(select(mtcars, mpg, am))
  expect_error(schema_check(x_schema, y_schema))
  
  y_schema <- get_schema(mutate(mtcars, am = as.character(am)))
  expect_error(schema_check(x_schema, y_schema))
  
  schema_compare <- schema_check(mtcars, mtcars)
  expect_equal(nrow(schema_compare), ncol(mtcars))
})
