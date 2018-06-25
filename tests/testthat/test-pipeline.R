

# Pipeline Uint Tests -----------------------------------------------------

library(testthat)
library(checkmate)
library(a2modeler)
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
spark_home_dir <- sparklyr::spark_installed_versions() %>%
  as.data.frame() %>%
  dplyr::filter(spark == "2.3.0") %>%
  dplyr::pull(dir)
sc <- spark_connect(master = "local", spark_home = spark_home_dir)

# Copy data to spark
dat <- copy_to(sc, df, overwrite = TRUE)

dat_y <- copy_to(sc, df_y, overwrite = TRUE)

time <-  Sys.time()


# Test:Pipeliner Constructer ----------------------------------------------


test_that("Pipeliner Constructer", {
  s1 <- new_pipeline(
    expr = function(df) {
      df %>%
        mutate (index_r = ifelse(index %% 5 == 0, 1, 0))
    },
    output = NULL,
    desc = "Example pipeline",
    created_on = Sys.time(),
    runtime = time
  )
  expect_class(s1, "segmenter")
})


############## with expr = IDENTITY

test_that("Pipeliner Constructer", {
  s1 <- new_pipeline(
    expr = identity,
    output = NULL,
    desc = "Example pipeline",
    created_on = Sys.time(),
    runtime = time
  )
  expect_class(s1, "segmenter")
})

############################################################


# Create a R DF,spark DF and modeler to test-execute,flush and flow -------

add_mod <-
  new_segmenter(df = dat_y, name = "test") %>%
  add_model(pipe = pipeline(
    expr = function(dat_y)
      dat_y
  ),
  method = "ml_kmeans")



pipe_r <- pipeline (
  expr = function(df) {
    df %>%
      mutate (index_r = ifelse(index %% 5 == 0, 1, 0))
  },
  desc = "Example pipeline"
)


pipe_sprk <- pipeline (
  expr = function(dat) {
    dat %>%
      mutate (index_r = ifelse(index %% 4 == 0, 1, 0))
  },
  desc = "Example pipeline"
)


pipe_mod <- pipeline (
  expr = function(add_mod) {
    add_mod %>%
      mutate (index_r = ifelse(index %% 5 == 0, 1, 0))
  },
  desc = "Example pipeline"
)


# Modeler Object creation -------------------------------------------------


mobj <- modeler(df=df,
                target = NULL,
                type = "segmenter",
                name = "test_segment1")

pipe_modeler <- pipeline (
  expr = function(mobj) {
    mobj %>%
      mutate (index_r = ifelse(index %% 5 == 0, 1, 0))
  },
  desc = "Example pipeline"
)
