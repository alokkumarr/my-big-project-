
#' Dataframe Index Function
#'
#' Function calculates the time between two date values as a numerical index.
#'
#' Spark version only caculates index for days and months units and single
#' period
#'
#' @param df data.frame
#' @param measure_vars column name of Date class to calculate index on. Can be
#'   more than 1 column
#' @param origin either string that can be converted to a Date or column name of
#'   seperate Date class
#' @param units time interval to measure time difference on. Valid units are
#'   years, months, weeks, days, hours, minutes, and seconds for R method and
#'   days and months for Spark
#' @param periods number of time periods to count. Valid only for R method
#' @param label optional input for custom column name of output
#'
#' @return data.frame
#' @export
#' @importFrom lubridate is.Date is.POSIXct interval
#'
#' @examples
#' df <- data.frame(today = Sys.Date())
#' indexer(df, "today", origin = "2018-05-20", units = "days", periods = 1)
#' indexer(df, "today", origin = "2018-05-20", units = "hours", periods = 1)
#' indexer(df, "today", origin = "2018-05-20", units = "hours", periods = 4)
indexer <- function(df, measure_vars, origin, units, periods, label) {
  UseMethod("indexer")
}


#' @rdname indexer
#' @export
#' @importFrom lubridate interval
indexer.data.frame <- function(df,
                               measure_vars,
                               origin = "2000-01-01",
                               units = "days",
                               periods = 1,
                               label = NULL) {
  date_names <- df %>%
    dplyr::summarise_all(class) %>%
    dplyr::select_if(funs(. == "Date")) %>%
    colnames()
  checkmate::assert_subset(measure_vars, date_names)
  checkmate::assert_choice(units,
                           c(
                             "years",
                             "months",
                             "weeks",
                             "days",
                             "hours",
                             "minutes",
                             "seconds"
                           ))
  checkmate::assert_number(periods, lower = 1)
  checkmate::assert_string(label, null.ok = TRUE)

  if (is.null(label)) {
    per_label <- if (periods == 1) units else paste0(periods, substr(units, 0, nchar(units) - 1), "_periods")
    label <- paste(per_label, "since", origin, sep = "_")
  }
  label <- rlang::quo_name(label)
  units_fun <- get(units, asNamespace("lubridate"))

  if (!origin %in% date_names) {
    checkmate::assert_true(any(is.Date(origin), is.POSIXct(origin), is.Date(as.Date(origin))))
    df %>%
      dplyr::mutate_at(measure_vars,
                       funs(!!label := interval(., origin) / units_fun(periods) * -1))
  } else {
    df %>%
      dplyr::mutate_at(measure_vars,
                       funs(!!label := interval(., !!rlang::sym(origin)) / units_fun(periods) * -1))
  }
}






#' @rdname indexer
#' @export
indexer.tbl_spark <- function(df,
                              measure_vars,
                              origin = "2000-01-01",
                              units = "days",
                              periods = 1,
                              label = NULL) {
  date_names <- df %>%
    sparklyr::sdf_schema() %>%
    purrr::map_df("type") %>%
    dplyr::select_if(funs(. == "DateType")) %>%
    colnames()
  checkmate::assert_subset(measure_vars, date_names)
  checkmate::assert_choice(units, c("months", "days"))
  checkmate::assert_number(periods, lower = 1)
  checkmate::assert_string(label, null.ok = TRUE)


  if (is.null(label)) {
    per_label <- if (periods == 1) units else paste0(periods, substr(units, 0, nchar(units) - 1), "_periods")
    label <- paste(per_label, "since", origin, sep = "_")
  }
  label <- rlang::quo_name(label)

  if (!origin %in% date_names) {
    checkmate::assert_true(any(is.Date(origin), is.Date(as.Date(origin))))
    if (units == "days") {
      df %>%
        dplyr::mutate_at(measure_vars,
                         funs(!!label := datediff(., origin)))
    } else {
      df %>%
        dplyr::mutate_at(measure_vars,
                         funs(!!label := months_between(., origin)))
    }

  } else {
    if (units == "days") {
      df %>%
        dplyr::mutate_at(measure_vars,
                         funs(!!label := datediff(., !!rlang::sym(origin))))
    } else {
      df %>%
        dplyr::mutate_at(measure_vars,
                         funs(!!label := months_between(., !!rlang::sym(origin))))
    }
  }
}




# date_parts --------------------------------------------------------------


#' Dataframe Date Parter Funtion
#'
#' Function to calculate and append date part values to a data.frame
#'
#' Calculates following date parts
#'    year    - numerical year
#'    quarter - numerical quarter
#'    month   - numerical month value. can be change to month label for R method
#'    day_of_month - numerical day of month - day_of_week('2000-01-01') = 1
#'    day_of_week - numerical weekday. 1 = Sunday. can be changed to label for R method
#'    day_of_year - numerical day of the year - day_of_year('2000-01-06') = 6
#'    days_left_in_month - number of days left in current month - days_left_in_month('2000-12-31') = 0
#'
#'
#' @param df data.frame
#' @param measure_vars column name of Date class to calculate date_parts on. Can
#'   be more than 1 column
#' @param label logical option to change month and day of week values to label.
#'   default is FALSE
#' @param abbr logical option to use abbreviate labels. default is FALSE
#' @param ... not currently implemented
#'
#' @return data.frame
#' @importFrom lubridate year quarter month week day hour minute wday yday days_in_month
#' @importFrom dplyr funs
#' @export
#' @examples
#' df <- data.frame(today = Sys.Date())
#' date_parter(df, "today")
date_parter <- function(df, measure_vars, label, abbr, ...) {
  UseMethod("date_parter")
}


#' @export
#' @rdname date_parter
date_parter.data.frame <- function(df, measure_vars, label = FALSE, abbr = FALSE, ...) {

  date_names <- df %>%
    dplyr::summarise_all(class) %>%
    dplyr::select_if(funs(. == "Date")) %>%
    colnames()
  checkmate::assert_subset(measure_vars, date_names)
  checkmate::assert_logical(label)
  checkmate::assert_logical(abbr)

   df %>%
    dplyr::mutate_at(
      measure_vars,
      funs(
        year = year(.),
        quarter = quarter(.),
        month = month(., abbr = abbr, label = label),
        week = week(.),
        day_of_month = day(.),
        day_of_week = wday(., abbr = abbr, label = label),
        day_of_year = yday(.),
        days_left_in_month = days_in_month(.) - day(.)
      )
    )
}


#' @export
#' @rdname date_parter
date_parter.tbl_spark <- function(df, measure_vars, ...) {

  date_names <- df %>%
    sparklyr::sdf_schema() %>%
    purrr::map_df("type") %>%
    dplyr::select_if(funs(. == "DateType")) %>%
    colnames()
  checkmate::assert_subset(measure_vars, date_names)

  df %>%
    dplyr::mutate_at(
      measure_vars,
      funs(
        year = year(.),
        quarter = quarter(.),
        month = month(.),
        week = weekofyear(.),
        day_of_month = day(.),
        day_of_week = date_format(., "EEEE"),
        day_of_year = dayofyear(.),
        days_left_in_month = datediff(last_day(.), .)
      )
    )
}



#' Date Parts
#'
#' Function to calculate date parts for Date
#'
#' @param x Date values
#' @inheritParams date_parter
#' @return tibble with date parts
#' @export
#' @examples
#' date_parts(Sys.Date())
date_parts <- function(x, label = FALSE, abbr = FALSE) {
  tibble(
    year = year(x),
    quarter = quarter(x),
    month = month(x, abbr = abbr, label = label),
    week = week(x),
    day_of_month = day(x),
    day_of_week= wday(x, abbr = abbr, label = label),
    day_of_year = yday(x),
    days_left_in_month = days_in_month(x) - day(x)
  )
}

