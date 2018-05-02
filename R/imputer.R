
is_na <- function(x) any(is.na(x))


# impute mean -------------------------------------------------------------



impute_mean <- function(df, measure_vars = NULL) {
  checkmate::assert_choice(class(df), c("data.frame", "tbl_spark"))

  if(is.null(measure_vars)){
    measure_vars <- df %>%
      select_if(is_na) %>%
      colnames()
  }

  impute_mean_at(df, measure_vars)
}


impute_mean_at <- function(df, measure_vars) {
  checkmate::assert_choice(class(df), c("data.frame", "tbl_spark"))
  numeric_vars <- df %>%
    dplyr::select_if(is.numeric) %>%
    colnames()

  checkmate::assert_subset(measure_vars, numeric_vars)

  df %>%
    dplyr::mutate_at(measure_vars,
                     dplyr::funs(ifelse(is.na(.), mean(., na.rm = TRUE), .)))
}


# impute constant ---------------------------------------------------------


impute_constant <- function(df, measure_vars = NULL, fill = 0) {
  checkmate::assert_choice(class(df), c("data.frame", "tbl_spark"))
  if(is.null(measure_vars)){
    measure_vars <- df %>%
      select_if(is_na) %>%
      colnames()
  }
  fill_type <- class(fill)
  numeric_vars <- df %>%
    dplyr::select_at(measure_vars) %>%
    dplyr::select_if(is.numeric) %>%
    colnames()

  if(fill_type %in% c("numeric", "integer")) {

    impute_constant_at_num(df, numeric_vars, fill)
  }else{
    char_vars <- setdiff(measure_vars, numeric_vars)
    impute_constant_at_chr(df, char_vars, fill)
  }
}


impute_constant_at_num <- function(df, measure_vars, fill) {
  checkmate::assert_subset(measure_vars, colnames(df))
  checkmate::assert_number(fill)
  df %>%
    dplyr::mutate_at(measure_vars, funs(ifelse(is.na(.), fill, .)))
}


impute_constant_at_chr <- function(df, measure_vars, fill) {
  UseMethod("impute_constant_at_chr")
}

impute_constant_at_chr.data.frame <- function(df, measure_vars, fill) {
  checkmate::assert_subset(measure_vars, colnames(df))
  checkmate::assert_character(fill)

  df %>%
    dplyr::mutate_at(measure_vars, funs(ifelse(is.na(.), fill, .)))
}


impute_constant_at_chr.tbl_spark <- function(df, measure_vars, fill) {
  checkmate::assert_subset(measure_vars, colnames(df))
  checkmate::assert_character(fill)

  df %>%
    dplyr::mutate_at(measure_vars, funs(ifelse(. == "NA", fill, .)))
}




# impute mode -------------------------------------------------------------


impute_mode <- function(df, measure_vars = NULL) {
  checkmate::assert_choice(class(df), c("data.frame", "tbl_spark"))
  if(is.null(measure_vars)) {
    measure_vars <- df %>%
      select_if(is_na) %>%
      colnames()
  }

  impute_mode_at(df, measure_vars)
}


impute_mode_at <- function(df, measure_vars) {
  checkmate::assert_choice(class(df), c("data.frame", "tbl_spark"))
  checkmate::assert_subset(measure_vars, colnames(df))
  for(mv in measure_vars) {
    fill <- df %>%
      count(!! rlang::sym(mv), sort=TRUE) %>%
      na.omit() %>%
      head(1) %>%
      select_at(mv) %>%
      collect()
    df <- impute_constant(df, mv, fill[[1]])
  }
  df
}


