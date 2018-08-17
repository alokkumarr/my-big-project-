#!/usr/bin/env Rscript

# Detecter Component ------------------------------------------------------
# The purpose of the Detecter component to is to allow A2 App Developers to
# run Anomaly Detection process without having to code the R logic for it.
# The below files are required in conjunction with the Detecter script
# to run Anomaly Detection
# 1. Configuration file template (or config file) which will hold Spark
#      parameter values, input/output dataset locations & Parameter file
#      location. Will be a JSON file. 
# 2. Shell script template which will include a trigger to Rscript with 
#      the parameters - a. Detecter Script path
#                       b. Batch ID
#                       c. Project Name
#                       d. Config File name with all required info filled up
#						e. R Home path on which Libraries are installed
#                       f. XDF Root

# Import Docopt library to read input command line parameters

library(docopt)

# Docopt Command Line Arguments -------------------------------------------

doc <- "Usage: Detecter.R [options] [-h]

-b --batch_id BATCH Batch or Session ID [default: NA]
-a --project PROJECT Project Name or Application ID [default: NA]
-c --conf_json CONFIG Config File Path [default: r_script_conf.jconf]
-o --r_home R_HOME Home path for R Installation with libraries & executables [default: NA]
-r --root ROOT XDF Root [default: hdfs:///data/bda]
"
# Get commandline arguments ----------------------------

opt <- docopt::docopt(doc)

# Check inputs
lapply(opt, function(o){
  if(is.na(o)){
    stop(paste(names(o), "input not provided", "\n"))
  }
})

# Set Component Inputs
batch_id   <- opt$batch_id
project     <- opt$project
conf_file <- opt$conf_json
r_home <- opt$r_home
root <- opt$root
batch_id

# Read the config file for Component, Spark Connection & Dataset Information

library(jsonlite, lib.loc = r_home)
library(crayon, lib.loc = r_home)
library(dplyr, lib.loc = r_home)
library(purrr, lib.loc = r_home)
library(a2sipr, lib.loc = r_home)
library(curl, lib.loc = r_home)

conf_json <- jsonlite::fromJSON(
  readLines(conf_file)
)

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

for(lib_name in unlist(r_libraries))
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

if(spk_conn_flag == "TRUE"){
  conf <- spark_config()
  
  spark_master <- as.character((spark_conn_df[spark_conn_df$name == "spark.master", "value"]))

  for(name in spark_conn_df$name) {
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

# Read Detecter Component parameter values

rcomp_conf_df <- as.data.frame(conf_json$detecter)

.index_var <- as.character(rcomp_conf_df$indexField)
.group_vars <- as.character(rcomp_conf_df$groupField)
.measure_vars <- as.character(rcomp_conf_df$measureField)
.fun_var <- as.character(rcomp_conf_df$functionName)
.frequency <- as.numeric(rcomp_conf_df$frequency)
.direction <- as.character(rcomp_conf_df$direction)
.alpha <- as.numeric(rcomp_conf_df$alpha)
.maxAnoms <- as.numeric(rcomp_conf_df$maxAnoms)
.trendWindow <- as.numeric(rcomp_conf_df$trendWindow)

# Group variables is not mandatory parameter for Detecter. Hence, logic
#    is required to handle Null scenario

if(is.na(.group_vars) || .group_vars == "") {
  .summ_group_vars <- .index_var
} else {
  .summ_group_vars <- c(.index_var, .group_vars)
}

# Derive Input & Output Dataset info from MaprDB Metastore

# Get Input dataset name from the config file

inputs_df <- as.data.frame(conf_json$inputs)

input_dataset_name <- as.character(inputs_df$dataSet)

# Get SAW Login Bearer token

saw_login_token <- sip_authenticate(saw_login_user, saw_login_password, saw_host_name)

# Get Input dataset details using SAW API

input_dataset_details <- sip_get_dataset_details(input_dataset_name, project, saw_host_name, saw_login_token)

input_dataset_folder <- input_dataset_details$system$physicalLocation
input_data_format <- input_dataset_details$system$format
input_repart_numb <- as.numeric(input_dataset_details$system$numberOfFiles)
input_mode <- as.character(input_dataset_details$system$mode)

# Create Spark Data frame from Input dataset

input_spk_df <- reader(sc, name = input_dataset_name, path = input_dataset_folder, type = input_data_format, 
              repartition = input_repart_numb)
			  
# Get Output dataset details from the config file

outputs_df <- as.data.frame(conf_json$outputs)

output_dataset_name <- as.character(outputs_df$dataSet)

data_src_ref <- "dl/fs"
data_folder <- "data"

out_folder <- paste(paste(root, project, sep = "/"),
                           data_src_ref, sep = "/")
                     
output_catalog <- as.character(rcomp_outputs_df$catalog)
output_mode <- as.character(rcomp_outputs_df$mode)
output_format <- as.character(rcomp_outputs_df$format)
output_repart_numb <- as.numeric(rcomp_outputs_df$numberOfFiles)

# Get dataset folder path based on catalog & dataset name

output_dataset_folder <- paste(paste(paste(out_folder, sep = "/"),
								output_catalog, output_dataset_name, sep = "/"),
						  data_folder, sep = "/")

# 1. Spark Data frame is not working with Detecter currently. 
#    Hence collect is required to transfer data to R data frame
# 2. More functionality is to be included to handle different
#    date periods based on configuration. Currently, Hourly 
#    is supported

rcomp_df <- input_spk_df %>%
  select(., .index_var, .measure_vars) %>%
  mutate(., !!.index_var := to_utc_timestamp(concat(
    substring(!!as.name(.index_var), 1, 4), '-',
    substring(!!as.name(.index_var), 6, 2), '-',
    substring(!!as.name(.index_var), 9, 2), ' ',
    substring(!!as.name(.index_var), 12, 2),
    ":00:00"), 'UTC')) %>%
  summariser(.,
             group_vars = .summ_group_vars,
             measure_vars = .measure_vars,
             fun = .fun_var) %>%
  arrange(., desc(!!as.name(.index_var))) %>%
  collect %>%
  detecter(.,
           index_var = .index_var,
           group_vars = if(is.na(.group_vars) || .group_vars == "") {NULL} else {.group_vars},
           measure_vars = paste(.measure_vars, .fun_var, sep = "_"),
           frequency = .frequency,
           direction = .direction,
           alpha = .alpha,
           max_anoms = .maxAnoms,
           trend_window = .trendWindow) %>%
  mutate(., !!.index_var := as.character(!!as.name(.index_var)),
         expected = value - resid)

# Mutation of R data frame Timestamp column to string is required
#    due to a pending Sparklyr issue where Spark data frame is unable 
#    to handle R data frame timestamp columns

rcomp_spk_df <- copy_to(sc, rcomp_df, overwrite = TRUE)

writer(rcomp_spk_df, path = output_dataset_folder, 
                     mode = output_mode, type = output_format, partitions = output_repart_numb)

sip_add_dataset(output_path = output_dataset_folder, output_format = output_format, script = "Detecter.R",
				project_id = project, hostname = saw_host_name, token = saw_login_token)
				
spark_disconnect(sc)
