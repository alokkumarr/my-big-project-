

# Reader Unit Tests -------------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)

context("reader function unit tests")


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
mtcars_tbl <- copy_to(sc, mtcars, overwrite = TRUE)

# Temp Directory
tmp <- paste0(tempdir(), "/reader-tests")


# Test Bed ----------------------------------------------------------------

test_that("parquet file type", {
  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "parquet")
  .file <- dir(tmp, full.names = TRUE)
  expect_class(reader(sc, "test", path = .file, type = "parquet"),
               "tbl_spark")
  file.remove( .file)
})


test_that("csv file type", {
  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "csv")
  .file <- dir(tmp, full.names = TRUE)
  expect_class(reader(sc, "test", path = .file, type = "csv"), "tbl_spark")
  file.remove( .file)
})


test_that("json file type", {
  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "json")
  .file <- dir(tmp, full.names = TRUE)
  expect_class(reader(sc, "test", path = .file, type = "json"), "tbl_spark")
  file.remove(.file)
})


test_that("directory path", {
  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "json")
  .file <- dir(tmp, full.names = TRUE)
  read_tbl <- reader(sc, "test", path = tmp, type = "json")
  expect_class(read_tbl, "tbl_spark")
  expect_equal(read_tbl %>%
                 collect() %>%
                 arrange(mpg),
               mtcars %>%
                 arrange(mpg))
  file.remove(.file)
})


test_that("type input", {
  writer(mtcars_tbl, path = tmp, name = "mtcars", type = "json")
  .file <- dir(tmp, full.names = TRUE)
  read_json <- reader(sc, "json", path = .file, type = "json")
  expect_class(read_json, "tbl_spark")
  expect_equal(sdf_nrow(read_json), nrow(mtcars))
  expect_equal(read_json %>%
                 collect() %>%
                 arrange(mpg),
               mtcars %>%
                 arrange(mpg))
  file.remove(.file)

  writer(mtcars_tbl %>% head(10), path = tmp, name = "mtcars", type = "csv")
  .file <- dir(tmp, full.names = TRUE)
  read_csv <- reader(sc, "csv", path = .file, type = "csv")
  expect_class(read_csv, "tbl_spark")
  expect_equal(sdf_nrow(read_csv), 10)
  file.remove(.file)
})
