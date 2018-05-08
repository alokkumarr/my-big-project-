

# gg_boxplot ------------------------------------------------------------

#' GGplot2 Boxplot Chart
#'
#' @inheritParams gg_histogram
#' @param x_variable x-variable to chart. expects a string. default is NULL. not
#'   a required input
#' @param y_variable continuous y-variable to chart. expects a string. default
#'   is NULL. either a y-variable is required or boxplot values (ymin, lower,
#'   middle, upper, ymax)
#' @param ymin computed min value. default is NULL
#' @param lower computed lower value of boxplot. default is NULL
#' @param middle computed middle value of boxplot. default is NULL
#' @param upper computed upper value of boxplot. default is NULL
#' @param ymax computed max value of boxplot. default is NULL
#' @param sort logical option to sort boxplots by y_variable. Default is FALSE
#' @param desc logical option to sort boxplots in descending order. Default is
#'   TRUE. Note sort has to be TRUE for desc option to apply
#'
#'
#' @inheritDotParams ggplot2::geom_boxplot
#'
#' @return returns a ggplot2 boxplot object
#' @export
#' @importFrom ggplot2 geom_boxplot
#'
#' @examples
#' #' # Create a data set
#' d <- mtcars %>% mutate(am = as.factor(am), cyl = as.factor(cyl))
#'
#' # Create series of basic charts
#' gg_boxplot(d, y_variable = "mpg")
#' gg_boxplot(d, y_variable = "mpg", x_variable = "am")
#' gg_boxplot(d, y_variable = "mpg", x_variable = "am", fill="blue")
#' gg_boxplot(d, y_variable ="mpg", notch=T)
#'
#' # Create boxplot from values
#' y <- rnorm(100)
#' df <- data.frame(
#'  x = 1,
#'  y0 = min(y),
#'  y25 = quantile(y, 0.25),
#'  y50 = median(y),
#'  y75 = quantile(y, 0.75),
#'  y100 = max(y))
#'
#' gg_boxplot(df, ymin = "y0", lower = "y25", middle = "y50", upper = "y75", ymax = "y100", fill="darkorange")
gg_boxplot <- function(df,
                       x_variable = NULL,
                       y_variable = NULL,
                       ymin = NULL,
                       lower = NULL,
                       middle = NULL,
                       upper = NULL,
                       ymax = NULL,
                       fill = sncr_pal()(1),
                       color = "black",
                       alpha = .50,
                       position = "dodge",
                       sort = FALSE,
                       desc = TRUE,
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
  checkmate::assert_choice(x_variable, df_names, null.ok = TRUE)
  checkmate::assert_choice(y_variable, df_names, null.ok = TRUE)
 
  
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
  
  
  # Only Y-variable provided
  if (is.null(x_variable)) {
    x_aes_args <- list(x = factor(1))
    if (is.null(x_axis_title)) {
      x_axis_title <- ""
    }
    if (sort) {
      warning("Sort option set to TRUE but x_variable not provided so ignored")
      sort = F
    }
  } else{
    sort_params <- gg_sort(sort, x_variable, y_variable, x_axis_title, desc)
    x_aes_args <- sort_params$sort_params
    x_axis_title <- sort_params$x_axis_title
  }
  
  y_pos_args <-
    list(
      ymin = ymin,
      lower = lower,
      middle = middle,
      upper = upper,
      ymax = ymax
    )
  aes_params <- c(x_aes_args, list(y = y_variable), y_pos_args)
  
  # Set the stat
  if (!is.null(unlist(y_pos_args))) {
    stat <- "identity"
    if (is.null(y_axis_title)) {
      y_axis_title <- ""
    }
  } else{
    stat <- "boxplot"
  }
  
  # Component Params
  facet_params <- modifyList(facet_args,
                             list(facet_formula = facet_formula,
                                  labeller = get(paste0("label_", facet_labeller),
                                                 asNamespace("ggplot2"))))
  coord_params <- modifyList(coord_args, list(coord = coord))
  
  
  # Get Geom Params
  params_list <- get_geom_params(
    colnames(df),
    fill = fill,
    color = color,
    alpha = alpha,
    aes_params = aes_params,
    geom_params = list(stat = stat, ...)
  )
  
  # Define Theme
  theme_name <- paste("theme", theme, sep = "_")
  theme_fun <- ifelse(theme == "sncr", match.fun(theme_name), get(theme_name, asNamespace("ggplot2")))
  
  
  ggplot(df, do.call("aes_string", params_list$aes_params)) +
    do.call("geom_boxplot", params_list$geom_params) +
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
