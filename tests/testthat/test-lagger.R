
# Lagger Unit Tests -------------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)

context("lagger function unit tests")


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
dat_tbl <- copy_to(sc, dat %>% mutate(date = as.character(date)), overwrite = TRUE)
dat_tbl <- dat_tbl %>% mutate(date = to_date(date))

spk_lag <- dat_tbl %>%
  lagger(order_vars = c('date'),
         group_vars = c("id"),
         measure_vars = c("metric1", "metric2"),
         lags = 1:3)

r_lag <- dat %>%
  lagger(order_vars = c('date'),
         group_vars = c("id"),
         measure_vars = c("metric1", "metric2"),
         lags = 1:3)



test_that("lagger methods consistent", {
  expect_equal(
    spk_lag %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5) ,
    r_lag %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      as.data.frame() %>%
      round(5)
  )
  expect_equal(colnames(spk_lag), colnames(r_lag))
})



test_that("lagger returns correct dimensions", {
  expect_equal(sdf_nrow(spk_lag), nrow(dat))
  expect_equal(nrow(r_lag), nrow(dat))
})


test_that("lag values are correct",{
  r_lag101 <- r_lag %>% filter(id == 101)
  expect_equal(r_lag101[["metric1"]][-nrow(r_lag101)],
               r_lag101[["metric1_lag1"]][-1])
  expect_equal(r_lag101[["metric1"]][1:10],
               r_lag101[["metric1_lag3"]][4:13])
})
