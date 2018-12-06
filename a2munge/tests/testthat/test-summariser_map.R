

# summariser_map function -------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)

context("summariser_map function unit tests")

# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
sim_tbl <- mutate_at(sim_df, "date", as.character) %>% 
  copy_to(sc, ., name = "df", overwrite = TRUE) %>%
  mutate(date = to_date(date))


r_map <- summariser_map(sim_df,
                        id_vars = 'id',
                        map = list(
                          list(
                            group_vars = c("cat1"),
                            measure_vars = c("metric1", "metric2"),
                            fun = c("sum")
                          ),
                          list(
                            group_vars = c("cat2"),
                            measure_vars = c("metric2"),
                            fun = c("mean")
                          )
                        ))

spk_map <- summariser_map(sim_tbl,
                          id_vars = "id",
                          map = list(
                            list(
                              group_vars = c("cat1"),
                              measure_vars = c("metric1", "metric2"),
                              fun = c("sum")
                            ),
                            list(
                              group_vars = c("cat2"),
                              measure_vars = c("metric2"),
                              fun = c("mean")
                            )
                          )) %>%
  collect()



test_that("summariser_map methods consistent", {
  expect_equal(
    spk_map %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_map %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(colnames(spk_map), colnames(r_map))
})


test_that("summariser_map return correct dimensions", {
  expect_equal(nrow(spk_map), length(unique(sim_df$id)))
  expect_equal(nrow(r_map), length(unique(sim_df$id)))
})


