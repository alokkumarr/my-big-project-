
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
#' @param dataset_name dataset name
#' @inheritParams sip_get_datasets
#'
#' @return list with dataset details
#' @export
sip_get_dataset_details <- function(dataset_name,
                                    project_id,
                                    hostname,
                                    token){
  checkmate::assert_character(dataset_name)
  checkmate::assert_character(project_id)
  checkmate::assert_character(hostname)
  checkmate::assert_character(token)

  # Add Headers
  headers <- httr::add_headers('Authorization' = paste("Bearer", token),
                               'Accept' = "application/json, text/plain, */*")

  # URL
  url <- paste0(hostname,
                "/saw/services/internal/workbench/projects/", project_id,
                "/datasets/", paste(project_id, dataset_name, sep = "::"))

  # GET request
  response <- httr::GET(url, config = headers)

  httr::content(response)
}



#' SIP Metastore Add Dataset
#'
#' Adds a dataset record to the SIP Metastore
#'
#' POST request to /saw/services/internal/workbench/projects/{project_id}/datasets/create
#'
#'
#' @param dataset_name name of dataset to add
#' @param dataset_path file path to saved dataset
#' @param dataset_format format of dataset. accepts parquet, JSON, csv and rds
#' @param row_count dataset record count. Default is NULL
#' @param component name of component used to create dataset
#' @param desc description of dataset
#' @param project_id project id string
#' @param batch_id batch id string
#' @param input_datasets_id string vector of input datasets
#' @param started string with starting time. Format should be YYYYMMDD-HHMMSS
#' @param finished string with starting time. Format should be YYYYMMDD-HHMMSS
#' @param status component status. default is 'SUCCESS'
#' @param hostname  hostname only of saw security location. No pathing required
#'
#' @return POST response content
#' @export
sip_add_dataset <- function(dataset_name,
                            dataset_path,
                            dataset_format,
                            row_count = NULL,
                            component,
                            desc,
                            project_id,
                            batch_id,
                            input_datasets_id,
                            started,
                            finished,
                            status = "SUCCESS",
                            hostname) {
  checkmate::assert_string(dataset_name)
  checkmate::assert_string(dataset_path)
  checkmate::assert_choice(dataset_format, c("parquet", "csv", "json", "rds"))
  checkmate::assert_number(row_count, null.ok = TRUE)
  checkmate::assert_string(component)
  checkmate::assert_string(desc)
  checkmate::assert_string(project_id)
  checkmate::assert_string(batch_id)
  checkmate::assert_character(input_datasets_id)
  checkmate::assert_string(started, pattern = "^[0-9]{8}([\\w-])([0-9]{6}$)")
  checkmate::assert_string(finished, pattern = "^[0-9]{8}([\\w-])([0-9]{6}$)")
  checkmate::assert_string(status)
  checkmate::assert_string(hostname)

  # Headers
  headers <- httr::add_headers('Content-Type' = "application/json;charset=UTF-8")

  # Body
  payload <- list(userData = list(description = desc,
                                  component = component),
                  system   = list(path = dataset_path,
                                  name = dataset_name,
                                  format = dataset_format),
                  asInput  = input_datasets_id,
                  asOfNow  = list(status = status,
                                  started = started,
                                  finished = finished,
                                  batchId = batch_id),
                  recordCount = row_count) %>%
    jsonlite::toJSON(., auto_unbox = TRUE)

  # URL
  url <- paste0(hostname, "/saw/services/internal/workbench/projects/", project_id, "/datasets/create")

  # Post request
  response <- httr::POST(url, config = headers, body = payload)

  # Return response
  httr::content(response)
}
