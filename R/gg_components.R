
# ggplot components -------------------------------------------------------

# Description - Set of helper functions that be reused across sncr_* charts


#' GGPlot2 Facet Component
#'
#' Wrapper function for ggplot2 facet_wrap and facet_grid functions. Used
#' internally by sncr_* functions to handle facet arguements
#'
#' @param facet_formula a valid r formula comprised of column names to create
#'   facets. expects a string. formula can either be a '~rhs' formula for a
#'   facet_wrap or 'lhs~rhs' to create a facet_grid. Default is NULL which
#'   creates no facets
#' @param ...
#'
#' @seealso ggplot2::facet_grid \url{http://ggplot2.tidyverse.org/reference/facet_grid.html}
#' @seealso ggplot2::facet_wrap \url{http://ggplot2.tidyverse.org/reference/facet_wrap.html}
#'
#' @export
#'
#' @examples
#' ggplot(mtcars, aes(x=mpg)) +
#' geom_histogram() +
#' gg_facet("~am")
gg_facet <- function(facet_formula = NULL, ...){
  
  # Default is to return null facet
  if(is.null(facet_formula)){
    
    facet <- facet_null()
  }else{
    
    # Check to see if facet_formula can be converted to formula
    if(! class(as.formula(facet_formula)) =="formula"){
      stop("facet_formula not a formula\nPlease try in this format: lhs~rhs or ~rhs")
    }
    
    # Check for left-hand side of formula to determin facet_grid or wrap
    lhs <- strsplit(facet_formula, "~")[[1]][1]
    
    if(lhs == ""){
      facet <- match.fun('facet_wrap')
    }else{
      facet <- match.fun('facet_grid')
    }
    
    #facet(eval(parse(text = facet_formula)), ...)
    facet(as.formula(facet_formula), ...)
  }
}


#' GGPlot2 Coordinate Component
#'
#' Wrapper function for ggplot2 coord_* family of functions. Used internally by
#' sncr_* functions to handle coordinate arguments
#'
#' @param coord name of coordinate transform. ex - 'flip' creates a coord_flip
#'   and a 'trans' creates a coord_trans. expects a string. default is NULL with
#'   creates no coordinate changes
#' @param ... takes additional arguments to coord function specified
#' @seealso ggplot2 coordinate fucntions
#'   \url{http://ggplot2.tidyverse.org/reference/#section-coordinate-systems}
#'
#' @export
#'
#' @examples
#' ggplot(mtcars, aes(x=mpg)) +
#' geom_histogram() +
#' gg_coord("flip")
gg_coord <- function(coord = NULL, ...){
  
  # Default is the identity transform
  if(is.null(coord)){
    
    geom_blank()
  }else{
    
    coord <- try(match.fun(paste("coord", coord, sep="_")), silent = T)
    if(class(coord) == "try-error"){
      stop("coord not valid ggplot2 coord_* function\nplease see: http://ggplot2.tidyverse.org/reference/#section-coordinate-systems")
    }else{
      coord(...)
    }
  }
}



#' GGPlot Label Component
#'
#' Wrapper function to create a geom_label layer. The function takes two
#' arguements to the aes() function within the geom and any number of additional
#' arguments to the geom function
#'
#' @param label variable name or function such as ..count.. expects a string.
#'   default is NULL. NULL value creates a blank geom
#' @param y variable name or function such as ..count.. xpects a string.
#'   default is NULL
#' @param ... additional arguments to geom_label
#' @seealso geom_label \url{http://ggplot2.tidyverse.org/reference/geom_text.html}
#'
#' @return returns either a geom_label or geom_blank object
#' @export
#'
gg_label <- function(label = NULL, y = NULL, ...){
  
  if(is.null(label)){
    geom_blank()
  }else{
    
    geom_label(aes_string(label=label, y=y), ...)
  }
}


#' Get GGPlot2 geom Parameters
#'
#' Function evaluates, parses, and sets the arguments for the
#' \code{\link{ggplot2::aes}} function and the geom function.
#'
#' Function required to handle fill and color arguments that can be both a
#' variable name or a color value
#'
#' Internal function for the sncr_* family of charting functions
#'
#' @param names colnames of the data set being used for charting. expects a
#'   vector of string values
#' @param fill either a variable name to apply a group by operation and create
#'   subgroups of histograms or color name or hex value to fill the histogram
#'   with color. expects a string.
#' @param color either a variable name to apply a group by operation and create
#'   subgroups of histograms or color name or hex value to color the outlines of
#'   the histogram. expects a string.
#' @param linetype Line types should be specified with either an integer, a name, or
#' with a string of an even number (up to eight) of hexadecimal digits which give
#' the lengths in consecutive positions in the string.
#' 0 = blank, 1 = solid, 2 = dashed, 3 = dotted, 4 = dotdash, 5 = longdash, 6 = twodash
#' @param size Should be specified with a numerical value (in millimetres),
#' or from a variable source
#' @param alpha sets the opacity of the points
#' @param group a variable name to apply a group by operation and create
#'  subgroups of line charts
#' @param shape Shape takes four types of values: an integer in [0, 25],
#' a single character-- which uses that character as the plotting symbol,
#' a . to draw the smallest rectangle that is visible (i.e., about one pixel)
#' an NA to draw nothing
#' @param aes_params list of parameters to pass to the aes function.
#'   default is empty list
#' @param geom_params list of parameters to pass to the geom function. default
#'   is empty list
#'
#' @return returns a list of 2 lists. First returned list is the updated
#'   arguments to pass to the aes function. the second list is the updated list
#'   of arguments to pass to the geom function
#' @export
#'
get_geom_params <- function(names,
                            #constraint_params = list(),
                            fill = NULL,
                            color = NULL,
                            linetype = NULL,
                            size = NULL,
                            alpha = NULL,
                            group = NULL,
                            shape = NULL,
                            aes_params = list(),
                            geom_params = list()){
  
  
  
  constraint_params <- list(fill = fill, color = color, linetype = linetype,
                            size = size, alpha = alpha, group = group,
                            shape = shape)
  constraint_params <- constraint_params[-which(sapply(constraint_params, is.null))]
  
  for(i in seq_along(constraint_params)){
    
    if(!is.null(constraint_params[[i]]) && constraint_params[[i]] %in% names){
      aes_params <- modifyList(aes_params, constraint_params[i])
    } else{
      geom_params <- modifyList(geom_params, constraint_params[i])
    }
  }
  
  return(list("aes_params" = aes_params, "geom_params" = geom_params))
}


#' GG Titles Component
#'
#' Wrapper function to labs function in ggplot2. Needed to handle parameter
#' inputs to the title values with default \code{link{waiver}}
#'
#' @param title chart title. expects a string. default is empty string.
#' @param subtitle chart subtitle. expects a string. default is empty string.
#' @param x_axis_title x-axis title. expects a string. default is empty string.
#' @param y_axis_title y-axis title. expects a string. default is empty string.
#' @param caption chart caption. expects a string. default is empty string.
#' 
#' @return ggplot2 label object
#' @export
gg_titles <- function(title, subtitle, x_axis_title, y_axis_title, caption){
  
  # Create list with waiver() default values
  title_list <- list(empty = "", title = title, subtitle = subtitle,
                     x = x_axis_title, y = y_axis_title, caption = caption)
  
  do.call("labs", Filter(Negate(is.null), title_list))
}



#' GGPlot Sort Component
#'
#' Wrapper function to sort x_variable by either count or median of y-variable
#' if provided
#'
#' @inheritParams sncr_bar
#' @seealso \code{\link[forcats]{fct_infreq}}
#' @seealso \code{\link[forcats]{fct_reorder}}
#'
#' @return list with sort params arguments and updated x axis title
#' @export
#'
gg_sort <- function(sort, x_variable, y_variable, x_axis_title){
  
  if(sort){
    if(is.null(y_variable)){
      sort_params <- paste0("forcats::fct_infreq(", x_variable, ")")
    }else{
      sort_params <- paste0("forcats::fct_reorder(f=", x_variable, ", x=", y_variable, ")")
    }
    if(is.null(x_axis_title)){
      x_axis_title <- x_variable
    }
  }else{
    sort_params <- x_variable
  }
  
  return(list(sort_params = sort_params, x_axis_title = x_axis_title))
}


#' GGPlot Points component
#'
#' Wrapper function for addding geom_point layer to a ggplot
#'
#' @param points TRUE/FALSE. if TRUE then creates geom_point layer with
#'   arguments
#' @inheritDotParams ggplot2::geom_point
#' @seealso \url{http://ggplot2.tidyverse.org/reference/geom_point.html}
#'
#' @return ggplot geom_point layer object
#' @export
#'
gg_point <- function(points=F, ...){
  
  if( points){
    geom_point(...)
  }else{
    geom_blank()
  }
}


#' GGPlot Line Component
#'
#' Wrapper function for adding geom_line layer by way of stat_summary
#'
#' @param line TRUE/FALSE. Default is TRUE. Option to add geom_line layer
#' @inheritDotParams ggplot2::stat_summary
#' @seealso \url{http://ggplot2.tidyverse.org/reference/geom_point.html}
#'
#' @return either a
#' @export
#'
gg_line <- function(line = TRUE, fun = "mean", geom = "line", ...){
  
  if(line){
    stat_summary(fun.y=match.fun(fun), geom=geom, ...)
  }else{
    geom_blank()
  }
}


