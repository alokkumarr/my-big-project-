



#' Index Class Constructer
#'
#' @param unit index unit. can by time base units such as days, weeks or years
#'   or numerical unit such as 1
#' @param periods number of units per period
#' @param start index starting value
#' @param end index ending value
#'
#' @return index class
#' @export
new_index <- function(unit, periods, start, end) {
  structure(list(
    unit = unit,
    periods = periods,
    start = start,
    end = end
  ),
  class = "index")
}


#' Index Creation Function
#'
#' Creates new index object for sequential data management
#'
#' Used by Forecaster to create forward looking indicies
#'
#' @param x index object
#' @inheritParams new_index
#' 
#' @export
index <- function(x, unit) {
  UseMethod("index")
}

#' @rdname index
#' @export
index.numeric <- function(x, unit = NULL) {
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
index.integer <- index.numeric


#' @rdname index
#' @export
index.Date <- function(x, unit = "days") {
  checkmate::assert_choice(unit, c("days", "weeks", "years"))
  periods <- abs(as.numeric(mean(diff(x))))
  if (unit == "days")
  {
    if ((periods %% 1) != 0) {
      stop("index not regular")
    }
    periods <- periods
  }

  else if (unit == "weeks")
  {
    if ((periods %% 1) != 0) {
      stop("index not regular")
    }
    periods <- round(periods / 7)
  }
  else if (unit == "years")
  {
    if ((periods %% 1 <= 0 && periods %% 1 >= 1)) {
      stop("index not regular")
    }
    periods <- round(periods / 365)
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
index.POSIXct <- function(x, unit = "hours") {
  checkmate::assert_choice(unit, c("seconds", "minutes", "hours", "days"))
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
index.POSIXt <- index.POSIXct


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
  seq(
    from = obj$end + obj$unit,
    length.out = length_out,
    by = obj$unit
  )
}


#' @importFrom lubridate seconds minutes hours days weeks years
#' @rdname extend
#' @export
extend.time_index <- function(obj, length_out) {
  obj$end + get(obj$unit, asNamespace("lubridate"))(seq(
    from = obj$periods,
    length.out = length_out,
    by = obj$periods
  ))
}
