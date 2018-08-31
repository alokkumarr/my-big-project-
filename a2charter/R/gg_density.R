


# gg_density_chart ----------------------------------------------------------


#'GGplot2 Density Chart
#'
#'This is a wrapper function to create a ggplot2 density chart. This function
#'unifies much of the ggplot2 functionality into a single function. Default
#'parameter values are set throughout to allow for quick interactive charting,
#'but is also highly customizable to allow for more sophisicated charts.
#'
#'#' This function takes any of the geom_density arguments
#'\url{http://ggplot2.tidyverse.org/reference/geom_density.html}
#'
#'@inheritParams gg_histogram
#'@inheritDotParams ggplot2::geom_density
#'
#'@seealso For more details on the geom_density check out
#'  \url{http://ggplot2.tidyverse.org/reference/geom_density.html}
#'
#'@return returns a ggplot2 object
#'@export
#'
#' @examples
#' # Create a data set
#' library(dplyr)
#' d <- mtcars %>% mutate(am = as.factor(am))
#'
#' # Create series of basic charts
#' gg_density_chart(d, "mpg")
#' gg_density_chart(d, "mpg", fill="steelblue", color="grey25")
#' gg_density_chart(d, "mpg", fill="steelblue", color="grey25",
#'                  title="Crouching Plot", caption = "hidden message")
#' gg_density_chart(d, "mpg", fill="steelblue", color="grey25",
#'                  title="Crouching Plot", caption = "hidden message",
#'                  theme="minimal")
#'
#' # A few charts with additional features
#' gg_density_chart(d, "mpg", fill="am", color="am")
#' gg_density_chart(d, "mpg", fill="am", color="am", adjust=1/2)
#' gg_density_chart(d, "mpg", fill="am", color="am", adjust=1/2, palette = "viridis")
#' gg_density_chart(d, "mpg", fill="am", facet_formula = "~am",
#'                  adjust=1/2, alpha=0.1)
#' gg_density_chart(d, "mpg", fill="am", facet_formula = "~am",
#'                  facet_args = list(scales="free"),
#'                  adjust=1/2, alpha=0.1)
#' gg_density_chart(d, "mpg", fill="am", facet_formula = "cyl~am",
#'                  facet_args = list(scales="free_y"),
#'                  adjust=1/2, alpha=0.1)
#' gg_density_chart(d, "mpg", fill="am", color="am", coord="flip", adjust=1/2)
gg_density_chart<- function(df,
                            x_variable,
                            fill = sncr_pal()(1),
                            color = "black",
                            alpha = 0.50,
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
                             list(facet_formula = facet_formula,
                                  labeller = get(paste0("label_", facet_labeller),
                                                 asNamespace("ggplot2"))))
  coord_params <- modifyList(coord_args, list(coord = coord))
  
  # Get Geom Params
  params_list <-
    get_geom_params(
      df_names,
      fill = fill,
      color = color,
      aes_params = list(x = x_variable),
      geom_params = list(alpha = alpha, ...)
    )
  
  # Define Theme
  theme_name <- paste("theme", theme, sep = "_")
  theme_fun <- ifelse(theme == "sncr", match.fun(theme_name), get(theme_name, asNamespace("ggplot2")))
  
  
  ggplot(df, do.call("aes_string", params_list$aes_params)) +
    do.call("geom_density", params_list$geom_params) +
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
