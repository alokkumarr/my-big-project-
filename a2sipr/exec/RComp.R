#!/usr/bin/env Rscript

# R Script Template ------------------------------------------------------
# The purpose of this template script is to allow App developers to code
# R scripts & execute them within the larger SIP framework. The usage
# of this template will help the developers to ignore the nitty-gritty of
# executing an R script within Spark/Mapr cluster such as configuring
# Spark connection, linking to Mapr Metastore DB etc.
# The below files are required in conjunction with the R script template
# to run R business logic
# 1. Configuration file template (or config file) which will hold Spark
#      parameter values, input/output dataset locations & Parameter file
#      location. Will be a JSON file.
# 2. Shell script template which will include a trigger to Rscript with
#      the parameters - a. R script with business logic filled in App Dev
#                       b. Batch ID
#                       c. Project Name
#                       d. Config File name with all required info filled up
#						e. R Home path on which Libraries are installed
#                       f. XDF Root

# Import Docopt libraries to read input
# command line parameters

library(docopt)

# Docopt Command Line Arguments -------------------------------------------

doc <- "Usage: R_Comp.R [options] [-h]

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
library(a2sipr, lib.loc = r_lib_home)

conf_json <- jsonlite::fromJSON(readLines(conf_file))

# Read R Script Component parameter values ---------------------------
# 1. R Script parameter file location. Should contain list of
#     libraries that will be used by the R script. JSONLITE is not
#     required in this list
# 2. Temp Flag decides whether the output objects that will be created
#     by R script are to be created in dout or temp catalog folders

rcomp_conf_df <- as.data.frame(conf_json$r_component)

# Read parameters for R Libraries location & list

r_lib_loc <- as.character(rcomp_conf_df$rLibraryLocation)
r_libraries <- rcomp_conf_df$rLibraryList

for (lib_name in unlist(r_libraries))
{
  suppressMessages(library(lib_name, character.only = T, lib.loc =  r_lib_loc))
}

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
  
  spark_master <-
    as.character((spark_conn_df[spark_conn_df$name == "spark.master", "value"]))
  
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

# Input Datasets Creation Logic -----------------------------------------
# Configure Input datasets location from Metastore. If this is not
# required & the input dataset logic is included in business
# logic script, this part of the can be removed

rcomp_inputs_df <- as.data.frame(conf_json$inputs)

for (inps in rcomp_inputs_df$dataSet) {
  # Derive Input & Output Dataset info from MaprDB Metastore
  
  # Get Input dataset name from the config file
  
  input_dataset_name <- inps
  input_dataset_ms <- paste(project, input_dataset_name, sep = "::")
  
  # Get SAW Login Bearer token
  
  saw_login_token <- sip_authenticate(saw_login_user, saw_login_password, saw_host_name)
  
  # Get Input dataset details using SAW API
  
  input_dataset_details <-
    sip_get_dataset_details(input_dataset_ms, project, saw_host_name, saw_login_token)
  
  input_dataset_folder <- input_dataset_details$system$physicalLocation
  input_data_format <- input_dataset_details$system$format
  input_repart_numb <- as.numeric(input_dataset_details$system$numberOfFiles)
  input_mode <- as.character(input_dataset_details$system$mode)
  
  input_df_name <- paste(inps, "DF", sep = "_")
  
  X <-
    reader(
      sc,
      name = input_df_name,
      path = input_dataset_folder,
      type = input_data_format,
      repartition = input_repart_numb
    )
  
  assign(input_df_name, X)
  
  rm(X)
}

#### End of Input Datasets Logic ####


# Add Business Logic R Script here ---------------------------------------





#### End of Business Logic ####


# Output Datasets Logic ---------------------------------------------------
# Configure Output datasets location from Metastore
# This part of the code can be commented if Output
# objects will be created in the Business Logic
# script itself

outputs_df <- as.data.frame(conf_json$outputs)

# Get Output dataset details from the config file
# Load Output datasets

for (row in 1:nrow(outputs_df)) {
   
  output_dataset_name <- as.character(outputs_df[row, "dataSet"])
  
  output_dataset_ms <- paste(project, output_dataset_name, sep = "::")
  
  output_catalog <- as.character(outputs_df[row, "catalog"])
  output_mode <- as.character(outputs_df[row, "mode"])
  output_data_format <- as.character(outputs_df[row, "format"])
  output_repart_numb <- as.numeric(outputs_df[row, "numberOfFiles"])

  output_schema <- list(
    list(name = "ID", type = "long"),
    list(name = "KEY", type = "string"),
    list(name = "SECRETS", type = "string")
  )
  
  sip_add_dataset(
    output_format = output_data_format,
    output_name = output_dataset_name,
    output_schema = output_schema,
    script = "R_Script_Component.R",
    created_by = "sipuser",
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
    component = "A2 R Script Component"
  )
  
  output_dataset_details <- sip_get_dataset_details(output_dataset_ms, project, saw_host_name, saw_login_token)
  
  # Get dataset folder path based on catalog & dataset name
  
  output_dataset_folder <- output_dataset_details$system$physicalLocation
  
  output_df_name <- paste(output_dataset_name, "DF", sep = "_")
  
  writer(
    df = eval(parse(text = output_df_name)),
    path = output_dataset_folder,
    mode = output_mode,
    type = output_data_format,
    partitions = output_repart_numb,
    name = output_dataset_name
  )

  if (class(eval(parse(text = output_df_name)))[1] == "data.frame") {
    output_schema <- lapply(eval(parse(text = output_df_name)), class)
  } else {
    output_schema <- sdf_schema(eval(parse(text = output_df_name)))
  }

  #output_schema <- list(
  #   list(name = "ID", type = "string")
  #)

  
  #sip_add_dataset_details()
  sip_add_dataset(
    output_format = output_data_format,
    output_name = output_dataset_name,
    output_schema = output_schema,
    script = "R_Script_Component.R",
    created_by = "sipuser",
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
    component = "A2 R Script Component"
  )
  
}

#### End of Output Datasets Logic ####

spark_disconnect(sc)
