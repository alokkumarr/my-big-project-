

# gg_bar_chart ----------------------------------------------------------------


#' GGplot2 Bar Chart
#'
#' This is a wrapper function to create a ggplot2 bar chart. This function
#' unifies much of the ggplot2 functionality into a single function. Default
#' parameter values are set throughout to allow for quick interactive charting,
#' but is also highly customizable to allow for more sophisicated charts.
#'
#' This function takes any of the geom_bar arguments
#' \url{http://ggplot2.tidyverse.org/reference/geom_bar.html}
#'
#' @inheritParams gg_histogram
#' @inheritDotParams ggplot2::geom_bar
#' @param x_variable x-variable to chart. expects a string. required input
#' @param y_variable y-variable to chart. default is NULL. If NULL then the
#'   x-variable is summarized and either a count or proportion is rendered.
#'   expects a string
#' @param weight variable to sum the count of the x_variable by. Default is
#'   NULL. expects a string with column name
#' @param position name of position function to apply
#' @seealso  position functions
#'   \url{http://ggplot2.tidyverse.org/reference/#section-layer-position-adjustment}
#'
#' @param proportion TRUE/FALSE. If TRUE, computes percentages, otherwise counts
#'   if y-variable not provided. default is FALSE
#' @param sort TRUE/FALSE. If TRUE, orders the bars by either the count in the
#'   case of x-variable only or by the y-variable provided. Default is FALSE
#' @param desc logical option to sort boxplots in descending order. Default is
#'   TRUE. Note sort has to be TRUE for desc option to apply
#' @param label logical. If TRUE creates a geom_label layer and sets the label
#'   variable to the y-variable
#' @param label_args list of arguments passed to the geom_label layer. default
#'   sets the vjust to 'bottom'
#' @seealso geom_label arguments
#'   \url{http://ggplot2.tidyverse.org/reference/geom_text.html}
#'
#' @return returns a ggplot2 object of a formatted bar chart
#' @export
#' 
#' @importFrom ggplot2 geom_bar
#' @importFrom ggplot2 position_identity position_jitter position_dodge
#'   position_stack position_fill
#' @importFrom ggplot2 label_both label_value label_context label_parsed
#'   label_wrap_gen
#' @importFrom scales percent
#' @importFrom stats as.formula
#' @importFrom utils modifyList
#' @importFrom forcats fct_reorder
#'
#' @examples
#' # Create a data set
#' library(dplyr)
#' d <- mtcars %>% mutate(am = as.factor(am), cyl = as.factor(cyl))
#'
#' # Create series of basic charts
#' gg_bar_chart(d, "am")
#' gg_bar_chart(d, "am", fill="steelblue", color="grey25")
#' gg_bar_chart(d, "am", fill="steelblue", color="grey25",
#'              title="Stranger", subtitle="Things")
#' gg_bar_chart(d, "am", fill="steelblue", color="grey25",
#'              title="Stranger", subtitle="Things", theme="minimal")
#'
#' # A few charts with more features
#' gg_bar_chart(d, "am", proportion = TRUE)
#' gg_bar_chart(d, "am", fill="cyl")
#' gg_bar_chart(d, "am", fill="cyl", position="fill")
#' gg_bar_chart(d, "am", fill="cyl", label=TRUE)
#'
#' # Example of x-variable and y-variable
#' d1 <- mtcars %>% group_by(am = factor(am)) %>% summarise(count = n())
#' gg_bar_chart(d1, x_variable="am", y_variable="count")
gg_bar_chart <- function(df,
                         x_variable,
                         y_variable = NULL,
                         weight = NULL,
                         fill = sncr_pal()(1),
                         color = "black",
                         position = "dodge",
                         proportion = FALSE,
                         sort = FALSE,
                         desc = TRUE,
                         label = FALSE,
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
  checkmate::assert_choice(y_variable, df_names, null.ok = TRUE)
  checkmate::assert_choice(weight, df_names, null.ok = TRUE)
  checkmate::assert_flag(proportion)
  checkmate::assert_flag(label)
  
  if (!is_color(fill)[[1]])
    checkmate::assert_choice(fill, df_names)
  if (!is_color(color)[[1]])
    checkmate::assert_choice(color, df_names)
  
  checkmate::assert_choice(facet_labeller,
                           c("value", "both", "context", "parsed", "wrap_gen", "bquote"))
  checkmate::assert_list(facet_args)
  checkmate::assert_list(coord_args)
  checkmate::assert_list(legend_args)
  checkmate::assert_list(label_args)
  checkmate::assert_choice(palette, names(sncr_palettes))
  checkmate::assert_choice(theme, c("sncr", "grey", "gray", "minimal", "bw", "linedraw",
                                    "light", "dark", "classic", "void", "test"))
  checkmate::assert_choice(position, c("identity", "jitter", "dodge", "stack", "fill"))
  
  # y_variable adjustments
  if (is.null(y_variable)) {
    if (proportion) {
      y_aes_arg <- "..count../sum(..count..)"
    } else{
      y_label_arg <- y_aes_arg <- "..count.."
    }
    stat <- "count"
  } else{
    y_aes_arg <- y_variable
    stat <- "identity"
  }
  
  
  # y_scale_continuous Adjustments
  if (position == "fill" | proportion) {
    y_axis_label <- match.fun(percent)
    y_label_arg <- paste0("scales::percent(", y_aes_arg, ")")
    y_axis_title <- ifelse(is.null(y_axis_title), "proportion", y_axis_title)
  } else{
    y_axis_label <- waiver()
    y_label_arg <- y_aes_arg
  }
  
  # Sort Params
  sort_args <- gg_sort(sort, x_variable, y_variable, x_axis_title, desc)
  sort_params <- sort_args$sort_params
  x_axis_title <- sort_args$x_axis_title
  
  
  # Component Params
  facet_params <- modifyList(facet_args,
                             list(facet_formula = facet_formula,
                                  labeller = get(paste0("label_", facet_labeller),
                                                 asNamespace("ggplot2"))))
  coord_params <- modifyList(coord_args, list(coord = coord))
  
  
  # Get Geom Params
  params_list <- get_geom_params(
    df_names,
    fill = fill,
    color = color,
    aes_params = list(x = sort_params),
    geom_params = list(
      do.call("aes_string", list(y = y_aes_arg, weight = weight)),
      stat = stat,
      position = position,
      ...
    )
  )
  
  # Set Guides Params
  if (x_variable == fill) {
    guides_params <- list(fill = "none")
  } else{
    guides_params <- list(fill = do.call("guide_legend", legend_args))
  }
  
  if (x_variable == color) {
    guides_params <- modifyList(guides_params, list(color = "none"))
  } else{
    guides_params <- modifyList(guides_params,
                                list(color = do.call("guide_legend", legend_args)))
  }
  
  
  # Set Labels Params
  if (label) {
    label_var <- y_aes_arg
  } else{
    y_label_arg <- label_var <- NULL
  }
  position_fun <- get(paste0("position_", position), asNamespace("ggplot2"))
  if (position == "dodge") {
    position_params <- list(width = .9)
  } else if (position == "stack" | position == "fill") {
    position_params <- list(vjust = .5)
  } else{
    position_params <- list()
  }
  label_params <- modifyList(list(
    label = y_label_arg,
    y = label_var,
    stat = stat,
    position = do.call("position_fun", position_params)
  ),
  label_args)
  
  
  # Define Theme
  theme_name <- paste("theme", theme, sep = "_")
  theme_fun <- ifelse(theme == "sncr",
                      match.fun(theme_name),
                      get(theme_name, asNamespace("ggplot2")))
  
  
  ggplot(df, do.call("aes_string", params_list$aes_params)) +
    do.call("geom_bar", params_list$geom_params) +
    scale_fill_sncr(palette = palette) +
    scale_color_sncr(palette = palette) +
    scale_y_continuous(labels = y_axis_label) +
    do.call("gg_label", label_params) +
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
