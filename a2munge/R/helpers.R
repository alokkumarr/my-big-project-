
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
