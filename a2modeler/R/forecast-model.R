
# Forecast Model Class Methods --------------------------------------------


#' @importFrom foreach foreach %dopar%
#' @rdname train_model
#' @export
train_model.forecast_model <- function(mobj, 
                                       data,
                                       measure,
                                       samples,
                                       save_submodels,
                                       execution_strategy,
                                       level){
  
  checkmate::assert_class(data, "data.frame")
  checkmate::assert_class(measure, "measure")
  checkmate::assert_class(samples, "samples")
  checkmate::assert_logical(save_submodels)
  checkmate::assert_numeric(level, max.len = 2, upper = 100)
  
  # Set Execution Strategy
  future::plan(obj$execution_strategy)
  doFuture::registerDoFuture()
  
  # Set model function
  model_fun <- get(mobj$method, asNamespace(mobj$package))
  
  # Get Indicies
  indicies <- get_indicies(samples)
  
  # Submodel grid
  submodel_grid <- data.frame(expand.grid(c(mobj$method_args,
                                            mobj$param_map,
                                            list(method = mobj$method)))) %>%
    dplyr::mutate(submodel_uid = purrr::map_chr(1:n(), ~ sparklyr::random_string("submodel")),
                  method = as.character(method))
  grid_vars <- setdiff(colnames(submodel_grid), c("submodel_uid"))
  
  # Define param_grid including samples
  param_grid <- data.frame(expand.grid(c(
    mobj$method_args,
    mobj$param_map,
    list(index = samples$indicies_names)
  )))
  param_vars <- colnames(param_grid)[-ncol(param_grid)]
  
  # Define packages for parallel computation
  packs <- model_methods %>%
    dplyr::filter(type == obj$type) %>%
    dplyr::pull(package) %>%
    c("a2modeler", "dplyr") %>%
    unique()
  
  # Performance Function:
  perf_fun <- match.fun(measure$method)
  
  # Fit submodel to train data for each sample
  fits <- foreach(
    rn = 1:nrow(param_grid),
    .packages = packs,
    .export = c("mobj", "data", "param_grid", "indicies", "measure", "level"),
    .errorhandling = "pass") %dopar% {
      
      # Params
      params <- as.list(param_grid[rn, param_vars, drop=FALSE])
      
      # Get Training Sample
      index <- as.character(param_grid[["index"]][rn])
      train_index <- indicies[[index]]$train
      train_smpl <- data[train_index, , drop=FALSE]
      
      # Set Method Args
      y <- as.numeric(train_smpl[[mobj$target]])
      x_vars <- setdiff(colnames(data), c(mobj$target, mobj$index_var))
      if (length(x_vars) > 0) {
        train_xreg <- train_smpl[, x_vars, drop = FALSE]
      } else {
        train_xreg <- NULL
      }
      args <- modifyList(params, c(list(y = y, xreg = train_xreg)))
      
      # Fit submodel to training sample
      fit <- do.call(model_fun, args)
      
      if(! is.null(indicies[[index]]$validation)) {
        
        # Predict each validation sample
        val_index <- indicies[[index]]$validation
        val_smpl <- data[val_index, , drop=FALSE]
        
        # Set Forecast Args
        if (length(x_vars) > 0) {
          val_xreg <- val_smpl[, x_vars, drop = FALSE]
        } else{
          val_xreg <- NULL
        }
        args <- c(list(
          object = fit,
          xreg = val_xreg,
          h = length(val_index),
          level = level
        ),
        params)
        fun <- get("forecast", asNamespace("forecast"))
        fcast <- get_forecasts(do.call(fun, args))
        
        # evalulate performance
        perf <- data.frame(
          perf_fun(cbind(fcast, val_smpl[, mobj$target, drop=FALSE]),
                   predicted = "mean",
                   actual = mobj$target),
          index = index,
          sample = "validation"
        )
        names(perf)[1] <- measure$method
        fit$performance <- perf
        fit$predictions <- fcast
      } else {
        
        perf <- data.frame(
          perf_fun(cbind(data.frame(mean = as.numeric(fit$fitted)),
                         train_smpl[, mobj$target, drop=FALSE]),
                   predicted = "mean",
                   actual = mobj$target),
          index = index,
          sample = "train"
        )
        names(perf)[1] <- measure$method
        fit$performance <- perf
      }
      fit$method_args <- params
      fit
    }
  
  
  # Calculate Final Performance
  fit_grid <- purrr::map_df(fits, "performance") %>%
    dplyr::bind_cols(purrr::map_df(fits, "method_args")) %>% 
    dplyr::mutate(method = mobj$method) %>% 
    dplyr::inner_join(submodel_grid, by = grid_vars) %>% 
    dplyr::select(-method)
  
  performance <- fit_grid %>% 
    dplyr::group_by_at(setdiff(colnames(fit_grid), c(measure$method, "index"))) %>%
    dplyr::summarise_at(measure$method, mean) %>% 
    dplyr::ungroup() 
  
  if(any(colnames(performance) %in% grid_vars)) {
    performance <- performance %>% 
      tidyr::nest(-submodel_uid, -sample, -!!measure$method, .key = "param_grid") 
  }else {
    performance <- performance %>% 
      dplyr::mutate(param_grid = vector("list", 1))
  }
  
  mobj$performance <- performance %>% 
    dplyr::select(submodel_uid, sample, !!measure$method, param_grid)
  
  # select best model
  best_submodel <- mobj$performance %>%
    dplyr::arrange_at(measure$method,
                      .funs = ifelse(measure$minimize, identity, dplyr::desc)) %>%
    head(1)
  
  # Refit on full sample
  if(is.null(indicies$validation)) {
    
    mobj$fit <- fits[[1]]
  }else {
    
    params <- best_submodel$param_grid[[1]] %>% as.list()
    y <- as.numeric(train_smpl[[mobj$target]])
    if (length(x_vars) > 0) {
      xreg <- data[, x_vars, drop = FALSE]
    } else{
      xreg <- NULL
    }
    args <- c(list(y = y, xreg = xreg), params)
    mobj$fit <- do.call(model_fun, args)
    
    # save submodels option
    if(save_submodels) {
      
      sub_models <- list()
      for(uid in submodel_grid$submodel_uid) {
        sub_model <- purrr::keep(fits, fit_grid$submodel_uid == uid)
        names(sub_model) <- samples$indicies_names
        sub_models[[uid]] <- sub_model
      }
      
      mobj$sub_models <- sub_models
    }
  }
 
  mobj
}


#' @param periods forecast horizon length
#' @rdname evaluate_model
#' @export
evaluate_model.forecast_model <- function(mobj,
                                          data,
                                          periods,
                                          measure,
                                          prediction_col = "predicted",
                                          ...){
  
  checkmate::assert_true(! is.null(mobj$fit))
  checkmate::assert_class(data, "data.frame")
  checkmate::assert_class(measure, "measure")
  
  # Measure Fun
  measure_fun <- match.fun(measure$method)
  
  ### Needs work - need to extract y var and any covariates ###
  
  # Make Predictions 
  predictions <- predict(mobj, data, periods, prediction_col) %>% 
    dplyr::bind_cols(data %>% select(!!mobj$target))
  
  # Calculate Performance
  performance <- predictions %>%
    measure_fun(predicted = prediction_col, actual = mobj$target)
  
  mobj$test_performance <- tibble::tibble(!! measure$method := performance)
  mobj$test_predictions <- predictions
  mobj$status <- "evaluated"
  mobj$last_updated <- Sys.time()
  mobj
}



#' Forecast Prediction Method
#' @rdname predict
#' @export
predict.forecast_model <- function(mobj,
                                   data = NULL,
                                   periods,
                                   level = c(80, 95)) {
  if (!is.null(data)) {
    if (nrow(data) != periods) {
      warning("number of data rows doesn't match forecast periods")
    }

    x_vars <- setdiff(colnames(data), c(mobj$target, mobj$index_var))
    if (length(x_vars) > 0) {
      xreg <- data[, x_vars, drop = FALSE]
    } else {
      xreg <- NULL
    }
  } else{
    xreg <- NULL
  }

  fun <- get("forecast", asNamespace("forecast"))
  f <- do.call(fun,
               modifyList(
                 mobj$method_args,
                 list(
                   object = mobj$fit,
                   xreg = xreg,
                   h = periods,
                   level = level
                 )
               ))
  get_forecasts(f)
}



#' Forecast Model Fitted Method
#'
#' Extracts fitted values from train step
#'
#' @rdname fitted
#' @export
fitted.forecast_model <- function(mobj) {
  as.numeric(fitted(mobj$fit))
}


#' @export
#' @rdname summary
summary.forecast_model <- function(mobj){
  mobj$fit
}


#' @export
#' @rdname print
print.forecast_model <- function(mobj){
  mobj$fit
}


#' Get Coefficients from Forecast Model Object
#'
#' @rdname get_coefs
#' @export
get_coefs.forecast_model <- function(mobj){
  coef(mobj$fit)
}



