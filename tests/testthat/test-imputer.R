# Imputer Unit Tests -----------------------------------------------------


library(testthat)
library(a2munge)
library(sparklyr)
library(dplyr)

context("imputer function unit tests")


# Function to create simulated data
sim_data <- function(n_ids, n_recs, n_iter, seed = 319){

  set.seed(seed)
  n <- n_ids * n_recs
  ids <- 1:n_ids
  dates <- seq(from=Sys.Date()-365, to=Sys.Date(), by="day")
  cat1 <- c("A", "B")
  cat2 <- c("X", "Y", "Z")

  do.call("rbind",
          replicate(n_iter,
                    {
                      data.frame(id = sample(ids, n, replace=T),
                                 date = sample(dates, n, replace = T),
                                 cat1 = as.character(sample(cat1, n, replace = T)),
                                 cat2 = as.character(sample(cat2, n, replace = T)),
                                 metric1 = sample(1:5, n, replace = T),
                                 metric2 = rnorm(n, mean=50, sd = 5))
                    },
                    simplify = FALSE)
  )
}

dat <- sim_data(10, 100, 1, seed = 319)

# Create Spark Connection and read in some data
sc <- spark_connect(master = "local")

# Load data into Spark
dat_tbl <- copy_to(sc, dat, overwrite = TRUE)


# Create Missing Values
dat <- dat %>%
  mutate(date = as.character(date),
         cat1 = ifelse(row_number()%%10==0, NA, as.character(cat1)),
         metric1 = ifelse(metric1 == 1, NA, metric1))

dat_tbl <- dat_tbl %>%
  mutate(date = as.character(date),
         cat1 = ifelse(row_number()%%10==0, NA, as.character(cat1)),
         metric1 = ifelse(metric1 == 1, NA, metric1))



# Test Bed ----------------------------------------------------------------


