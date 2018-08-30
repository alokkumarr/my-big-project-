
# Scatter Chart Unit Tests ----------------------------------------------------


library(testthat)
library(a2charter)
library(dplyr)
library(checkmate)
library(ggplot2)

context("scatter_chart unit tests")

# Create a data set
dat <- mtcars %>% mutate(am = as.factor(am), cyl = as.factor(cyl))


test_that("Basic Scatter Chart Unit Tests", {
  p1 <- gg_scatter_chart(dat, "wt", "mpg", title = "test", caption = "caption")
  
  expect_class(p1$layers[[1]], "ggproto")
  expect_class(p1$layers[[1]]$geom, "GeomPoint")
  expect_equal(as.character(p1$mapping$x)[2], "wt")
  expect_equal(as.character(p1$mapping$y)[2], "mpg")
  expect_null(p1$mapping$color)
  expect_equal(p1$layers[[1]]$aes_params$colour, "grey25")
  expect_equal(p1$plot_env$title, "test")
  expect_equal(p1$plot_env$caption, "caption")
  expect_null(p1$plot_env$subtitle)
  expect_null(p1$plot_env$x_axis_title)
  expect_null(p1$plot_env$y_axis_title)
  expect_true(all.equal(p1$plot_env$theme_fun(), theme_sncr()))
  
})


test_that("Scatter Smoother Option Unit Tests", {
  p1 <- gg_scatter_chart(dat, "wt", "mpg", smoother=TRUE)
  
  expect_class(p1$layers[[1]], "ggproto")
  expect_class(p1$layers[[1]]$geom, "GeomPoint")
  expect_class(p1$layers[[2]]$geom, "GeomSmooth")
})


test_that("Scatter Grouping Options Unit Tests", {
  p2 <- gg_scatter_chart(dat, "wt", "mpg",
                         color = "am", points = FALSE,
                         smoother = TRUE, smooth_ci = FALSE)
  expect_equal(as.character(p2$mapping$colour)[2], "am")

})


test_that("Scatter Smoother inputs Unit Tests", {
  p3 <- gg_scatter_chart(dat, "wt", "mpg",
                         points = FALSE,
                         smoother=TRUE,
                         smooth_method = "lm", 
                         smooth_ci = FALSE,
                         smooth_formula = as.formula("y~x"))
  expect_equal(p3$layers[[2]]$stat_params$method, "lm")
  expect_equal(p3$layers[[2]]$stat_params$formula, as.formula("y~x"))
  expect_false(p3$layers[[2]]$stat_params$se)
})

