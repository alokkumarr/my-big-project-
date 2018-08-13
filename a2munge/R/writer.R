

#'DataFrame Writer Function
#'
#'This is a function to write dataframes to files. Supports writing csv, text,
#'json and parquet file types.
#'
#'The default values for Spark method with delimited type are the consistent
#'with spark_write_csv function, comma. For files with a different delimiter,
#'include the dilimiter argument, i.e. dilimeter="|" for pipe. Parquet files use
#'default spark_read_parquet funtionality. Supports overwrite, append, and error
#'modes.
#'
#'Function writes files to temporary folder and then copies a renamed version to
#'destination directory
#'
#'
#'@param df DataFrame
#'@param path file path to write file to. Should include protocol if distributed
#'  path
#'@param dfs_mount dfs mount file path. applicable for distributed files only
#'@param type file type. type specifies the spark writer function. Only
#'  applicable for spark writer method
#'@param mode Specifies the behavior when data or table already exists.
#'  Supported values include: 'error', 'append', 'replace' and 'ignore'.
#'  Notice that 'replace' will also change the column structure. default is
#'  NULL
#'@param partition_by Partitions the output by the given columns on the file
#'  system
#'@param temp_folder_name name of temp folder
#'@param ... optional arguments passed to write function
#'
#'@export
writer <- function(df, path,  dfs_mount, type, mode, partitions, partition_by, temp_folder_name) {
  UseMethod("writer")
}



#' @export
#' @rdname writer
writer.tbl_spark <- function(df,
                             path,
                             dfs_mount = "/dfs",
                             type,
                             mode = NULL,
                             partitions = NULL,
                             partition_by = NULL,
                             temp_folder_name = "tmp",
                             ...) {
  checkmate::assert_character(path)
  checkmate::assert_character(dfs_mount)
  checkmate::assert_choice(type, c("csv", "parquet", "json", "jdbc", "source", "table", "text"))
  checkmate::assert_choice(mode, c('error', 'append', 'replace', 'ignore'), null.ok = TRUE)
  checkmate::assert_number(partitions, lower = 0, null.ok = TRUE)
  checkmate::assert_subset(partition_by, colnames(df), empty.ok = TRUE)
  checkmate::assert_character(temp_folder_name)

  if(! is.null(mode)) {
    if (mode == "replace") {
      overwrite <- T
      append <- F
      mode <- "overwrite"
    } else if (mode == "append") {
      append <- T
      overwrite <- F
    } else {
      overwrite <- F
      append <- F
    }
  } else {
    overwrite <- F
    append <- F
  }


  if(!is.null(partitions)) {
    df <- sparklyr::sdf_repartition(df, partitions = partitions, partition_by = NULL)
  }

  if( grepl("hdfs://|s3a://|file://", path)) {
    protocol <- paste0(strsplit(path, "//")[[1]][1],  "//")
    file_path <- paste0(dfs_mount, gsub(protocol, "", path))
  } else {
    protocol <- ""
    file_path <- path
    dfs_mount <- ""
  }

  name <- gsub(tools::file_ext(path), "", basename(path))
  name <- gsub("\\.", "", name)
  base_dir <- dirname(file_path)
  root_dir <- dirname(base_dir)
  #checkmate::assert_directory(root_dir, access = "w")
  file_tmp_folder <- paste(base_dir, temp_folder_name, sep = "/")
  file_tmp_path <- gsub(dfs_mount, "", file_tmp_folder)

  if(checkmate::check_directory_exists(file_tmp_folder) == TRUE) {
    stop("temp folder exists")
  }

  spark_write_fun <- match.fun(paste("spark_write", type, sep = "_"))
  spark_write_fun(x = df,
                  path = file_tmp_path,
                  mode = mode,
                  partition_by = partition_by,
                  ...)

  # Check to see if directory has sub-directory from partition_by
  if (sum(grepl(type, dir(file_tmp_folder))) == 0) {
    sub_folders <- dir(file_tmp_folder)
    for (sb in sub_folders) {
      files <- dir(paste(file_tmp_folder, sb, sep = "/"))
      n_files <- length(files)
      if (n_files > 0) {

        # Create sub-directory in base
        dir.create(paste(base_dir, sb, sep = "/"))
        for (i in seq_along(files)) {
          .f <- files[i]
          # Check if file is type file (others are just meta files)
          if (grepl(paste0("\\.", type), .f)) {

            sub_folder <- paste0(base_dir, "/", sb, "/")
            part <- regmatches(.f, regexpr("^[a-z]{4}([\\w-])([0-9]{5})", .f))
            new_file <- paste0(sub_folder,
                               paste(name, sb, part, sep="-"),
                               paste(".", type, sep = ""))

            # Check to see if file existings and in append mode. If so, update
            # part name to be 1+ the existing max
            if(file.exists(new_file) & append){
              all_files <- dir(sub_folder)[grepl(name, dir(sub_folder))]
              all_parts <- regmatches(all_files, regexpr("([0-9]{5})", all_files))
              max_part <- max(as.numeric(all_parts)) + 1
              new_part <- paste0("part-",
                                 paste(rep(0, 5-nchar(as.character(max_part))), collapse = ""), max_part)
              new_file <- paste0(sub_folder,
                                 paste(name, sb, new_part, sep="-"),
                                 paste(".", type, sep = ""))
            }

            file.copy(
              from = paste(file_tmp_folder, sb, .f, sep = "/"),
              to = new_file,
              overwrite = overwrite
            )
          }
        }
      }
    }
  } else {
    files <- dir(file_tmp_folder)
    n_files <- length(files)
    if (n_files > 0) {
      for (i in seq_along(files)) {
        .f <- files[i]
        # Check if file is type file (others are just meta files)
        if (grepl(paste0("\\.", type), .f)) {

          # Get part name & create new file path
          part <- regmatches(.f, regexpr("^[a-z]{4}([\\w-])([0-9]{5})", .f))
          new_file <- paste0(base_dir, "/", paste(name, part, sep="-"), paste(".", type, sep = ""))

          # Check to see if file existings and in append mode. If so, update
          # part name to be 1+ the existing max
          if(file.exists(new_file) & append){
            all_files <- dir(base_dir)[grepl(name, dir(base_dir))]
            all_parts <- regmatches(all_files, regexpr("([0-9]{5})", all_files))
            max_part <- max(as.numeric(all_parts)) + 1
            new_part <- paste0("part-",
                               paste(rep(0, 5-nchar(as.character(max_part))), collapse = ""), max_part)
            new_file <- paste0(base_dir, "/", paste(name, new_part, sep="-"), paste(".", type, sep = ""))
          }

          file.copy(
            from = paste(file_tmp_folder, files[i], sep = "/"),
            to = new_file,
            overwrite = overwrite
          )
        }
      }
    }
  }
  unlink(file_tmp_folder, recursive = T)
}



#' @export
#' @rdname writer
writer.data.frame <- function(df,
                              path,
                              type,
                              mode = NULL,
                              ...) {
  checkmate::assert_character(path)
  checkmate::assert_choice(type, c("csv", "txt"))
  checkmate::assert_choice(mode, c('error', 'append', 'replace', 'ignore'), null.ok = TRUE)

  name <- gsub(tools::file_ext(path), "", basename(path))
  name <- gsub("\\.", "", name)
  base_dir <- dirname(path)
  append <- ifelse(!is.null(mode), ifelse(mode == "append", T, F), F)
  col_names <- ifelse(append, FALSE, TRUE)
  checkmate::assert_directory(base_dir, access = "w")

  suppressWarnings(
    write.table(df, file = path, append = append, col.names = col_names, ...)
  )
}
