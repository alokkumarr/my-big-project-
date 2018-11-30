
# a2 Testing Datasets -----------------------------------------------------

# R Packages
library(dplyr)
library(tidyr)
library(tibble)
library(lubridate)



# Time Series Data --------------------------------------------------------

# Create simulated dataset
set.seed(319)
n <- 200
x <- arima.sim(n = n,
               list(order = c(1,0,0), ar = 0.7),
               rand.gen = function(n, ...) rt(n, df = 2))
dates <- floor_date(today(), unit = "years") + days(0:(n-1))
sim_df_ts <- tibble(index = 1:n,
                    date = dates,
                    y = as.numeric(x))

# Save to Data folder
save(sim_df_ts, file = 'data/sim-df-timeseries.rdata', compress = 'xz')

