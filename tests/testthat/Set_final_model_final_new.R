


# Set_final_model Uint Tests -----------------------------------------------------

library(testthat)
library(checkmate)
library(a2modeler)
library(sparklyr)
library(dplyr)

context("Set_final_model- function unit tests")

n <- 100
x <- 1:n
y <- 1:n
df <- data.frame(x = x)

df_y <- data.frame(y = y)

# Create Spark Connection
spark_home_dir <- sparklyr::spark_installed_versions() %>%
  as.data.frame() %>%
  dplyr::filter(spark == "2.3.0") %>%
  dplyr::pull(dir)
sc <- spark_connect(master = "local", spark_home = spark_home_dir)

# Copy data to spark
dat <- copy_to(sc, df, overwrite = TRUE)

dat_y <- copy_to(sc, df_y, overwrite = TRUE)

time <-  Sys.time()

# Copy data to spark
spk_df <- copy_to(sc, mtcars, name = "df", overwrite = TRUE)

# Test 1:Test case for newsegmneter with NULL Pipe ------------------------

f1_seg <- new_segmenter(df = spk_df, name = "test_multi_model") %>%
  add_model(pipe = NULL,
            method = "ml_kmeans",
            desc = "model1-ml_kmeans ") %>%
  add_model(pipe = NULL,
            method = "ml_bisecting_kmeans",
            desc = "model2-ml_bisecting_kmeans") %>%
  add_model(pipe = NULL,
            method = "ml_gaussian_mixture",
            desc = "model4-ml_kmeans2") %>%
  train_models() %>%
  evaluate_models()

final_model <-
  f1_seg %>% set_final_model(method = "best", refit = TRUE)


# Comapre if final model is same as the one with max siloutte -------------


id_val_1 <- (get_models_status(final_model))[1]
id_val_2 <- (get_models_status(final_model))[2]
id_val_3 <- (get_models_status(final_model))[3]

pos <- "selected"

val_1 <- grepl(pos, id_val_1)
val_2 <- grepl(pos, id_val_2)
val_3 <- grepl(pos, id_val_3)

if (val_1 == "TRUE")
{
  id_model <- (get_models_status(f1_seg) %>% names())[1]
} else if (val_2 == "TRUE")
{
  id_model <- (get_models_status(f1_seg) %>% names())[2]
} else{
  id_model <- (get_models_status(f1_seg) %>% names())[3]
}

mod1 <- get_evalutions(final_model)
mx_sil <- max(mod1$silhouette)

max_m <- mod1 %>%
  filter(mod1$silhouette == mx_sil)

model_with_max_silhotte <- max_m$model

test_that("The trained model is the one with max Silhouette", {
  expect_equal(id_model , model_with_max_silhotte)
})


# Test 2:Find final model with a known ID ---------------------------------

id_for_manual <- (get_models_status(f1_seg) %>% names())[1]
final_model_id <-
  f1_seg %>% set_final_model(
    method = "manual",
    id = id_for_manual,
    reevaluate = TRUE,
    refit = TRUE
  )


# Comapre if final model is same as the one with max siloutte -------------

id_val_1 <- (get_models_status(final_model))[1]
id_val_2 <- (get_models_status(final_model))[2]
id_val_3 <- (get_models_status(final_model))[3]

pos <- "selected"

val_1 <- grepl(pos, id_val_1)
val_2 <- grepl(pos, id_val_2)
val_3 <- grepl(pos, id_val_3)

if (val_1 == "TRUE")
{
  id_model <- (get_models_status(f1_seg) %>% names())[1]
} else if (val_2 == "TRUE")
{
  id_model <- (get_models_status(f1_seg) %>% names())[2]
} else{
  id_model <- (get_models_status(f1_seg) %>% names())[3]
}

mod1 <- get_evalutions(final_model)
mx_sil <- max(mod1$silhouette)

max_m <- mod1 %>%
  filter(mod1$silhouette == mx_sil)

model_with_max_silhotte <- max_m$model

test_that("The trained model is the one with max Silhouette", {
  expect_equal(id_model , model_with_max_silhotte)
})


# Test-3:Create Spark DF and test pipeline methods like flow,execute a --------

df <- data.frame(x = x)

# Copy data to spark
dat <- copy_to(sc, df, overwrite = TRUE)

new_seg <- new_segmenter(df = dat, name = "test_multi_model")

df <- data.frame(x = x) %>%
  mutate(index = row_number())

# Copy data to spark
dat <- copy_to(sc, df, overwrite = TRUE)


pipe_sprk <- pipeline (
  expr = function(dat) {
    dat %>%
      mutate (index_r = ifelse(index %% 4 == 0, 1, 0))
  },
  desc = "Example pipeline"
)

# flow and execute output validation --------------------------------------

new_model <- new_seg %>%
  add_model(pipe = pipe_sprk,
            method = "ml_kmeans",
            desc = "model1-ml_kmeans ") %>%
  add_model(pipe = pipe_sprk,
            method = "ml_bisecting_kmeans",
            desc = "model2-ml_bisecting_kmeans") %>%
  add_model(pipe = pipe_sprk,
            method = "ml_gaussian_mixture",
            desc = "model4-ml_kmeans2") %>%
  train_models() %>%
  evaluate_models()


final_model <-
  new_model %>% set_final_model(method = "best", refit = TRUE)

# Comapre if final model is same as the one with max siloutte -------------

id_val_1 <- (get_models_status(final_model))[1]
id_val_2 <- (get_models_status(final_model))[2]
id_val_3 <- (get_models_status(final_model))[3]

pos <- "selected"

val_1 <- grepl(pos, id_val_1)
val_2 <- grepl(pos, id_val_2)
val_3 <- grepl(pos, id_val_3)

if (val_1 == "TRUE")
{
  id_model <- (get_models_status(final_model) %>% names())[1]
} else if (val_2 == "TRUE")
{
  id_model <- (get_models_status(final_model) %>% names())[2]
} else{
  id_model <- (get_models_status(final_model) %>% names())[3]
}

mod1 <- get_evalutions(final_model)
mx_sil <- max(mod1$silhouette)

max_m <- mod1 %>%
  filter(mod1$silhouette == mx_sil)

model_with_max_silhotte <- max_m$model

test_that("The trained model is the one with max Silhouette", {
  expect_equal(id_model , model_with_max_silhotte)
})



# Test4:Reevaluate Option is added ----------------------------------------

# Copy data to spark
spk_df_1 <- copy_to(sc, mtcars, name = "df", overwrite = TRUE)

s1_model <- new_segmenter(df = spk_df_1, name = "test") %>%
  add_holdout_samples(splits = c(.5, .25, .25)) %>%
  add_model(pipe = NULL,
            method = "ml_kmeans",
            k = 3) %>%
  add_model(pipe = NULL,
            method = "ml_kmeans",
            k = 4) %>%
  train_models() %>%
  evaluate_models() %>%
  set_final_model(.,
                  method = "best",
                  reevaluate = TRUE,
                  refit = FALSE)


# Comapre if final model is same as the one with max siloutte -------------

id_val_1 <- (get_models_status(s1_model))[1]
id_val_2 <- (get_models_status(s1_model))[2]

pos <- "selected"

val_1 <- grepl(pos, id_val_1)
val_2 <- grepl(pos, id_val_2)

if (val_1 == "TRUE")
{
  id_model <- (get_models_status(s1_model) %>% names())[1]
} else{
  id_model <- (get_models_status(s1_model) %>% names())[2]
}

mod1 <- get_evalutions(s1_model)
mx_sil <- max(mod1$silhouette)

max_m <- mod1 %>%
  filter(mod1$silhouette == mx_sil)

model_with_max_silhotte <- max_m$model

test_that("The trained model is the one with max Silhouette", {
  expect_equal(id_model , model_with_max_silhotte)
})


# Test 5:Find final model with a known ID with reevaluate=TRUE---------------------------------

id_for_manual <- (get_models_status(s1_model) %>% names())[2]
final_model_with_id <-
  s1_model %>% set_final_model(
    method = "manual",
    id = id_for_manual,
    reevaluate = TRUE,
    refit = TRUE
  )

# Comapre if final model is same as the one with max siloutte -------------

id_val_1 <- (get_models_status(final_model_with_id))[1]
id_val_2 <- (get_models_status(final_model_with_id))[2]

pos <- "selected"

val_1 <- grepl(pos, id_val_1)
val_2 <- grepl(pos, id_val_2)

if (val_1 == "TRUE")
{
  id_model <- (get_models_status(final_model_with_id) %>% names())[1]
} else{
  id_model <- (get_models_status(final_model_with_id) %>% names())[2]
}

mod1 <- get_evalutions(final_model_with_id)
mx_sil <- max(mod1$silhouette)

max_m <- mod1 %>%
  filter(mod1$silhouette == mx_sil)

model_with_max_silhotte <- max_m$model

test_that("The trained model is the one with max Silhouette", {
  expect_equal(id_model , model_with_max_silhotte)
})

# Test6:Add the models post evaluation Option is added ----------------------------------------

# Copy data to spark
#spk_df_multi_model <- copy_to(sc, mtcars, name = "df", overwrite = TRUE)


#multi_model <- new_segmenter(df = spk_df_multi_model, name = "test") %>%
# add_holdout_samples(splits = c(.5, .25, .25)) %>%
#add_model(pipe = NULL,
#         method = "ml_kmeans",
#        k = 3) %>%
#add_model(pipe = NULL,
#         method = "ml_kmeans",
#        k = 4) %>%
#train_models() %>%
#evaluate_models() %>%
#set_final_model(method = "best", refit = TRUE) %>%
#add_model(
#  pipe=NULL,
#  method = "ml_kmeans",
#  desc = "model1-ml_kmeans ")%>%
#add_model(
#  pipe = NULL,
#  method = "ml_bisecting_kmeans",
#  desc = "model2-ml_bisecting_kmeans") %>%
#add_model(
#  pipe = NULL,
#  method = "ml_gaussian_mixture",
#  desc = "model4-ml_kmeans2")


# Test that newly added 3 models are "added" and not trained or se --------

# Comapre if final model is same as the one with max siloutte -------------

#id_val_3 <- (get_models_status(multi_model))[3]
#id_val_4 <- (get_models_status(multi_model))[4]
#id_val_5 <- (get_models_status(multi_model))[5]

#val_3 <- grepl("added",id_val_3)
#val_4 <- grepl("added",id_val_4)
#val_5 <- grepl("added",id_val_5)


#test_that("Test that last 3 models are-added", {
# expect_equal(val_3,TRUE)
# expect_equal(val_4,TRUE)
# expect_equal(val_5,TRUE)
#})

#Train_second_set <- multi_model%>%
#  train_models() %>%

################################### Failure when the newly added model gets evaluated #########################

#eval_the_model <- Train_second_set %>%
#evaluate_models()

#eval_the_model <- eval_the_model %>%
#  set_final_model(method = "best", refit = TRUE)
