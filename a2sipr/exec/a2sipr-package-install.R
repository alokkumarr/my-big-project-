#!/usr/bin/env Rscript


# a2sipr Build & Install Script ------------------------------------------------
# Description: Configurable script to install a2sipr package on specified server

# Command Line Arguments --------------------------------------------------
# Read source directory where package is present & library location where 
# package is to be installed

args <- commandArgs(trailingOnly = TRUE)

# Validate Args
if (length(args) == 0) {
  stop("No arguments provided")
} else if (length(args) == 1) {
  stop("valid source and destinationdirectory paths not provided")
} else {
  if(! dir.exists(args[1])) {
    stop("source directory path does not exist")
  }
  if(! dir.exists(args[2])) {
    stop("destination directory path does not exist")
  }
}


# Assign Args
source_dir <- args[1]
dest_dir <- args[2]

# Install Packages --------------------------------------------------------

setwd(source_dir)
setwd("..")

cat(format(Sys.time(), "%c"), "-", "build starting -------------------- \n\n")

system("R CMD build a2sipr")

cat(format(Sys.time(), "%c"), "-", "build completed\n\n")

cat(format(Sys.time(), "%c"), "-", "install starting -------------------- \n\n")

install.packages(list.files(pattern = "a2sipr.*tar.gz"), lib = dest_dir, repos = NULL)

cat(format(Sys.time(), "%c"), "-", "a2sipr install completed\n\n")