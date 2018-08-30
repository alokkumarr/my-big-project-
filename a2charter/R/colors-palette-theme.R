
# Synchronoss Colors
sncr_colors <- c(
  "PersianBlue"   = "#1D3AB2",
  "Denim"         = "#206BCE",
  "PacificBlue"   = "#0096D5",
  "DarkTurquoise" = "#00C9E8",
  "Gainsboro"     = "#DADADA",
  "Charcoal"      = "#464646",
  "VividViolet"   = "#914191",
  "Cardinal"      = "#B71234",
  "HDOrange"      = "#D93E00",
  "DarkOrange"    = "#FF9000",
  "Amber"         = "#FFBE00",
  "Christi"       = "#6FB320"
)


#' Function to extract Synchronoss colors as hex codes
#'
#' @param ... Character names of sncr_colors 
#' @export
sncr_cols <- function(...) {
  cols <- c(...)
  
  if (is.null(cols))
    return (sncr_colors)
  
  sncr_colors[cols]
}


# Synchronoss Palettes
sncr_palettes <- list(
  `primary`  = sncr_cols("PersianBlue", "Denim", "PacificBlue", "DarkTurquoise", "Gainsboro", "Charcoal"),
  
  `secondary`  = sncr_cols("VividViolet", "Cardinal", "HDOrange", "DarkOrange", "Amber", "Christi"),
  
  `a2`   = sncr_cols("PersianBlue", "Cardinal", "DarkOrange", "Gainsboro" , "Charcoal"),
  
  `set1` = pals::brewer.set1(9),
  
  `blues` = sncr_cols("PersianBlue", "Denim", "PacificBlue", "DarkTurquoise"),
  
  `warmcool` = pals::warmcool(5),
  
  `coolwarm` = pals::coolwarm(5),
  
  `viridis` = pals::viridis(3)
) 


#' Return function to interpolate a synchronoss color palette
#'
#' @param palette Character name of palette in sncr_palettes
#' @param reverse Boolean indicating whether the palette should be reversed
#' @param ... Additional arguments to pass to colorRampPalette()
#'
#' @export
sncr_pal <- function(palette = "a2", reverse = FALSE, ...) {
  checkmate::assert_choice(palette, names(sncr_palettes))
  checkmate::assert_flag(reverse)
  pal <- sncr_palettes[[palette]]
  
  if (reverse) pal <- rev(pal)
  
  grDevices::colorRampPalette(pal, ...)
}



#' Color scale constructor for Synchronoss colors
#'
#' @param palette Character name of palette in sncr_palettes
#' @param discrete Boolean indicating whether color aesthetic is discrete or not
#' @param reverse Boolean indicating whether the palette should be reversed
#' @param ... Additional arguments passed to discrete_scale() or
#'            scale_color_gradientn(), used respectively when discrete is TRUE or FALSE
#'
#' @export
scale_color_sncr <- function(palette = "a2", discrete = TRUE, reverse = FALSE, ...) {
  checkmate::assert_choice(palette, names(sncr_palettes))
  checkmate::assert_flag(discrete)
  pal <- sncr_pal(palette = palette, reverse = reverse)
  
  if (discrete) {
    ggplot2::discrete_scale("colour", paste0("sncr_", palette), palette = pal, ...)
  } else {
    ggplot2::scale_color_gradientn(colours = pal(256), ...)
  }
}

#' Fill scale constructor for Synchronoss colors
#'
#' @param palette Character name of palette in sncr_palettes
#' @param discrete Boolean indicating whether color aesthetic is discrete or not
#' @param reverse Boolean indicating whether the palette should be reversed
#' @param ... Additional arguments passed to discrete_scale() or
#'            scale_fill_gradientn(), used respectively when discrete is TRUE or FALSE
#'
#' @export
scale_fill_sncr <- function(palette = "a2", discrete = TRUE, reverse = FALSE, ...) {
  checkmate::assert_choice(palette, names(sncr_palettes))
  checkmate::assert_flag(discrete)
  pal <- sncr_pal(palette = palette, reverse = reverse)
  
  if (discrete) {
    ggplot2::discrete_scale("fill", paste0("sncr_", palette), palette = pal, ...)
  } else {
    ggplot2::scale_fill_gradientn(colours = pal(256), ...)
  }
}


#' Synchronoss GGplot2 Theme
#'
#'
#' Uses theme_minimal as base and replaces some panel, plot, axis, legend, and
#' panel.grib properties
#'
#' @param base_size Base font size.
#' @param base_family Base font family.
#'
#' @return A ggtheme
#' @export
#' @import ggplot2
theme_sncr <- function (base_size = 12,
                        base_family = "serif") {
  checkmate::assert_number(base_size)
  checkmate::assert_choice(base_family, c("serif", "sans", "mono"))
  
  theme_minimal(base_size = base_size, base_family = base_family) %+replace%
    theme(
      panel.background = element_rect(fill = "white", colour = NA),
      panel.border = element_rect(fill = NA, colour = "grey50"),
      plot.background = element_rect(
        fill = "grey95",
        colour = "black",
        size = 0.5
      ),
      plot.caption = element_text(
        size = base_size - 2,
        colour = "black",
        face = "italic",
        family = base_family,
        hjust = 1,
        vjust = 1,
        margin = margin(t = base_size / 2 * 0.9)
      ),
      axis.ticks =  element_line(colour = "grey35", size = 0.1),
      legend.position = "bottom",
      legend.title = element_text(
        size = base_size - 2,
        colour = "black",
        face = "plain",
        family = base_family,
        hjust = 0
      ),
      legend.direction = "horizontal",
      legend.text       =  element_text(size = base_size - 2),
      legend.key        =  element_rect(fill = NA, colour = NA),
      legend.background =  element_rect(fill = "transparent", colour = NA),
      panel.grid = element_blank(),
      complete = TRUE
    )
}



#' Is Color
#'
#' Helper function to validate color inputs
is_color <- function(x) {
  sapply(x, function(X) {
    tryCatch(
      is.matrix(grDevices::col2rgb(X)),
      error = function(e)
        FALSE
    )
  })
}

