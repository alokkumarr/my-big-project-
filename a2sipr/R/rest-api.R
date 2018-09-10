
#' SIP Metastore Authenticate Function
#'
#' Generates authentication token for metastore communication
#'
#' POST request to saw/security/doAuthenticate
#'
#' @param login master login character
#' @param password password character
#' @param hostname hostname only of saw security location. No pathing required
#'   Ex: hostname = "https://saw-bda-cert-vaste.sncrcorp.net"
#'
#' @return aToken authentication string
#' @export
#' @import httr
sip_authenticate <- function(login,
                             password,
                             hostname){
  checkmate::assert_character(login)
  checkmate::assert_character(password)
  checkmate::assert_character(hostname)
  
  # Headers
  headers <- httr::add_headers('Content-Type' = "application/json;charset=UTF-8")
  
  # Body
  body_str <- paste0("{\"masterLoginId\": \"", login, "\", \"password\": \"", password, "\"}")
  
  # URL
  url <- paste0(hostname, "/saw/security/doAuthenticate")
  
  # Post request
  response <- httr::POST(url, config = headers, body = body_str)
  
  # Return Token
  httr::content(response)$aToken
}



#' SIP Metastore Get Datasets Function
#'
#' Returns all datasets in Workbench Metastore for a given project
#'
#' Get request to saw/services/internal/workbench/projects/{project_id}/datasets
#'
#' @param project_id id of project to retrieve datasets from
#' @inheritParams sip_authenticate
#' @param token valid SIP token. Result of sip_authenticate function
#'
#' @return list with datasets metadata
#' @export
sip_get_datasets <- function(project_id,
                             hostname,
                             token){
  checkmate::assert_character(project_id)
  checkmate::assert_character(hostname)
  checkmate::assert_character(token)
  
  # Add Headers
  headers <- httr::add_headers('Authorization' = paste("Bearer", token),
                               'Accept' = "application/json, text/plain, */*")
  
  # URL
  url <- paste0(hostname, "/saw/services/internal/workbench/projects/", project_id, "/datasets")
  
  # GET request
  response <- httr::GET(url, config = headers)
  
  httr::content(response)
}



#' SIP Metastore Get Dataset Details
#'
#' Returns dataset metadata details
#'
#' GET request to saw/services/internal/workbench/projects/{project_id}/datasets/{dataset_id}
#'
#' @param dataset_id dataset id
#' @inheritParams sip_get_datasets
#'
#' @return list with dataset details
#' @export
sip_get_dataset_details <- function(dataset_id,
                                    project_id,
                                    hostname,
                                    token){
  checkmate::assert_character(dataset_id)
  checkmate::assert_character(project_id)
  checkmate::assert_character(hostname)
  checkmate::assert_character(token)
  
  # Add Headers
  headers <- httr::add_headers('Authorization' = paste("Bearer", token),
                               'Accept' = "application/json, text/plain, */*")
  
  # URL
  url <- paste0(hostname,
                "/saw/services/internal/workbench/projects/", project_id,
                "/datasets/", dataset_id)
  
  # GET request
  response <- httr::GET(url, config = headers)
  
  httr::content(response)
}



#' SIP Metastore Add Dataset
#'
#' Adds a dataset record to the SIP Metastore
#'
#' POST request to
#' /saw/services/internal/workbench/projects/{project_id}/datasets/create
#'
#'
#' @param output_name output dataset name. Used in the dynamic file path
#'   creation
#' @param output_format output dataset format. accepts parquet, JSON, csv and
#'   rds
#' @param output_schema output dataset sechema. Should be a list with name and
#'   type elements. list should not be named
#' @param catalog catalog location for output dataset. Used in the dynamic file
#'   path creation
#' @param output_rows ouput dataset record count. Default is NULL
#' @param component component name used to create dataset. Default is RComponent
#' @param script path to executable R script
#' @param desc description of dataset. Default is empty string
#' @param created_by user name. Default is system user name
#' @param project_id project id string
#' @param batch_id batch id string. Default is empty string
#' @param input_paths vector of input datasets paths. Default is empty string
#' @param input_ids vector of input dataset ids. Default is empty string
#' @param started string with starting time. Format should be YYYYMMDD-HHMMSS.
#'   Default is NULL which inserts current time
#' @param finished string with starting time. Format should be YYYYMMDD-HHMMSS
#' @param status component status. default is 'SUCCESS'. Default is NULL which
#'   inserts current time
#' @param hostname  hostname only of saw security location. No pathing required
#' @param token valid SIP token. Result of sip_authenticate function
#'
#' @return POST response content
#' @export
sip_add_dataset <- function(output_name,
                            output_format,
                            output_schema,
                            catalog = "data",
                            output_rows = 0,
                            component = 'RComponent',
                            script,
                            desc = "",
                            created_by = NULL,
                            project_id,
                            batch_id = "",
                            input_paths = "",
                            input_formats = "",
                            input_ids = "",
                            started = NULL,
                            finished = NULL,
                            status = "SUCCESS",
                            hostname,
                            token) {
  checkmate::assert_string(output_name)
  checkmate::assert_choice(output_format, c("parquet", "csv", "json", "rds"))
  checkmate::assert_list(output_schema)
  checkmate::assert_string(catalog)
  checkmate::assert_number(output_rows, lower = 0)
  checkmate::assert_string(component)
  checkmate::assert_string(desc)
  checkmate::assert_string(created_by, null.ok = TRUE)
  checkmate::assert_string(project_id)
  checkmate::assert_string(batch_id)
  checkmate::assert_character(input_paths)
  checkmate::assert_character(input_ids)
  checkmate::assert_string(started, pattern = "^[0-9]{8}([\\w-])([0-9]{6}$)", null.ok = TRUE)
  checkmate::assert_string(finished, pattern = "^[0-9]{8}([\\w-])([0-9]{6}$)", null.ok = TRUE)
  checkmate::assert_string(status)
  checkmate::assert_string(hostname)
  checkmate::assert_character(token)
  
  if(is.null(created_by)) created_by <- as.character(Sys.info()["user"])
  if(is.null(started)) started <- format(Sys.time(),  "%Y%m%d-%I%M%S")
  if(is.null(finished)) finished <- format(Sys.time(),  "%Y%m%d-%I%M%S")
  output_schema_ub <- purrr::map(output_schema, ~purrr::map(., jsonlite::unbox))
  
  # Headers
  headers <- httr::add_headers('Authorization' = paste("Bearer", token),
                               'Content-Type' = "application/json;charset=UTF-8")
  
  # Body
  payload <- list(userdata = list(description = jsonlite::unbox(desc),
                                  component = jsonlite::unbox(component),
                                  createdBy = jsonlite::unbox(created_by),
                                  script = jsonlite::unbox(script)),
                  system   = list(inputPath = input_paths,
                                  name = jsonlite::unbox(output_name),
                                  inputFormat = jsonlite::unbox(input_formats),
                                  outputFormat = jsonlite::unbox(output_format),
                                  catalog = jsonlite::unbox(catalog)),
                  asInput  = list(jsonlite::unbox(input_ids)),
                  #transformations = list(asOutputLocation = jsonlite::unbox(input_ids)),
                  asOfNow  = list(status = jsonlite::unbox(status),
                                  started = jsonlite::unbox(started),
                                  finished = jsonlite::unbox(finished),
                                  batchId = jsonlite::unbox(batch_id)),
                  recordCount = jsonlite::unbox(output_rows),
                  physicalLocation = jsonlite::unbox(""),
                  schema = list(fields = output_schema_ub))
  payload <- jsonlite::toJSON(payload, auto_unbox = FALSE)
  
  # URL
  url <- paste0(hostname, "/saw/services/internal/workbench/projects/", project_id, "/datasets/create")
  
  # Post request
  response <- httr::POST(url, config = headers, body = payload)
  
  # Return response
  httr::content(response)
}




#' SIP Metastore get Data File Path
#'
#' Function to call SIP Rest API to generate valid file path for dataset
#'
#' API handles the project
#'
#' @param name dataset name
#' @inheritParams sip_get_dataset_details
#'
#' @return dataset physical file path location string
#' @export
sip_get_datapath <- function(name,
                             project_id,
                             hostname,
                             token) {
  checkmate::assert_character(name)
  checkmate::assert_character(project_id)
  checkmate::assert_string(hostname)
  checkmate::assert_character(token)
  
  # Headers
  headers <- httr::add_headers('Authorization' = paste("Bearer", token),
                               'Content-Type' = "application/json;charset=UTF-8")
  
  # URL
  url <- paste0(hostname,
                "/saw/services/internal/workbench/projects/", project_id,
                "/", name, "/datapath")
  
  # Request
  response <- httr::GET(url, config = headers)
  
  # Response
  httr::content(response)
}
