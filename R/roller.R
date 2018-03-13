






#'@export
roller <- function(df, ...) {
  UseMethod("roller", df)
}



roller.data.frame <- function(df,
                              order_vars,
                              group_vars,
                              measure_vars,
                              fun,
                              width,
                              by,
                              partial,
                              mode,
                              ...) {
  args <- roller_args(order_vars,
                      group_vars,
                      measure_vars,
                      fun,
                      width,
                      by,
                      mode,
                      ...)
}


roller.tbl_spark <- function(df,
                             order_vars,
                             group_vars,
                             measure_vars,
                             fun,
                             width,
                             by,
                             partial = FALSE,
                             mode = "summarise",
                             ...) {
  args <- roller_args(
    order_vars = order_vars,
    group_vars = group_vars,
    measure_vars = measure_vars,
    fun = fun,
    width = width,
    by = by,
    partial = partial,
    mode = mode,
    ...
  )

  # Define index variables
  indx_var <- paste(order_vars[1], "rn", sep = "_")
  indx_grp_var <- paste(indx_var, "grp", sep = "_")

  # Add row numbers
  df2 <- df %>%
    mutater(
      .,
      order_vars = args$order_vars,
      group_vars = args$group_vars,
      measure_vars = args$order_vars[1],
      funs(rn = row_number())
    )

  # Define groupings based on by parameter
  df3 <- df2 %>%
    mutater(.,
            measure_vars = indx_var,
            fun = funs(grp = args$by * ceil(. / args$by))) %>%
    # create lags for the width
    lagger(
      .,
      order_vars = args$order_vars[1],
      group_vars = args$group_vars,
      measure_vars = indx_var,
      lags = 1:(args$width - 1)
    ) %>%
    # filter to max order var for each group
    dplyr::group_by_at(indx_grp_var) %>%
    dplyr::filter_at(indx_var, any_vars(. == max(.))) %>%
    dplyr::filter_at(indx_var, any_vars(. >= ifelse(partial, 1, args$width))) %>%
    # convert lag values to long format
    melter(
      .,
      id_vars = c(args$group_vars, indx_grp_var),
      measure_vars = c(indx_var, paste(indx_var, paste0(
        "lag", 1:(args$width - 1)
      ), sep =
        "_")),
      value_name = indx_var
    ) %>%
    # join back to df and summarise
    dplyr::inner_join(.,
                      df2,
                      by = c(args$group_vars, indx_var)) %>%
    summariser(
      .,
      group_vars = c(args$group_vars, indx_grp_var),
      measure_vars = args$measure_vars,
      fun = args$fun
    )

  # Rename index group varible
  df3 <- df3 %>%
    dplyr::select_(.dots = stats::setNames(colnames(df3),
                                           c(
                                             args$group_vars,
                                             indx_var,
                                             paste(args$measure_vars,
                                                   args$fun,
                                                   sep = "_")
                                           )))

  # Format output
  if (mode == "mutate") {
    df4 <- df3 %>%
      dplyr::right_join(df2,
                        by = c(args$group_vars, indx_var)) %>%
      dplyr::select_at(c(
        args$order_vars,
        args$group_vars,
        args$measure_vars,
        paste(args$measure_vars, args$fun, sep = "_")
      ))
  }
  if (mode == "summarise") {
    df4 <- df3 %>%
      dplyr::inner_join(df2,
                        by = c(args$group_vars, indx_var)) %>%
      dplyr::select_at(c(
        args$order_vars,
        args$group_vars,
        paste(args$measure_vars, args$fun, sep = "_")
      ))
  }

  df4
}



#' Roller Arguments Constructor function
#'
#' Creates new object of class roller_args
#'
#' @inheritParams roller
#'
#' @return roller_args object
#' @export
new_roller_args <- function(order_vars,
                            group_vars,
                            measure_vars,
                            fun,
                            width,
                            by,
                            partial,
                            mode,
                            ...) {
  stopifnot(is.character(order_vars) | is.null(order_vars))
  stopifnot(is.character(group_vars) | is.null(group_vars))
  stopifnot(is.character(measure_vars) | is.null(measure_vars))
  stopifnot(is.character(fun))
  stopifnot(is.numeric(by))
  stopifnot(is.numeric(width))
  stopifnot(is.logical(partial))
  stopifnot(is.character(mode))

  structure(
    list(
      order_vars = order_vars,
      group_vars = group_vars,
      measure_vars = measure_vars,
      fun = fun,
      width = width,
      by = by,
      partial = partial,
      mode = mode,
      ...
    ),
    class = "roller_args"
  )
}



#' Roller Arguments Validation Function
#'
#' Checks for valid inputs to roller_args class
#'
#' @param x obj of class roller_args
validate_roller_args <- function(x) {
  funs <- c(
    "n_distinct",
    "min",
    "max",
    "sum",
    "mean",
    "variance",
    "sd",
    "kurtosis",
    "skewness",
    "percentile"
  )
  if (!all(x$fun %in% funs)) {
    stop(
      "Supplied function not supported.\nPlease use one of following: ",
      paste(funs, collapse = ", "),
      .call = FALSE
    )
  }
  if (is.null(x$measure_vars)) {
    stop(
      "Measure_vars not specified.\nNeed to supply one valid column name to apply function to\n",
      .call = FALSE
    )
  }
  if (is.null(x$order_vars)) {
    message(
      "Order var not specified. Rolling function applied on the dataframe in its current order"
    )
  }
  if (x$by < 1) {
    stop("by input should be >= 1", .call = FALSE)
  }
  if (x$width < 1) {
    stop("width should be >= 1", .call = FALSE)
  }
  if (!x$mode %in% c("mutate", "summarise")) {
    stop("mode should be either 'mutate', or 'summarise'")
  }

  x
}



#' Roller Argument Helper Function
#'
#' Creates a valid object of roller_args class
#'
#' Function should be used in roller internals
#' @inheritParams roller
#'
#' @export
#' @importFrom magrittr %>%
roller_args <- function(order_vars,
                        group_vars,
                        measure_vars,
                        fun,
                        width,
                        by,
                        partial,
                        mode,
                        ...) {
  new_roller_args(order_vars,
                  group_vars,
                  measure_vars,
                  fun,
                  width,
                  by,
                  partial,
                  mode,
                  ...) %>%
    validate_roller_args()
}
