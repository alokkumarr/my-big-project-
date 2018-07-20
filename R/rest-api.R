
#' SIP Metastore Authenticate Function
#'
#' Generates authentication token for metastore communication
#'
#' @param login master login character
#' @param password password character
#' @param hostname hostname only of saw security location. No pathing required.
#'   Ex: hostname = "https://saw-bda-cert-vaste.sncrcorp.net"
#'
#' @return aToken authentication string
#' @export
sip_authenticate <- function(login,
                             password,
                             hostname
                             ){
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
#' @param project_id id of project to retrieve datasets from
#' @inheritParams sip_authenticate
#' @param token valid SIP token. Result of sip_authenticate function
#'
#' @return list with datasets metadata
#' @export
sip_get_datasets <- function(project_id,
                             hostname,
                             token
                             ){
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
#' @param dataset_name dataset name
#' @inheritParams sip_get_datasets
#'
#' @return list with dataset details
#' @export
sip_get_dataset_details <- function(dataset_name,
                                    project_id,
                                    hostname,
                                    token
                                    ){
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
