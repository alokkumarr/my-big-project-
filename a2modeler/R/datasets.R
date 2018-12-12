#' Available Model Methods for Modeler Objects
#'
#' A dataset containing the avaialble model methods for each modeler type
#'
#' @format A data frame with 24 rows and 5 variables:
#'  \describe{
#'   \item{type}{modeler type}
#'   \item{method}{model method function name}
#'   \item{name}{name of method}
#'   \item{package}{R package backend}
#'   \item{class}{method class membership. can be more than 1} ...
#'   }
"model_methods"



#' Simulated Time Series Dataset
#'
#' A dataset with sequential data created from arima.sim function with random
#' innovations
#' 
#' @format A tibble with 200 rows and 2 variables
#'  \describe{
#'   \item{index}{integer type with sequential index of records}
#'   \item{date}{date type. with date of record}
#'   \item{y}{time series values}
#'  }
"sim_df_ts"
