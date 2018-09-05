# A2sipr Unit Tests -----------------------------------------------------

library(a2sipr)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)

context("A2Sipr catalog function unit tests")
# 
# 
# output_name1 <- "DataSet_1"
# output_name2 <- "DataSet_2"
# output_name3 <- "DataSet_3"
# output_format <- "parquet"
# catalog1 <- "data"
# catalog2 <- "temp"
# catalog3 <- "dinp"
# output_rows <- 10
# component = "RComponent"
# script <- "/dfs/data/bda/xda-test/Test.R"
# desc <- "Test R new code"
# created_by <- "Aks"
# project_id <- "aa-sb-poc"
# batch_id <- "B252356"
# input_paths <- "/dfs/data/bda/xda-test"
# input_formats <- "csv"
# input_ids <- "123"
# started <- "20180801-113751"
# finished <- "20180801-113807"
# status <- "SUCCESS"
# hostname <- "http://saw-rd601.ana.dev.vaste.sncrcorp.net"
# 
# jwt_token <-
#   sip_authenticate(
#     "sawadmin@synchronoss.com",
#     "Sawsyncnewuser1!",
#     "http://saw-rd601.ana.dev.vaste.sncrcorp.net"
#   )
# 
# output_schema_list <- list(
#   list(name = "ID", type = "long"),
#   list(name = "KEY", type = "string"),
#   list(name = "SECRETS", type = "string")
# )
# 
# 
# # Test 1: RestAPI-Add DS function test with catalog="data" ----------------------------
# 
# add_ds1 <- sip_add_dataset(
#   output_name = output_name1,
#   output_format = output_format,
#   output_schema = output_schema_list,
#   catalog = catalog1,
#   output_rows = output_rows,
#   component = component,
#   script = script,
#   desc = desc,
#   created_by = created_by,
#   project_id = project_id,
#   batch_id = batch_id,
#   input_paths = input_paths,
#   input_formats = input_formats,
#   input_ids = input_ids,
#   started = started,
#   finished = finished,
#   status = status,
#   hostname = hostname,
#   token = jwt_token
# )
# 
# test_that("Check if catalog and output name are expected", {
#   expect_equal(add_ds1$system$name, output_name1)
#   expect_equal(add_ds1$system$catalog, catalog1)
# })
# 
# 
# # Test the physical location is derived using catalog ---------------------
# 
# exp_phys_loc <-
#   paste0("maprfs:///var/sip/",
#          project_id,
#          "/dl/fs/",
#          catalog1,
#          "/",
#          output_name1,
#          "/",
#          "data")
# 
# actual_phys_loc <- add_ds1$system$physicalLocation
# 
# test_that("Physical location matches with expected one", {
#   expect_equal(exp_phys_loc, actual_phys_loc)
# })
# 
# # Test that the added dataset details match the correct catalog and physical loc --------
# 
# get_ds_detail1 <- sip_get_dataset_details(
#   dataset_id = add_ds1$`_id`,
#   project_id = project_id,
#   hostname = hostname,
#   token = jwt_token
# )
# 
# output_ds <- add_ds1$system$name
# output_format <-  add_ds1$system$outputFormat
# output_phys_loc <- add_ds1$system$physicalLocation
# output_proj_id <- add_ds1$system$project
# output_catalog <- add_ds1$system$catalog
# get_ds <- get_ds_detail1$system$name
# get_ds_output_format <- get_ds_detail1$system$outputFormat
# get_ds_physical_loc <- get_ds_detail1$system$physicalLocation
# get_ds_project_id <- get_ds_detail1$system$project
# get_ds_catalog <- get_ds_detail1$system$catalog
# 
# test_that("Newly added DS content matches with get DS details", {
#   expect_equal(output_ds, get_ds)
#   expect_equal(output_ds, get_ds)
#   expect_equal(output_format, get_ds_output_format)
#   expect_equal(output_phys_loc, get_ds_physical_loc)
#   expect_equal(output_proj_id, get_ds_project_id)
#   expect_equal(output_catalog, get_ds_catalog)
# })
# 
# 
# 
# # Test 2: RestAPI-Add DS function test with catalog="temp" ----------------------------
# 
# add_ds2 <- sip_add_dataset(
#   output_name = output_name2,
#   output_format = output_format,
#   output_schema = output_schema_list,
#   catalog = catalog2,
#   output_rows = output_rows,
#   component = component,
#   script = script,
#   desc = desc,
#   created_by = created_by,
#   project_id = project_id,
#   batch_id = batch_id,
#   input_paths = input_paths,
#   input_formats = input_formats,
#   input_ids = input_ids,
#   started = started,
#   finished = finished,
#   status = status,
#   hostname = hostname,
#   token = jwt_token
# )
# 
# test_that("Check if catalog and output name are expected", {
#   expect_equal(add_ds2$system$name, output_name2)
#   expect_equal(add_ds2$system$catalog, catalog2)
# })
# 
# 
# # Test the physical location is derived using catalog ---------------------
# 
# exp_phys_loc <-
#   paste0("maprfs:///var/sip/",
#          project_id,
#          "/dl/fs/",
#          catalog2,
#          "/",
#          output_name2,
#          "/",
#          "data")
# 
# actual_phys_loc <- add_ds2$system$physicalLocation
# 
# test_that("Physical location matches with expected one", {
#   expect_equal(exp_phys_loc, actual_phys_loc)
# })
# 
# # Test that the added dataset details match the correct catalog and physical loc --------
# 
# get_ds_detail2 <- sip_get_dataset_details(
#   dataset_id = add_ds2$`_id`,
#   project_id = project_id,
#   hostname = hostname,
#   token = jwt_token
# )
# 
# output_ds <- add_ds2$system$name
# output_format <-  add_ds2$system$outputFormat
# output_phys_loc <- add_ds2$system$physicalLocation
# output_proj_id <- add_ds2$system$project
# output_catalog <- add_ds2$system$catalog
# get_ds <- get_ds_detail2$system$name
# get_ds_output_format <- get_ds_detail2$system$outputFormat
# get_ds_physical_loc <- get_ds_detail2$system$physicalLocation
# get_ds_project_id <- get_ds_detail2$system$project
# get_ds_catalog <- get_ds_detail2$system$catalog
# 
# test_that("Newly added DS content matches with get DS details", {
#   expect_equal(output_ds, get_ds)
#   expect_equal(output_format, get_ds_output_format)
#   expect_equal(output_phys_loc, get_ds_physical_loc)
#   expect_equal(output_proj_id, get_ds_project_id)
#   expect_equal(output_catalog, get_ds_catalog)
# })
# 
# # Test 3: RestAPI-Add DS function test with catalog="dinp" ----------------------------
# 
# add_ds3 <- sip_add_dataset(
#   output_name = output_name3,
#   output_format = output_format,
#   output_schema = output_schema_list,
#   catalog = catalog3,
#   output_rows = output_rows,
#   component = component,
#   script = script,
#   desc = desc,
#   created_by = created_by,
#   project_id = project_id,
#   batch_id = batch_id,
#   input_paths = input_paths,
#   input_formats = input_formats,
#   input_ids = input_ids,
#   started = started,
#   finished = finished,
#   status = status,
#   hostname = hostname,
#   token = jwt_token
# )
# 
# test_that("Check if catalog and output name are expected", {
#   expect_equal(add_ds3$system$name, output_name3)
#   expect_equal(add_ds3$system$catalog, catalog3)
# })
# 
# # Test the physical location is derived using catalog ---------------------
# 
# exp_phys_loc <-
#   paste0("maprfs:///var/sip/",
#          project_id,
#          "/dl/fs/",
#          catalog3,
#          "/",
#          output_name3,
#          "/",
#          "data")
# 
# actual_phys_loc <- add_ds3$system$physicalLocation
# 
# test_that("Physical location matches with expected one", {
#   expect_equal(exp_phys_loc, actual_phys_loc)
# })
# 
# # Test that the added dataset details match the correct catalog and physical loc --------
# 
# get_ds_detail3 <- sip_get_dataset_details(
#   dataset_id = add_ds3$`_id`,
#   project_id = project_id,
#   hostname = hostname,
#   token = jwt_token
# )
# 
# output_ds <- add_ds3$system$name
# output_format <-  add_ds3$system$outputFormat
# output_phys_loc <- add_ds3$system$physicalLocation
# output_proj_id <- add_ds3$system$project
# output_catalog <- add_ds3$system$catalog
# get_ds <- get_ds_detail3$system$name
# get_ds_output_format <- get_ds_detail3$system$outputFormat
# get_ds_physical_loc <- get_ds_detail3$system$physicalLocation
# get_ds_project_id <- get_ds_detail3$system$project
# get_ds_catalog <- get_ds_detail3$system$catalog
# 
# test_that("Newly added DS content matches with get DS details", {
#   expect_equal(output_ds, get_ds)
#   expect_equal(output_format, get_ds_output_format)
#   expect_equal(output_phys_loc, get_ds_physical_loc)
#   expect_equal(output_proj_id, get_ds_project_id)
#   expect_equal(output_catalog, get_ds_catalog)
# })
