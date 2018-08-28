

# Samples Object ----------------------------------------------------------

#' Sample Class Constructer function
new_samples <-  function(validation_method,
                         validation_args,
                         test_holdout_prct,
                         test_holdout_method,
                         downsample_prct,
                         train_indicies,
                         validation_indicies,
                         indicies_names,
                         test_index) {
  checkmate::assert_character(validation_method, len = 1)
  checkmate::assert_list(validation_args)
  checkmate::assert_number(test_holdout_prct, lower = 0, upper = 1, null.ok = TRUE)
  checkmate::assert_character(test_holdout_method, len = 1, null.ok = TRUE)
  checkmate::assert_number(downsample_prct, lower = 0, upper = 1, null.ok = TRUE)
  checkmate::assert_list(train_indicies, unique = TRUE)
  checkmate::assert_list(validation_indicies, unique = TRUE, null.ok = TRUE)
  checkmate::assert_character(indicies_names, unique = TRUE)
  checkmate::assert_numeric(test_index, null.ok = TRUE)

  structure(
    list(
      validation_method = validation_method,
      validation_args = validation_args,
      test_holdout_prct = test_holdout_prct,
      test_holdout_method = test_holdout_method,
      downsample_prct = downsample_prct,
      train_indicies = train_indicies,
      validation_indicies = validation_indicies,
      indicies_names = indicies_names,
      test_index = test_index
    ),
    class = "samples"
  )
}

#' Samples Class Validation function
valid_samples <- function(x){

  if (!is.null(x$test_holdout_prct)) {
    if (x$test_holdout_prct > .5) {
      message("Test holdout pecentage greater than 50%. This is larger than usual. Recommend 20%.")
    }
  }

  if (!is.null(x$downsample_prct)) {
    if (x$downsample_prct > .5) {
      message(
        "Downsample pecentage greater than 50%. Downsampling reduces the total data modelled and evaluated.",
        "\n50% is rather large - make sure there is sufficient data to do so."
      )
    }
  }

  if(x$validation_method != "none" & length(x$train_indicies) != length(x$validation_indicies)){
    stop("Numbers of training and validation data indicies don't match")
  }

  if(length(x$train_indicies) != length(x$indicies_names)){
    stop("Indicies names length doesn't match training indicies")
  }

  if(is.null(x$test_holdout_prct) & (! is.null(x$test_index))){
    stop("Test holdout index created when none configured")
  }

  x
}


#' Sample Class Helper function
#'
#' Creates a valid object of samples class
#'
#' @param validation_method name of validation method. examples are holdout,
#'   cross-validation, etc...
#' @param validation_args list of arguments to pass to validation method
#' @param test_holdout_prct percent of total data records to use for test
#'   dataset
#' @param downsample_prct percent of total data to downsample and use for
#'   analysis
#' @param train_indicies list of numeric train indicies. each index contains
#'   numeric vector pertaining to row numbers to use for model fitting
#' @param validation_indicies list of numeric validation indicies. each index contains
#'   numeric vector pertaining to row numbers to use for model validation
#' @param indicies_names character vector of indicies names
#' @param test_index  numeric index with records to use for model testing   
samples <- function(validation_method,
                    validation_args,
                    test_holdout_prct,
                    test_holdout_method,
                    downsample_prct,
                    train_indicies,
                    validation_indicies,
                    indicies_names,
                    test_index) {
  valid_samples(
    new_samples(
      validation_method,
      validation_args,
      test_holdout_prct,
      test_holdout_method,
      downsample_prct,
      train_indicies,
      validation_indicies,
      indicies_names,
      test_index
    )
  )
}



#' Get Training Samples
#'
#' Function to extract training sample indicies from samples object
#'
#' @param obj object with valid samples object
#' @param ... additional parameters. currently not used
#'
#' @return list of train indicies
#' @export
#'
#' @examples
#' # Simple example
#' library(dplyr)
#' mtcars %>%
#' add_holdout_samples(., splits = c(.8, .2)) %>%
#' get_train_samples(.)
get_train_samples <- function(obj){
  UseMethod("get_train_samples")
}

#' @rdname get_train_samples
#' @export
get_train_samples.samples <- function(obj){
  obj$train_indicies
}

#' @rdname get_train_samples
#' @export
get_train_samples.modeler <- function(obj){
  get_train_samples(obj$samples)
}


#' Get Validation Samples
#'
#' Function to extract validation sample indicies from samples object
#'
#' @param obj object with valid samples object
#' @param ... additional parameters. currently not used
#'
#' @return list of validation indicies
#' @export
#'
#' @examples
#' # Simple example
#' library(dplyr)
#' mtcars %>%
#' add_holdout_samples(., splits = c(.8, .2)) %>%
#' get_validation_samples(.)
get_validation_samples <- function(obj){
  UseMethod("get_validation_samples")
}

#' @rdname get_validation_samples
#' @export
get_validation_samples.samples <- function(obj){
  obj$validation_indicies
}

#' @rdname get_validation_samples
#' @export
get_validation_samples.modeler <- function(obj){
  get_validation_samples(obj$samples)
}


#' Get Test Sample
#'
#' Function to extract test sample index from samples object
#'
#' @param obj object with valid samples object
#' @param ... additional parameters. currently not used
#'
#' @return test index
#' @export
#'
#' @examples
#' # Simple example
#' library(dplyr)
#' mtcars %>%
#' add_holdout_samples(., splits = c(.6, .2, .2)) %>%
#' get_test_samples(.)
get_test_samples <- function(obj){
  UseMethod("get_test_samples", obj)
}

#' @rdname get_test_samples
#' @export
get_test_samples.samples <- function(obj){
  obj$test_index
}

#' @rdname get_test_samples
#' @export
get_test_samples.modeler <- function(obj){
  get_test_samples(obj$samples)
}



#' Get Sample Indicies
#'
#' Function to extract the train and validation indicies pairs
#'
#' Returns a list with an element for each train and validation indicies pairs.
#' Each pair used in model evaluation step where a model is fit to the train
#' index and evaluated on the predictions made on the validation index
#'
#' @param obj modeler object
#'
#' @return list with train and validation indicies pairs
#' @export
#'
#' @examples
#' # Simple example
#' library(dplyr)
#' mtcars %>%
#' add_holdout_samples(., splits = c(.6, .2, .2)) %>%
#' get_indicies(.)
get_indicies <- function(obj){
  UseMethod("get_indicies", obj)
}


#' @rdname get_indicies
#' @export
get_indicies.samples <- function(obj){
  n <- length(obj$indicies_names)
  indicies <- vector("list", length = n)
  for(i in 1:n){
    indicies[[i]] <- list(train = get_train_samples(obj)[[i]],
                        validation = get_validation_samples(obj)[[i]])
    names(indicies)[[i]] <- obj$indicies_names[i]
  }
  indicies
}


#' @rdname get_indicies
#' @export
get_indicies.modeler <- function(obj){
  get_indicies(obj$samples)
}



# default_samples ---------------------------------------------------------


#' Add Default Samples function
#'
#' Function creates a no-frills sampling object. Creates a single train index
#' only. No validation or test indicies created.
#'
#' Function used when modeler object created as a place holder for modeler
#' object. Default sampling can be used for simple out of the box models.
#'
#' @param x numeric vector, data.frame, spark dataframe or modeler object to
#'   create samples from
#' @export
add_default_samples <- function(x){
  UseMethod("add_default_samples", x)
}


#' @rdname add_default_samples
#' @export
add_default_samples.numeric <- function(x){
  samples(
    validation_method = "none",
    validation_args = list(),
    test_holdout_prct = NULL,
    test_holdout_method = "none",
    downsample_prct = NULL,
    train_indicies = list(default = x),
    validation_indicies = NULL,
    indicies_names = "train",
    test_index = NULL
  )
}


#' @rdname add_default_samples
#' @export
add_default_samples.data.frame <- function(x){
  z <- 1:nrow(x)
  add_default_samples(z)
}


#' @rdname add_default_samples
add_default_samples.tbl_spark <- function(x){
  z <- 1:sparklyr::sdf_nrow(x)
  add_default_samples(z)
}


#' @rdname add_default_samples
#' @export
add_default_samples.modeler <- function(x){

  default <- add_default_samples(x$data)
  x$samples <- default
  x
}


# holdout_samples ---------------------------------------------------------


#' Add Holdout Samples function
#'
#' Function creates a new samples object with holdout samples based on
#' configuration
#'
#' Holdout samples take sequential row indicies. Holdout samples are used
#' primarily in forecasting applications but can be used for other modeling
#' applications
#'
#'
#' @param x numeric vector, data.frame, spark dataframe, or modeler object to
#'   create holdout samples from
#' @param splits numeric vector of holdout splits. Values need to sum to 1. Each
#'   value represents the amount of data partitioned. Order matters - the splits
#'   correspond to train/validation/test*. Test value optional.
#'
#' @export
#' @return Returns updated modeler object if modeler object provided otherwise
#' returns samples object
#'
#' @examples
#'
#' # Data.frame example
#' add_holdout_samples(mtcars, splits = c(.8, .2))
add_holdout_samples <- function(x, splits) {
  UseMethod("add_holdout_samples")
}


#' @rdname add_holdout_samples
#' @export
add_holdout_samples.numeric <- function(x, splits){

  checkmate::assert_numeric(splits, lower = 0, upper = 1, min.len = 2, max.len = 3)
  if(sum(splits) != 1){
    stop("splits don't sum to 1. Recommend either an 80-20 or 60-20-20 split")
  }

  if(length(splits) == 3){
    tv_total <- sum(splits[-3])
    s1 <- holdout(x, tv_total)
    test_index <- s1$tail
    test_holdout_prct <- splits[3]
  }else{
    test_index <- NULL
    test_holdout_prct <- NULL
  }
  s2 <- holdout(x, splits[1])

  train_indicies <- list(holdout = s2$head)
  val_indicies <- list(holdout = setdiff(s2$tail, test_index))

  samples(
    validation_method = "holdout",
    validation_args = list(split = splits[1]),
    test_holdout_prct = test_holdout_prct,
    test_holdout_method = "holdout",
    downsample_prct = NULL,
    train_indicies = train_indicies,
    validation_indicies = val_indicies,
    indicies_names = "holdout",
    test_index = test_index
  )
}

#' @rdname add_holdout_samples
#' @export
add_holdout_samples.integer <- add_holdout_samples.numeric


#' @rdname add_holdout_samples
#' @export
add_holdout_samples.data.frame <- function(x, splits){
  z <- 1:nrow(x)
  add_holdout_samples(z, splits)
}


#' @rdname add_holdout_samples
#' @export
add_holdout_samples.tbl_spark <- function(x, splits){
  z <- 1:sparklyr::sdf_nrow(x)
  add_holdout_samples(z, splits)
}


#' @rdname add_holdout_samples
#' @export
add_holdout_samples.modeler <- function(x, splits){

  holdout_samples <- add_holdout_samples(x$data, splits)
  x$samples <- holdout_samples
  x
}



#' Holdout Sampling function
#'
#' Function creates two row indicies from a dataset based on split parameter.
#' Indicies used to create data samples
#'
#' @param x numeric vector, dataframe, spark dataframe, or modeler object to
#'   create samples from
#' @param split numeric input for percentage of rows in head index
#' @export
holdout <- function(x, split) {
  UseMethod("holdout")
}


#' @rdname holdout
#' @export
holdout.numeric <- function(x, split){

  checkmate::assert_numeric(x, any.missing = FALSE)
  checkmate::assert_number(split, lower = 0, upper= 1)

  n <- length(x)
  h <- floor(n * split)
  list(head = 1:h, tail = (h+1):n)
}


#' @rdname holdout
#' @export
holdout.data.frame <- function(x, split){
  z <- 1:nrow(x)
  holdout(z, split)
}


#' @rdname holdout
#' @export
holdout.tbl_spark <- function(x, split){
  z <- 1:sparklyr::sdf_nrow(x)
  holdout(z, split)
}


# random_samples ----------------------------------------------------------


#' Random Sample function
#'
#' Function creates a random sample index from a dataset. Index can be used to
#' create data samples
#'
#' @param x numeric vector, dataframe, spark dataframe, or modeler object to
#'   create samples from
#' @param number number of resamples to create
#' @param amount percent of data to randomly sample
#' @param seed optional input for setting random seed
#' @export
resample <- function(x, number, amount, seed) {
  UseMethod("resample")
}


#' @rdname resample
#' @export
resample.numeric <- function(x, number, amount, seed = NULL){

  checkmate::assert_numeric(x, any.missing = FALSE)
  checkmate::assert_number(amount, lower = 0, upper= 1)
  checkmate::assert_number(seed, lower = 0, null.ok = TRUE)

  set.seed(seed)
  n <- length(x)
  amt <- floor(n*amount)
  indicies <- replicate(number, sample(1:n, amt, replace = FALSE), simplify = FALSE)
  names(indicies) <- paste0("resample", 1:number)
  indicies
}


#' @rdname resample
#' @export
resample.data.frame <- function(x, number, amount, seed = NULL){
  z <- 1:nrow(x)
  resample(z, number, amount, seed)
}


#' @rdname resample
#' @export
resample.tbl_spark <- function(x, number, amount, seed = NULL){
  z <- 1:sparklyr::sdf_nrow(x)
  resample(z, number, amount, seed)
}



#' Add Random Resamples function
#'
#' Function creates a new samples object with random resamples based on
#' configuration. Each index is random sample of the dataset
#'
#' The function creates train and validation pairs using all of the dataset rows
#'
#'
#' @param x numeric vector, dataframe, spark dataframe, or modeler object to
#'   create samples from
#' @param number number of random resamples
#' @param amount amount of data used for train sample. 1-amount used for
#'   validation sample.
#' @param test_holdout_prct amount of data to holdout out for test sample
#' @param seed random seed generator
#'
#' @return either an updated modeler object if provided otherwise a samples
#'   object with resamples
#' @export
#'
#' @examples
#'
#' # Data.frame example
#' add_resample_samples(mtcars, number = 5, amount = .5)
add_resample_samples <- function(x, number, amount, test_holdout_prct, seed){
  UseMethod("add_resample_samples")
}


#' @rdname add_resample_samples
#' @export
add_resample_samples.numeric <- function(x, number, amount, test_holdout_prct = NULL, seed = NULL){

  if(number > 100){
    warning("Woah - that's a lot of resamples there. Do you really need that many???")
  }

  if(! is.null(test_holdout_prct)){
    s1 <- resample(x, number = 1, amount = test_holdout_prct, seed)
    test_index <- s1[[1]]
    x1 <- x[-test_index]
  }else{
    test_index <- NULL
    x1 <- x
  }
  s2 <- resample(x1, number, amount, seed)

  train_indicies <- s2
  val_indicies <- lapply(s2, function(s) x1[-s])

  samples(
    validation_method = "resample",
    validation_args = list(number = number, amount = amount, seed = seed),
    test_holdout_prct = test_holdout_prct,
    test_holdout_method = "resample",
    downsample_prct = NULL,
    train_indicies = train_indicies,
    validation_indicies = val_indicies,
    indicies_names = names(train_indicies),
    test_index = test_index
  )
}


#' @rdname add_resample_samples
#' @export
add_resample_samples.data.frame <- function(x, number, amount, test_holdout_prct = NULL, seed = NULL){
  z <- 1:nrow(x)
  add_resample_samples(z, number, amount, test_holdout_prct, seed)
}


#' @rdname add_resample_samples
#' @export
add_resample_samples.tbl_spark <- function(x, number, amount, test_holdout_prct = NULL, seed = NULL){
  z <- 1:sparklyr::sdf_nrow(x)
  add_resample_samples(z, number, amount, test_holdout_prct, seed)
}



#' @rdname add_resample_samples
#' @export
add_resample_samples.modeler <- function(x, number, amount, test_holdout_prct = NULL, seed = NULL){

  resample_samples <- add_resample_samples(x$data, number, amount, test_holdout_prct, seed)
  x$samples <- resample_samples
  x
}


#' @rdname add_resample_samples
#' @export
add_resample_samples.forecaster <- function(x, number, amount, test_holdout_prct = NULL, seed = NULL){

  stop("Resamples not appropriate for forecasting use cases.",
       "\n  Use either default, holdout or time_slices samples. ")
}



# time_slice_samples ------------------------------------------------------


#' Time Slice Sample function
#'
#'
#' @param x numeric vector, dataframe, spark dataframe, or modeler object to
#'   create samples from
#' @param width number of training sample periods
#' @param horizon number of test sample periods
#' @param skip number of samples skipped. used to thin sample pairs
#' @param fixed_width logical option to keep all training widths the same.
#'   default is TRUE
#' @export
#' @note similiar in design to caret's createTimeSlices function
#'   \url{https://github.com/topepo/caret/blob/master/pkg/caret/R/createDataPartition.R}
#'
time_slice <- function(x, width, horizon, skip, fixed_width) {
  UseMethod("time_slice")
}



#' @rdname time_slice
#' @export
time_slice.numeric <- function(x,
                               width,
                               horizon,
                               skip = 0,
                               fixed_width = TRUE) {
  n <- length(x)
  checkmate::assert_numeric(x, any.missing = FALSE)
  checkmate::assert_number(width, lower = 1, upper = n - 1)
  checkmate::assert_number(horizon, lower = 1, upper = n - width)
  checkmate::assert_number(skip, lower = 0, upper = n - 1)
  checkmate::assert_flag(fixed_width)
  
  stops <- seq(width, (n - horizon), by = skip + 1)
  
  if (fixed_width) {
    starts <- stops - width + 1
  } else {
    starts <- rep(1, length(stops)) # all start at 1
  }
  
  train <- mapply(seq, starts, stops, SIMPLIFY = FALSE)
  validation <-
    mapply(seq, stops + 1, stops + horizon, SIMPLIFY = FALSE)
  labels <- paste("slice", gsub(" ", "0", format(stops)), sep = "")
  names(train) <- labels
  names(validation) <- labels
  
  indicies <- list(train = train, validation = validation)
  indicies
}

#' @rdname time_slice
#' @export
time_slice.integer <- time_slice.numeric


#' @rdname time_slice
#' @export
time_slice.data.frame <- function(x, width, horizon, skip = 0, fixed_width = TRUE){
  z <- 1:nrow(x)
  time_slice(z, width, horizon, skip, fixed_width)
}


#' @rdname time_slice
#' @export
time_slice.tbl_spark <- function(x, width, horizon, skip = 0, fixed_width = TRUE){
  z <- 1:sparklyr::sdf_nrow(x)
  time_slice(z, width, horizon, skip, fixed_width)
}



#' Add Time Slice function
#'
#' Function creates a new samples object with time slice resamples based on
#' configuration. Each index is time slice sample of the dataset
#'
#' The function creates train and validation pairs
#'
#' @inheritParams time_slice
#'
#' @return either an updated modeler object if provided otherwise a samples
#'   object with resamples
#' @export
#'
#' @examples
#'
#' # Basic Time Slice example
#' add_time_slice_samples(1:20, width = 15, horizon = 1)
#'
#' # Time Slice with Skip
#'  add_time_slice_samples(1:20, width = 15, horizon = 1, skip = 1)
add_time_slice_samples <- function(x, width, horizon, skip = 0, fixed_width = TRUE){
  UseMethod("add_time_slice_samples")
}


#' @rdname add_time_slice_samples
#' @export
add_time_slice_samples.numeric <- function(x, width, horizon, skip = 0, fixed_width = TRUE){

  ts1 <- time_slice(x, width, horizon, skip, fixed_width)

  train_indicies <- ts1$train
  val_indicies <- ts1$validation

  samples(
    validation_method = "time_slice",
    validation_args = list(width = width, horizon = horizon,
                           skip = skip, fixed_width = fixed_width),
    test_holdout_prct = 0,
    test_holdout_method = "none",
    downsample_prct = NULL,
    train_indicies = train_indicies,
    validation_indicies = val_indicies,
    indicies_names = names(train_indicies),
    test_index = NULL
  )
}



#' @rdname add_time_slice_samples
#' @export
add_time_slice_samples.data.frame <- function(x,
                                              width,
                                              horizon,
                                              skip = 0,
                                              fixed_width = TRUE) {
  z <- 1:nrow(x)
  add_time_slice_samples(
    z,
    width = width,
    horizon = horizon,
    skip = skip,
    fixed_width = fixed_width
  )
}


#' @rdname add_time_slice_samples
#' @export
add_time_slice_samples.tbl_spark <- function(x,
                                             width,
                                             horizon,
                                             skip = 0,
                                             fixed_width = TRUE) {
  z <- 1:sparklyr::sdf_nrow(x)
  add_time_slice_samples(
    z,
    width = width,
    horizon = horizon,
    skip = skip,
    fixed_width = fixed_width
  )
}


#' @rdname add_time_slice_samples
#' @export
add_time_slice_samples.forecaster <- function(x,
                                              width,
                                              horizon,
                                              skip = 0,
                                              fixed_width = TRUE) {
  time_slice_samples <-
    add_time_slice_samples(
      x$data,
      width = width,
      horizon = horizon,
      skip = skip,
      fixed_width = fixed_width
    )
  x$samples <- time_slice_samples
  x
}





# cross_validation_samples ------------------------------------------------



#' Cross Validation Sample function
#'
#' Function creates a cross validation sample index from a dataset. Index can be used to
#' create data samples
#'
#' @param x numeric vector, dataframe, spark dataframe, or modeler object to
#'   create samples from
#' @param folds number of cross validation folds
#' @export
cross_validation <- function(x, folds) {
  UseMethod("cross_validation")
}


#' @rdname cross_validation
#' @export
cross_validation.numeric <- function(x, folds){

  checkmate::assert_numeric(x, any.missing = FALSE)
  checkmate::assert_number(folds, lower = 2)

  n <- length(x)
  seq_by <- floor(n/folds)
  begin <- floor(seq(1, n, by = n/folds))
  end <- begin + seq_by -1
  end[folds] <- n

  validation <- purrr::map2(begin, end, function(x, y) x:y)
  train <- purrr::map(validation, function(x, index) {index[-x]}, index = x)
  names(validation) <- paste0("fold", 1:length(validation))
  names(train) <- paste0("fold", 1:length(train))

  indicies <- list(train = train, validation = validation)
  indicies
}


#' @rdname cross_validation
#' @export
cross_validation.data.frame <- function(x, folds){
  z <- 1:nrow(x)
  cross_validation(z, folds)
}


#' @rdname cross_validation
#' @export
cross_validation.tbl_spark <- function(x, folds){
  z <- 1:sparklyr::sdf_nrow(x)
  cross_validation(z, folds)
}



#' Add Cross Validation Samples function
#'
#' Function creates a new cross_validation sample object based on configuration.
#' Each index is fold of the dataset
#'
#' The function creates train and validation pairs using all of the dataset rows
#'
#'
#' @param x numeric vector, dataframe, spark dataframe, or modeler object to
#'   create samples from
#' @param folds number of cross validation folds
#' @param test_holdout_prct amount of data to holdout out for test sample
#' @param seed optional random seed generator
#'
#' @return either an updated modeler object if provided otherwise a samples
#'   object with resamples
#' @export
#'
#' @examples
#'
#' # Data.frame example
#' add_cross_validation_samples(mtcars, folds = 4)
add_cross_validation_samples <- function(x, folds, test_holdout_prct, seed){
  UseMethod("add_cross_validation_samples")
}


#' @rdname add_cross_validation_samples
#' @export
add_cross_validation_samples.numeric <- function(x, folds, test_holdout_prct = NULL, seed = NULL){


  if(! is.null(test_holdout_prct)){
    s1 <- resample(x, number = 1, amount = test_holdout_prct, seed)
    test_index <- s1[[1]]
    x1 <- x[-test_index]
  }else{
    test_index <- NULL
    x1 <- x
  }
  s2 <- cross_validation(x1, folds)

  train_indicies <- s2$train
  val_indicies <- s2$validation

  samples(
    validation_method = "cross_validation",
    validation_args = list(folds = folds),
    test_holdout_prct = test_holdout_prct,
    test_holdout_method = "cross_validation",
    downsample_prct = NULL,
    train_indicies = train_indicies,
    validation_indicies = val_indicies,
    indicies_names = names(train_indicies),
    test_index = test_index
  )
}


#' @rdname add_cross_validation_samples
#' @export
add_cross_validation_samples.data.frame <- function(x, folds, test_holdout_prct = NULL, seed = NULL){
  z <- 1:nrow(x)
  add_cross_validation_samples(z, folds, test_holdout_prct, seed)
}


#' @rdname add_cross_validation_samples
#' @export
add_cross_validation_samples.tbl_spark <- function(x, folds, test_holdout_prct = NULL, seed = NULL){
  z <- 1:sparklyr::sdf_nrow(x)
  add_cross_validation_samples(z, folds, test_holdout_prct, seed)
}



#' @rdname add_cross_validation_samples
#' @export
add_cross_validation_samples.modeler <- function(x, folds, test_holdout_prct = NULL, seed = NULL){

  cross_validation_samples <- add_cross_validation_samples(x$data, folds, test_holdout_prct, seed)
  x$samples <- cross_validation_samples
  x
}


#' @rdname add_cross_validation_samples
#' @export
add_cross_validation_samples.forecaster <- function(x, folds, test_holdout_prct = NULL, seed = NULL){

  stop("Cross Validation not appropriate for forecasting use cases.",
       "\n  Use either default, holdout or time_slices samples. ")
}


