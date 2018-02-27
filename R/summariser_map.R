



#'DataFrame Summariser Map function
#'
#'Function that allows for 1 or more aggregation combinations for a single
#'dataset. Applies aggregation logic, pivots and joins to create a single
#'dataframe. A wrapper function for multiple combinations of summariser function
#'and pivoter. See summariser documentation for more functionality details
#'
#'@param df DataFrame
#'@param id_vars vector of column names. used in the group by aggregration. used
#'  as the left hand side of pivot. used as the by in the join step. can be zero
#'  or more columns.
#'@param map nested list of summariser function arguments. use summariser_args
#'  helper function to create arguments to summariser function. Can handle more
#'  than one set of summariser_args. Can also process additional parameters to
#'  the aggregation function - similiar to the ... argument in summariser
#'@param sep argument passed to pivoter function. see pivoter function for
#'  details
#'@param fill optional argument passed to pivoter function. see pivoter function
#'  for details
#'
#'@return returns DataFrame
#'@export
#'
#' @examples
#'library(dplyr)
#'
#'
#'# Create toy dataset
#'set.seed(319)
#'id_vars <- seq(101, 200, by=1)
#'dates <- seq(from=Sys.Date()-365, to=Sys.Date(), by="day")
#'cat1 <- c("A", "B")
#'cat2 <- c("X", "Y", "Z")
#'
#'dat <- data.frame()
#'for(id in id_vars){
#'   n <- floor(runif(1)*100)
#'   d <- data.frame(id = id,
#'                  date = sample(dates, n, replace = TRUE),
#'                  cat1 = sample(cat1, n, replace = TRUE),
#'                  cat2 = sample(cat2, n, replace = TRUE),
#'                  metric1 = sample(1:5, n, replace = TRUE),
#'                  metric2 = rnorm(n, mean=50, sd = 5))
#'  dat <- rbind(dat, d)
#'}
#'
#' summariser_map(dat,
#'                id_vars = "id",
#'                map = list(
#'                  summariser_args(
#'                       group_vars = c("cat1"),
#'                       measure_vars = c("metric1", "metric2"),
#'                       fun = c("sum")),
#'                      summariser_args(group_vars = c("cat2"),
#'                           measure_vars = c("metric2"),
#'                           fun = c("mean"))
#'                    ))
#'@export
summariser_map <- function(df, ...) {
  UseMethod("summariser_map", df)
}


#' @importFrom utils modifyList
#' @export
#' @rdname summariser_map
summariser_map.data.frame <- function(df,
                                      id_vars,
                                      map = list(),
                                      sep = "_",
                                      fill = NULL) {
  for (i in seq_along(map)) {
    agg <- do.call("summariser",
                   modifyList(map[[i]],
                              list(
                                df = df,
                                group_vars = c(id_vars, map[[i]]$group_vars)
                              )))

    if (is.null(map[[i]]$group_vars)) {
      message("no grouping variables provided. No pivot required.")
      agg_pvt <- agg
    } else{
      agg_pvt <- pivoter(
        agg,
        id_vars = id_vars,
        group_vars =  map[[i]]$group_vars,
        measure_vars = setdiff(colnames(agg), c(id_vars, map[[i]]$group_vars)),
        sep = sep,
        fill = fill
      )
    }

    if (i == 1) {
      result <- agg_pvt

    } else{
      result <- dplyr::full_join(result,
                                 agg_pvt,
                                 by = id_vars)
    }
  }

  result
}


#' @importFrom utils modifyList
#' @export
#' @rdname summariser_map
summariser_map.tbl_spark <- function(df,
                                     id_vars,
                                     map = list(),
                                     sep = "_",
                                     fill = NULL) {
  for (i in seq_along(map)) {
    agg <- do.call("summariser",
                   modifyList(
                     map[[i]],
                     list(
                       df = df,
                       group_vars = c(id_vars, map[[i]]$group_vars),
                       measure_vars = map[[i]]$measure_vars
                     )
                   ))

    if (is.null(map[[i]]$group_vars)) {
      message("no grouping variables provided. No pivot required.")
      agg_pvt <- agg
    } else{
      agg_pvt <- pivoter(
        agg,
        id_vars = id_vars,
        group_vars =  map[[i]]$group_vars,
        measure_vars = setdiff(colnames(agg), c(id_vars, map[[i]]$group_vars)),
        sep = sep,
        fill = fill
      )
    }

    if (i == 1) {
      result <- agg_pvt

    } else{
      result <- dplyr::full_join(result,
                                 agg_pvt,
                                 by = id_vars)
    }
  }

  result
}
