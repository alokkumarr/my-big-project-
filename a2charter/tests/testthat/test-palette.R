
# Palette Unit Tests ---------------------------------------------------


library(testthat)
library(a2charter)
library(dplyr)
library(checkmate)

context("palette unit tests")

test_that("palette function works as expected", {
  expect_equal(length(sncr_pal(palette = "a2")(5)), 5)
  expect_equal(sncr_pal()(2)[1], sncr_palettes$a2[1][[1]])
})