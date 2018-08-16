
# Spark Model Sub-Class Methods --------------------------------------------------


#' Fitted Method for Spark Model Object
#'
#' Extracts fitted values from train_model step
#'
#' @param mobj spark_model object
#' @rdname fitted
#' @export
fitted.spark_model <- function(mobj, prediction_col = "predicted") {
  mobj$fit$stages[[2]]$summary$predictions %>%
    dplyr::rename(!!prediction_col := prediction) %>% 
    dplyr::select(!!prediction_col, features)
}





#' Predict Method for Spark-ML Classification Object
#' @rdname predict
#' @export
predict.spark_model_classification <- function(mobj,
                                               data,
                                               prediction_col = "predicted",
                                               ...) {
  checkmate::assert_class(data, "tbl_spark")
  
  sparklyr::ml_predict(mobj$fit, data,  ...) %>%
    sparklyr::sdf_separate_column("probability", into = c("prob_0", "prob_1")) %>% 
    dplyr::select(prediction, prob_0, prob_1) %>% 
    dplyr::rename(!!prediction_col := prediction) %>% 
    dplyr::select(!!prediction_col, prob_0, prob_1)
}


#' Predict Method for Spark-ML Regression Object
#' @rdname predict
#' @export
predict.spark_model_regression <- function(mobj,
                                           data,
                                           prediction_col = "predicted",
                                           ...) {
  checkmate::assert_class(data, "tbl_spark")
  
  sparklyr::ml_predict(mobj$fit, data,  ...) %>%
    dplyr::select(prediction) %>% 
    dplyr::rename(!!prediction_col := prediction) 
}






