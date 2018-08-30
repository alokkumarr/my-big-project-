
# forecast methods --------------------------------------------------------

#' Get Forecasts from forecast object
#'
#' @rdname get_forecasts
#' @export
#' @return data.frame with forecats from forecast model
get_forecasts.forecast <- function(mobj){

  df <- as.data.frame(mobj)
  cns <- tolower(colnames(df))
  cns <- gsub("lo", "lower", cns)
  cns <- gsub("hi", "upper", cns)
  cns <- gsub(" ", "", cns)
  cns[1] <- "mean"
  colnames(df) <- cns
  df
}

