
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
#' @param output_path output dataset file path
#' @param output_format output dataset format. accepts parquet, JSON, csv and
#'   rds
#' @param row_count ouput dataset record count. Default is NULL
#' @param component component name used to create dataset. Default is RComponent
#' @param script path to executable R script
#' @param desc description of dataset. Default is empty string
#' @param created_by user name. Default is system user name
#' @param project_id project id string
#' @param batch_id batch id string. Default is empty string
#' @param input_paths vector of input datasets paths. Default is empty string
#' @param input_ids vector of input dataset ids. Default is empty string
#' @param input_names vectore of input dataset names. Default is empty string
#' @param started string with starting time. Format should be YYYYMMDD-HHMMSS.
#'   Default is NULL which inserts current time
#' @param finished string with starting time. Format should be YYYYMMDD-HHMMSS
#' @param status component status. default is 'SUCCESS'. Default is NULL which inserts current time
#' @param hostname  hostname only of saw security location. No pathing required
#' @param token valid SIP token. Result of sip_authenticate function
#'
#' @return POST response content
#' @export
sip_add_dataset <- function(output_path,
                            output_format,
                            output_rows = NULL,
                            component = 'RComponent',
                            script,
                            desc = "",
                            created_by = NULL,
                            project_id,
                            batch_id = "",
                            input_paths = "",
                            input_formats = "",
                            input_ids = "",
                            input_names = "",
                            started = NULL,
                            finished = NULL,
                            status = "SUCCESS",
                            hostname,
                            token) {
  checkmate::assert_string(output_path)
  checkmate::assert_choice(output_format, c("parquet", "csv", "json", "rds"))
  checkmate::assert_number(output_rows, null.ok = TRUE)
  checkmate::assert_string(component)
  checkmate::assert_string(desc)
  checkmate::assert_string(created_by, null.ok = TRUE)
  checkmate::assert_string(project_id)
  checkmate::assert_string(batch_id)
  checkmate::assert_string(input_names)
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

  # Headers
  headers <- httr::add_headers('Authorization' = paste("Bearer", token),
                               'Content-Type' = "application/json;charset=UTF-8")

  # Body
  payload <- list(userData = list(description = unbox(desc),
                                  component = unbox(component),
                                  createdBy = unbox(created_by),
                                  script = unbox(script)),
                  system   = list(inputPath = input_paths,
                                  name = unbox(input_names),
                                  inputFormat = unbox(input_formats),
                                  outputFormat = unbox(output_format)),
                  asInput  = list(unbox(input_ids)),
                  transformations = list(asOutputLocation = unbox(output_path)),
                  asOfNow  = list(status = unbox(status),
                                  started = unbox(started),
                                  finished = unbox(finished),
                                  batchId = unbox(batch_id)),
                  recordCount = unbox(output_rows))
  payload <- jsonlite::toJSON(payload, auto_unbox = FALSE)

  # URL
  url <- paste0(hostname, "/saw/services/internal/workbench/projects/", project_id, "/datasets/create")

  # Post request
  response <- httr::POST(url, config = headers, body = payload)

  # Return response
  httr::content(response)
}
