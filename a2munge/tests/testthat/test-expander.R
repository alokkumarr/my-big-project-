# Expander Unit Tests -----------------------------------------------------

library(a2munge)
library(testthat)
library(sparklyr)
library(dplyr)
library(checkmate)
library(tidyr)
library(lubridate)

context("expander function unit tests")

# Create Spark Connection
sc <- spark_connect(master = "local")

# Copy R data.frame to Spark - need to convert date class to character
sim_tbl <- mutate_at(sim_df, "date", as.character) %>% 
  copy_to(sc, ., name = "df", overwrite = TRUE) %>%
  mutate(date = to_date(date))


# Test 1:Expander- with mode=nesting for unique " id-vars" ----------------

expand_nest <- expander(sim_df,
                        id_vars = c("id", "cat1"),
                        mode = "nesting",
                        complete = FALSE)


test_that("expander results right for nested with selected id_var columns", {
  expect_equal(
    arrange(expand_nest, id, cat1),
    sim_df %>% 
      distinct(id, cat1) %>%
      arrange(id, cat1)
  )
})

# Test 2:Expander-with mode=nesting for unique "id-vars" for spark DS----------

expand_nest_spark_DF <- expander(sim_tbl,
                                 id_vars = c("id", "cat1"),
                                 mode = "nesting",
                                 complete = FALSE)

test_that("expander results right nested for spark DS", {
  expect_equal(expand_nest , collect(expand_nest_spark_DF))
})


# Test 5:Expander- with mode=crossing for unique " id-vars" ----------------

expand_crossing <- expander(sim_df,
                            id_vars = c("id", "date"),
                            mode = "crossing",
                            complete = FALSE) %>%
  arrange(id, date)

test_that("expander with Crossing with only id_vars as output ", {
  
  ndates <- length(unique(sim_df$date))
  
  expect_gte(nrow(expand_crossing), nrow(sim_df))
  expect_true(all(
    ndates == expand_crossing %>%
      count(id) %>% 
      pull(n) 
  ))
})

# Test 6:Expander- with mode=crossing for unique " id-vars" for Sa --------

expand_crossing_spark_DS <- expander(sim_tbl,
                                     id_vars = c("id", "date"),
                                     mode = "crossing",
                                     complete = FALSE) %>%
  collect() %>%
  arrange(id, date) 

test_that("expander with Crossing with only id_vars as output for spark DS ", {
  expect_equal(expand_crossing_spark_DS,
               expand_crossing)

})


# Test 7: Crossing with Complete ------------------------------------------

r_expand1 <- expander(sim_df,
                      id_vars = c("id", "date"),
                      mode = "crossing",
                      complete = TRUE) %>% 
  select(index, id, date, cat1, cat2, metric1, metric2) %>% 
  arrange(id, date)

spk_expand1 <- expander(sim_tbl,
                        id_vars = c("id", "date"),
                        mode = "crossing",
                        complete = FALSE) %>%
  select(index, id, date, cat1, cat2, metric1, metric2) %>% 
  collect() %>%
  arrange(id, date) 

test_that("expander with crossing and complete ", {
  expect_equal(r_expand1, spk_expand1)
  
})


#Test 9:Expander-with fun for sequence data  ----------------------------


test_that("Expander for sequence data is correct for full DS", {
  
  n1 <- 10
  fun_expander_val_seris <- expander(sim_df,
                                     id_vars = c('id'),
                                     fun = funs(metric1 = 1:n1),
                                     complete = FALSE) 
  
  expect_true(all(
    n1 == fun_expander_val_seris %>%
      count(id) %>% 
      pull(n) 
  ))
  expect_equal(unique(fun_expander_val_seris$metric1), 1:n1)
})
