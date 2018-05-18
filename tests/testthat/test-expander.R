
# Expander Unit Tests -----------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)

context("expander function unit tests")


# Data Creation -----------------------------------------------------------

# Function to create simulated dataset
sim_data <- function(n_ids, n_recs, n_iter, seed = 319) {
  n <- n_ids * n_recs
  ids <- 1:n_ids
  dates <- seq(from = Sys.Date() - 365,
               to = Sys.Date(),
               by = "day")
  cat1 <- c("A", "B")
  cat2 <- c("X", "Y", "Z")

  do.call("rbind",
          replicate(n_iter,
                    {
                      data.frame(
                        id = sample(ids, n, replace = T),
                        date = sample(dates, n, replace = T),
                        cat1 = as.character(sample(cat1, n, replace = T)),
                        cat2 = as.character(sample(cat2, n, replace = T)),
                        metric1 = sample(1:7, n, replace = T),
                        metric2 = rnorm(n, mean = 50, sd = 5),
                        metric3 = sample(11:15, n, replace = T)
                      )
                    },
                    simplify = FALSE))
}

# R data.frame
dat <- sim_data(5, 25, 1, seed = 319) %>%
  mutate(index = row_number())

# Create Spark Connection
sc <- spark_connect(master = "local")

# Copy R data.frame to Spark - need to convert date class to character
tbl <- copy_to(sc, mutate_at(dat, "date", as.character), name = "dat", overwrite = TRUE) %>%
  mutate(date = to_date(date))


# Test Bed ----------------------------------------------------------------


