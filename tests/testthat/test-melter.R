



# melter unit tests ---------------------------------------------------------

library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)

context("melter function unit tests")

# Format Data
Iris <- iris %>%
  setNames(gsub("\\.", "_", tolower(colnames(iris)))) %>%
  mutate(species = as.character(species))

id_vars <- "species"
measure_vars <-
  c("sepal_length", "sepal_width", "petal_length", "petal_width")


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
iris_tbl <- copy_to(sc, Iris, overwrite = TRUE)


spk_melt <- melter(iris_tbl,
                   id_vars = id_vars,
                   measure_vars = measure_vars)


r_melt <- melter(Iris,
                 id_vars = id_vars,
                 measure_vars = measure_vars)

# Tests
test_that("melter methods consistent", {
  expect_equal(colnames(spk_melt), colnames(r_melt))
  expect_equal(spk_melt %>% collect(), r_melt)
})

test_that("melter methods return correct df dimensions", {
  expect_equal(nrow(r_melt), nrow(Iris) * length(measure_vars))
  expect_equal(sdf_nrow(spk_melt), nrow(Iris) * length(measure_vars))
})


spark_disconnect(sc)
