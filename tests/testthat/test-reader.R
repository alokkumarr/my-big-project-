

# Reader Unit Tests -------------------------------------------------------


library(testthat)
library(a2munge)
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

test_that("parquet file type", {
  writer(mtcars_tbl, path = paste0(tmp, "/mtcars.parquet"))
  assert_class(reader(sc, "test", path = paste0(tmp, "/mtcars.parquet")), "tbl_spark")
  file.remove(paste0(tmp, "/mtcars.parquet"))
})


test_that("csv file type", {
  writer(mtcars_tbl, path = paste0(tmp, "/mtcars.csv"))
  assert_class(reader(sc, "test", path = paste0(tmp, "/mtcars.csv")), "tbl_spark")
  file.remove(paste0(tmp, "/mtcars.csv"))
})


test_that("json file type", {
  writer(mtcars_tbl, path = paste0(tmp, "/mtcars.json"))
  assert_class(reader(sc, "test", path = paste0(tmp, "/mtcars.json")), "tbl_spark")
  file.remove(paste0(tmp, "/mtcars.json"))
})



test_that("directory path", {
  writer(mtcars_tbl, path = paste0(tmp, "/mtcars.json"))
  read_tbl <- reader(sc, "test", path = tmp)
  assert_class(read_tbl, "tbl_spark")
  expect_equal(read_tbl %>%
                 collect() %>%
                 arrange(mpg),
               mtcars_tbl %>%
                 collect() %>%
                 arrange(mpg))
  file.remove(paste0(tmp, "/mtcars.json"))
})
