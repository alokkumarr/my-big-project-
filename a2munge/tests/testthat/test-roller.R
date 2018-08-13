
# Roller Unit Tests -------------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)
library(zoo)

context("roller function unit tests")


# Create toy dataset
set.seed(319)
id_vars <- seq(101, 200, by=1)
dates <- seq(from=Sys.Date()-365, to=Sys.Date(), by="day")
cat1 <- c("A", "B")
cat2 <- c("X", "Y", "Z")

dat <- data.frame()
for(id in id_vars){
  n <- max(10, floor(runif(1)*100))
  d <- data.frame(id = id,
                  date = sample(dates, n, replace = TRUE),
                  cat1 = sample(cat1, n, replace = TRUE),
                  cat2 = sample(cat2, n, replace = TRUE),
                  metric1 = sample(1:5, n, replace = TRUE),
                  metric2 = rnorm(n, mean=50, sd = 5))
  dat <- rbind(dat, d)
}
dat <- dat %>% mutate(date = as.character(date))

# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
dat_tbl <- copy_to(sc, dat, overwrite = TRUE)
dat_tbl <- dat_tbl %>% mutate(date = to_date(date))
dat <- dat %>% mutate(date = as.Date(date))


r_roll <- dat %>%
  summariser(.,
             group_vars = c("date"),
             measure_vars = c("metric1", "metric2"),
             fun = c("sum")) %>%
  roller(.,
         order_vars = "date",
         group_vars = NULL,
         measure_vars = c("metric1_sum", "metric2_sum"),
         fun = "mean",
         width = 5) %>%
  arrange(date)


spk_roll <- dat_tbl %>%
  summariser(.,
             group_vars = c("date"),
             measure_vars = c("metric1", "metric2"),
             fun = c("sum")) %>%
  roller(.,
         order_vars = "date",
         group_vars = NULL,
         measure_vars = c("metric1_sum", "metric2_sum"),
         fun = "mean",
         width = 5) %>%
  collect() %>%
  arrange(date)



test_that("roller methods consistent", {
  expect_equal(
    spk_roll %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_roll %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(colnames(spk_roll), colnames(r_roll))
})


test_that("roller returns correct dimensions", {
  expect_equal(nrow(spk_roll), length(unique(dat$date)))
})

