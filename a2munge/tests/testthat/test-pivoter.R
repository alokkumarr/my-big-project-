


# pivoter unit tests ---------------------------------------------------------

library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)

context("pivoter function unit tests")

# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
sim_tbl <- mutate_at(sim_df, "date", as.character) %>% 
  copy_to(sc, ., name = "df", overwrite = TRUE) %>%
  mutate(date = to_date(date))


id_vars <- c("id", "date")
group_vars <- c("cat1")
measure_vars <- c("metric1", "metric2")
fun <- "sum"
sep = "_"

spk_wide <- pivoter(sim_tbl,
                    id_vars = id_vars,
                    group_vars = group_vars,
                    measure_vars = measure_vars,
                    fun = "sum",
                    sep = "_",
                    fill = 0)

r_wide <- pivoter(sim_df,
                  id_vars = id_vars,
                  group_vars = group_vars,
                  measure_vars = measure_vars,
                  fun = "sum",
                  sep = "_",
                  fill = 0)



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
  dat_unq_ids <- sim_df %>% 
    count_(id_vars) %>%
    nrow()
  
  expect_equal(dat_unq_ids, nrow(r_wide))
  expect_equal(dat_unq_ids, sdf_nrow(spk_wide))
})



test_that("aggregation function works as expected", {
  ids_test <- sim_df %>%
    filter(cat1 == "A") %>%
    count_(id_vars) %>%
    filter(n == 2) %>%
    .[1, id_vars]

  id_agg <- ids_test %>%
    inner_join(sim_df, by = id_vars) %>%
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
    sim_tbl,
    id_vars = id_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = "sum",
    sep = "_",
    fill = 5
  )

  r_wide2 <- pivoter(
    sim_df,
    id_vars = id_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = "sum",
    sep = "_",
    fill = 5
  )

  spk_wide3 <- pivoter(
    sim_tbl,
    id_vars = id_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = "sum",
    sep = "_",
    fill = NULL
  )

  r_wide3 <- pivoter(
    sim_df,
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
    sim_tbl,
    id_vars = id_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = "sum"
  )

  r_wide <- pivoter(
    sim_df,
    id_vars = id_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = "sum"
  )

  expect_equal(colnames(spk_wide), colnames(r_wide))
})

