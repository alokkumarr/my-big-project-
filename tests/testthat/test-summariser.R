

# summariser unit tests ---------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)

context("summariser function unit tests")

# Format Data
Iris <- iris %>%
  setNames(gsub("\\.", "_", tolower(colnames(iris)))) %>%
  mutate(species = as.character(species))

group_vars <- "species"
measure_vars <-
  c("sepal_length", "sepal_width", "petal_length", "petal_width")
fun <- "sum"


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
iris_tbl <- copy_to(sc, Iris, overwrite = TRUE)


spk_agg <- summariser(iris_tbl, group_vars, measure_vars, fun) %>%
  collect() %>%
  arrange_at(group_vars)

r_agg <- summariser(Iris, group_vars, measure_vars, fun) %>%
  arrange_at(group_vars)


test_that("summariser methods consistent", {
  expect_equal(
    spk_agg %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_agg %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(colnames(spk_agg), colnames(r_agg))
})


test_that("summariser return correct dimensions", {
  expect_equal(nrow(spk_agg), length(unique(Iris[[group_vars]])))
  expect_equal(nrow(r_agg), length(unique(Iris[[group_vars]])))
})


test_that("aggregation function works as expected", {
  expect_equal(summariser(Iris, NULL, measure_vars[1], "sum")[[1]], sum(Iris[[measure_vars[1]]]))
  expect_equal(summariser(iris_tbl, NULL, measure_vars[1], "sum") %>% collect() %>% .[[1]],
               sum(Iris[[measure_vars[1]]]))
})


test_that("column naming logic is consistent", {
  expect_equal(colnames(summariser(Iris, NULL, measure_vars[1], "sum")),
               colnames(summariser(iris_tbl, NULL, measure_vars[1], "sum")))

  expect_equal(colnames(summariser(Iris, NULL, measure_vars[1:2], "sum")),
               colnames(summariser(iris_tbl, NULL, measure_vars[1:2], "sum")))

  expect_equal(colnames(summariser(
    Iris, NULL, measure_vars[1], c("sum", "mean")
  )),
  colnames(summariser(
    iris_tbl, NULL, measure_vars[1], c("sum", "mean")
  )))

  expect_equal(colnames(summariser(
    Iris, NULL, measure_vars[1:2], c("sum", "mean")
  )),
  colnames(summariser(
    iris_tbl, NULL, measure_vars[1:2], c("sum", "mean")
  )))
})


test_that("summariser_args correctly checks inputs", {
  expect_error(summariser(Iris, NULL, NULL, "sum"))
  expect_error(summariser(Iris, "species", NULL, "bucket"))
  expect_error(summariser(iris_tbl, "species", NULL, "bucket"))
})


test_that("Count only feature works as expected", {

  d1 <- Iris %>% count_(group_vars) %>% .[[2]]

  expect_equal(d1, summariser(Iris, group_vars, NULL) %>% .[[2]])
  expect_equal(d1, summariser(iris_tbl, group_vars, NULL) %>% collect() %>% .[[2]])
})
