

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
#'@param data Spark DataFrame
#'@param folder directory to the write files to (directory only).
#'@param file file name. file type inferred by extension provided. invokes
#'  specific writer function
#'@param mode Specifies the behavior when data or table already exists.
#'  Supported values include: 'error', 'append', 'overwrite' and 'ignore'. Notice
#'  that 'overwrite' will also change the column structure. default is NULL
#'@param partition_by Partitions the output by the given columns on the file
#'  system
#'@param temp_folder_name name of temp folder
#'@param ... optional arguments passed to write function
#'
#'@export
writer <- function(df, path, mode, partition_by, temp_folder_name) {
  UseMethod("writer")
}



#' @export
#' @rdname writer
writer.tbl_spark <- function(df,
                             path,
                             mode = NULL,
                             partition_by = NULL,
                             temp_folder_name = "tmp",
                             ...) {
  checkmate::assert_character(path)
  checkmate::assert_character(temp_folder_name)
  checkmate::assert_choice(mode, c('error', 'append', 'overwrite', 'ignore'), null.ok = TRUE)
  checkmate::assert_subset(partition_by, colnames(df), empty.ok = TRUE)

  if( grepl("hdfs://|s3a://|file://", path)) {
    protocol <- paste0(strsplit(path, "//")[[1]][1],  "//")
    path <- gsub(protocol, "", path)
  } else {
    protocol <- ""
  }
  type <- tools::file_ext(path)
  name <- gsub(paste0(".", type), "", basename(path))
  base_dir <- dirname(path)
  root_dir <- dirname(base_dir)
  file_tmp_folder <- paste(base_dir, temp_folder_name, sep = "/")
  overwrite <- ifelse(!is.null(mode), ifelse(mode == "overwrite", T, F), F)
  append <- ifelse(!is.null(mode), ifelse(mode == "append", T, F), F)

  checkmate::assert_directory(root_dir, access = "w")
  checkmate::assert_choice(type, c("csv", "parquet", "json", "jdbc", "source", "table", "text"))
  if(checkmate::check_directory_exists(file_tmp_folder) == TRUE) {
    stop("temp folder exists")
  }

  spark_write_fun <- match.fun(paste("spark_write", type, sep = "_"))
  spark_write_fun(x = df,
                  path = paste0(protocol, file_tmp_folder),
                  mode = mode,
                  partition_by = partition_by,
                  ...)

  if (sum(grepl(type, dir(file_tmp_folder))) == 0) {
    sub_folders <- dir(file_tmp_folder)
    for (sb in sub_folders) {
      files <- dir(paste(file_tmp_folder, sb, sep = "/"))
      n_files <- length(files)
      if (n_files > 0) {
        dir.create(paste(base_dir, sb, sep = "/"))
        for (i in seq_along(files)) {
          isep <- ifelse(n_files == 1, "", paste0("-", i))
          if (grepl(paste0("\\.", type), files[i])) {
            sub_folder <- paste0(base_dir, "/", sb, "/")
            new_file <- paste0(sub_folder,
                               paste0(name, "-", sb, isep),
                               paste(".", type, sep = ""))

            if(file.exists(new_file) & append){
              all_files <- dir(sub_folder)[grepl(name, dir(sub_folder))]
              all_imax <- suppressWarnings(as.numeric(
                gsub(paste(c(type, name, "\\-",  "\\_", "\\."), collapse="|"), "", all_files)))
              all_imax <- ifelse(is.na(all_imax), 0, all_imax)
              isep <- paste0("-", all_imax+1)
              new_file <- paste0(sub_folder,
                                 paste0(name, "-", sb, isep),
                                 paste(".", type, sep = ""))
            }

            file.copy(
              from = paste(file_tmp_folder, sb, files[i], sep = "/"),
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
        imax <- suppressWarnings(as.numeric(
          gsub(paste(c(type, name, "\\-",  "\\_", "\\."), collapse="|"), "", files)))
        imax <- ifelse(is.na(imax), 0, imax)
        isep <- ifelse(sum(grepl(type, files)) == 1, "", paste0("-", imax+1))
        if (grepl(paste0("\\.", type), files[i])) {
          new_file <- paste0(base_dir, "/", paste0(name, isep), paste(".", type, sep = ""))
          if(file.exists(new_file) & append){
            all_files <- dir(base_dir)[grepl(name, dir(base_dir))]
            all_imax <- suppressWarnings(as.numeric(
              gsub(paste(c(type, name, "\\-",  "\\_", "\\."), collapse="|"), "", all_files)))
            all_imax <- ifelse(is.na(all_imax), 0, all_imax)
            isep <- paste0("-", all_imax+1)
            new_file <- paste0(base_dir, "/", paste0(name, isep), paste(".", type, sep = ""))
          }

          file.copy(
            from = paste(file_tmp_folder, files[i], sep = "/"),
            to = new_file ,
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
                              mode = NULL,
                              ...) {
  checkmate::assert_character(path)
  checkmate::assert_choice(mode, c('error', 'append', 'overwrite', 'ignore'), null.ok = TRUE)

  type <- tools::file_ext(path)
  name <- gsub(paste0(".", type), "", basename(path))
  base_dir <- dirname(path)
  append <- ifelse(!is.null(mode), ifelse(mode == "append", T, F), F)
  col_names <- ifelse(append, FALSE, TRUE)

  checkmate::assert_directory(base_dir, access = "w")
  checkmate::assert_choice(type, c("csv", "txt"))

  suppressWarnings(
    write.table(df, file = path, append = append, col.names = col_names, ...)
  )
}
