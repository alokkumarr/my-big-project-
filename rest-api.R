
#' Metastore Authenticate Function
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
metastore_authenticate <- function(login,
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

  # Post reqest
  response <- httr::POST(url = url, added_headers, body = body_str)

  # Return Token
  content(response)$aToken
}
