
#' Highcharts Anomaly Chart
#' @export
hc_detect_anomalies_chart <- function(df,
                                      title = "Anomaly Detecter",
                                      subtitle = "Anomaly Chart",
                                      credits = "Synchronoss") {

  anom_df <- df %>%
    filter(anomaly == 1) %>%
    select(date, index, value, anomaly)  %>%
    mutate(title = sprintf("Anom #%s", seq_along(anomaly))) %>%
    mutate(text = sprintf("Anomaly #%s Detected on %s", seq_along(anomaly), date))

  highchart(type = "stock") %>%
    hc_tooltip(shared = TRUE) %>%
    hc_exporting(enabled = TRUE) %>%
    hc_add_series_times_values(dates = df$date,
                               values = df$value,
                               color = "#708090",
                               id = "series",
                               name = "Series") %>%
    hc_add_series(anom_df, hcaes(x = date),
                  title = anom_df$title,
                  text = anom_df$text,
                  color = "red",
                  type = "flags",
                  onSeries = "series") %>%
    hc_title(text = title) %>%
    hc_subtitle(text = subtitle) %>%
    hc_credits(text = text) %>%
    hc_add_theme(hc_theme_smpl())
}


#' Highcharts Detecter Decomposition Chart
#' @export
hc_detect_decomp_chart <- function(df,
                                   title = "Anomaly Detecter",
                                   subtitle = "Decomposition Chart",
                                   credits = "Synchronoss") {

  # Decomp Chart
  highchart(type = "stock") %>%
    hc_tooltip(shared = TRUE) %>%
    hc_exporting(enabled = TRUE) %>%
    hc_yAxis_multiples(
      create_yaxis(3, height = c(2, 1, 1), turnopposite = TRUE)
    ) %>%
    hc_add_series_times_values(dates = df$date,
                               values = df$value,
                               color = "#708090",
                               id = "series",
                               name = "Series",
                               yAxis = 0) %>%
    hc_add_series_times_values(dates = df$date,
                               values = df$trend,
                               color = "blue",
                               id = "trend",
                               name = "Trend",
                               yAxis = 0) %>%
    hc_add_series_times_values(dates = df$date,
                               values = df$seasonal,
                               color = "green",
                               id = "seasonal",
                               name = "Seasonal",
                               yAxis = 1) %>%
    hc_add_series_times_values(dates = df$date,
                               values = df$resid,
                               color = "#ff5349",
                               id = "resid",
                               name = "Resid",
                               yAxis = 2) %>%
    hc_title(text = "Anomaly Detecter") %>%
    hc_subtitle(text = "Decomposition Chart") %>%
    hc_credits(text = "Synchronoss") %>%
    hc_add_theme(hc_theme_smpl())
}
