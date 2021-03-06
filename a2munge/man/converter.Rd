% Generated by roxygen2: do not edit by hand
% Please edit documentation in R/converter.R
\name{converter}
\alias{converter}
\alias{converter.data.frame}
\alias{converter.tbl_spark}
\title{Date Converter Function}
\usage{
converter(df, measure_vars, input_format, output_type, time_zone,
  output_suffix)

\method{converter}{data.frame}(df, measure_vars, input_format,
  output_type = "datetime", time_zone = "UTC",
  output_suffix = "CONV")

\method{converter}{tbl_spark}(df, measure_vars, input_format,
  output_type = "datetime", time_zone = "UTC",
  output_suffix = "CONV")
}
\arguments{
\item{df}{DataFrame}

\item{measure_vars}{Input string column which is to be converted to date}

\item{input_format}{Input format of String column. Allowed formats are
MM/dd/yyyy HH:mm:ss, MM/dd/yyyy, yyyy/MM/dd HH:mm:ss, yyyy/MM/dd,
dd/MM/yyyy HH:mm:ss, dd/MM/yyyy. The forward slash symbol can be interchanged
with hifen.}

\item{output_type}{Output Data type required. Should be either date or datetime.
Default is datetime.}

\item{time_zone}{Timezone to be considered in Date conversions. Default is UTC}

\item{output_suffix}{Suffix to be added to the new column generated by the function}
}
\value{
returns DataFrame with converted Date column added with name as
{INPUT_COLUMN_NAME}_CONV & format as either yyyy-MM-dd or yyyy-MM-dd HH:mm:ss.
}
\description{
Date Converter function which is used to convert String date column with
given format to Date or Datetime field with format as either yyyy-MM-dd
or yyyy-MM-dd HH:mm:ss
}
\examples{

library(dplyr)
library(lubridate)

date_func_df <-
  data.frame(
    STRING_COL = c(
      "2017-01-01 10:15:15",
      "2017-09-23 14:26:59",
      "2017-11-15 05:05:05",
      "2018-05-11 08:15:18",
      "2018-03-27 23:59:59"
    ),
    stringsAsFactors = FALSE
  )

converter(date_func_df, "STRING_COL", "yyyy-MM-dd HH:mm:ss", "datetime")
}
