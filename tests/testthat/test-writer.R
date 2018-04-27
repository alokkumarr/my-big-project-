
# writer unit tests -------------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)
library(checkmate)

context("writer function unit tests")


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
mtcars_tbl <- copy_to(sc, mtcars, overwrite = TRUE)


test_that("parquet file type with default value", {
  writer(mtcars_tbl, path = "~/writer-tests/mtcars.parquet")
  expect_file_exists("~/writer-tests/mtcars.parquet")
  file.remove("~/writer-tests/mtcars.parquet")
})


test_that("overwrite feature works as expected", {
  writer(mtcars_tbl, path = "~/writer-tests/mtcars.parquet")
  expect_file_exists("~/writer-tests/mtcars.parquet")
  writer(mtcars_tbl, path = "~/writer-tests/mtcars.parquet", mode = "overwrite")
  expect_file_exists("~/writer-tests/mtcars.parquet")
  expect_equal(length(dir("~/writer-tests")), 1)
  file.remove("~/writer-tests/mtcars.parquet")
})



test_that("append feature works as expected", {
  writer(mtcars_tbl, path = "~/writer-tests/mtcars.parquet")
  expect_file_exists("~/writer-tests/mtcars.parquet")
  writer(mtcars_tbl, path = "~/writer-tests/mtcars.parquet", mode = "append")
  expect_file_exists("~/writer-tests/mtcars-1.parquet")
  expect_equal(length(dir("~/writer-tests")), 2)
  file.remove("~/writer-tests/mtcars.parquet")
  file.remove("~/writer-tests/mtcars-1.parquet")
})



test_that("partition_by feature works as expected", {
  writer(mtcars_tbl, path = "~/writer-tests/mtcars.parquet", partition_by = "am")
  expect_directory_exists("~/writer-tests/am=1.0")
  expect_directory_exists("~/writer-tests/am=0.0")
  expect_file_exists("~/writer-tests/am=0.0/mtcars-am=0.0.parquet")
  expect_file_exists("~/writer-tests/am=1.0/mtcars-am=1.0.parquet")

  writer(mtcars_tbl, path = "~/writer-tests/mtcars.parquet",
         partition_by = "am", mode = "append")

  expect_equal(length(dir("~/writer-tests/am=0.0")), 2)
  expect_equal(length(dir("~/writer-tests/am=1.0")), 2)
  expect_file_exists("~/writer-tests/am=0.0/mtcars-am=0.0-1.parquet")
  expect_file_exists("~/writer-tests/am=1.0/mtcars-am=1.0-1.parquet")

  unlink("~/writer-tests/am=1.0", recursive = TRUE)
  unlink("~/writer-tests/am=0.0", recursive = TRUE)
})


test_that("csv feature works as expected", {
  writer(mtcars_tbl, path = "~/writer-tests/mtcars.csv")
  expect_file_exists("~/writer-tests/mtcars.csv")
  file.remove("~/writer-tests/mtcars.csv")
})


test_that("json feature works as expected", {
  writer(mtcars_tbl, path = "~/writer-tests/mtcars.json")
  expect_file_exists("~/writer-tests/mtcars.json")
  file.remove("~/writer-tests/mtcars.json")
})
