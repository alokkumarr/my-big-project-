
# a2 Testing Datasets -----------------------------------------------------

# R Packages
library(dplyr)
library(tidyr)
library(tibble)
library(lubridate)



# Behavioral Data ---------------------------------------------------------


# Function to create simulated dataset
sim_data <- function(n_ids, n_recs, n_iter, seed = 319) {
  n <- n_ids * n_recs
  ids <- 1:n_ids
  dates <- floor_date(today(), "year") + days(0:364)
  cat1 <- LETTERS[1:2]
  cat2 <- LETTERS[c(22:26)]

  cat1_prob <- c(.75, .25)
  cat2_prob <- c(.3, .25, .20, .15, .10)

  do.call("rbind",
          replicate(n_iter,
                    {
                      tibble(
                        id = sample(ids, n, replace = T),
                        date = sample(dates, n, replace = T),
                        cat1 = as.character(sample(cat1, n, prob = cat1_prob, replace = T)),
                        cat2 = as.character(sample(cat2, n, prob = cat2_prob, replace = T)),
                        metric1 = sample(1:7, n, replace = T),
                        metric2 = rnorm(n, mean = 50, sd = 5),
                        metric3 = sample(11:15, n, replace = T)
                      )
                    },
                    simplify = FALSE))
}

# Simulated behavioral dataset
sim_df <- sim_data(10, 200, 1, seed = 319) %>%
  mutate(index = row_number()) %>%
  select(index, id, date, cat1, cat2, metric1, metric2)

# Save to Data folder
save(sim_df, file = 'data/sim-df-behavioral.rdata', compress = 'xz')



# Detecter Dataset --------------------------------------------------------


# Dataset for Detecter Tests
set.seed(319)
n <- 100
dates <- floor_date(today(), unit = "years") + days(0:(n-1))
x <- arima.sim(n = n, list(order = c(1,0,0), ar = 0.7))

sim_df_anom <- tibble(index = 1:n,
                      date = dates,
                      y = as.numeric(x))

# Add anomalies
pos_anoms_indx <- sample(1:n, 5, replace = F)
neg_anoms_indx <- sample(setdiff(1:n, pos_anoms_indx), 5, replace = F)
sim_df_anom <- sim_df_anom %>% 
  mutate(y = ifelse(index %in% pos_anoms_indx, y + rnorm(5, mean = 5), y),
         y = ifelse(index %in% neg_anoms_indx, y - rnorm(5, mean = 5), y),
         flag = case_when(index %in% pos_anoms_indx ~ 1, 
                          index %in% neg_anoms_indx ~ -1,
                          TRUE ~ 0))

# Save to Data folder
save(sim_df_anom, file = 'data/sim-df-anomalies.rdata', compress = 'xz')
