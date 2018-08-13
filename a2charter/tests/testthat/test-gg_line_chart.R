
# Line Chart Unit Tests ----------------------------------------------------


library(testthat)
library(a2charter)
library(dplyr)
library(checkmate)
library(proto)

context("line_chart unit tests")

# Create a data set

# Function to create simulated data
sim_data <- function(n_ids, n_recs, n_iter, seed = 319){
  
  set.seed(seed)
  n <- n_ids * n_recs
  ids <- 1:n_ids
  dates <- seq(from=Sys.Date()-365, to=Sys.Date(), by="day")
  cat1 <- c("A", "B")
  cat2 <- c("X", "Y", "Z")
  
  do.call("rbind",
          replicate(n_iter,
                    {
                      data.frame(id = sample(ids, n, replace=T),
                                 date = sample(dates, n, replace = T),
                                 cat1 = as.character(sample(cat1, n, replace = T)),
                                 cat2 = as.character(sample(cat2, n, replace = T)),
                                 metric1 = sample(1:5, n, replace = T),
                                 metric2 = rnorm(n, mean=50, sd = 5))
                    },
                    simplify = FALSE)
  )
}

dat <- sim_data(1, 100, 1, seed = 319)


test_that("Basic Line Chart Unit Tests", {
  p1 <- dat %>% 
    count(date) %>% 
    gg_line_chart(., "date", "n", title = "test", caption = "caption")
  
  expect_class(p1$layers[[1]], "ggproto")
  expect_class(p1$layers[[1]]$geom, "GeomLine")
  expect_equal(as.character(p1$mapping$x), "date")
  expect_null(p1$mapping$color)
  expect_equal(p1$layers[[1]]$aes_params$colour, sncr_pal()(1))
  expect_equal(p1$plot_env$title, "test")
  expect_equal(p1$plot_env$caption, "caption")
  expect_null(p1$plot_env$subtitle)
  expect_null(p1$plot_env$x_axis_title)
  expect_null(p1$plot_env$y_axis_title)
  expect_true(all.equal(p1$plot_env$theme_fun(), theme_sncr()))
  
})



test_that("Grouping Options Unit Tests", {
  p2 <- dat %>% 
    group_by(cat1) %>% 
    count(date) %>% 
    gg_line_chart(., "date", "n", color = "cat1", title = "test", caption = "caption")
  expect_equal(as.character(p2$mapping$colour), "cat1")
  expect_equal(p2$layers[[1]]$aes_params$linetype, 1)
  expect_equal(p2$layers[[1]]$aes_params$alpha, 1)
  expect_equal(p2$layers[[1]]$aes_params$size, 1)
})


test_that("Points option Unit Tests", {
  p3 <- dat %>% 
    count(date) %>% 
    gg_line_chart(., "date", "n", points = TRUE)
  expect_class(p3$layers[[2]]$geom, "GeomPoint")
})

