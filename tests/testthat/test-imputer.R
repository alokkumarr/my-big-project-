# Imputer Unit Tests -----------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)

context("imputer function unit tests")


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
dat <- dat %>%
  mutate(date = as.character(date),
         cat1 = ifelse(row_number()%%10==0, as.character(NA), as.character(cat1)),
         metric1 = ifelse(metric1 == 1, NA, metric1))


# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
dat_tbl <- copy_to(sc, dat, overwrite = TRUE)


