
#' SIP Metastore Authenticate Function
#'
#' Generates authentication token for metastore communication
#'
#' @param login master login character
#' @param password password character
#' @param url url to post request to SAW security service
#' @param content_type content type for headers
#' @param charset character set
#'
#' @return aToken string
#' @export
sip_authenticate <- function(login,
                             password,
                             url = "https://saw-bda-cert-vaste.sncrcorp.net/saw/security/doAuthenticate",
                             headers = list('Content-Type' = "application/json;charset=UTF-8")){
  checkmate::assert_character(login)
  checkmate::assert_character(password)
  checkmate::assert_character(url)
  checkmate::assert_list(headers, null.ok = TRUE)


  # Headers
  if(! is.null(headers)){
    purrr::map(headers, checkmate::assert_character)
    .fun <- get("add_headers", asNamespace("httr"))
    added_headers <- do.call(".fun", headers)
  }else{
    added_headers <- NULL
  }

  # Body
  body_str <- paste0("{\"masterLoginId\": \"", login, "\", \"password\": \"", password, "\"}")

  # Post request
  response <- httr::POST(url = url, added_headers, body = body_str)

  # Return Token
  httr::content(response)$aToken
}


#' SIP Metastore Get Datasets Function
#'
#' Returns all datasets in Workbench Metastore
#'
#' @param token
#' @param url
#' @param headers
#'
#' @return
#' @export
#'
#' @examples
sip_get_datasets <- function(token,
                             url = "https://saw-bda-cert-vaste.sncrcorp.net/saw/services/internal/workbench/projects/workbench/datasets",
                             headers = list('Accept' = "application/json, text/plain, */*")){
  checkmate::assert_character(url)
  checkmate::assert_list(headers, null.ok = TRUE)
  if(! is.null(headers)){
    purrr::map(headers, checkmate::assert_character)
  }

  # Add Headers
  headers <- c(headers, list("Authorization" = paste("Bearer", token)))
  .fun <- get("add_headers", asNamespace("httr"))
  added_headers <- do.call(".fun", headers)

  # GET request
  response <- httr::GET(url, added_headers)

  httr::content(response)
}
