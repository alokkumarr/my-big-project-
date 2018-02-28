

# summariser_map function -------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)

context("summariser_map function unit tests")


# Create toy dataset
set.seed(319)
id_vars <- seq(101, 200, by=1)
dates <- seq(from=Sys.Date()-365, to=Sys.Date(), by="day")
cat1 <- c("A", "B")
cat2 <- c("X", "Y", "Z")

dat <- data.frame()
for(id in id_vars){
  n <- floor(runif(1)*100)
  d <- data.frame(id = id,
                 date = sample(dates, n, replace = T),
                 cat1 = sample(cat1, n, replace = T),
                 cat2 = sample(cat2, n, replace = T),
                 metric1 = sample(1:5, n, replace = T),
                 metric2 = rnorm(n, mean=50, sd = 5))
 dat <- rbind(dat, d)
}


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
dat_tbl <- copy_to(sc, dat, overwrite = TRUE)


r_map <- summariser_map(dat,
                        id_vars = 'id',
                        map = list(
                          summariser_args(
                            group_vars = c("cat1"),
                            measure_vars = c("metric1", "metric2"),
                            fun = c("sum")
                          ),
                          summariser_args(
                            group_vars = c("cat2"),
                            measure_vars = c("metric2"),
                            fun = c("mean")
                          )
                        ))

spk_map <- summariser_map(dat_tbl,
                          id_vars = "id",
                          map = list(
                            summariser_args(
                              group_vars = c("cat1"),
                              measure_vars = c("metric1", "metric2"),
                              fun = c("sum")
                            ),
                            summariser_args(
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
  expect_equal(nrow(spk_map), length(unique(dat$id)))
  expect_equal(nrow(r_map), length(unique(dat$id)))
})


