
# writer unit tests -------------------------------------------------------


library(a2munge)
library(testthat)
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
 
  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "parquet")
  .file <- dir(tmp, full.names = TRUE)
  expect_file_exists( .file)
  file.remove(.file)
})


test_that("replace feature works as expected", {
 
  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "parquet")
  .file <- dir(tmp, full.names = TRUE)
  expect_file_exists( .file)

  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "parquet", mode = "replace")
  .file <- dir(tmp, full.names = TRUE)
  expect_equal(length(dir(tmp)), 1)
  file.remove(.file)
})



test_that("append feature works as expected", {
 
  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "parquet")
  .file <- dir(tmp, full.names = TRUE)
  expect_file_exists( .file)

  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "parquet", mode = "append")
  expect_file_exists(paste0(tmp, "/mtcars-part-00001.parquet"))
  expect_equal(length(dir(tmp)), 2)

  for(.f in dir(tmp, full.names = TRUE)) file.remove(.f)
})



test_that("partition_by feature works as expected", {
 
  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "parquet", partition_by = "am")
  expect_directory_exists(paste0(tmp, "/am=1.0"))
  expect_directory_exists(paste0(tmp, "/am=0.0"))
  expect_file_exists(paste0(tmp, "/am=0.0/mtcars-am=0.0-part-00000.parquet"))
  expect_file_exists(paste0(tmp, "/am=1.0/mtcars-am=1.0-part-00000.parquet"))

  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "parquet",
         partition_by = "am", mode = "append")

  expect_equal(length(dir(paste0(tmp,"/am=0.0"))), 2)
  expect_equal(length(dir(paste0(tmp, "/am=1.0"))), 2)
  expect_file_exists(paste0(tmp, "/am=0.0/mtcars-am=0.0-part-00000.parquet"))
  expect_file_exists(paste0(tmp, "/am=1.0/mtcars-am=1.0-part-00000.parquet"))

  unlink(paste0(tmp, "/am=1.0"), recursive = TRUE)
  unlink(paste0(tmp, "/am=0.0"), recursive = TRUE)
})


test_that("csv feature works as expected", {

  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "csv")
  .file <- dir(tmp, full.names = TRUE)
  expect_file_exists(.file)
  file.remove(.file)
})


test_that("json feature works as expected", {
 
  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "json")
  .file <- dir(tmp, full.names = TRUE)
  expect_file_exists( .file)
  file.remove(.file)
})


test_that("data.frame method works as expected", {
 
  writer(mtcars, path = tmp, name = "mtcars", type = "csv")
  expect_file_exists(paste0(tmp, "/mtcars.csv"))

  writer(mtcars, path = tmp, name = "mtcars", mode = "append", type = "csv")
  expect_file_exists(paste0(tmp, "/mtcars.csv"))
  expect_equal(length(dir(tmp)), 1)
  expect_equal(nrow(read.csv(paste0(tmp, "/mtcars.csv"))), nrow(mtcars) * 2)
  file.remove(paste0(tmp, "/mtcars.csv"))
})

