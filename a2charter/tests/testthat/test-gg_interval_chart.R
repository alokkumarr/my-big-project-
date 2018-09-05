
# Interval Chart Unit Tests ----------------------------------------------------


library(testthat)
library(a2charter)
library(dplyr)
library(checkmate)
library(ggplot2)

context("interval_chart unit tests")

# Create a data set
dat <- mtcars %>% mutate(am = as.factor(am), cyl = as.factor(cyl))


test_that("Basic Interval Chart Unit Tests", {
  p1 <- gg_interval_chart(dat, x_variable = "am", y_variable = "mpg", chart_type = "pointrange")
  
  expect_class(p1$layers[[1]], "ggproto")
  expect_class(p1$layers[[1]]$geom, "GeomLine")
  expect_class(p1$layers[[2]]$geom, "GeomPointrange")
  expect_equal(as.character(p1$mapping$x)[2], "am")
  expect_equal(as.character(p1$mapping$y)[2], "mpg")
  expect_null(p1$mapping$color)
  expect_true(all.equal(p1$plot_env$theme_fun(), theme_sncr()))
  
})


test_that("Interval Line Option Unit Tests", {
  p1 <- gg_interval_chart(dat, x_variable = "am", y_variable = "mpg", chart_type = "pointrange", line = FALSE)
  
  expect_class(p1$layers[[1]], "ggproto")
  expect_class(p1$layers[[1]]$geom, "GeomBlank")
  expect_class(p1$layers[[2]]$geom, "GeomPointrange")
})



test_that("Interval Chart Type Option Unit Tests", {
  p1 <- gg_interval_chart(dat, x_variable = "am", y_variable = "mpg", chart_type = "pointrange")
  expect_class(p1$layers[[1]], "ggproto")
  expect_class(p1$layers[[2]]$geom, "GeomPointrange")
  
  p2 <- gg_interval_chart(dat, x_variable = "am", y_variable = "mpg", chart_type = "errorbar")
  expect_class(p2$layers[[1]], "ggproto")
  expect_class(p2$layers[[2]]$geom, "GeomErrorbar")
  
  p3 <- gg_interval_chart(dat, x_variable = "am", y_variable = "mpg", chart_type = "linerange")
  expect_class(p3$layers[[1]], "ggproto")
  expect_class(p3$layers[[2]]$geom, "GeomLinerange")
  
  p4 <- gg_interval_chart(dat, x_variable = "am", y_variable = "mpg", chart_type = "crossbar")
  expect_class(p4$layers[[1]], "ggproto")
  expect_class(p4$layers[[2]]$geom, "GeomCrossbar")
})