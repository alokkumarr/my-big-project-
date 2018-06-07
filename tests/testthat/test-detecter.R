# Detecter Unit Tests -----------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)

# adding lubridate for time sequences
library(lubridate)

context("detecter function unit tests")
Sys.time()

# Create toy datasets
set.seed(319)
id_vars <- seq(101, 200, by=1)
dates <- seq(from=as.POSIXct(Sys.Date(),format='%a %b %d %H:%M:%S EST %Y')-365, to=as.POSIXct(Sys.Date(),format='%a %b %d %H:%M:%S EST %Y'), by="hours")

Date1 <-Sys.time() +60*60

date1 <- as.character(as.POSIXct(paste(substr(Sys.time(),1,13),":00:00",sep= ""),format = "%Y-%m-%d %H:%M:%S"))
n <- 200
dat1 <- data.frame(index = 1:n,
                   y = as.numeric(arima.sim(n = n,
                                            list(order = c(1,0,0), ar = 0.7),
                                            rand.gen = function(n, ...) rt(n, df = 2))))

dat2 <- data.frame(index = 1:(n+1),
                   y = as.numeric(arima.sim(n = n,
                                            list(order = c(1,1,0), ar = 0.7),
                                            rand.gen = function(n, ...) rt(n, df = 2))))

dat3 <- rbind(data.frame(index = 1:n,
                         y = as.numeric(arima.sim(n = n,
                                                  list(order = c(1,0,0), ar = 0.5),
                                                  rand.gen = function(n, ...) rt(n, df = 3))),
                         group = "A"),
              data.frame(index = 1:n,
                         y = as.numeric(arima.sim(n = n,
                                                  list(order = c(1,0,0), ar = 0.8),
                                                  rand.gen = function(n, ...) rt(n, df = 1))),
                         group = "B"))

# Take a look at lubridate package
# can create an hourly seq with following code
hr_seq <- seq(Sys.time() - lubridate::hours(x=n), Sys.time(), by = "hour") %>% tail(n)

# Getting error here -
dat4 <- data.frame(date = as.character(seq(from = Sys.Date() - (n-1), to = Sys.Date(), by = "hours")),
                   y = as.numeric(arima.sim(n = n,
                                            list(order = c(1,0,0), ar = 0.7),
                                            rand.gen = function(n, ...) rt(n, df = 2))))

dat5 <- data.frame(index = 1:n,
                   y = as.numeric(arima.sim(n = n,
                                            list(order = c(1,0,0), ar = 0.7)))) %>%
  mutate(y = ifelse(index %in% c(50, 100, 150), -10, y))


# Notes - you can use ctrl+i to re-indent code
# don't need the nest data.frame
# opportunity to re-use code
dat6 <- rbind(
  data.frame(index = 1:n,
             mval1 = as.numeric(arima.sim(n = n,
                                          list(order = c(1,0,0), ar = 0.5),
                                          rand.gen = function(n, ...) rt(n, df = 3))),
             data.frame(date = as.character(seq(from = Sys.time() - (n-1), to = Sys.time(), by = "mins")),
                        valu2 = as.numeric(arima.sim(n = n,
                                                     list(order = c(1,0,0), ar = 0.7),
                                                     rand.gen = function(n, ...) rt(n, df = 2)))),
             country = "USA", state="AL"),
  data.frame(index = 1:n,
             mval1 = as.numeric(arima.sim(n = n,
                                          list(order = c(1,0,0), ar = 0.8),
                                          rand.gen = function(n, ...) rt(n, df = 2))),
             data.frame(date = as.character(seq(from = Sys.time()+1*60*60 - (n-1), to = Sys.time()+1*60*60, by = "mins")),
                        valu2 = as.numeric(arima.sim(n = n,
                                                     list(order = c(1,0,0), ar = 0.7),
                                                     rand.gen = function(n, ...) rt(n, df = 1)))),
             country = "USA", state = "AR"),
  data.frame(index = 1:n,
             mval1 = as.numeric(arima.sim(n = n,
                                          list(order = c(1,0,0), ar = 0.3),
                                          rand.gen = function(n, ...) rt(n, df = 2))),
             data.frame(date = as.character(seq(from = Sys.time()+2*60*60 - (n-1), to = Sys.time()+2*60*60, by = "mins")),
                        valu2 = as.numeric(arima.sim(n = n,
                                                     list(order = c(1,0,0), ar = 0.7),
                                                     rand.gen = function(n, ...) rt(n, df = 1)))),
             country = "IND", state = "KAR"),
  data.frame(index = 1:n,
             mval1 = as.numeric(arima.sim(n = n,
                                          list(order = c(1,0,0), ar = 0.7),
                                          rand.gen = function(n, ...) rt(n, df = 2))),
             data.frame(date = as.character(seq(from = Sys.time()+3*60*60 - (n-1), to = Sys.time()+3*60*60, by = "mins")),
                        valu2 = as.numeric(arima.sim(n = n,
                                                     list(order = c(1,0,0), ar = 0.7),
                                                     rand.gen = function(n, ...) rt(n, df = 1)))),
             country = "USA", state = "AR")
  )


# functional approach
new_df <- function(n, order, ar, df, grain, country, state){
  grain_fun <- match.fun(grain)
  tibble(
    index = 1:n,
    date_time = seq(from = Sys.time() - grain_fun(n-1),
                    to = Sys.time(),
                    by = grain)[1:n],
    val1 = as.numeric(arima.sim(n = n,
                                list(order = order[[1]], ar = ar[1]),
                                rand.gen = function(n, ...) rt(n, df = df[1]))),

    val2 = as.numeric(arima.sim(n = n,
                                list(order = order[[2]], ar = ar[2]),
                                rand.gen = function(n, ...) rt(n, df = df[2]))),
    country = country,
    state = state
  )
}

dat6_new <- rbind(
  new_df(200, list(c(1,0,0), c(1,0,0)), c(.7, .5), df = c(2, 3),
         grain = "hours", country = "US", state = "NY"),
  new_df(200, list(c(1,0,0), c(1,0,0)), c(.8, .4), df = c(2, 3),
         grain = "hours", country = "US", state = "MA")
)



# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
dat1_tbl <- copy_to(sc, dat1, overwrite = TRUE)
dat2_tbl <- copy_to(sc, dat2, overwrite = TRUE)
dat3_tbl <- copy_to(sc, dat3, overwrite = TRUE)
dat4_tbl <- copy_to(sc, dat4, overwrite = TRUE)
dat5_tbl <- copy_to(sc, dat5, overwrite = TRUE)
dat6_tbl <- copy_to(sc, dat6, overwrite = TRUE)
dat6a_tbl <- copy_to(sc, dat6_new %>% mutate_at("date_time", as.character), name = "dat6a", overwrite = TRUE)

dat4_tbl <- mutate(dat4_tbl, date = to_date(date))
#dat6_tbl <-  mutate(dat6_tbl, date = to_date(date)) # Converting hourly date time to date only

# Converting to time stamp
dat6_tbl <-  mutate(dat6_tbl, time_stamp = unix_timestamp(date))
dat6a_tbl <-  mutate(dat6a_tbl, time_stamp = unix_timestamp(date_time))




r_detect1 <- dat1 %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75)

r_detect2 <- dat2 %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75)

r_detect3 <- dat3 %>%
  detecter(.,
           index_var = "index",
           group_vars = "group",
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75)


r_detect5 <- dat5 %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "neg",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75)


r_detect5b <- dat5 %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "both",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75)


spk_detect1 <- dat1_tbl %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()

spk_detect2 <- dat2_tbl %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()

spk_detect3 <- dat3_tbl %>%
  detecter(.,
           index_var = "index",
           group_vars = "group",
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()

# Getting error here
spk_detect4 <- dat4_tbl %>%
  detecter(.,
           index_var = "date",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()


spk_detect5 <- dat5_tbl %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "neg",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()


spk_detect5b <- dat5_tbl %>%
  detecter(.,
           index_var = "index",
           group_vars = NULL,
           measure_vars = "y",
           frequency = 7,
           direction = "both",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()


# Creating Spark test 6 to test
spk_detect6 <- dat6_tbl %>%
  detecter(.,
           index_var = "time_stamp",
           group_vars = c("country", "state"),
           measure_vars = c("mval1", "valu2"),
           frequency = 60,
           direction = "both",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()


# Creating Spark test 6a to test
spk_detect6a <- dat6a_tbl %>%
  detecter(.,
           index_var = "time_stamp",
           group_vars = c("country", "state"),
           measure_vars = c("val1", "val2"),
           frequency = 24,                    # hourly data
           direction = "both",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75) %>%
  collect()


# Tests -------------------------------------------------------------------


test_that("detecter methods consistent", {
  expect_equal(
    spk_detect1 %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_detect1 %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(
    spk_detect2 %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_detect2 %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(
    spk_detect3 %>%
      select(index, group, measure, value, seasonal, trend, resid, anomaly) %>%
      arrange(group, measure, index) %>%
      select_if(is.numeric) %>%
      round(5) ,
    r_detect3 %>%
      select(index, group, measure, value, seasonal, trend, resid, anomaly) %>%
      arrange(group, measure, index) %>%
      select_if(is.numeric) %>%
      round(5)
  )
  expect_equal(colnames(spk_detect1), colnames(r_detect1))
  expect_equal(colnames(spk_detect3), colnames(r_detect3))
})


test_that("detecter return correct dimensions", {
  expect_equal(nrow(r_detect1), nrow(dat1))
  expect_equal(nrow(r_detect3), nrow(dat3))
})


test_that("direction option works as expected", {

  r_detect1b <- dat1 %>%
    detecter(.,
             index_var = "index",
             group_vars = NULL,
             measure_vars = "y",
             frequency = 7,
             direction = "both",
             alpha = 0.02,
             max_anoms = 0.10,
             trend_window = .75)

  expect_true(all(r_detect1$index %in% r_detect1b$index))
})


test_that("detecter preserves index var date type", {

  expect_class(spk_detect4[[1]], "Date")
})



test_that("neg direction option works as expected", {

  r_detect5_anoms <- r_detect5 %>% filter(anomaly == 1)
  spk_detect5_anoms <- spk_detect5 %>% filter(anomaly == 1)

  expect_subset(c(50, 100, 150), r_detect5_anoms$index)
  expect_subset(c(50, 100, 150), spk_detect5_anoms$index)
})



test_that("both direction option works as expected", {

  r_detect5b_anoms <- r_detect5b %>% filter(anomaly == 1)
  spk_detect5b_anoms <- spk_detect5b %>% filter(anomaly == 1)

  expect_subset(c(50, 100, 150), r_detect5b_anoms$index)
  expect_subset(c(50, 100, 150), spk_detect5b_anoms$index)
})


#Test 3:Detecter with Multiple Group vars -------------------------------


.group_vars <- c("country","state")

r_detect_multi_Group <- dat6 %>%
  detecter(.,
           index_var = "index",
           group_vars = .group_vars,
           measure_vars = "mval1",
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75)


#Test 4:Test detecter with multiple measure variables -------------------

r_detect_multi_mesure <- dat6 %>%
  detecter(.,
           index_var = "index",
           group_vars = "country",
           measure_vars = c('mval1','valu2'),
           frequency = 7,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75)


# #Test 5-Check frequency with every minutes data -------------------------

r_detect_freq_with_hour <- dat6 %>%
  detecter(.,
           index_var = "index",
           group_vars = c("country"),
           measure_vars = "mval1",
           frequency = 60,
           direction = "pos",
           alpha = 0.01,
           max_anoms = 0.05,
           trend_window = .75)


