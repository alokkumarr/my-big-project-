
# Test Helpers Script -----------------------------------------------------


library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)

context("helper functions unit tests")


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
mtcars_tbl <- copy_to(sc, mtcars, overwrite = TRUE)



# Schema Unit Tests -------------------------------------------------------


test_that("schema methods are consistent", {

  r_schema <- schema(mtcars)
  spk_schema <- schema(mtcars_tbl)

  expect_equal( length(r_schema), length(spk_schema))
  expect_equal( colnames(r_schema), colnames(spk_schema))
  expect_equal(names(r_schema)[[1]], names(spk_schema)[[1]])
})
