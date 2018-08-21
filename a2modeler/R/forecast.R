
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


#' Convert Forecast object to data.frame
#'
#' @param mobj forecast model object
#' @rdname as_data_frame
#' @export
as_data_frame.forecast <- function(mobj){

  df <- as.data.frame(fobj)
  cns <- tolower(colnames(df))
  cns <- gsub("lo", "lower", cns)
  cns <- gsub("hi", "upper", cns)
  cns <- gsub(" ", "", cns)
  cns[1] <- "mean"
  cns <- paste("forecast", sep="_")
  colnames(df) <- cns
  df
}
