
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

# Temp Directory
tmp <- paste0(tempdir(), "/writer-tests")


test_that("parquet file type with default value", {
  .path <- paste0(tmp, "/mtcars.parquet")
  writer(mtcars_tbl, path = .path)
  expect_file_exists(.path)
  file.remove(.path)
})


test_that("overwrite feature works as expected", {
  .path <- paste0(tmp, "/mtcars.parquet")
  writer(mtcars_tbl, path = .path)
  expect_file_exists(.path)
  writer(mtcars_tbl, path = .path, mode = "overwrite")
  expect_file_exists(.path)
  expect_equal(length(dir(tmp)), 1)
  file.remove(.path)
})



test_that("append feature works as expected", {
  .path <- paste0(tmp, "/mtcars.parquet")
  writer(mtcars_tbl, path = .path)
  expect_file_exists(.path)
  writer(mtcars_tbl, path = .path, mode = "append")
  expect_file_exists(paste0(tmp, "/mtcars-1.parquet"))
  expect_equal(length(dir(tmp)), 2)
  file.remove(.path)
  file.remove(paste0(tmp, "/mtcars-1.parquet"))
})



test_that("partition_by feature works as expected", {
  .path <- paste0(tmp, "/mtcars.parquet")
  writer(mtcars_tbl, path = .path, partition_by = "am")
  expect_directory_exists(paste0(tmp, "/am=1.0"))
  expect_directory_exists(paste0(tmp, "/am=0.0"))
  expect_file_exists(paste0(tmp, "/am=0.0/mtcars-am=0.0.parquet"))
  expect_file_exists(paste0(tmp, "/am=1.0/mtcars-am=1.0.parquet"))

  writer(mtcars_tbl, path = .path, partition_by = "am", mode = "append")

  expect_equal(length(dir(paste0(tmp,"/am=0.0"))), 2)
  expect_equal(length(dir(paste0(tmp, "/am=1.0"))), 2)
  expect_file_exists(paste0(tmp, "/am=0.0/mtcars-am=0.0-1.parquet"))
  expect_file_exists(paste0(tmp, "/am=1.0/mtcars-am=1.0-1.parquet"))

  unlink(paste0(tmp, "/am=1.0"), recursive = TRUE)
  unlink(paste0(tmp, "/am=0.0"), recursive = TRUE)
})


test_that("csv feature works as expected", {
  .path <- paste0(tmp, "/mtcars.csv")
  writer(mtcars_tbl, path = .path)
  expect_file_exists(.path)
  file.remove(.path)
})


test_that("json feature works as expected", {
  .path <- paste0(tmp, "/mtcars.json")
  writer(mtcars_tbl, path = .path)
  expect_file_exists(.path)
  file.remove(.path)
})


test_that("data.frame method works as expected", {
  .path <- paste0(tmp, "/mtcars.csv")
  writer(mtcars_tbl, path = .path)
  expect_file_exists(.path)
  writer(mtcars, path = .path, mode = "append")
  expect_file_exists(.path)
  expect_equal(length(dir(tmp)), 1)
  expect_equal(nrow(read.csv(.path)), nrow(mtcars) * 2)
  file.remove(.path)
})

