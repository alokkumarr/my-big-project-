# A2sipr Unit Tests -----------------------------------------------------

library(a2sipr)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)

context("A2Sipr rest-api function unit tests")

output_name <- "DataSet_1"
output_format <- "parquet"
catalog <- "data"
output_rows <- 10
component = "RComponent"
script <- "/dfs/data/bda/xda-test/Test.R"
desc <- "Test R new code"
created_by <- "Aks"
project_id <- "aa-sb-poc"
batch_id <- "B252356"
input_paths <- "/dfs/data/bda/xda-test"
input_formats <- "csv"
input_ids <- "123"
started <- "20180801-113751"
finished <- "20180801-113807"
status <- "SUCCESS"
hostname <- "http://saw-rd601.ana.dev.vaste.sncrcorp.net"


jwt_token <-
  sip_authenticate(
    "sawadmin@synchronoss.com",
    "Sawsyncnewuser1!",
    "http://saw-rd601.ana.dev.vaste.sncrcorp.net"
  )

output_schema_list <- list(
  list(name = "ID", type = "long"),
  list(name = "KEY", type = "string"),
  list(name = "SECRETS", type = "string")
)


# Test 1: RestAPI-Authontication function test ----------------------------

add_ds <- sip_add_dataset(
  output_name = output_name,
  output_format = output_format,
  output_schema = output_schema_list,
  catalog = catalog,
  output_rows = output_rows,
  component = component,
  script = script,
  desc = desc,
  created_by = created_by,
  project_id = project_id,
  batch_id = batch_id,
  input_paths = input_paths,
  input_formats = input_formats,
  input_ids = input_ids,
  started = started,
  finished = finished,
  status = status,
  hostname = hostname,
  token = jwt_token
)


test_that("Physical location matches with expected one", {
  expect_equal(add_ds$system$name, output_name)
  expect_equal(add_ds$system$outputFormat, output_format)
})

# Test2: Get all the Dataset list from MaprDB -----------------------------

# ds_get <- sip_get_datasets(project_id = project_id,
#                            hostname = hostname,
#                            token = jwt_token)
# 
# val <- length(ds_get)
# 
# for (i in 1:val)
# {
#   latest_DS <- getElement(ds_get, i)
#   if (latest_DS$`_id` == add_ds$`_id`)
#   {
#     new_dataset <- getElement(ds_get, i)
#     paste0("i value is", i)
#     break
#   }
# }

new_dataset <- sip_get_dataset_details(add_ds$`_id`,
                                       project_id = add_ds$system$project,
                                       hostname = hostname,
                                       token = jwt_token)


test_that("Physical location matches with expected one", {
  expect_equal(class(new_dataset), "list")
  #expect_gt(val, 0)
  expect_equal(new_dataset$`_id`, add_ds$`_id`)
  expect_equal(new_dataset$system$catalog, catalog)
  expect_equal(new_dataset$system$outputFormat, output_format)
  expect_equal(new_dataset$system$project, project_id)
  expect_equal(new_dataset$system$physicalLocation,
               add_ds$system$physicalLocation)
})

# Test 3:Rest API for Get Data set details --------------------------------

get_ds_detail <- sip_get_dataset_details(
  dataset_id = add_ds$`_id`,
  project_id = project_id,
  hostname = hostname,
  token = jwt_token
)

output_ds <- add_ds$system$name
output_format <-  add_ds$system$outputFormat
output_phys_loc <- add_ds$system$physicalLocation
output_proj_id <- add_ds$system$project
get_ds <- get_ds_detail$system$name
get_ds_output_format <- get_ds_detail$system$outputFormat
get_ds_physical_loc <- get_ds_detail$system$physicalLocation
get_ds_project_id <- get_ds_detail$system$project

test_that("Newly added DS content matches with get DS details", {
  expect_equal(output_ds, get_ds)
  expect_equal(output_format, get_ds_output_format)
  expect_equal(output_phys_loc, get_ds_physical_loc)
  expect_equal(output_proj_id, get_ds_project_id)
})


# Test 4:Rest API to get the Data path from Mapr DB -----------------------

get_dpath <- sip_get_datapath(
  name = "DataSet_1",
  project_id = project_id,
  hostname = hostname,
  token = jwt_token
)

output_phys_loc <- add_ds$system$physicalLocation

test_that("Test if the Data path matches Physical location", {
  expect_equal(get_dpath, output_phys_loc)
})
