


# pivoter unit tests ---------------------------------------------------------

library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)

context("pivoter function unit tests")

# Create DataFrame
set.seed(319)
id_vars <- seq(101, 200, by = 1)
dates <-
  seq(
    from = as.Date("2017-01-01"),
    to = as.Date("2017-12-31"),
    by = "day"
  )
cat1 <- c("A", "B")
cat2 <- c("X", "Y", "Z")
dat <- data.frame()
for (id in id_vars) {
  n <- floor(runif(1) * 100)
  d <- data.frame(
    id = id,
    date = sample(dates, n, replace = T),
    cat1 = sample(cat1, n, replace = T),
    cat2 = sample(cat2, n, replace = T),
    metric1 = sample(1:5, n, replace = T),
    metric2 = rnorm(n, mean = 50, sd = 5)
  )
  dat <- rbind(dat, d)
}
dat2 <- dat %>% mutate(date = as.character(date))

# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
dat_tbl <- copy_to(sc, dat2, overwrite = TRUE)


id_vars <- c("id", "date")
group_vars <- c("cat1")
measure_vars <- c("metric1", "metric2")
fun <- "sum"
sep = "_"

spk_wide <- pivoter(
  dat_tbl,
  id_vars = id_vars,
  group_vars = group_vars,
  measure_vars = measure_vars,
  fun = "sum",
  sep = "_",
  fill = 0
)

r_wide <- pivoter(
  dat,
  id_vars = id_vars,
  group_vars = group_vars,
  measure_vars = measure_vars,
  fun = "sum",
  sep = "_",
  fill = 0
)



# Tests
test_that("pivoter methods consistent", {
  expect_equal(colnames(spk_wide), colnames(r_wide))
  expect_equal(
    spk_wide %>%
      collect() %>%
      as.data.frame() %>%
      mutate(date = as.Date(date)) %>%
      arrange_at(id_vars),
    r_wide %>%
      arrange_at(id_vars)
  )
})


test_that("pivoter methods return correct df dimensions", {
  dat_unq_ids <- dat %>% count_(id_vars) %>% nrow()
  expect_equal(dat_unq_ids, nrow(r_wide))
  expect_equal(dat_unq_ids, sdf_nrow(spk_wide))
})



test_that("aggregation function works as expected", {
  ids_test <- dat %>%
    filter(cat1 == "A") %>%
    count_(id_vars) %>%
    filter(n == 2) %>%
    .[1, id_vars]

  id_agg <- ids_test %>%
    inner_join(dat, by = id_vars) %>%
    group_by_at(group_vars) %>%
    summarise_at(measure_vars, .funs = fun)

  r_wide_agg <- r_wide %>% inner_join(ids_test, by = id_vars)
  spk_wide_agg <- spk_wide %>%
    collect() %>%
    mutate(date = as.Date(date)) %>%
    inner_join(ids_test, by = id_vars)

  expect_equal(id_agg$metric1, r_wide_agg$cat1_A_metric1)
  expect_equal(id_agg$metric2, r_wide_agg$cat1_A_metric2)
  expect_equal(id_agg$metric1, spk_wide_agg$cat1_A_metric1)
  expect_equal(id_agg$metric2, spk_wide_agg$cat1_A_metric2)
})



test_that("fill value works as expected", {
  spk_wide2 <- pivoter(
    dat_tbl,
    id_vars = id_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = "sum",
    sep = "_",
    fill = 5
  )

  r_wide2 <- pivoter(
    dat,
    id_vars = id_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = "sum",
    sep = "_",
    fill = 5
  )

  spk_wide3 <- pivoter(
    dat_tbl,
    id_vars = id_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = "sum",
    sep = "_",
    fill = NULL
  )

  r_wide3 <- pivoter(
    dat,
    id_vars = id_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = "sum",
    sep = "_",
    fill = NULL
  )


  expect_equal(
    spk_wide2 %>%
      collect() %>%
      as.data.frame() %>%
      mutate(date = as.Date(date)) %>%
      arrange_at(id_vars),
    r_wide2 %>%
      arrange_at(id_vars)
  )

  expect_equal(r_wide2$cat1_A_metric1[1], r_wide$cat1_A_metric1[1] + 5)

  expect_equal(
    spk_wide3 %>%
      collect() %>%
      as.data.frame() %>%
      mutate(date = as.Date(date)) %>%
      arrange_at(id_vars),
    r_wide3 %>%
      arrange_at(id_vars)
  )
})

test_that("seperator input works as expected on column names", {
  spk_wide <- pivoter(
    dat_tbl,
    id_vars = id_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = "sum"
  )

  r_wide <- pivoter(
    dat,
    id_vars = id_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = "sum"
  )

  expect_equal(colnames(spk_wide), colnames(r_wide))
})


spark_disconnect(sc)
