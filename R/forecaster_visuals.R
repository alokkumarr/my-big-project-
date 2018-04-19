#' Highcharts Forecast Chart
#' @export
hc_forecast_chart <- function(fobj,
                              title = "Forecaster",
                              subtitle = "Forecast Chart",
                              credits = "Synchronoss") {


  forecasts <- get_forecasts(fobj)

  hc_area_80_data <- forecasts %>%
    select(date, lower80, upper80) %>%
    mutate_if(is.numeric, round, digits=0) %>%
    mutate(date = datetime_to_timestamp(date))

  hc_area_95_data <- forecasts %>%
    select(date, lower95, upper95) %>%
    mutate_if(is.numeric, round, digits=0) %>%
    mutate(date = datetime_to_timestamp(date))

  highchart(type = "stock") %>%
    hc_xAxis(type = "datetime") %>%
    hc_tooltip(shared = TRUE) %>%
    hc_add_series_times_values(dates = as.Date(fobj$fit$date),
                               values = round(fobj$fit$y),
                               color = "grey25",
                               id = "trend",
                               name = "Simple Chat Count") %>%
    hc_add_series_times_values(dates = as.Date(forecasts$date),
                               values = round(forecasts$mean),
                               color = "red",
                               id = "forecast",
                               name = "Simple Chat Forecast",
                               zIndex = 3) %>%
    hc_add_series(data = list_parse2(hc_area_95_data),
                  color = "lightgrey",
                  id = "forecast",
                  type ="arearange",
                  fillOpacity = 0.3,
                  name = "Forecast 95% Confidence Interval",
                  zIndex = 1, lineWidth = 0) %>%
    hc_add_series(data = list_parse2(hc_area_80_data),
                  color = "grey",
                  id = "forecast",
                  type ="arearange",
                  fillOpacity = 0.3,
                  name = "Forecast 80% Confidence Interval",
                  zIndex = 2, lineWidth = 0) %>%
    hc_title(text = title) %>%
    hc_subtitle(text = subtitle) %>%
    hc_credits(text = credits) %>%
    hc_add_theme(hc_theme_smpl())
}
