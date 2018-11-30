
#' Simulated Behavioral Dataset
#'
#' A simulated dataset to represent a behavioral dataset with date and id based
#' records
#'
#' @format A tibble with 2000 rows and 7 variables 
#'  \describe{
#'   \item{index}{integer type with sequential index of records}
#'   \item{id}{integer type. example of an unique identifier}
#'   \item{date}{date type. date of record}
#'   \item{cat1}{character type. record attribute example. 2 distinct values.}
#'   \item{cat2}{character type. record attribute example. 5 disitinct values.}
#'   \item{metric1}{integer type. numerical metric example. 5 distinct values.}
#'   \item{metric2}{numeric type. numerical metric example.}
#'   }
"sim_df"

#' Simulated Time Series with Anomalies Dataset
#'
#' A simulated time series dataset with anomalies to be used in detecter testing
#'
#' @format A tibble with 100 rows and 4 variables 
#'  \describe{
#'   \item{index}{integer type with sequential index of records}
#'   \item{date}{date type. date of record}
#'   \item{y}{numeric type. time series value. created with arima.sim function. anomalies added}
#'   \item{flag}{integer type. flag for anomalies added. 1 = positive anomaly, -1 = negative anomaly, 0 = no anomaly }
#'   }
"sim_df_anom"