

# ggplot components -------------------------------------------------------

# Description - Set of helper functions that be reused across sncr_* charts



# Wrapper function for ggplot2 facet_wrap and facet_grid functions. Used
# internally by sncr_* functions to handle facet arguements
gg_facet <- function(facet_formula = NULL, ...) {
  checkmate::assert_string(facet_formula, null.ok = TRUE)
  
  # Default is to return null facet
  if (is.null(facet_formula)) {
    facet <- ggplot2::facet_null()
  } else{
    # Check to see if facet_formula can be converted to formula
    if (!class(as.formula(facet_formula)) == "formula") {
      stop("facet_formula not a formula\nPlease try in this format: lhs~rhs or ~rhs")
    }
    
    # Check for left-hand side of formula to determin facet_grid or wrap
    lhs <- strsplit(facet_formula, "~")[[1]][1]
    
    if (lhs == "") {
      facet <- get("facet_wrap", asNamespace("ggplot2"))
    } else{
      facet <- get("facet_grid", asNamespace("ggplot2"))
    }
    
    #facet(eval(parse(text = facet_formula)), ...)
    facet(as.formula(facet_formula), ...)
  }
}



# Wrapper function for ggplot2 coord_* family of functions. Used internally by
# sncr_* functions to handle coordinate arguments
gg_coord <- function(coord = NULL, ...) {
  checkmate::assert_choice(
    coord,
    choices = c(
      "trans",
      "polar",
      "map",
      "quickmap",
      "flip",
      "fixed",
      "cartesian"
    ),
    null.ok = TRUE
  )
  if (is.null(coord)) {
    # Default is the identity transform
    ggplot2::geom_blank()
  } else{
    coord_name <- paste("coord", coord, sep = "_")
    coord <- get(coord_name, asNamespace("ggplot2"))
    coord(...)
  }
}


# Wrapper function to create a geom_label layer. The function takes two
# arguements to the aes() function within the geom and any number of additional
# arguments to the geom function
gg_label <- function(label = NULL, y = NULL, ...) {
  checkmate::assert_string(label, null.ok = TRUE)
  checkmate::assert_string(y, null.ok = TRUE)
  if (is.null(label)) {
    ggplot2::geom_blank()
  } else{
    ggplot2::geom_label(ggplot2::aes_string(label = label, y = y), ...)
  }
}


# Function evaluates, parses, and sets the arguments for the
# \code{\link{ggplot2::aes}} function and the geom function.
#
# Function required to handle fill and color arguments that can be both a
# variable name or a color value
#
# Internal function for the sncr_* family of charting functions
get_geom_params <- function(names,
                            fill = NULL,
                            color = NULL,
                            linetype = NULL,
                            size = NULL,
                            alpha = NULL,
                            group = NULL,
                            shape = NULL,
                            aes_params = list(),
                            geom_params = list()) {
  
  checkmate::assert_character(names, unique = TRUE, null.ok = TRUE)
  
  constraint_params <-
    list(
      fill = fill,
      color = color,
      linetype = linetype,
      size = size,
      alpha = alpha,
      group = group,
      shape = shape
    )
  constraint_params <-
    constraint_params[-which(sapply(constraint_params, is.null))]
  
  for (i in seq_along(constraint_params)) {
    if (!is.null(constraint_params[[i]]) &&
        constraint_params[[i]] %in% names) {
      aes_params <- modifyList(aes_params, constraint_params[i])
    } else{
      geom_params <- modifyList(geom_params, constraint_params[i])
    }
  }
  
  return(list("aes_params" = aes_params, "geom_params" = geom_params))
}



# Wrapper function to labs function in ggplot2. Needed to handle parameter
# inputs to the title values with default \code{link{waiver}}
gg_titles <- function(title,
                      subtitle ,
                      x_axis_title,
                      y_axis_title,
                      caption) {
  
  checkmate::assert_string(title, null.ok = TRUE)
  checkmate::assert_string(subtitle, null.ok = TRUE)
  checkmate::assert_string(x_axis_title, null.ok = TRUE)
  checkmate::assert_string(y_axis_title, null.ok = TRUE)
  checkmate::assert_string(caption, null.ok = TRUE)
  
  # Create list with waiver() default values
  title_list <- list(
    empty = "",
    title = title,
    subtitle = subtitle,
    x = x_axis_title,
    y = y_axis_title,
    caption = caption
  )
  
  do.call("labs", Filter(Negate(is.null), title_list))
}




# Wrapper function to sort x_variable by either count or median of y-variable
# if provided
gg_sort <- function(sort,
                    x_variable,
                    y_variable,
                    x_axis_title,
                    desc = TRUE) {
  checkmate::assert_flag(sort)
  
  if (sort) {
    if (is.null(y_variable)) {
      if(desc) {
        sort_params <- paste0("fct_infreq(", x_variable, ")")
      }else{
        sort_params <- paste0("fct_inorder(", x_variable, ")")
      }
    } else{
      sort_params <- paste0("fct_reorder(.f=",
                            x_variable,
                            ", .x=",
                            y_variable,
                            ", .desc=",
                            desc,
                            ")")
    }
    if (is.null(x_axis_title)) {
      x_axis_title <- x_variable
    }
  } else{
    sort_params <- x_variable
  }
  
  return(list(sort_params = sort_params, x_axis_title = x_axis_title))
}



# Wrapper function for addding geom_point layer to a ggplot
gg_point <- function(points = F, ...) {
  checkmate::assert_flag(points)
  if (points) {
    ggplot2::geom_point(...)
  } else{
    ggplot2::geom_blank()
  }
}



# Wrapper function for addding geom_smooth layer to a ggplot
gg_smooth <- function(smooth = F, ...) {
  checkmate::assert_flag(smooth)
  if (smooth) {
    ggplot2::geom_smooth(...)
  } else{
    ggplot2::geom_blank()
  }
}



# Wrapper function for adding geom_line layer by way of stat_summary
gg_line <- function(line = TRUE,
                    fun = "mean",
                    geom = "line",
                    ...) {
  checkmate::assert_flag(line)
  if (line) {
    ggplot2::stat_summary(fun.y = match.fun(fun), geom = geom, ...)
  } else{
    ggplot2::geom_blank()
  }
}
