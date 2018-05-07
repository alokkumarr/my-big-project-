
# Univariate Charts Unit Tests --------------------------------------------

library(testthat)
library(a2charter)
library(dplyr)
library(checkmate)
library(proto)

context("univariate charts unit tests")

# Create a data set
dat <- mtcars %>% mutate(am = as.factor(am))


test_that("Basic Histogram Unit Tests", {
  p1 <- gg_histogram(dat, "mpg", title = "test", caption = "caption")
  
  expect_class(p1$layers[[1]], "ggproto")
  expect_class(p1$layers[[1]]$geom, "GeomBar")
  expect_equal(as.character(p1$mapping$x), "mpg")
  expect_null(p1$mapping$fill)
  expect_equal(p1$layers[[1]]$aes_params$fill, sncr_pal()(1))
  expect_equal(p1$layers[[1]]$aes_params$colour, "black")
  expect_equal(p1$plot_env$title, "test")
  expect_equal(p1$plot_env$caption, "caption")
  expect_null(p1$plot_env$subtitle)
  expect_null(p1$plot_env$x_axis_title)
  expect_null(p1$plot_env$y_axis_title)
  expect_true(all.equal(p1$plot_env$theme_fun(), theme_sncr()))
  
  p2 <- gg_histogram(dat, "mpg", theme = "light")
  expect_class(p2$layers[[1]], "ggproto")
  expect_true(all.equal(p2$plot_env$theme_fun(), theme_light()))
  
  p3 <- gg_histogram(dat, "mpg", bins = 10)
  expect_equal(p3$layers[[1]]$stat_params$bins, 10)
})



test_that("Basic Density Unit Tests", {
  p1 <- gg_density(dat, "mpg", title = "test", caption = "caption")
  
  expect_class(p1$layers[[1]], "ggproto")
  expect_class(p1$layers[[1]]$geom, "GeomDensity")
  expect_equal(as.character(p1$mapping$x), "mpg")
  expect_null(p1$mapping$fill)
  expect_equal(p1$layers[[1]]$aes_params$fill, sncr_pal()(1))
  expect_equal(p1$layers[[1]]$aes_params$colour, "black")
  expect_equal(p1$plot_env$title, "test")
  expect_equal(p1$plot_env$caption, "caption")
  expect_null(p1$plot_env$subtitle)
  expect_null(p1$plot_env$x_axis_title)
  expect_null(p1$plot_env$y_axis_title)
  expect_true(all.equal(p1$plot_env$theme_fun(), theme_sncr()))
  
  p2 <- gg_density(dat, "mpg", adjust = .5)
  expect_equal(p2$layers[[1]]$stat_params$adjust, .5)
})



test_that("Basic Boxplot Unit Tests", {
  p1 <- gg_boxplot(dat, y_variable = "mpg", title = "test", caption = "caption")
  
  expect_class(p1$layers[[1]], "ggproto")
  expect_class(p1$layers[[1]]$geom, "GeomBoxplot")
  expect_equal(as.character(p1$mapping$y), "mpg")
  expect_null(p1$mapping$fill)
  expect_equal(p1$layers[[1]]$aes_params$fill, sncr_pal()(1))
  expect_equal(p1$layers[[1]]$aes_params$colour, "black")
  expect_equal(p1$plot_env$title, "test")
  expect_equal(p1$plot_env$caption, "caption")
  expect_null(p1$plot_env$subtitle)
  expect_null(p1$plot_env$y_axis_title)
  expect_true(all.equal(p1$plot_env$theme_fun(), theme_sncr()))
  
  p2 <-  gg_boxplot(dat, y_variable = "mpg", outlier.color = "red")
  expect_equal(p2$plot_env$params_list$geom_params$outlier.color, "red")
  
  y <- rnorm(100)
  df <- data.frame(
   x = 1,
   y0 = min(y),
   y25 = quantile(y, 0.25),
   y50 = median(y),
   y75 = quantile(y, 0.75),
   y100 = max(y))

  p3 <- gg_boxplot(df, ymin = "y0", lower = "y25", middle = "y50", upper = "y75", ymax = "y100", fill="darkorange")
  expect_class(p3$layers[[1]], "ggproto")
  expect_class(p3$layers[[1]]$geom, "GeomBoxplot")
  expect_subset(c("ymin", "lower", "middle", "upper", "ymax"), as.character(names(p3$mapping)))
})



test_that("Fill Variable Unit Tests", {
  p1 <- gg_histogram(dat, "mpg", fill="am", color="am")
  
  expect_equal(as.character(p1$mapping$fill), "am")
  expect_class(p1$layers[[1]]$geom, "GeomBar")
  
  p2 <- gg_histogram(dat, "mpg", fill="am", color="am", palette = "a2")
  expect_equal(p2$plot_env$fill, "am")
  expect_equal(p2$plot_env$color, "am")
  expect_equal(p2$plot_env$palette, "a2")
  expect_class(p2$layers[[1]]$position, "PositionStack")
  
  p3 <- gg_histogram(dat, "mpg", fill="am", color="am", palette = "a2", position = "dodge")
  expect_class(p3$layers[[1]]$position, "PositionDodge")
})


test_that("Facet Options Unit Tests", {
  p1 <- gg_histogram(dat, "mpg", fill="am", facet_formula = "~am")
  expect_class(p1$facet, "FacetWrap")
  expect_equal(as.character(p1$facet$params$facets[[1]]), "am")
  
  p2 <- gg_histogram(dat, "mpg", fill="am", facet_formula = "~am", facet_args = list(scales="free_y"))
  expect_true(p2$facet$params$free$y)
  
  p3 <- gg_histogram(dat, "mpg", fill="am", facet_formula = "~am", facet_args = list(nrow = 2))
  expect_equal(p3$facet$params$nrow, 2)
})