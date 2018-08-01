

#' Spark Reader Function
#'
#' Function to read files into Spark. Supports several file types, including
#' text, csv, parquet, and json
#'
#' If directory path provided, reader will scan the directory and calculate the
#' most frequency file type and apply the appropriate read function
#'
#' @param sc A spark_connection.
#' @param name The name to assign to the newly generated table
#' @param path The path to the file. If data is distributed, supply the dfs
#'   protocol
#' @param type required file type. Specifiecs the sub reader function. Accepts
#'   csv, parquet, json, jdbc, source, table and text
#' @param repartition The number of partitions used to distribute the generated
#'   table. Use 0 (the default) to avoid partitioning.
#' @param memory Logical; should the data be loaded eagerly into memory? (That
#'   is, should the table be cached?)
#' @param overwrite Logical; overwrite the table with the given name if it
#'   already exists?
#' @param ... additional arguments to read function
#'
#' @return Spark Dataframe
#' @export
reader <- function(sc,
                   name,
                   path,
                   type,
                   repartition = 0,
                   memory = TRUE,
                   overwrite = TRUE,
                   ...) {
  checkmate::assert_class(sc, "spark_connection")
  checkmate::assert_character(name)
  checkmate::assert_character(path)
  checkmate::assert_choice(type,
                           c("csv", "parquet", "json", "jdbc", "source", "table", "text"))
  checkmate::assert_number(repartition, lower = 0)
  checkmate::assert_flag(memory)
  checkmate::assert_flag(overwrite)

  spark_read_fun <- match.fun(paste("spark_read", type, sep = "_"))
  spark_read_fun(
    sc,
    name,
    path,
    repartition = repartition,
    memory = memory,
    overwrite = overwrite,
    ...
  )
}


