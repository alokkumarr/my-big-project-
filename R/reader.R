

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
#'   protocol seperately with dfs_protocol input
#' @param dfs_protocol optional dfs protocol for accessing files on cluster.
#'   default is NULL for local file paths
#' @param type optional file type path. Default is NULL. If provided, only files
#'   of type provided will be read. Can be used to wildcard file types in
#'   combination to a directory only path input
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
                   dfs_protocol = NULL,
                   type = NULL,
                   repartition = 0,
                   memory = TRUE,
                   overwrite = TRUE,
                   ...) {
  checkmate::assert_class(sc, "spark_connection")
  checkmate::assert_character(name)
  checkmate::assert_choice(dfs_protocol, c("hdfs://", "s3a://", "file://"), null.ok = TRUE)
  checkmate::assert_false(grepl(paste(
    c("hdfs://", "s3a://", "file://"), collapse = "|"
  ), path))
  checkmate::assert_number(repartition, lower = 0)
  checkmate::assert_flag(memory)
  checkmate::assert_flag(overwrite)

  # Type Detection
  if (checkmate::test_directory(path)) {
    if (is.null(type)) {
      type <- dir(path) %>%
        tools::file_ext(.) %>%
        table() %>%
        sort(., decreasing = TRUE) %>%
        .[1] %>%
        names()
    } else {
      path <- paste(path, paste0("*.", type), sep = "/")
    }
  } else{
    checkmate::assert_file_exists(path)
    type <- tools::file_ext(path)
  }
  checkmate::assert_choice(type,
                           c("csv", "parquet", "json", "jdbc", "source", "table", "text"))

  if (!is.null(dfs_protocol)) {
    path <- paste0(dfs_protocol, path)
  }

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


