#' Dataframe Anomaly Detection function
#'
#' Anomaly detection function for time series data. Function determines
#' statistical anomlies in the dataset based on configurable theshold
#' parameters.
#'
#' Function is similiar to Seasonl Hybrid ESD algorithm discussed
#' here \url{https://arxiv.org/pdf/1704.07706.pdf} and implemented in the
#' \code{\link[AnomalyDetection]{AnomalyDetectionTs}}
#'
#'
#' @param df dataframe
#' @param index_var index date/datetime variable. Default is NULL which will set
#'   the index variable to the first column in the dataframe
#' @param measure_var numeric measure variable to analyze. Default is NULL which
#'   will set the measure variable to the second column in the dataframe
#' @param frequency numeric period length of seasonality. Ex - 7 for daily data
#'   with weekly seasonality
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
detect <- function(df, ...) {
  UseMethod("detect", df)
}


#' @inheritParams detect
#' @rdname detect
#' @export
detect.data.frame <- function(df,
                              index_var = NULL,
                              measure_var = NULL,
                              frequency,
                              direction = 'pos',
                              alpha = .01,
                              max_anoms = .01,
                              trend_window = 0.75) {

  stopifnot(any(class(df) %in% c("data.frame")))

  if (is.null(index_var))
    index_var <- colnames(df)[1]
  if (is.null(measure_var))
    measure_var <- colnames(df)[2]

  stopifnot(class(df[[measure_var]]) %in% c("numeric", "integer"))

  if (!direction %in% c("pos", "both", "neg")) {
    stop("direction should be either 'pos', 'both' or 'neg'")
  }

  if (alpha > .1) {
    message("alpha input larger than norm - anomaly threshold may be low. Recommend 0.01 or 0.001")
  }

  if (alpha < 0 | alpha > 1) {
    stop("alpha has to be greater than zero and less than 1")
  }

  if (max_anoms > .1) {
    message("max anoms input larger than norm - anomaly threshold may be low. Recommend 0.05 or 0.01")
  }

  if (max_anoms < 0 | max_anoms > 1) {
    stop("max_anoms has to be greater than zero and less than 1")
  }

  if (trend_window < 0 | trend_window > 1) {
    stop("trend_window has to be greater than zero and less than 1. Recommend 0.5 or 0.75")
  }


  col_names <- colnames(df)
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
    abs(df$resid_std <- (df$resid - median(df$resid)) / mad(df$resid))
  }

  df$anomaly <- ifelse(df$resid_std > cv, 1, 0)
  df <- df[order(df$resid_std, decreasing = T), ]
  df$metric_index <- 1:n
  df$anomaly <- ifelse(df$metric_index > ma, 0, df$anomaly)
  df[order(df[[index_var]]), c(col_names, "seasonal", "trend", "resid", "anomaly")]
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

  stopifnot(any(class(df) %in% c("data.frame")))
  stopifnot(class(df[[measure_var]]) %in% c("numeric", "integer"))

  if(! direction %in% c("pos", "both", "neg")){
    stop("direction should be either 'pos', 'both' or 'neg'")
  }

  if(alpha > .1 ){
    message("alpha input larger than norm - anomaly threshold may be low. Recommend 0.01 or 0.001")
  }

  if(alpha < 0 | alpha > 1){
    stop("alpha has to be greater than zero and less than 1")
  }

  if(max_anoms > .1 ){
    message("max anoms input larger than norm - anomaly threshold may be low. Recommend 0.05 or 0.01")
  }

  if(max_anoms < 0 | max_anoms > 1){
    stop("max_anoms has to be greater than zero and less than 1")
  }

  if(trend_window < 0 | trend_window > 1){
    stop("trend_window has to be greater than zero and less than 1. Recommend 0.5 or 0.75")
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
detecter <- function(df, ...) {
  UseMethod("detecter", df)
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

  if(! index_var %in% colnames(df)){
    stop("index_var not a valid column name for dataframe input")
  }
  if(! all(group_vars %in% colnames(df))){
    stop("group_vars not all valid column names for dataframe input")
  }
  if(! all(measure_vars %in% colnames(df))){
    stop("measure_vars not all valid column names for dataframe input")
  }

  df %>%
    dplyr::select_at(c(index_var, group_vars, measure_vars)) %>%
    melter(.,
           id_vars = c(index_var, group_vars),
           measure_vars,
           variable_name = "measure",
           value_name = "value") %>%
    dplyr::group_by_at(c(group_vars, "measure")) %>%
    do(
      detect(.,
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

  if(! index_var %in% colnames(df)){
    stop("index_var not a valid column name for dataframe input")
  }
  if(! all(group_vars %in% colnames(df))){
    stop("group_vars not all valid column names for dataframe input")
  }
  if(! all(measure_vars %in% colnames(df))){
    stop("measure_vars not all valid column names for dataframe input")
  }


  df %>%
    dplyr::select_at(c(index_var, group_vars, measure_vars)) %>%
    melter(
      .,
      id_vars = c(index_var, group_vars),
      measure_vars,
      variable_name = "measure",
      value_name = "value"
    ) %>%
    sparklyr::spark_apply(.,
                          function(e, l) {
                            index_var <- l$i_var
                            measure_var <- l$m_var
                            freq <- l$freq
                            dir <- l$dir
                            alpha <- l$a
                            max_anoms <- l$anoms
                            detect_fun <- l$fun
                            trend_win <- l$trend_win
                            detect_fun(e[, c(index_var, measure_var)], NULL, NULL,
                                       freq, dir, alpha, max_anoms, trend_win)
                          },
                          group_by = c(group_vars, "measure"),
                          names = c(
                            index_var,
                            "value",
                            "seasonal",
                            "trend",
                            "resid",
                            "anomaly"
                          ),
                          context = {
                            l = list(
                              i_var = index_var,
                              m_var = "value",
                              freq = frequency,
                              dir = direction,
                              a = alpha,
                              anoms = max_anoms,
                              trend_win = trend_window,
                              fun = match.fun("detect.data.frame")
                            )
                          }) %>%
    select_at(c(index_var, group_vars, "measure", "value", "seasonal", "trend", "resid", "anomaly"))
}



