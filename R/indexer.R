


#' Title
#'
#' @param df
#' @param measure_vars
#'
#' @return
#' @export
#' @importFrom lubridate year month week day hour minute wday yday days_in_month interval
#' @importFrom dplyr funs
#'
#' @examples
indexer <- function(df, measure_vars) {
  UseMethod("indexer")
}

#' @export
#' @rdname indexer
indexer.data.frame <- function(df, measure_vars) {
  date_names <- df %>%
    dplyr::summarise_all(class) %>%
    dplyr::select_if(funs(. == "Date")) %>%
    colnames()
  checkmate::assert_subset(measure_vars, date_names)


  df %>%
    dplyr::mutate_at(
      measure_vars,
      funs(
        year = year(.),
        month = month(.),
        week = week(.),
        day = day(.),
        day_of_week = wday(.) + 1,
        day_of_year = yday(.),
        days_left_in_month = days_in_month(.) - day,
        days_elapsed = interval(., today()) / days(1)
      )
    )
}
