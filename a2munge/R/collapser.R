#'Date Interval Ceiling Value function
#'
#'This is a function to extract the maximum possible value for a date column
#'for a given interval
#'
#'For R data frames, the base ceiling date function will be used from the lubridate 
#'package with logic to negate the resulting date value by 1 period of the relevant 
#'interval Function returns a date value which will be the maximum possible value.
#'For Spark data frames, SQL logic has been coded to get the ceiling date. The output
#'format will be yyyy-MM-dd or yyyy-MM-dd HH:mm:ss only since Spark SQL does not allow
#'formatted Dates
#'
#'@param df Dataframe
#'@param input_col_name Input Date column for which ceiling date has to be derived
#'@param input_format Date format of the input column. Primary usage is for Spark Data frames
#'@param period_value Period for which collapsing logic has to be applied
#'@param side Direction in which collapsing logic has to be applied
#'
#'@return returns DataFrame with ceiling date value derived on column {INPUT_COLUMN_NAME}_CEI
#'
#'
#'@export
#'@import dplyr
#'@import lubridate
#'@import checkmate
#'@import sparklyr
#'
#'
#'@examples
#'
#'library(lubridate)
#'
#'collapser(df, "2018-12-28 12:15:15", "yyyy-MM-dd HH:mm:ss", "month", "end")
#'Return value = 2018-12-31 00:00:00

collapser <- function(df,
                      input_col_name,
                      input_format,
                      period_value,
                      side) {
  UseMethod("collapser")
}

#' @rdname collapser
#' @export
collapser.data.frame <- function(df,
                                 input_col_name,
                                 input_format,
                                 period_value,
                                 side) {
  
  checkmate::assert_subset(input_col_name, colnames(df), empty.ok = TRUE)
  
  checkmate::assert_choice(
    period_value,
    c(
      "minute",
      "hour",
      "day",
      "month",
      "year"
    )
  )
  
  checkmate::assert_subset(
    side,
    c(
      "start",
      "end"
    )
  )
  
  # Derive Output colum name by adding "CEI" suffix
  
  output_col_name <- paste(input_col_name, "CEI", sep = "_")
  
  df <- df %>%
    rename(., DT_CEI_1 = input_col_name) %>%
    mutate(., !!output_col_name := case_when((!!side == "start" ~ lubridate::floor_date(DT_CEI_1, unit = period_value)),
                                             (!!side == "end" ~ lubridate::ceiling_date(DT_CEI_1, unit = period_value) - 1)
    )
    ) %>%
    rename(., !!input_col_name := DT_CEI_1)
  
  df
  
}


#' @rdname collapser
#' @export
collapser.tbl_spark <- function(df,
                                input_col_name,
                                input_format,
                                period_value,
                                side) {
  
  checkmate::assert_subset(input_col_name, colnames(df), empty.ok = TRUE)
  
  checkmate::assert_choice(
    period_value,
    c(
      "minute",
      "hour",
      "day",
      "month",
      "year"
    )
  )
  
  checkmate::assert_subset(
    side,
    c(
      "start",
      "end"
    )
  )
  
  # Derive Output colum name by adding "CEI" suffix
  
  output_col_name <- paste(input_col_name, "CEI", sep = "_")
  
  # Derive the format which will give the collapsed value for 
  # the given period
  
  if (side == "start" && period_value == "minute") {
    sdf_output_format <- "yyyy-MM-dd HH:mm:00"
  } else if (side == "end" && period_value == "minute") {
    sdf_output_format <- "yyyy-MM-dd HH:mm:59"
  } else if (side == "start" && period_value == "hour") {
    sdf_output_format <- "yyyy-MM-dd HH:00:00"
  } else if (side == "end" && period_value == "hour") {
    sdf_output_format <- "yyyy-MM-dd HH:59:59"
  } else if (side == "start" && period_value == "day") {
    sdf_output_format <- "yyyy-MM-dd 00:00:00"
  } else if (side == "end" && period_value == "day") {
    sdf_output_format <- "yyyy-MM-dd 23:59:59"
  } else if (side == "start" && period_value == "month") {
    sdf_output_format <- "yyyy-MM-01 00:00:00"
  } else if (side == "start" && period_value == "year") {
    sdf_output_format <- "yyyy-01-01 00:00:00"
  } else if (side == "end" && period_value == "month") {
    sdf_output_format <- "yyyy-MM-dd 23:59:59"
  } else if (side == "end" && period_value == "year") {
    sdf_output_format <- "yyyy-12-31 23:59:59"
  }
  
  if(grepl("HH", toupper(input_format))){
    df <- df %>%
      rename(., DT_CHK_1 = input_col_name) %>%
      mutate(., !!output_col_name := case_when(!!side == "start" && (!!period_value == "minute" || !!period_value == "hour" || !!period_value == "day" || !!period_value == "month" || !!period_value == "year") ~ to_utc_timestamp(from_unixtime(unix_timestamp(DT_CHK_1, !!input_format), !!sdf_output_format), "UTC"),
                                               !!side == "end" && (!!period_value == "minute" || !!period_value == "hour" || !!period_value == "day" || !!period_value == "year") ~ to_utc_timestamp(from_unixtime(unix_timestamp(DT_CHK_1, !!input_format), !!sdf_output_format), "UTC"),
                                               !!side == "end" && !!period_value == "month" ~ to_utc_timestamp(from_unixtime(
                                                 unix_timestamp(
                                                   last_day(
                                                     from_unixtime(
                                                       unix_timestamp(DT_CHK_1, !!input_format)
                                                       , "yyyy-MM-dd HH:mm:ss")
                                                   ),
                                                   "yyyy-MM-dd"),
                                                 !!sdf_output_format), "UTC")
      )) %>%
      rename(., !!input_col_name := DT_CHK_1)    
  } else {
    df <- df %>%
      rename(., DT_CHK_1 = input_col_name) %>%
      mutate(., !!output_col_name := case_when(!!side == "start" && (!!period_value == "minute" || !!period_value == "hour" || !!period_value == "day" || !!period_value == "month" || !!period_value == "year") ~ to_date(from_unixtime(unix_timestamp(DT_CHK_1, !!input_format), !!sdf_output_format)),
                                               !!side == "end" && (!!period_value == "minute" || !!period_value == "hour" || !!period_value == "day" || !!period_value == "year") ~ to_date(from_unixtime(unix_timestamp(DT_CHK_1, !!input_format), !!sdf_output_format)),
                                               !!side == "end" && !!period_value == "month" ~ to_date(from_unixtime(
                                                 unix_timestamp(
                                                   last_day(
                                                     from_unixtime(
                                                       unix_timestamp(DT_CHK_1, !!input_format)
                                                       , "yyyy-MM-dd HH:mm:ss")
                                                   ),
                                                   "yyyy-MM-dd"),
                                                 !!sdf_output_format))
      )) %>%
      rename(., !!input_col_name := DT_CHK_1)    
  }
  
  df
  
}