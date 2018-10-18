
# Spark Model Sub-Class Methods --------------------------------------------------


#' Fitted Method for Spark Model Object
#'
#' Extracts fitted values from train_model step
#'
#' @param mobj spark_model object
#' @rdname fitted
#' @export
fitted.spark_model <- function(mobj, prediction_col = "predicted", ...) {
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


#' Get Model Coefficients
#'
#' Extracts coefficient summary from Spark-ML linear regression model
#' @rdname get_coefs
#' @export
get_coefs.ml_model_linear_regression <- function(mobj) {
  
  intercept <- mobj$model$param_map$fit_intercept
  if(intercept) {
    features <- c("Intercept", mobj$.features)
    estimates <- c(mobj$model$intercept, mobj$model$coefficients)
  }else {
    features <- mobj$.features
    estimates <- as.numeric(mobj$model$coefficients)
  }
  
  tibble(feature  = features,
         estimate = estimates,
         stderr   = mobj$summary$coefficient_standard_errors(),
         t_stat   = mobj$summary$t_values,
         p_values = mobj$summary$p_values)
}


#' @rdname get_coefs
#' @export
get_coefs.ml_linear_regression_model <- function(mobj, features) {
  
  intercept <- mobj$param_map$fit_intercept
  if(intercept) {
    features <- c("Intercept", features)
    estimates <- c(mobj$intercept, mobj$coefficients)
  }else {
    estimates <- as.numeric(mobj$coefficients)
  }
  
  tibble(feature  = features,
         estimate = estimates,
         stderr   = mobj$summary$coefficient_standard_errors(),
         t_stat   = mobj$summary$t_values,
         p_values = mobj$summary$p_values)
}


#' @rdname get_variable_importance
#' @export
get_variable_importance.ml_linear_regression_model <- get_coefs.ml_linear_regression_model



#' Get Model Coefficients
#'
#' Extracts coefficient summary from Spark-ML logistic regression model
#' @rdname get_coefs
#' @export
get_coefs.ml_model_logistic_regression <- function(mobj) {
  
  intercept <- mobj$model$param_map$fit_intercept
  if(intercept) {
    features <- c("Intercept", mobj$.features)
    estimates <- c(mobj$model$intercept, mobj$model$coefficients)
  }else {
    features <- mobj$.features
    estimates <- as.numeric(mobj$model$coefficients)
  }
  
  tibble(feature  = features,
         estimate = estimates)
}


#' @rdname get_coefs
#' @export
get_coefs.ml_logistic_regression_model <- function(mobj, features) {
  
  intercept <- mobj$param_map$fit_intercept
  if(intercept) {
    features <- c("Intercept", features)
    estimates <- c(mobj$intercept, mobj$coefficients)
  }else {
    estimates <- as.numeric(mobj$coefficients)
  }
  
  tibble(feature = features,
         estimate = estimates)
}


#' @rdname get_variable_importance
#' @export
get_variable_importance.ml_logistic_regression_model <- get_coefs.ml_logistic_regression_model


#' Get Model Coefficients
#'
#' Extracts coefficient summary from Spark-ML generalized regression model
#' @rdname get_coefs
#' @export
get_coefs.ml_model_generalized_linear_regression <- function(mobj) {
  
  intercept <- mobj$model$param_map$fit_intercept
  if(intercept) {
    features <- c("Intercept", mobj$.features)
    estimates <- c(mobj$model$intercept, mobj$model$coefficients)
  }else {
    features <- mobj$.features
    estimates <- as.numeric(mobj$model$coefficients)
  }
  
  tibble(feature  = features,
         estimate = estimates,
         stderr   = mobj$summary$coefficient_standard_errors(),
         t_stat   = mobj$summary$t_values(),
         p_values = mobj$summary$p_values())
}


#' @rdname get_variable_importance
#' @export
get_variable_importance.ml_decision_tree_regression_model <- function(mobj, features) {
  
  tibble(feature    = features,
         importance = mobj$feature_importances())
}


#' @rdname get_variable_importance
#' @export
get_variable_importance.ml_decision_tree_classification_model <- 
  get_variable_importance.ml_decision_tree_regression_model


#' @rdname get_variable_importance
#' @export
get_variable_importance.ml_random_forest_regression_model <- 
  get_variable_importance.ml_decision_tree_regression_model


#' @rdname get_variable_importance
#' @export
get_variable_importance.ml_random_forest_classification_model <- 
  get_variable_importance.ml_decision_tree_regression_model


#' @rdname get_variable_importance
#' @export
get_variable_importance.ml_gbt_regression_model <- 
  get_variable_importance.ml_decision_tree_regression_model


#' @rdname get_variable_importance
#' @export
get_variable_importance.ml_gbt_classification_model <- 
  get_variable_importance.ml_decision_tree_regression_model
