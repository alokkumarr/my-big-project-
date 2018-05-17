
expander <- function(df, group_vars, id_vars, order_fun, mode, complete) {
  UseMethod("expander")
}



expander.data.frame <- function(df,
                                group_vars = NULL,
                                id_vars = NULL,
                                order_fun = NULL,
                                mode = "crossing",
                                complete = TRUE) {
  df_names <- colnames(df)
  checkmate::assert_subset(group_vars, df_names, empty.ok = TRUE)
  checkmate::assert_subset(id_vars, df_names, empty.ok = TRUE)
  checkmate::assert_class(order_fun, "fun_list", null.ok = TRUE)
  checkmate::assert_choice(mode, c("crossing", "nesting"))
  checkmate::assert_logical(complete)

  if(! is.null(group_vars)) {
    df <- df %>% dplyr::group_by_at(group_vars)
  }
  mode_fun <- get(mode, asNamespace("tidyr"))

  if(complete) {
   results <- df %>%
      tidyr::complete(., mode_fun(!!!rlang::syms(id_vars)), !!!order_fun)

  } else {
    results <-  df %>%
      tidyr::expand(., mode_fun(!!!rlang::syms(id_vars)), !!!order_fun)
  }

  if(! is.null(group_vars)) {
    results %>% dplyr::ungroup()
  } else {
    results
  }
}

