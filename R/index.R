
#' Index Class Constructer
new_index <- function(unit, periods, start, end) {
  structure(list(
    unit = unit,
    periods = periods,
    start = start,
    end = end
  ),
  class = "index")
}


#' @export
index <- function(x, units) {
  UseMethod("index")
}


#' @rdname
#' @export
index.numeric <- index.integer <- function(x, unit = NULL) {
  unit <- mean(diff(x))
  if ((unit %% 1) != 0) {
    stop("index not regular")
  }
  structure(
    new_index(
      unit = unit,
      periods = NULL,
      start = min(x),
      end = max(x)
    ),
    class = c("numeric_index", "index")
  )
}


#' @rdname index
#' @export
index.Date <- function(x, unit = "days") {
  checkmate::assert_choice(unit, c("days", "weeks", "months", "years"))
  #checkmate::assert_numeric(periods, lower = 1, null.ok = TRUE)
  periods <- abs(as.numeric(mean(diff(x))))
  if ((periods %% 1) != 0) {
    stop("index not regular")
  }
  structure(
    new_index(
      unit = unit,
      periods = periods,
      start = min(x),
      end = max(x)
    )
    ,
    class = c("time_index", "index")
  )
}


#' @rdname index
#' @export
index.POSIXct <- index.POSIXt <- function(x, unit = "hours") {
  checkmate::assert_choice(unit, c("seconds", "minutes", "hours", "days"))
  checkmate::assert_numeric(periods, lower = 1, null.ok = TRUE)
  periods <- abs(as.numeric(mean(diff(x))))
  if ((periods %% 1) != 0) {
    stop("index not regular")
  }
  structure(
    new_index(
      unit = unit,
      periods = periods,
      start = min(x),
      end = max(x)
    )
    ,
    class = c("time_index", "index")
  )
}


#' Extend an Index
#'
#' Function to extend out an index by specified length
#'
#' @param obj index object
#' @param length_out number of periods to extend index by
#' @export
extend <- function(obj, length_out) {
  UseMethod("extend")
}


#' @rdname extend
#' @export
extend.index <- function(obj, length_out) {
  seq(from = obj$end + 1,
      length.out = length_out,
      by = obj$unit)
}


#' @rdname extend
#' @export
extend.time_index <- function(obj, length_out) {
  obj$end + match.fun(obj$unit)(seq(
    from = 1,
    length.out = length_out,
    by = obj$periods
  ))
}
