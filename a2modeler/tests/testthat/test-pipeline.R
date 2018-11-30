

# Pipeline Uint Tests -----------------------------------------------------

library(a2modeler)
library(testthat)
library(checkmate)
library(sparklyr)
library(dplyr)

context("pipeline class unit tests")


# Create Spark Connection
sc <- spark_connect(master = "local")

# Copy data to spark
sim_tbl_ts <- copy_to(sc, sim_df_ts, overwrite = TRUE)


# Example Pipeline
pipe1 <- pipeline(expr = identity,
                  desc = "Example pipeline",
                  uid = "pipe-test")

# Test-1 :Pipeliner Constructer-Test  ----------------------------------------------


test_that("Pipeliner Constructer", {
  
  expect_class(pipe1, "pipeline")
})

# flow and execute output validation --------------------------------------


test_that("Test that Flow and execute output matches", {
  
  flow_test_pipe_construct <- flow(sim_df_ts, pipe1)
  pipe_ex_constructer <- execute(sim_df_ts, pipe1)
  ex_out_constructer <- pipe_ex_constructer$output
  
  expect_equal(flow_test_pipe_construct, ex_out_constructer)
})


# Flush method validation -------------------------------------------------

clean_pipe_constructer <- clean(pipe1)

test_that("Post clean up the output should be NULL", {
  expect_equal(clean_pipe_constructer$output, NULL)
})




# Test-5: Create Spark DF and test pipeline methods -----------------------

pipe2 <- pipeline (expr = identity,
                   desc = "Spark Example pipeline",
                   uid  = "Spark-pipe")

test_that("Pipeliner Constructer", {
  expect_class(pipe2, "pipeline")
})

# flow and execute output validation --------------------------------------

flow_test_sprk <- flow(sim_tbl_ts, pipe2)
pipe_ex_sprk <- execute(sim_tbl_ts, pipe2)
ex_out_sprk <- pipe_ex_sprk$output

test_that("Test that Flow and execute output matches", {
  expect_equal(flow_test_sprk, ex_out_sprk)
})


# Flush method validation -------------------------------------------------

clean_pipe_sprk <- clean(pipe2)

test_that("Post clean up the output should be NULL", {
  expect_equal(clean_pipe_sprk$output, NULL)
})


