
# Mutater Unit Tests ------------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)

context("mutater function unit tests")


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

spk_mtr <- dat_tbl %>%
  mutater(order_vars = c("id", 'date'),
          group_vars = c("cat1", "cat2"),
          measure_vars = c("metric1", "metric2"),
          fun = "cumsum")

r_mtr <- dat %>%
  mutater(order_vars = c("id", 'date'),
          group_vars = c("cat1", "cat2"),
          measure_vars = c("metric1", "metric2"),
          fun = "cumsum")


test_that("mutater methods consistent", {
  expect_equal(
    spk_mtr %>%
      collect() %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_mtr %>%
      arrange(id, date, cat1, cat2) %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(colnames(spk_mtr), colnames(r_mtr))
})



test_that("mutater returns correct dimensions", {
  expect_equal(sdf_nrow(spk_mtr), nrow(dat))
  expect_equal(nrow(r_mtr), nrow(dat))
})


test_that("custom expression works as expected", {
  spk_mtr1 <- dat_tbl %>% mutater(measure_vars = "metric1", fun = funs(add2 = .+2))
  r_mtr1 <- dat %>% mutater(measure_vars = "metric1", fun = funs(add2 = .+2))

  expect_equal(colnames(spk_mtr1), colnames(r_mtr1))
  expect_equal(r_mtr1[["metric1_add2"]], r_mtr1[["metric1"]]+2)
  expect_equal(sdf_nrow(spk_mtr1), nrow(r_mtr1))
  expect_equal(spk_mtr1 %>%
                 collect() %>%
                 arrange(id, date, cat1, cat2) %>%
                 as.data.frame() %>%
                 select_if(is.numeric),
               r_mtr1 %>%
                 arrange(id, date, cat1, cat2) %>%
                 select_if(is.numeric))
})
