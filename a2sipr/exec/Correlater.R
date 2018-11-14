#!/usr/bin/env Rscript

# Correlater Component ------------------------------------------------------
# The purpose of the Correlater component to is to allow A2 App Developers to
# run metric correlation process without having to code the R logic for it.
# The below files are required in conjunction with the Correlater script
# 1. Configuration file template (or config file) which will hold Spark
#      parameter values, input/output dataset locations & Parameter file
#      location. Will be a JSON file.
# 2. Shell script template which will include a trigger to Rscript with
#      the parameters - a. Correlater Script path
#                       b. Batch ID
#                       c. Project Name
#                       d. Config File name with all required info filled up
#						e. R Home path on which Libraries are installed
#                       f. XDF Root

# Import Docopt libraries to read input
# command line parameters

library(docopt)

# Docopt Command Line Arguments -------------------------------------------

doc <- "Usage: Correlater.R [options] [-h]

-b --batch_id BATCH Batch or Session ID [default: NA]
-a --project PROJECT Project Name or Application ID [default: NA]
-c --conf_json CONFIG Config File Path [default: r_script_conf.jconf]
-o --r_home R_HOME Home path for R Installation with libraries & executables [default: NA]
-r --root ROOT XDF Root [default: hdfs:///data/bda]
"
# Get commandline arguments ----------------------------

opt <- docopt::docopt(doc)

# Check inputs
lapply(opt, function(o) {
  if (is.na(o)) {
    stop(paste(names(o), "input not provided", "\n"))
  }
})

# Set Component Inputs
batch_id   <- opt$batch_id
project     <- opt$project
conf_file <- opt$conf_json
r_home <- opt$r_home
root <- opt$root

# Read the config file for Component, Spark Connection & Dataset Information

r_lib_home <- paste(r_home, "libraries", sep = "/")

library(jsonlite, lib.loc = r_lib_home)
library(dplyr, lib.loc = r_lib_home)
library(sparklyr, lib.loc = r_lib_home)
library(a2sipr, lib.loc = r_lib_home)
library(a2munge, lib.loc = r_lib_home)

conf_json <- jsonlite::fromJSON(readLines(conf_file))

# Configure Spark connection ----------------------------------------
# Get Spark configuration parameters using the main
# config file & configure spark connection

# SAW login parameters are also included in the system parameters section

spark_conn_df <- as.data.frame(conf_json$parameters)

# Get SAW credentials & host info from configuration

saw_conn_df <- as.data.frame(conf_json$saw_parameters)

saw_login_user <- as.character((saw_conn_df[saw_conn_df$name == "saw.login.user", "value"]))
saw_login_password <- as.character((saw_conn_df[saw_conn_df$name == "saw.login.password", "value"]))
saw_host_name <- as.character((saw_conn_df[saw_conn_df$name == "saw.host.name", "value"]))

# Get Environment system variable parameters from configuration

system_env_df <- as.data.frame(conf_json$system_env_parameters)

spark_home <- as.character((system_env_df[system_env_df$name == "spark.home", "value"]))
java_home <- as.character((system_env_df[system_env_df$name == "java.home", "value"]))
hadoop_home <- as.character((system_env_df[system_env_df$name == "hadoop.home", "value"]))
hadoop_conf_dir <- as.character((system_env_df[system_env_df$name == "hadoop.conf.dir", "value"]))

# Set environment variables for Sparklyr operations

Sys.setenv(SPARK_HOME = spark_home)
Sys.setenv(JAVA_HOME = java_home)
Sys.setenv(HADOOP_HOME = hadoop_home)
Sys.setenv(HADOOP_CONF_DIR = hadoop_conf_dir)

# Check if Spark connection Flag is set. Set up spark context only if
# Flag is True

spk_conn_flag <- as.character((spark_conn_df[spark_conn_df$name == "spark.conn.flag", "value"]))

if (spk_conn_flag == "TRUE") {
  conf <- spark_config()
  
  spark_master <- as.character((spark_conn_df[spark_conn_df$name == "spark.master", "value"]))
  
  for (name in spark_conn_df$name) {
    n <- name
    conf[[name]] <- spark_conn_df %>%
      filter(name == n) %>%
      dplyr::pull(value) %>%
      as.character()
  }
  
  conf$sparklyr.log.console <- "TRUE"
  
  # Set up Spark connection using the config parameters
  
  sc <- spark_connect(master = spark_master, config = conf)
}

# Derive Input & Output Dataset info from MaprDB Metastore

# Get Input dataset name from the config file

inputs_df <- as.data.frame(conf_json$inputs)

input_dataset_name <- as.character(inputs_df$dataSet)

# Get SAW Login Bearer token

saw_login_token <- sip_authenticate(saw_login_user, saw_login_password, saw_host_name)

# Get Input dataset details using SAW API

input_dataset_ms <- paste(project, input_dataset_name, sep = "::")

input_dataset_details <- sip_get_dataset_details(input_dataset_ms, project, saw_host_name, saw_login_token)

input_dataset_folder <- input_dataset_details$system$physicalLocation
input_data_format <- input_dataset_details$system$format
input_repart_numb <- as.numeric(input_dataset_details$system$numberOfFiles)
input_mode <- as.character(input_dataset_details$system$mode)

# Create Spark Data frame from Input dataset

input_spk_df <-
  a2munge::reader(
    sc,
    name = input_dataset_name,
    path = input_dataset_folder,
    type = input_data_format,
    repartition = input_repart_numb
  )

# Get Output dataset details from the config file

outputs_df <- as.data.frame(conf_json$outputs)

output_dataset_name <- as.character(outputs_df$dataSet)

output_dataset_ms <- paste(project, output_dataset_name, sep = "::")

output_catalog <- as.character(outputs_df$catalog)
output_mode <- as.character(outputs_df$mode)
output_data_format <- as.character(outputs_df$format)
output_repart_numb <- as.numeric(outputs_df$numberOfFiles)
output_partit_by <- as.character(outputs_df[row, "partitionKeys"])

if (is.na(output_partit_by) ||
    output_partit_by == "" ||
    identical(output_partit_by, character(0))) {
  output_partit_by <- NULL
}

output_schema <- list(list(name = "Dummy Column", type = "string"))

sip_add_dataset(
  output_format = output_data_format,
  output_name = output_dataset_name,
  output_schema = output_schema,
  script = "Correlater.R",
  created_by = Sys.info()["user"],
  batch_id = batch_id,
  started = format(as.POSIXct(Sys.time()), "%Y%m%d-%H%M%S"),
  catalog = output_catalog,
  project_id = project,
  status = "In Progress",
  hostname = saw_host_name,
  token = saw_login_token,
  input_paths = input_dataset_folder,
  input_formats = input_data_format,
  input_ids = input_dataset_ms,
  component = "A2 Correlater Component"
)

output_dataset_details <- sip_get_dataset_details(output_dataset_ms, project, saw_host_name, saw_login_token)

output_dataset_folder <- output_dataset_details$system$physicalLocation

# Read Correlater Component parameter values ---------------------------

rcomp_conf_df <- as.data.frame(conf_json$correlater)

i <- 1
for (row in 1:nrow(rcomp_conf_df)) {
  
  .target_var <- as.character(rcomp_conf_df[row, "targetField"])
  .transform <- as.character(rcomp_conf_df[row, "transform"])
  .output_col_names <- rcomp_conf_df[row, "outputColNames"][[1]]
  .remove_diag <- as.logical(rcomp_conf_df[row, "removeDiag"])
  .collect <- as.logical(rcomp_conf_df[row, "collect"])
  
  if (is.na(.output_col_names) || .output_col_names == "") {
    .output_col_names <- NULL
  }
  
  if (is.na(.remove_diag) || .remove_diag == "") {
    .remove_diag <- NULL
  }
  
  if (is.na(.transform) || .transform == "") {
    .transform <- NULL
  }
  
  if (is.na(.collect) || .collect == "") {
    .collect <- NULL
  }
  
  X <- input_spk_df %>%
    correlater(
      .,
      target_var = .target_var,
      transform = .transform,
      output_col_names = .output_col_names,
      remove_diag = .remove_diag,
      collect = .collect
    )
  
  if (i == 1) {
    rcomp_spk_df <- X
  } else {
    rcomp_spk_df <- rbind(rcomp_spk_df, X)
  }
  
  rm(X)
  
  i <- i + 1
  
}

a2munge::writer(
  rcomp_spk_df,
  path = output_dataset_folder,
  mode = output_mode,
  type = output_data_format,
  partitions = output_repart_numb,
  partition_by = output_partit_by,
  name = output_dataset_name
)

# Get Dataset schema using the helpers function

output_schema <- a2munge::schema(rcomp_spk_df)

sip_add_dataset(
  output_format = output_data_format,
  output_name = output_dataset_name,
  output_schema = output_schema,
  script = "Correlater.R",
  created_by = Sys.info()["user"],
  batch_id = batch_id,
  finished = format(as.POSIXct(Sys.time()), "%Y%m%d-%H%M%S"),
  catalog = output_catalog,
  project_id = project,
  status = "Success",
  hostname = saw_host_name,
  token = saw_login_token,
  input_paths = input_dataset_folder,
  input_formats = input_data_format,
  input_ids = input_dataset_ms,
  component = "A2 Correlater Component"
)

spark_disconnect(sc)