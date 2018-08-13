#' GGplot2 Line Chart
#'
#' This is a wrapper function to create a ggplot2 line chart. This function
#' unifies much of the ggplot2 functionality into a single function. Default
#' parameter values are set throughout to allow for quick interactive charting,
#' but is also highly customizable to allow for more sophisicated charts. There
#' is an option to add points to the line as well.
#'
#' This function takes any of the geom_line arguments
#' \url{http://ggplot2.tidyverse.org/reference/geom_line.html}
#'
#' @param df data.frame object with variable to chart
#' @param x_variable x-variable to chart. expects a string. required input
#' @param y_variable y-variable to chart. expects a string. required input
#' @inheritParams ggplot2::geom_line
#' @param group a variable name to apply a group by operation and create
#'   subgroups of line charts
#' @param points TRUE/FALSE. If TRUE scatter plot points are added to the chart
#' @param point_args list of arguments to pass to geom_point function. default
#'   is an empty list
#' @inheritParams gg_bar_chart
#' @seealso geom_label arguments
#'   \url{http://ggplot2.tidyverse.org/reference/geom_text.html}
#'
#'
#' @return returns a ggplot2 object of a formatted bar chart
#' @export
#' @importFrom ggplot2 label_both label_value label_both label_context
#'   label_parsed label_wrap_gen label_bquote
#' @importFrom ggplot2 geom_line aes_string guides geom_point
#' @importFrom ggplot2 guide_legend
#'
#' @examples
#' library(ggplot2)
#' library(dplyr)
#' d <- mtcars %>% mutate(am = as.factor(am))
#' gg_line_chart(d, "wt", "mpg", facet_formula = ".~cyl", facet_args = list(scales = "free"), color = "blue")
#' gg_line_chart(d, "wt", "mpg")
#' gg_line_chart(d, "wt", "mpg", point = T)
#' gg_line_chart(d, "wt", "mpg", point = T, point_args = list(size = 3))
#' gg_line_chart(d, "wt", "mpg", color = "red", linetype = "dotdash")
#' gg_line_chart(d, "wt", "mpg", color = "red", point = T, point_args = list(size = 3))
#' gg_line_chart(d, "wt", "mpg", color = 'am', point = T, point_args = list(size = 3, shape = 15))
#' gg_line_chart(d, "wt", "mpg", point = T, point_args = list(size = 3, shape = 15, color = 'red'))
gg_line_chart <- function(df,
                          x_variable,
                          y_variable,
                          position = "identity",
                          color = sncr_pal()(1),
                          size = 1,
                          alpha = 1,
                          linetype = 1,
                          group = NULL,
                          points = F,
                          point_args = list(shape = 16),
                          label = F,
                          label_args = list(vjust = "bottom"),
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
  checkmate::assert_choice(group, df_names, null.ok = TRUE)
  checkmate::assert_flag(label)
  checkmate::assert_flag(points)
  
  if (!is_color(color)[[1]])
    checkmate::assert_choice(color, df_names)
  if (!is.numeric(alpha))
    checkmate::assert_choice(alpha, df_names)
  if (!is.numeric(size))
    checkmate::assert_choice(size, df_names)
  if (!is.numeric(linetype))
    checkmate::assert_choice(linetype, df_names)
  
  checkmate::assert_choice(facet_labeller,
                           c("value", "both", "context", "parsed", "wrap_gen", "bquote"))
  checkmate::assert_list(facet_args)
  checkmate::assert_list(coord_args)
  checkmate::assert_list(legend_args)
  checkmate::assert_list(point_args)
  checkmate::assert_list(label_args)
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
  checkmate::assert_choice(position, c("identity", "jitter", "dodge", "stack", "fill"))
  
  
  # Component Params
  facet_params <-  facet_params <- modifyList(facet_args,
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
    linetype = linetype,
    alpha = alpha,
    size = size,
    group = group,
    aes_params = list(x = x_variable, y = y_variable),
    geom_params = list(stat = "identity", position = position)
  )
  
  if (points) {
    if (color %in% colnames(df)) {
      point_params <- modifyList(list(size = size, alpha = alpha),
                                 point_args)
    } else {
      point_params <- modifyList(list(
        size = size,
        alpha = alpha,
        color = color
      ),
      point_args)
    }
  } else {
    point_params <- modifyList(list(alpha = 0.01), point_args)
  }
  
  # Set Guides Params
  if (x_variable == color) {
    guides_params <- list(color = "none")
  } else{
    guides_params <- list(color = do.call("guide_legend", legend_args))
  }
  
  # Set Labels Params
  if (label) {
    y_label_arg <- label_var <- y_variable
  } else{
    y_label_arg <- label_var <- NULL
  }
  
  # define options for position
  position_fun <-
    get(paste0("position_", position), asNamespace("ggplot2"))
  if (position == "jitter") {
    position_params <- list(width = 0, height = 0.1)
  } else if (position == "dodge") {
    # would one ever use "dodge" with line?
    position_params <- list(width = .9)
  } else{
    # assuming this is "identity"
    position_params <- list()
  }
  label_params <- modifyList(
    list(
      label = y_label_arg,
      y = label_var,
      stat = "identity",
      position = do.call("position_fun", position_params)
    ) ,
    label_args
  )
  
  
  # Define Theme
  theme_name <- paste("theme", theme, sep = "_")
  theme_fun <- ifelse(theme == "sncr",
                      match.fun(theme_name),
                      get(theme_name, asNamespace("ggplot2")))
  
  ggplot(df, do.call("aes_string", params_list$aes_params)) +
    do.call("geom_line", params_list$geom_params) +
    do.call("geom_point", point_params) +
    scale_fill_sncr(palette = palette) +
    scale_color_sncr(palette = palette) +
    do.call('gg_label', label_params) +
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
