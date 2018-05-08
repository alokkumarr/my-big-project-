


# sncr_bivariate_chart ----------------------------------------------------------------

#' GGplot2 Bi-variate Chart
#'
#' This is a wrapper function to create bivariate charts. continuous x-variable
#' type should use a gg_scatter_chart categorical x-variable should use
#' gg_interval_chart. multi-class y-variables are a special case that need not
#' be included for this story
#'
#'
#' @inheritParams gg_scatter_chart
#' @inheritParams gg_interval_chart
#' @inheritParams gg_boxplot
#' @param data_name name for dataset. added to title if provided. default input
#'   is NULL which defers to dataset object name
#' @param interval_shape shape value to pass to interval chart
#' @param interval_size size of point passed to interval chart
#' @param interval_chart_type interval chart type. see gg_interval_chart for
#'   options
#' @param smooth_degree degree spline agrument. default is three. should be 1-3
#' @param smooth_knots number of knots used by spline
#' @param plot_sizes vector of two ratios that add to 1. Sets the size ratios of
#'   the two charts. The smooth/interval corresponds to the first ratio, the
#'   boxplot the second
#'
#'   \code{\link{gg_scatter_chart}} \code{\link{gg_interval_chart}}
#'
#' @return returns a ggplot2 graphic object
#' @export
#' @importFrom dplyr summarise
#' @importFrom ggplot2 element_blank
#'
#' @examples
#' Create a data set
#' library(dplyr)
#' d <- mtcars %>% mutate(am = as.factor(am), cyl = as.factor(cyl))
#' sncr_bivariate_chart(d, x_variable='cyl', y_variable='am')
#' sncr_bivariate_chart(d, x_variable='mpg', y_variable='am', smooth_ci = F)
#' sncr_bivariate_chart(d, x_variable='cyl', y_variable='mpg', width=0.2)
#' sncr_bivariate_chart(d, x_variable='mpg', y_variable='wt')
sncr_bivariate_chart <- function(df,
                                 x_variable,
                                 y_variable,
                                 data_name = NULL,
                                 fill = "grey75",
                                 color = "#1D3AB2",
                                 alpha = 0.75,
                                 interval_shape = 18,
                                 interval_size = 1.05,
                                 interval_chart_type = "pointrange",
                                 points = TRUE,
                                 smooth_degree = 3,
                                 smooth_knots = 7,
                                 smooth_ci = TRUE,
                                 caption = "Synchronoss",
                                 theme = "sncr",
                                 palette = "a2",
                                 plot_sizes = c(.7, .3),
                                 ...) {
  checkmate::assert_true(any(class(df) %in% "data.frame"))
  df_names <- colnames(df)
  checkmate::assert_choice(x_variable, df_names)
  checkmate::assert_choice(y_variable, df_names)
  checkmate::assert_string(data_name, null.ok = TRUE)
  checkmate::assert_number(interval_shape, lower = 0)
  checkmate::assert_number(interval_size, lower = 0)
  checkmate::assert_number(alpha, lower = 0, upper=1)
  checkmate::assert_number(smooth_degree, lower = 1, upper=3)
  checkmate::assert_number(smooth_knots, lower = 3, upper = 21)
  checkmate::assert_numeric(plot_sizes, lower = 0, upper=1, len = 2)
  checkmate::assert_number(sum(plot_sizes), lower = 1, upper = 1)
  
  # Set chart titles
  if (is.null(data_name))
    data_name <- deparse(substitute(df))
  title <-
    paste(paste0(data_name, ":"), y_variable, "by", x_variable)
  subtitle <- "Bivariate Chart"
  
  is_char_or_fctr_df <- function(data, x_variable) {
    var_x_class <- data %>%
      dplyr::select_(x_variable) %>%
      sapply(class)
    var_x_class[1] %in% c("character", "factor")
  }
  
  # Get variable class and create chart based on type
  x_var_char <- is_char_or_fctr_df(df, x_variable)
  y_var_char <- is_char_or_fctr_df(df, y_variable)
  
  # Determine points
  if (nrow(df) > 1000) {
    points <- F
    if (!x_var_char) {
      warning("Data has more than a 1000 records. Removing points from scatter")
    }
  }
  
  
  # Check for multiple y_var distinct values if character
  if (y_var_char) {
    if (df %>% dplyr::count_(y_variable) %>% nrow() > 2) {
      stop(
        "y-variable input has more than 2 distinct categories\nMultinomial targets are not currently supported in the sncr_bivariate_chart function"
      )
    } else{
      # Get first factor
      y_var_fact_label <- df %>%
        dplyr::summarise(levels(as.factor(!!as.name(y_variable)))[2]) %>%
        as.character()
      y_axis_title <- paste0("Prob(", y_variable, " = ", y_var_fact_label, ")")
      smooth_family <- "binomial"
      
      # convert to y_variable to numeric
      df <- df %>%
        mutate(!!y_variable :=  as.numeric(as.factor(!!as.name(y_variable))) - 1)
    }
  } else{
    y_axis_title <- y_variable
    smooth_family <- "gaussian"
  }
  
  # X Variable is Categorical
  if (x_var_char) {
    # error bar with bar chart
    p1 <- gg_interval_chart(
        df,
        x_variable = x_variable,
        y_variable = y_variable,
        chart_type = interval_chart_type,
        shape = interval_shape,
        size = interval_size,
        color = color,
        lines_args = list(color = "black"),
        alpha = alpha,
        title = title,
        subtitle = subtitle,
        caption = NULL,
        y_axis_title = y_axis_title,
        x_axis_title = x_variable,
        theme = theme,
        palette = palette
      )
    p2 <- gg_bar_chart(
      df,
      x_variable = x_variable,
      fill = color,
      alpha = alpha,
      theme = theme,
      palette = palette,
      title = NULL,
      subtitle = NULL,
      x_axis_title = NULL,
      caption = caption
    )
    gridExtra::grid.arrange(p1, p2, ncol = 1, heights = plot_sizes)
  } else{
    # Smoother with boxplot
    p1 <- gg_scatter_chart(
      df,
      x_variable = x_variable,
      y_variable = y_variable,
      x_axis_title = "",
      color = color,
      title = title,
      subtitle = subtitle,
      caption = NULL,
      theme = theme,
      palette = palette,
      points = points,
      smoother = TRUE,
      smooth_method = "glm",
      smooth_ci = smooth_ci,
      smooth_args = list(family = smooth_family),
      smooth_formula = y ~ splines::bs(x, degree = smooth_degree, knots = smooth_knots)
    )
    p2 <- gg_boxplot(
      df,
      y_variable = x_variable,
      fill = color,
      alpha = alpha,
      coord = "flip",
      theme = theme,
      palette = palette,
      x_axis_title = "",
      y_axis_title = x_axis_title,
      caption = caption,
      title = NULL,
      subtitle = NULL
      ) +
      theme(axis.text.y = element_blank(),
            axis.ticks.y = element_blank())
    
    gridExtra::grid.arrange(p1, p2, ncol = 1, heights = plot_sizes)
  }
}
