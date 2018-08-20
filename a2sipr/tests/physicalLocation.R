# A2sipr Unit Tests -----------------------------------------------------

library(a2sipr)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)

context("A2Sipr rest-api function unit tests-Physical location check")

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

add_ds_phis_path <- sip_add_dataset(
  output_name = "DataSet_1",
  output_format = "parquet",
  output_schema = output_schema_list,
  catalog = "data",
  output_rows = 10,
  component = "RComponent",
  script = "/dfs/data/bda/xda-test/Test.R",
  desc = "Test R new code",
  created_by = "Aks",
  project_id = "aa-sb-poc",
  batch_id = "B252356",
  input_paths = "/dfs/data/bda/xda-test",
  input_formats = "csv",
  input_ids = "123",
  started = "20180801-113751",
  finished = "20180801-113807",
  status = "SUCCESS",
  hostname = "http://saw-rd601.ana.dev.vaste.sncrcorp.net",
  token = jwt_token
)
output_name <- add_ds$system$name
projectid <- add_ds$system$project
catalog_name <- add_ds$system$catalog

exp_phys_loc <-
  paste0("maprfs:///var/sip/",
         projectid,
         "/dl/fs/data/",
         output_name,
         "/",
         catalog_name)

actual_phys_loc <- add_ds_phis_path$system$physicalLocation

test_that("Physical location matches with expected one", {
  expect_equal(exp_phys_loc, actual_phys_loc)
})
