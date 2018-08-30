
#' GGplot Interval Chart
#'
#' This is a wrapper function to create a ggplot2 vertical interval chart. This
#' function unifies much of the ggplot2 functionality into a single function.
#' Default parameter values are set throughout to allow for quick interactive
#' charting, but is also highly customizable to allow for more sophisicated
#' charts.
#'
#' This function takes any of the geom_errorbar arguments
#' \url{http://ggplot2.tidyverse.org/reference/geom_linerange.html}
#'
#' @inheritParams gg_scatter_chart
#' @inheritDotParams ggplot2::stat_summary
#' @param lines_args list of arguments to pass to geom_line function. default is
#'   an empty list
#' @param alpha controls the opacity of the fill and color
#' @param size controls the line size
#' @param shape controls the shape of the point
#' @param chart_type chart types. accepts errorbar, linerange, pointrange, or
#'   crossbar
#' @param chart_fun summary function to apply. accepts mean_cl_normal, mean_sdl,
#'   mean, median, median_hilow, mean_se
#' @param line logical option to overlay a line between mean/median point values
#'
#' @seealso geom_line
#'   \url{http://ggplot2.tidyverse.org/reference/geom_path.html}
#'
#' @return returns a ggplot2 graphic object
#' @export
#' @importFrom ggplot2 stat_summary
#' @importFrom Hmisc smean.cl.boot smean.cl.normal smean.sdl smedian.hilow
#'
#' @examples
#' # Create a data set
#' library(dplyr)
#' d <- mtcars %>% mutate(am = as.factor(am), cyl = as.factor(cyl))
#'
#' # Create series of basic charts
#' gg_interval_chart(d, y_variable = "mpg", x_variable = "am",
#'                   chart_type= "linerange" ,size=1.1)
#' gg_interval_chart(d, y_variable = "mpg", x_variable = "am",
#'                   chart_type= "pointrange", size=1.1, shape=18)
#' gg_interval_chart(d, y_variable = "mpg", x_variable = "am",
#'                   chart_type="errorbar", size=1.1, width = 0.2,
#'                   color='red',lines_args=list(color='blue'))
#' gg_interval_chart(d, y_variable = "mpg", x_variable = "am",
#'                   chart_type="crossbar", size=1.5, shape=18)
#' gg_interval_chart(d, y_variable = "mpg", x_variable = "am",
#'                   chart_type='errorbar',width=0.2,color='red')
#' gg_interval_chart(d, y_variable = "mpg", x_variable = "am",
#'                   chart_type='errorbar',width=0.2,
#'                   facet_formula='~cyl', color='cyl',
#'                   lines_args=list(color='blue'))
#' gg_interval_chart(d, y_variable = "mpg", x_variable = "am",
#'                   chart_type='errorbar', width=0.2, color='red',
#'                   lines_args=list(color='blue'))
gg_interval_chart <- function(df,
                              x_variable,
                              y_variable,
                              color = sncr_pal()(1),
                              alpha = 1.0,
                              size = 1.1,
                              shape = 18,
                              chart_type = "pointrange",
                              chart_fun = "mean_sdl",
                              line = TRUE,
                              lines_args = list(),
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
  checkmate::assert_flag(line)
  
  
  if (!is_color(color)[[1]])
    checkmate::assert_choice(color, df_names)
  if (!is.numeric(alpha))
    checkmate::assert_choice(alpha, df_names)
  if (!is.numeric(size))
    checkmate::assert_choice(size, df_names)
  if (!is.numeric(shape))
    checkmate::assert_choice(shape, df_names)
  
  
  checkmate::assert_choice(chart_type,
                           c("errorbar", "linerange", "pointrange", "crossbar"))
  checkmate::assert_choice(
    chart_fun,
    c(
      "mean_cl_normal",
      "mean_sdl",
      "mean",
      "median",
      "median_hilow",
      "mean_se"
    )
  )
  checkmate::assert_choice(facet_labeller,
                           c("value", "both", "context", "parsed", "wrap_gen", "bquote"))
  checkmate::assert_list(facet_args)
  checkmate::assert_list(coord_args)
  checkmate::assert_list(legend_args)
  checkmate::assert_choice(palette, names(sncr_palettes))
  checkmate::assert_choice(
    theme,
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
  facet_params <- modifyList(facet_args, list(facet_formula=facet_formula,
                                              labeller = get(paste0("label_", facet_labeller),
                                                             asNamespace("ggplot2"))))
  coord_params <- modifyList(coord_args, list(coord = coord))
  
  
  # Get Geom Params
  params_list <- get_geom_params(df_names,
                                 color = color,
                                 size = size,
                                 shape = shape,
                                 alpha = alpha,
                                 aes_params = list(x=x_variable, y=y_variable),
                                 geom_params = list(geom=chart_type, ...))
  
  if(x_variable == color){
    guides_params <- list(color = "none")
  }else{
    guides_params <- list(color = do.call("guide_legend", legend_args))
  }
  
  # Define Theme
  theme_name <- paste("theme", theme, sep = "_")
  theme_fun <- ifelse(theme == "sncr",
                      match.fun(theme_name),
                      get(theme_name, asNamespace("ggplot2")))
  
  
  ggplot(df, do.call("aes_string", params_list$aes_params)) +
    do.call("gg_line", c(list(line=line, group=x_variable), lines_args)) +
    do.call("stat_summary", c(fun.data=chart_fun, params_list$geom_params)) +
    scale_fill_sncr(palette = palette) +
    scale_color_sncr(palette = palette) +
    do.call('guides', guides_params) +
    do.call('gg_facet', facet_params) +
    do.call("gg_coord", coord_params) +
    gg_titles(title = title,
              subtitle = subtitle,
              x_axis_title = x_axis_title, 
              y_axis_title = y_axis_title,
              caption = caption) +
    theme_fun()
}
