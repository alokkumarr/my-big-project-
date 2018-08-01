

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
  writer(mtcars_tbl, path = paste0(tmp, "/mtcars.parquet"))
  expect_class(reader(sc, "test", path = paste0(tmp, "/mtcars.parquet"), type = "parquet"),
               "tbl_spark")
  file.remove(paste0(tmp, "/mtcars.parquet"))
})


test_that("csv file type", {
  writer(mtcars_tbl, path = paste0(tmp, "/mtcars.csv"))
  expect_class(reader(sc, "test", path = paste0(tmp, "/mtcars.csv"), type = "csv"), "tbl_spark")
  file.remove(paste0(tmp, "/mtcars.csv"))
})


test_that("json file type", {
  writer(mtcars_tbl, path = paste0(tmp, "/mtcars.json"))
  expect_class(reader(sc, "test", path = paste0(tmp, "/mtcars.json"), type = "json"), "tbl_spark")
  file.remove(paste0(tmp, "/mtcars.json"))
})


test_that("directory path", {
  writer(mtcars_tbl, path = paste0(tmp, "/mtcars.json"))
  read_tbl <- reader(sc, "test", path = tmp, type = "json")
  expect_class(read_tbl, "tbl_spark")
  expect_equal(read_tbl %>%
                 collect() %>%
                 arrange(mpg),
               mtcars %>%
                 arrange(mpg))
  file.remove(paste0(tmp, "/mtcars.json"))
})


test_that("type input", {
  writer(mtcars_tbl, path = paste0(tmp, "/mtcars.json"))
  writer(mtcars_tbl %>% head(10), path = paste0(tmp, "/mtcars.csv"))

  read_json <- reader(sc, "json", path = paste0(tmp, "/mtcars.json"), type = "json")
  expect_class(read_json, "tbl_spark")
  expect_equal(sdf_nrow(read_json), nrow(mtcars))
  expect_equal(read_json %>%
                 collect() %>%
                 arrange(mpg),
               mtcars %>%
                 arrange(mpg))

  read_csv <- reader(sc, "csv", path = paste0(tmp, "/mtcars.csv"), type = "csv")
  expect_class(read_csv, "tbl_spark")
  expect_equal(sdf_nrow(read_csv), 10)

  file.remove(paste0(tmp, "/mtcars.json"))
  file.remove(paste0(tmp, "/mtcars.csv"))
})
