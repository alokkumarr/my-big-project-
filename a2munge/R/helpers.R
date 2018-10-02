
# Helper Functions --------------------------------------------------------



#' Dataset Schema Function
#'
#' Function to extract dataset schema. Returns a nested list with an element for
#' each column in the dataset. Output is similiar to sparklyr::sdf_schema.
#'
#' @param df dataframe
#'
#' @return nested list with element for each column in dataset
#' @export
#'
#' @examples
#' schema(mtcars)
schema <- function(df) {
  UseMethod("schema")
}


#' @rdname schema
#' @export
schema.data.frame <- function(df) {
  purrr::imap(df, function(x, name) {
    list(name = name, type = class(x))
  })
}


#' @rdname schema
#' @export
schema.tbl_spark <- function(df) {
  sparklyr::sdf_schema(df)
}


#' Get Dataset Schema
#'
#' Returns named list of column types. Names refer to column names. Uses schema
#' function to apply correct method
#'
#' @param df dataset
#'
#' @return named list of column types
#' @export
#'
#' @examples
#' get_schema(mtcars)
get_schema <- function(df) {
  if(! any(c("data.frame", "tbl_spark") %in% class(df))) {
    stop("Dataset lass assertion failed. Must include either data.frame or tbl_spark")
  }
  
  df %>% 
    a2munge::schema(.) %>% 
    purrr::map(., "type")
}


#' Schema Check
#'
#' Function to check to if a schema matches a second. Validate option will
#' create a stop error if not all of x columns present in y or if there is a
#' type mismatch
#'
#' Function supports either dataframe inputs (R or Spark) or schema lists
#'
#' @return data.frame with schema comparison if stop error not thrown
#' @export
#' 
#' @examples 
#' 
#' # Schema Input Example
#' schema_check(get_schema(mtcars), get_schema(select(mtcars, mpg, am)))
#' 
#' # Dataframe Input Example
#' schema_check(mtcars, select(mtcars, mpg, am))
schema_check <- function(x, y, validate = TRUE) {
  
  if(any(c("data.frame", "tbl_spark") %in% class(x))) {
    x <- get_schema(x)
  }
  
  if(any(c("data.frame", "tbl_spark") %in% class(y))) {
    y <- get_schema(y)
  }
  
  # Schema Compare
  schema_compare <- purrr::flatten(x) %>%
    tibble::as_tibble() %>%
    tidyr::gather() %>%
    dplyr::left_join(
      purrr::flatten(y) %>%
        tibble::as_tibble() %>%
        tidyr::gather(),
      by = "key")
  
  check <- schema_compare %>% 
    dplyr::filter(value.x != value.y | is.na(value.y))
  
  
  if(validate & nrow(check) > 0) {
    stop(paste("New Data schema check failed:",
               "\n     columns missing:",
               check %>%
                 dplyr::filter(is.na(value.y)) %>%
                 dplyr::pull(key) %>% 
                 paste(collapse = ", "),
               "\n     columns with miss-matching type:",
               check %>%
                 dplyr::filter(!is.na(value.y)) %>%
                 dplyr::pull(key) %>% 
                 paste(collapse = ", ")),
         .call=FALSE)
  }
  
  schema_compare
}