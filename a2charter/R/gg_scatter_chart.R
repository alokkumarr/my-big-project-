

# gg_smoother Chart -----------------------------------------------------


#' GGplot Smoother Chart
#'
#' This is a wrapper function to create a ggplot2 xy scatter and smoother chart.
#' This function unifies much of the ggplot2 functionality into a single
#' function. Default parameter values are set throughout to allow for quick
#' interactive charting, but is also highly customizable to allow for more
#' sophisicated charts.
#'
#' This function combines many of the features from the ggplot2 function's:
#' geom_point \url{http://ggplot2.tidyverse.org/reference/geom_point.html} and
#' geom_smooth  \url{http://ggplot2.tidyverse.org/reference/geom_smooth.html}
#'
#' @inheritParams gg_line_chart
#' @param alpha controls the opacity of the fill and color
#' @param size controls the line size
#' @param shape controls the shape of the point
#' @param smoother logical option to add smoother to chart
#' @param smooth_method string name for smoother method to apply. accepts "lm",
#'   "glm", "rlm", "loess", "gam", "rlm"
#' @param smooth_args list of additional arguments to pass to method
#' @param smooth_formula regression formula provided to smoother method. ex -
#'   y~x
#' @param smooth_ci logical option to add smoother confidence interval to chart
#' @param smooth_color color option for smoother line
#' @param smooth_fill fill option for smoother confidence interval
#' @seealso geom_point
#'   \url{http://ggplot2.tidyverse.org/reference/geom_point.html}
#' @seealso geom_smooth
#'   \url{http://ggplot2.tidyverse.org/reference/geom_smooth.html}
#'
#' @return returns a ggplot2 geom_smooth object
#' @export
#' @importFrom ggplot2 geom_smooth
#'
#' @examples
#' # Create a data set
#' library(dplyr)
#' d <- mtcars %>% mutate(am = as.factor(am), cyl = as.factor(cyl))
#'
#' # Create series of basic charts
#' gg_scatter_chart(d, y_variable = "mpg", x_variable = "wt")
#' gg_scatter_chart(d, y_variable = "mpg", x_variable = "wt")
#' gg_scatter_chart(d, y_variable = "mpg", x_variable = "wt", color="am")
#' gg_scatter_chart(d, y_variable ="mpg", x_variable = "wt", points = FALSE)
#'
#' # Smoother Options
#' gg_scatter_chart(d, y_variable = "mpg", x_variable = "wt",
#'                  smoother = TRUE, smooth_method="lm") # linear model
#' gg_scatter_chart(d, y_variable = "mpg", x_variable = "wt",
#'                  smoother = TRUE, smooth_method="lm",
#'                  smooth_ci=F, smooth_color="red") # linear model without CI
#' gg_scatter_chart(d, y_variable = "mpg", x_variable = "wt",
#'                  smoother = TRUE, smooth_method="lm",
#'                  smooth_ci=F, smooth_color="red",
#'                  smooth_formula=y~splines::bs(x, degree=1, knots=2)) # linear spline model
#' gg_scatter_chart(d, y_variable = "vs", x_variable = "wt",
#'                  smoother = TRUE, smooth_method="glm",
#'                  smooth_color="red",
#'                  smooth_args=list(family = "binomial")) # binomial logistic model
#' 
gg_scatter_chart <- function(df,
                             x_variable,
                             y_variable,
                             color = "grey25",
                             size = 2,
                             alpha = 1,
                             shape = 16,
                             points = TRUE,
                             position = "identity",
                             smoother = FALSE,
                             smooth_method = "auto", 
                             smooth_args = list(),
                             smooth_formula = as.formula("y~x"),
                             smooth_ci = TRUE,
                             smooth_color = 'red',
                             smooth_fill = "grey75",
                             facet_formula = NULL,
                             facet_labeller = "both",
                             facet_args = list(),
                             coord = NULL,
                             coord_args = list(),
                             title = NULL,
                             subtitle = NULL,
                             x_axis_title = NULL,
                             y_axis_title = NULL,
                             caption = NULL,
                             legend_args = list(),
                             theme = "sncr",
                             palette = "a2",
                             ...) {
  checkmate::assert_true(any(class(df) %in% "data.frame"))
  df_names <- colnames(df)
  checkmate::assert_choice(x_variable, df_names)
  checkmate::assert_choice(y_variable, df_names)
  checkmate::assert_flag(smoother)
  checkmate::assert_flag(points)
  checkmate::assert_choice(smooth_method, c("auto", "lm", "glm", "loess", "gam", "rlm"))
  checkmate::assert_list(smooth_args)
  checkmate::assert_class(smooth_formula, "formula")
  checkmate::assert_flag(smooth_ci)
  checkmate::assert_true(is_color(smooth_color)[[1]])
  checkmate::assert_true(is_color(smooth_fill)[[1]] )
  
 
  if (!is_color(color)[[1]]) {
    checkmate::assert_choice(color, df_names)
    smooth_color <- NULL
  }
  if (!is.numeric(alpha))
    checkmate::assert_choice(alpha, df_names)
  if (!is.numeric(size))
    checkmate::assert_choice(size, df_names)
  if (!is.numeric(shape))
    checkmate::assert_choice(shape, df_names)
  
  checkmate::assert_choice(position, c("identity", "jitter", "dodge", "stack", "fill"))
  checkmate::assert_choice(facet_labeller,
                           c("value", "both", "context", "parsed", "wrap_gen", "bquote"))
  checkmate::assert_list(facet_args)
  checkmate::assert_list(coord_args)
  checkmate::assert_list(legend_args)
  checkmate::assert_choice(palette, names(sncr_palettes))
  checkmate::assert_choice(theme,
                           c(
                             "sncr",
                             "grey",
                             "gray",
                             "minimal",
                             "bw",
                             "linedraw",
                             "light",
                             "dark",
                             "classic",
                             "void",
                             "test"
                           )
  )
  
  # Component Params
  facet_params <- modifyList(facet_args,
                             list(
                               facet_formula = facet_formula,
                               labeller = get(paste0("label_", facet_labeller),
                                              asNamespace("ggplot2"))
                             ))
  coord_params <- modifyList(coord_args, list(coord = coord))
  
  
  # Get Geom Params
  params_list <- get_geom_params(
    df_names,
    color = color,
    size = size,
    alpha = alpha,
    shape = shape,
    aes_params = list(x = x_variable, y = y_variable),
    geom_params = list(stat = "identity", position = position, ...)
  )
  
  # Set Smoother params
  smoother_parms <- list(smooth = smoother,
                         method = smooth_method,
                         method.args = smooth_args,
                         formula = smooth_formula,
                         se = smooth_ci,
                         color = smooth_color,
                         fill = smooth_fill)
  smoother_parms <- Filter(Negate(is.null), smoother_parms)
  
  # Set Guides Params
  if (x_variable == color) {
    guides_params <- list(color = "none")
  } else{
    guides_params <- list(color = do.call("guide_legend", legend_args))
  }
  
  # Define Theme
  theme_name <- paste("theme", theme, sep = "_")
  theme_fun <- ifelse(theme == "sncr",
                      match.fun(theme_name),
                      get(theme_name, asNamespace("ggplot2")))
  
  
  ggplot(df, do.call("aes_string", params_list$aes_params)) +
    do.call("gg_point", c(list(points = points), params_list$geom_params)) +
    do.call("gg_smooth", smoother_parms) +
    scale_fill_sncr(palette = palette) +
    scale_color_sncr(palette = palette) +
    do.call('guides', guides_params) +
    do.call('gg_facet', facet_params) +
    do.call("gg_coord", coord_params) +
    gg_titles(
      title = title,
      subtitle = subtitle,
      x_axis_title = x_axis_title,
      y_axis_title = y_axis_title,
      caption = caption
    ) +
    theme_fun()
}
