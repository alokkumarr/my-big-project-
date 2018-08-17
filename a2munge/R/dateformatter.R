#'Date Period Formatter Function
#'
#'Date Period formatter function which is configurable & can be used in
#'other a2 functions to get required formatted Date value. Return column
#'will be of the class string. 
#'
#'@param df DataFrame
#'@param input_col_name Input Date column on which transformation has to be applied
#'@param input_format Input format of Date column. Allowed formats are
#' MM/dd/yyyy HH:mm:ss, MM/dd/yyyy, yyyy/MM/dd HH:mm:ss, yyyy/MM/dd,
#' dd/MM/yyyy HH:mm:ss, dd/MM/yyyy. The forward slash symbol can be interchanged
#' with hifen.
#'@param output_format Required format for the output Date column. Default value
#' is "yyyy-MM-dd HH:mm:ss"
#'
#'@return returns DataFrame with formatted string column added with name as
#' {INPUT_COLUMN_NAME}_FMT
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
#'dateformatter(df, "10/14/2018 23:59:59", "MM/dd/yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss")
#'Return value = "2018-10-14 23:59:59"

dateformatter <- function(df, input_col_name, input_format, output_format) {
  UseMethod("dateformatter")
}


#' @rdname dateformatter
#' @export
dateformatter.data.frame <- function(df,
                                     input_col_name,
                                     input_format,
                                     output_format = "yyyy-MM-dd HH:mm:ss") {
  
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
  
  checkmate::assert_choice(output_format,
                           c(
                             "MM/dd/yyyy HH:mm:ss",
                             "MM/dd/yyyy",
                             "MM-dd-yyyy HH:mm:ss",
                             "MM-dd-yyyy",
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
  
  # Derive Output colum name by adding "_FMT" suffix
  
  output_col_name <- paste(input_col_name, "FMT", sep = "_")
  
  # Get a list of columns to be extracted so that all intermediate columns are removed
  # from final result
  
  select_vars <- c(colnames(df), output_col_name)
  
  # Derive the Input & Output formats required for R data frames in the
  # format required for the lubridate functions
  
  chk_input_format <- gsub("[^A-z0-9_ ]", "", input_format)
  inp_for_chk <- ""
  
  inp_for_chk <- dplyr::case_when(chk_input_format == "MMddyyyy HHmmss" ~ "mdy_hms",
                                  chk_input_format == "MMddyyyy" ~ "mdy",
                                  chk_input_format == "MMddyy" ~ "mdy",
                                  chk_input_format == "yyyyMMdd HHmmss" ~ "ymd_hms",
                                  chk_input_format == "yyyyMMdd" ~ "ymd",
                                  chk_input_format == "ddMMyyyy HHmmss" ~ "dmy_hms",
                                  chk_input_format == "ddMMyyyy" ~ "dmy"
  )
  
  rdf_output_format <- dplyr::case_when(output_format == "MM/dd/yyyy HH:mm:ss" ~ "%m/%d/%Y %H:%M:%S",
                                        output_format == "MM/dd/yyyy" ~ "%m/%d/%Y",
                                        output_format == "MM-dd-yyyy HH:mm:ss" ~ "%m-%d-%Y %H:%M:%S",
                                        output_format == "MM-dd-yyyy" ~ "%m-%d-%Y",
                                        output_format == "MM-dd-yy HH:mm:ss" ~ "%m-%d-%Y %H:%M:%S",
                                        output_format == "MM-dd-yy" ~ "%m-%d-%Y",
                                        output_format == "MM/dd/yy HH:mm:ss" ~ "%m-%d-%Y %H:%M:%S",
                                        output_format == "MM/dd/yy" ~ "%m-%d-%Y",
                                        output_format == "yyyy-MM-dd HH:mm:ss" ~ "%Y-%m-%d %H:%M:%S",
                                        output_format == "yyyy-MM-dd" ~ "%Y-%m-%d",
                                        output_format == "yyyy/MM/dd HH:mm:ss" ~ "%Y/%m/%d %H:%M:%S",
                                        output_format == "yyyy/MM/dd" ~ "%Y/%m/%d",
                                        output_format == "dd-MM-yyyy HH:mm:ss" ~ "%d-%m-%Y %H:%M:%S",
                                        output_format == "dd-MM-yyyy" ~ "%d-%m-%Y",
                                        output_format == "dd/MM/yyyy HH:mm:ss" ~ "%d/%m/%Y %H:%M:%S",
                                        output_format == "dd/MM/yyyy" ~ "%d/%m/%Y"
  )
  
  # Update the DF with the output column which will have required
  # format date value
  
  df <- df %>%
    dplyr::rename(., DT_CHK_1 = input_col_name) %>%
    mutate(., DT_CHK_2 = do.call(inp_for_chk, list(DT_CHK_1))) %>%
    mutate(., !!output_col_name := format(DT_CHK_2, rdf_output_format)) %>%
    dplyr::rename(., !!input_col_name := DT_CHK_1) %>%
    select(., select_vars)
  
  df
}



#' @rdname dateformatter
#' @export
dateformatter.tbl_spark <- function(df,
                                    input_col_name,
                                    input_format,
                                    output_format = "yyyy-MM-dd HH:mm:ss") {
  
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
  
  checkmate::assert_choice(output_format,
                           c(
                             "MM/dd/yyyy HH:mm:ss",
                             "MM/dd/yyyy",
                             "MM-dd-yyyy HH:mm:ss",
                             "MM-dd-yyyy",
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
  
  # Derive Output colum name by adding "FMT" suffix
  
  output_col_name <- paste(input_col_name, "FMT", sep = "_")
  
  # If period is Month & Collapse direction is end, Last_Day function has
  # to be used to get last day of the month.
  # Important Note. More testing is required to check if Time zones might be
  # an issue here due to the usage of epoch time for date formatting.
  
  df <- df %>%
    rename(., DT_CHK_1 = input_col_name) %>%
    mutate(., !!output_col_name := from_unixtime(unix_timestamp(DT_CHK_1, !!input_format), 
                                                 !!output_format)
    ) %>%
    rename(., !!input_col_name := DT_CHK_1)
  
  df
}