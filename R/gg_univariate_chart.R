
#' GGplot2 Univariate Chart
#'
#' @inheritDotParams gg_bar_chart
#' @param data_name name for dataset. added to title if provided. default input
#'   is NULL which defers to dataset object name
#'
#'   \code{\link{gg_bar_chart}} \code{\link{gg_boxplot}}
#'   \code{\link{gg_histogram}}
#' @param plot_sizes vector of two ratios that add to 1. Sets the size ratios of
#'   the two charts. 
#'   
#' @return returns a ggplot2 object as a result of a call to a gg_* charting
#'   function
#' @export
#' @importFrom gridExtra grid.arrange
#' @importFrom dplyr top_n count_ select_
#' @importFrom magrittr %>%
#'
#'
#' @examples
#'
#' gg_univariate_chart(mtcars, "mpg")
#' gg_univariate_chart(mutate(mtcars, am = as.factor(am)), "am")
gg_univariate_chart <- function(df,
                                x_variable,
                                data_name = NULL,
                                fill = "#1D3AB2",
                                color = "grey15",
                                alpha = .60,
                                bins = 13,
                                max_bars = 20,
                                y_axis_title = 'Count',
                                caption = "Synchronoss",
                                outlier.colour = "red",
                                outlier.shape = 1,
                                theme = "sncr",
                                palette = "a2",
                                plot_sizes = c(.75, .25),
                                ...) {
  
  checkmate::assert_true(any(class(df) %in% "data.frame"))
  df_names <- colnames(df)
  checkmate::assert_choice(x_variable, df_names)
  checkmate::assert_string(data_name, null.ok = TRUE)
  checkmate::assert_numeric(plot_sizes, lower = 0, upper=1, len = 2)
  checkmate::assert_number(sum(plot_sizes), lower = 1, upper = 1)
  
  # Get variable class and create chart based on type
  var_class <- df %>%
    dplyr::select_(x_variable) %>%
    sapply(class) %>%
    as.character
  cat(x_variable, "class is a", var_class, "\n")
  
  # Set chart titles
  if(is.null(data_name)) data_name <- deparse(substitute(df))
  title <- paste(paste0(data_name, ":"), x_variable)
  subtitle <- "Univariate Chart"
  
  if (var_class %in% c("numeric", "integer")) {
    p1 <- gg_histogram(
      df,
      x_variable,
      fill = fill,
      color = color,
      alpha = alpha,
      bins = bins,
      title = title,
      subtitle = subtitle,
      x_axis_title = "",
      y_axis_title = y_axis_title,
      theme = theme,
      palette = palette,
      ...
    )
    p2 <- gg_boxplot(
      df,
      y_variable = x_variable,
      fill = fill,
      color = color,
      alpha = alpha,
      coord = "flip",
      caption = caption,
      outlier.colour = outlier.colour,
      outlier.shape = outlier.shape,
      y_axis_title = NULL,
      theme = theme,
      palette = palette,
      ...
    )
    
    gridExtra::grid.arrange(p1, p2, ncol = 1, heights = plot_sizes)
    
  } else if (var_class %in% c("character", "factor")) {
    data_counts <- df %>%
      dplyr::count_(x_variable) %>%
      dplyr::top_n(max_bars, n)
    
    p1 <- gg_bar_chart(
      data_counts,
      x_variable = x_variable,
      y_variable = "n",
      fill = fill,
      color = color,
      alpha = alpha,
      title = title,
      subtitle = subtitle,
      y_axis_title = y_axis_title,
      theme = theme,
      palette = palette,
      ...
    )
    print(p1)
  } else if (var_class %in% c("Date", "POSIXct", "POSIXlt", "POSIXt")) {
    p1 <- gg_bar_chart(
      df,
      x_variable = x_variable,
      fill = fill,
      color = color,
      alpha = alpha,
      title = title,
      subtitle = subtitle,
      y_axis_title = y_axis_title,
      caption = caption,
      theme = theme,
      palette = palette,
      ...
    )
    print(p1)
  } else{
    cat("unknown data type", "\n", "no chart created", "\n")
  }
}
