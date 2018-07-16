#!/usr/bin/env Rscript


# R Package Install Script ------------------------------------------------
# Description: Configurable script to install required SIP R packages. Installs
# all r packages in destination directory


# Command Line Arguments --------------------------------------------------

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

# Get all zip files in source directory
all_files <- dir(source_dir)
zip_files <- all_files[tools::ext(all_files) == "zip"]

for(zf in zip_files) {
  cat(format(Sys.time(), "%c"), "-", zf, "install starting -------------------- \n\n")
  install.packages(pkgs = paste(source_dir, zf, sep = "/"),
                   lib = dest_dir,
                   repos = NULL)
  cat(format(Sys.time(), "%c"), "-", zf, "install completed\n\n")
}




