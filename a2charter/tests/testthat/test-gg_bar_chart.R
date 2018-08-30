
# Bar Chart Unit Tests ----------------------------------------------------


library(testthat)
library(a2charter)
library(dplyr)
library(checkmate)
library(ggplot2)

context("barchart unit tests")

# Create a data set
dat <- mtcars %>% mutate(am = as.factor(am), cyl = as.factor(cyl))
d1 <- dat %>% count(cyl)


test_that("Basic Bar Chart Unit Tests", {
  p1 <- gg_bar_chart(dat, "am", title = "test", caption = "caption")
  
  expect_class(p1$layers[[1]], "ggproto")
  expect_class(p1$layers[[1]]$geom, "GeomBar")
  expect_equal(as.character(p1$mapping$x)[2], "am")
  expect_null(p1$mapping$fill)
  expect_equal(p1$layers[[1]]$aes_params$fill, sncr_pal()(1))
  expect_equal(p1$layers[[1]]$aes_params$colour, "black")
  expect_equal(p1$plot_env$title, "test")
  expect_equal(p1$plot_env$caption, "caption")
  expect_null(p1$plot_env$subtitle)
  expect_null(p1$plot_env$x_axis_title)
  expect_null(p1$plot_env$y_axis_title)
  expect_true(all.equal(p1$plot_env$theme_fun(), theme_sncr()))
  
})
  


test_that("Proportion option Unit Tests", {
  p2 <- gg_bar_chart(dat, "am", proportion = TRUE)
  expect_class(p2$layers[[1]]$stat, "StatCount")
  
})


test_that("Label option Unit Tests", {
  p3 <- gg_bar_chart(dat, "am", proportion = TRUE, label = TRUE)
  expect_class(p3$layers[[2]], "LayerInstance")
})


test_that("Sort option Unit Tests", {
  p4 <- gg_bar_chart(dat, "am", sort = TRUE, desc = TRUE)
  expect_equal(as.character(p4$mapping$x)[2], "fct_infreq(am)")
  
  p5 <- gg_bar_chart(dat, "am", sort = TRUE, desc = FALSE)
  expect_equal(as.character(p5$mapping$x)[2], "fct_inorder(am)")
})

