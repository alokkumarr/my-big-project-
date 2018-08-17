#'Date Converter Function
#'
#'Date Converter function which is used to convert String date column with
#'given format to Date or Datetime field with format as either yyyy-MM-dd 
#'or yyyy-MM-dd HH:mm:ss
#'@param df DataFrame
#'@param input_col_name Input string column which is to be converted to date
#'@param input_format Input format of String column. Allowed formats are
#' MM/dd/yyyy HH:mm:ss, MM/dd/yyyy, yyyy/MM/dd HH:mm:ss, yyyy/MM/dd,
#' dd/MM/yyyy HH:mm:ss, dd/MM/yyyy. The forward slash symbol can be interchanged
#' with hifen.
#'
#'@return returns DataFrame with converted Date column added with name as
#' {INPUT_COLUMN_NAME}_CONV & format as either yyyy-MM-dd or yyyy-MM-dd HH:mm:ss. 
#'
#'@export
#'@import dplyr
#'@import checkmate
#'@import sparklyr
#'@import lubridate
#'@import fansi
#'@import cli
#'@import utf8
#'
#'
#'@examples
#'
#'library(dplyr)
#'library(sparklyr)
#'library(lubridate)
#'
#'dateconverter(df, "EVENT_TIMESTAMP", "MM/dd/yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "month", "start")

dateconverter <- function(df, input_col_name, input_format) {
  UseMethod("dateconverter")
}


#' @rdname dateconverter
#' @export
dateconverter.data.frame <- function(df,
                                     input_col_name,
                                     input_format) {
  
  checkmate::assert_subset(input_col_name, colnames(df), empty.ok = TRUE)
  
  checkmate::assert_choice(input_format,
                           c(
                             "MM/dd/yyyy HH:mm:ss",
                             "MM/dd/yyyy",
                             "MM-dd-yyyy HH:mm:ss",
                             "MM-dd-yyyy",
                             "MM/dd/yy HH:mm:ss",
                             "MM/dd/yy",
                             "MM-dd-yy HH:mm:ss",
                             "MM-dd-yy",
                             "yyyy-MM-dd HH:mm:ss",
                             "yyyy-MM-dd",
                             "yyyy/MM/dd HH:mm:ss",
                             "yyyy/MM/dd",
                             "dd-MM-yyyy HH:mm:ss",
                             "dd-MM-yyyy",
                             "dd/MM/yyyy HH:mm:ss",
                             "dd/MM/yyyy"
                           )
  )
  
  # Derive Output colum name by adding "CONV" suffix
  
  output_col_name <- paste(input_col_name, "CONV", sep = "_")
  
  # Get a list of columns to be extracted so that all intermediate columns are removed
  # from final result
  
  select_vars <- c(colnames(df), output_col_name)
  
  # Derive the Input & Output formats required for R data frames in the
  # format required for the lubridate functions
  
  chk_input_format <- gsub("[^A-z0-9_ ]", "", input_format)
  inp_for_chk <- ""
  
  inp_for_chk <- case_when(chk_input_format == "MMddyyyy HHmmss" ~ "mdy_hms",
                           chk_input_format == "MMddyyyy" ~ "mdy",
                           chk_input_format == "MMddyy" ~ "mdy",
                           chk_input_format == "yyyyMMdd HHmmss" ~ "ymd_hms",
                           chk_input_format == "yyyyMMdd" ~ "ymd",
                           chk_input_format == "ddMMyyyy HHmmss" ~ "dmy_hms",
                           chk_input_format == "ddMMyyyy" ~ "dmy"
  )
  
  # Update the DF with the output column which will have formatted
  # date value with collapsed period
  
  if(grepl("HH", toupper(input_format))){
    df <- df %>%
      dplyr::rename(., DT_CHK_1 = input_col_name) %>%
      mutate(., !!output_col_name := do.call(inp_for_chk, list(DT_CHK_1))) %>%
      dplyr::rename(., !!input_col_name := DT_CHK_1)
  } else {
    df <- df %>%
      dplyr::rename(., DT_CHK_1 = input_col_name) %>%
      mutate(., !!output_col_name := as.Date(do.call(inp_for_chk, list(DT_CHK_1)))) %>%
      dplyr::rename(., !!input_col_name := DT_CHK_1)
  }
  
  df
}




#' @rdname dateconverter
#' @export
dateconverter.tbl_spark <- function(df,
                                    input_col_name,
                                    input_format) {
  
  checkmate::assert_subset(input_col_name, colnames(df), empty.ok = TRUE)
  
  checkmate::assert_choice(input_format,
                           c(
                             "MM/dd/yyyy HH:mm:ss",
                             "MM/dd/yyyy",
                             "MM-dd-yyyy HH:mm:ss",
                             "MM-dd-yyyy",
                             "MM/dd/yy HH:mm:ss",
                             "MM/dd/yy",
                             "MM-dd-yy HH:mm:ss",
                             "MM-dd-yy",
                             "yyyy-MM-dd HH:mm:ss",
                             "yyyy-MM-dd",
                             "yyyy/MM/dd HH:mm:ss",
                             "yyyy/MM/dd",
                             "dd-MM-yyyy HH:mm:ss",
                             "dd-MM-yyyy",
                             "dd/MM/yyyy HH:mm:ss",
                             "dd/MM/yyyy"
                           )
  )
  
  # Derive Output colum name by adding "CONV" suffix
  
  output_col_name <- paste(input_col_name, "CONV", sep = "_")
  
  # Get a list of columns to be extracted so that all intermediate columns are removed
  # from final result
  
  select_vars <- c(colnames(df), output_col_name)
  
  # If period is Month & Collapse direction is end, Last_Day function has
  # to be used to get last day of the month.
  # Important Note. More testing is required to check if Time zones might be
  # an issue here due to the usage of epoch time for date formatting.
  
  if(grepl("HH", toupper(input_format))){
    df <- df %>%
      rename(., DT_CHK_1 = input_col_name) %>%
      mutate(., DER_TIME_1 = from_unixtime(unix_timestamp(DT_CHK_1, !!input_format), 
                                           "yyyy-MM-dd HH:mm:ss")) %>%
      mutate(., !!output_col_name := to_utc_timestamp(DER_TIME_1, "UTC")
      ) %>%
      dplyr::rename(., !!input_col_name := DT_CHK_1) %>%
      select(., select_vars)
  } else {
    df <- df %>%
      rename(., DT_CHK_1 = input_col_name) %>%
      mutate(., DER_TIME_1 = from_unixtime(unix_timestamp(DT_CHK_1, !!input_format), 
                                           "yyyy-MM-dd HH:mm:ss")) %>%
      mutate(., !!output_col_name := to_date(DER_TIME_1)
      ) %>%
      dplyr::rename(., !!input_col_name := DT_CHK_1) %>%
      select(., select_vars)
  }
  
  df
}