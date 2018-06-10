#' Dataframe Anomaly Detection function
#'
#' Anomaly detection function for time series data. Function determines
#' statistical anomlies in the dataset based on configurable theshold
#' parameters.
#'
#' Function is similiar to Seasonl Hybrid ESD algorithm discussed here
#' \url{https://arxiv.org/pdf/1704.07706.pdf} and implemented in the
#' \code{\link[AnomalyDetection]{AnomalyDetectionTs}}
#'
#'
#' @param df dataframe
#' @param index_var index date/datetime variable
#' @param measure_var numeric measure variable to analyze
#' @param frequency numeric period length of seasonality. Ex - 7 for daily data
#'   with weekly seasonality. see
#'   \url{https://robjhyndman.com/hyndsight/seasonal-periods/} for more details
#' @param direction character input for direction of anomalies to detect.
#'   Options are 'pos' - only positive anomalies are detected, 'neg' only
#'   negative anomalies are detected, or 'both' for both 'pos' and 'neg'
#'   anomalies
#' @param alpha numeric statistical threshold for anomlie detection. default is
#'   .01 or 99 percent Confidence.
#' @param max_anoms numeric threshold for percent of data identified as
#'   anomalies. default is .01 or 1 percent of the data
#' @param trend_window numeric input for percentage of total data width for
#'   trend detection. default is 0.75. Large values smooth the trend which may
#'   not be appropriate for non-stationary trends. Small values may overfit the
#'   trend and lead to may false negatives
#'
#' @return dataframe with same number of records as input dataframe. Appends
#'   additional fields for seasonality, trend, residual, and anomaly. Anomalies
#'   are coded with a binary flag of 1
#' @export
detect <- function(df,
                   index_var,
                   measure_var,
                   frequency,
                   direction,
                   alpha,
                   max_anoms,
                   trend_window) {
  UseMethod("detect", df)
}


#' @inheritParams detect
#' @rdname detect
#' @export
detect.data.frame <- function(df,
                              index_var,
                              measure_var,
                              frequency,
                              direction = 'pos',
                              alpha = .01,
                              max_anoms = .01,
                              trend_window = 0.75) {

  df_names <- colnames(df)
  checkmate::assert_choice(index_var, df_names)
  checkmate::assert_choice(measure_var, df_names)
  checkmate::assert_subset(class(df[[measure_var]]), c("numeric", "integer"))
  checkmate::assert_choice(direction, c("pos", "both", "neg"))
  checkmate::assert_number(frequency, lower=2, upper=Inf)
  checkmate::assert_number(alpha, lower=0, upper=1)
  checkmate::assert_number(max_anoms, lower=0, upper=1)
  checkmate::assert_number(trend_window, lower=0, upper=1)

  if(alpha > .1 ){
    message("alpha input larger than norm - anomaly threshold may be low. Recommend 0.01 or 0.001")
  }

  if (max_anoms > .1) {
    message("max anoms input larger than norm - anomaly threshold may be low. Recommend 0.05 or 0.01")
  }

  two_tail <- ifelse(direction == "both", TRUE, FALSE)
  n <- nrow(df)
  ma <- max_anoms * n
  p <- ifelse(two_tail, 1 - alpha / 2, 1 - alpha)
  cv <- qnorm(p, 0, 1)
  t_window <- round(n * trend_window)
  if (t_window %% 2 == 1)
    t_window <- t_window + 1

  df_ts <- ts(df[[measure_var]], frequency = frequency)
  df_stl <- as.data.frame(stl(
    df_ts,
    s.window = "period",
    robust = TRUE,
    t.window = t_window
  )$time.series)

  df$seasonal <- df_stl$seasonal
  df$trend <- df_stl$trend
  df$resid <- df_stl$remainder

  if (direction == "pos") {
    df$resid_std <- (df$resid - median(df$resid)) / mad(df$resid)
  } else if (direction == "neg") {
    df$resid_std <- (median(df$resid) - df$resid) / mad(df$resid)
  } else {
    df$resid_std <- abs((df$resid - median(df$resid)) / mad(df$resid))
  }

  df$anomaly <- ifelse(df$resid_std > cv, 1, 0)
  df <- df[order(df$resid_std, decreasing = T), ]
  df$metric_index <- 1:n
  df$anomaly <- ifelse(df$metric_index > ma, 0, df$anomaly)
  df[order(df[[index_var]]), c(df_names, "seasonal", "trend", "resid", "anomaly")]
}



#' @inheritParams detect
#' @rdname detect
#' @export
detect.grouped_df <- function(df,
                              index_var,
                              measure_var,
                              frequency,
                              direction = 'pos',
                              alpha = .01,
                              max_anoms = .01,
                              trend_window = 0.75){

  df_names <- colnames(df)
  checkmate::assert_choice(index_var, df_names)
  checkmate::assert_choice(measure_var, df_names)
  checkmate::assert_subset(class(df[[measure_var]]), c("numeric", "integer"))
  checkmate::assert_choice(direction, c("pos", "both", "neg"))
  checkmate::assert_number(frequency, lower=2, upper=Inf)
  checkmate::assert_number(alpha, lower=0, upper=1)
  checkmate::assert_number(max_anoms, lower=0, upper=1)
  checkmate::assert_number(trend_window, lower=0, upper=1)

  if (alpha > .1) {
    message("alpha input larger than norm - anomaly threshold may be low. Recommend 0.01 or 0.001")
  }

  if(max_anoms > .1 ){
    message("max anoms input larger than norm - anomaly threshold may be low. Recommend 0.05 or 0.01")
  }

  two_tail <- ifelse(direction == "both", TRUE, FALSE)
  p <- ifelse(two_tail, 1-alpha/2, 1-alpha)
  cv <- qnorm(p, 0, 1)

  df_ts <- ts(df[[measure_var]], frequency = frequency)
  t_window <- round(length(df_ts)*trend_window)
  if(t_window %% 2 == 1) t_window <- t_window + 1
  df_stl <- as.data.frame(stl(df_ts,
                              s.window = "period",
                              robust = TRUE,
                              t.window = t_window)$time.series)

  df %>%
    dplyr::select_at(c(index_var, measure_var)) %>%
    dplyr::mutate(seasonal = df_stl$seasonal,
                  trend = df_stl$trend,
                  resid = df_stl$remainder) %>%
    dplyr::mutate_at("resid", funs(resid_std = if (direction == "pos") {
      (. - median(.)) / mad(.)
    } else if (direction == "neg") {
      (median(.) - .) / mad(.)
    } else{
      abs((. - median(.)) / mad(.))
    })) %>%
    dplyr::mutate(anomaly = ifelse(resid_std > cv, 1, 0)) %>%
    dplyr::arrange(desc(resid_std)) %>%
    dplyr::mutate(anomaly = ifelse(row_number() > max_anoms*n(), 0, anomaly)) %>%
    dplyr::arrange_at(index_var) %>%
    dplyr::select_at(c(index_var, measure_var, 'seasonal', 'trend', 'resid', 'anomaly'))
}


#' Anomaly Dectection Wrapper Function
#'
#' Detecter is a wrapper function for the detect function. Detecter takes a
#' normalized index based dataframe and reshapes for core detect function.
#' Detecter allows for grouping and multiple measure variables
#'
#' @param group_vars optional vector of grouping variables to apply dectect
#'   function to. default is NULL - no grouping
#' @inheritParams detect
#' @export
detecter <- function(df,
                     index_var,
                     group_vars,
                     measure_vars,
                     frequency,
                     direction,
                     alpha,
                     max_anoms,
                     trend_window) {
  UseMethod("detecter")
}


#' @rdname detecter
#' @export
detecter.data.frame <- function(df,
                                index_var,
                                group_vars = NULL,
                                measure_vars,
                                frequency,
                                direction = 'pos',
                                alpha = .01,
                                max_anoms = .01,
                                trend_window = 0.75){


  df_names <- colnames(df)
  checkmate::assert_number(length(index_var), lower=1, upper=1)
  checkmate::assert_choice(index_var, df_names)
  checkmate::assert_subset(measure_vars, df_names)
  checkmate::assert_subset(group_vars, df_names, empty.ok = TRUE)

  df %>%
    dplyr::select_at(c(index_var, group_vars, measure_vars)) %>%
    a2munge::melter(.,
                    id_vars = c(index_var, group_vars),
                    measure_vars,
                    variable_name = "measure",
                    value_name = "value") %>%
    dplyr::group_by_at(c(group_vars, "measure")) %>%
    dplyr::do(
      a2munge::detect(.,
                      index_var = index_var,
                      measure_var = "value",
                      frequency = frequency,
                      alpha = alpha,
                      direction = direction,
                      max_anoms = max_anoms,
                      trend_window = trend_window)
    ) %>%
    dplyr::ungroup()
}


#' @rdname detecter
#' @export
detecter.tbl_spark <- function(df,
                               index_var,
                               group_vars,
                               measure_vars,
                               frequency,
                               direction = 'pos',
                               alpha = .01,
                               max_anoms = .01,
                               trend_window = 0.75){
  df_names <- colnames(df)
  df_schema <- sdf_schema(df)
  checkmate::assert_number(length(index_var), lower=1, upper=1)
  checkmate::assert_choice(index_var, df_names)
  checkmate::assert_subset(measure_vars, df_names)
  checkmate::assert_subset(group_vars, df_names, empty.ok = TRUE)


  index_date_chk <- df_schema[[index_var]]$type == "DateType"

  if(index_date_chk) {
    df <- dplyr::mutate_at(df, index_var, as.character)
  }

  results <- df %>%
    dplyr::select_at(c(index_var, group_vars, measure_vars)) %>%
    a2munge::melter(
      .,
      id_vars = c(index_var, group_vars),
      measure_vars,
      variable_name = "measure",
      value_name = "value"
    ) %>%
    sparklyr::spark_apply(.,
                          function(e, l) {
                            library(a2munge)
                            #library(checkmate)
                            index_var <- l$i_var
                            measure_var <- l$m_var
                            freq <- l$freq
                            dir <- l$dir
                            alpha <- l$a
                            max_anoms <- l$anoms
                            detect_fun <- l$fun
                            trend_win <- l$trend_win
                            detect_fun(e[, c(index_var, measure_var)],
                                       index_var,
                                       measure_var,
                                       freq,
                                       dir,
                                       alpha,
                                       max_anoms,
                                       trend_win)
                          },
                          group_by = c(group_vars, "measure"),
                          names = c(index_var,
                                    "value",
                                    "seasonal",
                                    "trend",
                                    "resid",
                                    "anomaly"),
                          context = {
                            l = list(
                              i_var = index_var,
                              m_var = "value",
                              freq = frequency,
                              dir = direction,
                              a = alpha,
                              anoms = max_anoms,
                              trend_win = trend_window,
                              fun = get("detect", asNamespace("a2munge"))
                            )
                          }) %>%
    select_at(c(
      index_var,
      group_vars,
      "measure",
      "value",
      "seasonal",
      "trend",
      "resid",
      "anomaly"
    ))

  if(index_date_chk) {
    results <- dplyr::mutate_at(results, index_var,  funs(to_date))
  }

  results
}



