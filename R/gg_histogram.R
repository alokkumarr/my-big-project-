


# gg_histogram ----------------------------------------------------------


#'GGplot2 Histogram Chart
#'
#'This is a wrapper function to create a ggplot2 histogram chart. This function
#'unifies much of the ggplot2 layers into a single function. Default
#'parameter values are set throughout to allow for quick interactive charting,
#'but is also highly customizable to allow for more sophisicated charts.
#'
#'This function takes any of the geom_histogram arguments
#'\url{http://ggplot2.tidyverse.org/reference/geom_histogram.html}
#'
#'@param data data.frame object with variable to chart
#'@param variable variable name to chart. expects a string
#'@param fill either a variable name to apply a group by operation and create
#'  subgroups of histograms or color name or hex value to fill the histogram
#'  with color. expects a string. default is 'grey25'
#'@param color either a variable name to apply a group by operation and create
#'  subgroups of histograms or color name or hex value to color the outlines of
#'  the histogram. expects a string. default is 'black'
#'@inheritParams gg_facet
#'@param facet_labeller takes a string. expecting name of one of label_*
#'  functions. ex - "both" creates a facet label with both the column name and
#'  the value. The two most common examples are 'both' and
#'  'value'\url{http://ggplot2.tidyverse.org/reference/#section-facetting-labels}
#'
#'@param facet_args a list of arguments to pass to either the facet_grid or
#'  facet_wrap functions. default is a list with only he labeller parameter set
#'  to both colname and field value.
#'  \url{http://ggplot2.tidyverse.org/reference/facet_grid.html} or
#'  \url{http://ggplot2.tidyverse.org/reference/facet_wrap.html}
#'@inheritParams gg_coord
#'@param coord_args list of arguments to pass to the coord_* function created.
#'  default is empty list.
#'@inheritParams gg_titles
#'@param legend_args list of arguments to pass to guide_legend function to
#'  control the legend formatting
#'  \url{http://ggplot2.tidyverse.org/reference/guide_legend.html}. default is
#'  empty list
#'@param theme argument to change the chart theme. expects a ggplot_theme
#'  function. default is theme_grey(). check out
#'  \url{http://ggplot2.tidyverse.org/reference/#section-themes} for more
#'  options
#'@param palette argument to change the color palette used is either the fill or
#'  color is set to a variable. expects a vector of either color names or hex
#'  values. default is the Set1 Brewer Palette
#'@inheritDotParams ggplot2::geom_histogram
#'@seealso For more details on the geom_histogram check out
#'  \url{http://ggplot2.tidyverse.org/reference/geom_histogram.html}
#'
#'@return returns a ggplot2 object
#'@export
#'@import ggplot2
#'
#' @examples
#' # Create a data set
#' library(dplyr)
#' d <- mtcars %>% mutate(am = as.factor(am))
#'
#' # Create series of basic charts
#' gg_histogram(d, "mpg")
#' gg_histogram(d, "mpg", fill="steelblue", color="grey25")
#' gg_histogram(d, "mpg", fill="steelblue", color="grey25", title="Crouching Plot", caption = "hidden message")
#' gg_histogram(d, "mpg", fill="steelblue", color="grey25", title="Crouching Plot", caption = "hidden message", theme=theme_minimal())
#'
#' # A few charts with additional features
#' gg_histogram(d, "mpg", fill="am", color="am")
#' gg_histogram(d, "mpg", fill="am", color="am", bins=7)
#' gg_histogram(d, "mpg", fill="am", color="am", bins=7, palette = pals::viridis(2), alpha=.75)
#' gg_histogram(d, "mpg", fill="am", facet_formula = "~am", bins=7)
#' gg_histogram(d, "mpg", fill="am", facet_formula = "~am", facet_args = list(scales="free"), bins=7)
#' gg_histogram(d, "mpg", fill="am", facet_formula = "cyl~am", facet_args = list(scales="free_y"), bins=7)
#' gg_histogram(d, "mpg", fill="am", color="am", coord="flip", bins=7)
#'
gg_histogram <- function(df,
                         x_variable,
                         fill = "grey25",
                         color = "black",
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
  checkmate::assert_subset(x_variable, df_names)
  
  if (!is_color(fill)[[1]])
    checkmate::assert_choice(fill, df_names)
  if (!is_color(color)[[1]])
    checkmate::assert_choice(color, df_names)
  
  checkmate::assert_choice(facet_labeller,
                           c("value", "both", "context", "parsed", "wrap_gen", "bquote"))
  checkmate::assert_list(facet_args)
  checkmate::assert_list(coord_args)
  checkmate::assert_list(legend_args)
  checkmate::assert_choice(palette, names(sncr_palettes))
  checkmate::assert_choice(theme, c("sncr", "grey", "gray", "minimal", "bw", "linedraw",
                                    "light", "dark", "classic", "void", "test"))
  
  
  # Component Params
  facet_params <- modifyList(facet_args,
                             list(
                               facet_formula = facet_formula,
                               labeller = get(paste0("label_", facet_labeller), asNamespace("ggplot2"))
                             ))
  coord_params <- modifyList(coord_args, list(coord = coord))
  
  # Get Geom Params
  params_list <- get_geom_params(
    df_names,
    fill = fill,
    color = color,
    aes_params = list(x = x_variable),
    geom_params = list(...)
  )
  
  # Define Theme
  theme_name <- paste("theme", theme, sep = "_")
  theme_fun <- ifelse(theme == "sncr", match.fun(theme_name), get(theme_name, asNamespace("ggplot2")))
  
  
  ggplot(df, do.call("aes_string", params_list$aes_params)) +
    do.call("geom_histogram", params_list$geom_params) +
    scale_fill_sncr(palette = palette) +
    scale_color_sncr(palette = palette) +
    guides(
      fill = do.call("guide_legend", legend_args),
      color = do.call("guide_legend", legend_args)
    ) +
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
