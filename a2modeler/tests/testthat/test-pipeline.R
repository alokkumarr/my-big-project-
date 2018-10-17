

# Pipeline Uint Tests -----------------------------------------------------

library(a2modeler)
library(testthat)
library(checkmate)
library(sparklyr)
library(dplyr)

context("pipeline class unit tests")

n <- 100
x <- 1:n
y <- 1:n
df <- data.frame(x = x) %>%
  mutate(index = row_number())

df_y <- data.frame(y = y)

# Create Spark Connection
# spark_home_dir <- sparklyr::spark_installed_versions() %>%
#   as.data.frame() %>%
#   dplyr::filter(spark == "2.3.0") %>%
#   dplyr::pull(dir)
sc <- spark_connect(master = "local")

# Copy data to spark
dat <- copy_to(sc, df, overwrite = TRUE)

dat_y <- copy_to(sc, df_y, overwrite = TRUE)

time <-  Sys.time()


# Test-1:Pipeliner Constructer-Test  ----------------------------------------------

s1 <- new_pipeline(
  expr = function(df) {
    df %>%
      mutate (index_r = ifelse(index %% 5 == 0, 1, 0))
  },
  output = NULL,
  desc = "Example pipeline",
  created_on = Sys.time(),
  runtime = NULL,
  uid = "pipe-test"
)

test_that("Pipeliner Constructer", {
  expect_class(s1, "pipeline")
})

# flow and execute output validation --------------------------------------

flow_test_pipe_construct <- a2modeler::flow(df, s1)
pipe_ex_constructer <- execute(df, s1)
ex_out_constructer <- pipe_ex_constructer$output

test_that("Test that Flow and execute output matches", {
  expect_equal(flow_test_pipe_construct, ex_out_constructer)
})


# Flush method validation -------------------------------------------------

clean_pipe_constructer <- clean(s1)

test_that("Post clean up the output should be NULL", {
  expect_equal(clean_pipe_constructer$output, NULL)
})


# Test-2:With expr = IDENTITY in pipeline constructer ---------------------

s1_id <- new_pipeline(
  expr = identity,
  output = NULL,
  desc = "Example pipeline",
  created_on = Sys.time(),
  runtime = NULL,
  uid = "pipe-test"
)

test_that("Pipeliner Constructer with expression=identity", {
  expect_class(s1_id, "pipeline")
})


# flow and execute output validation --------------------------------------

flow_test_pipe_construct_id <- a2modeler::flow(df, s1_id)
pipe_ex_constructer_id <- execute(df, s1_id)
ex_out_constructer_id <- pipe_ex_constructer_id$output

test_that("Test that Flow and execute output matches", {
  expect_equal(flow_test_pipe_construct_id, ex_out_constructer_id)
})


# Flush method validation -------------------------------------------------

clean_pipe_constructer_id <- clean(s1_id)

test_that("Post clean up the output should be NULL", {
  expect_equal(clean_pipe_constructer_id$output, NULL)
})



# Test-3: Pipeliner function-R Dataframe-flow,execute and clean me --------


pipe_1 <- pipeline(
  expr = function(df) {
    df %>%
      mutate (index_r = ifelse(index %% 4 == 0, 1, 0))
  },
  desc = "Example pipeline output"
)
test_that("Pipeliner function", {
  expect_class(pipe_1, "pipeline")
})


# flow and execute output validation --------------------------------------

flow_test <- a2modeler::flow(df, pipe_1)
pipe_ex <- execute(df, pipe_1)
ex_out <- pipe_ex$output

test_that("Test that Flow and execute output matches", {
  expect_equal(flow_test, ex_out)
})


# Flush method validation -------------------------------------------------

clean_pipe <- clean(pipe_1)

test_that("Post clean up the output should be NULL", {
  expect_equal(clean_pipe$output, NULL)
})


# Test-4-Create a "modeler" to test-execute,clean and flow with pipeline -------

add_mod <-
  segmenter(df = dat_y, name = "test") %>%
  add_model(pipe = pipeline(
    expr = function(dat_y)
      dat_y
  ),
  method = "ml_kmeans")


# Use modeler obj and create pipeline -------------------------------------


pipe_test <- pipeline(
  expr = function(add_mod) {
    add_mod %>%
      mutate (index_r = ifelse(index %% 6 == 0, 1, 0))
  },
  desc = "Example pipeline output"
)

test_that("Pipeliner Constructer", {
  expect_class(pipe_test, "pipeline")
})

# flow and execute output validation --------------------------------------

flow_test <- a2modeler::flow(df, pipe_test)
pipe_ex <- execute(df, pipe_test)
ex_out <- pipe_ex$output

test_that("Test that Flow and execute output matches", {
  expect_equal(flow_test, ex_out)
})


# Flush method validation -------------------------------------------------

clean_pipe <- clean(pipe_test)

test_that("Post clean up the output should be NULL", {
  expect_equal(clean_pipe$output, NULL)
})



# Test-5:Create Spark DF and test pipeline methods like flow,execute a --------

pipe_sprk <- pipeline (
  expr = function(dat) {
    dat %>%
      mutate (index_r = ifelse(index %% 4 == 0, 1, 0))
  },
  desc = "Example pipeline"
)

test_that("Pipeliner Constructer", {
  expect_class(pipe_sprk, "pipeline")
})

# flow and execute output validation --------------------------------------

flow_test_sprk <- a2modeler::flow(dat, pipe_sprk)
pipe_ex_sprk <- execute(dat, pipe_sprk)
ex_out_sprk <- pipe_ex_sprk$output

test_that("Test that Flow and execute output matches", {
  expect_equal(flow_test_sprk, ex_out_sprk)
})


# Flush method validation -------------------------------------------------

clean_pipe_sprk <- clean(pipe_sprk)

test_that("Post clean up the output should be NULL", {
  expect_equal(clean_pipe_sprk$output, NULL)
})



# Test-6: Modeler Object creation  and test flow,execute and clean methods---------------------------------------------

mobj <- modeler(
  df = df,
  target = NULL,
  type = "segmenter",
  name = "test_segment1"
)

pipe_modeler <- pipeline (
  expr = function(mobj) {
    mobj %>%
      mutate (index_r = ifelse(index %% 5 == 0, 1, 0))
  },
  desc = "Example pipeline"
)

test_that("Pipeliner Constructer", {
  expect_class(pipe_modeler, "pipeline")
})


# flow and execute output validation --------------------------------------

flow_test_modeler <- a2modeler::flow(df, pipe_modeler)
pipe_ex <- execute(df, pipe_modeler)
ex_out_modeler <- pipe_ex$output

test_that("Test that Flow and execute output matches", {
  expect_equal(flow_test_modeler, ex_out_modeler)
})


# Flush method validation -------------------------------------------------

clean_pipe_modeler <- clean(pipe_modeler)

test_that("Post clean up the output should be NULL", {
  expect_equal(clean_pipe_modeler$output, NULL)
})
