#!/usr/bin/env Rscript


# a2modules Build & Install Script ------------------------------------------------
# Description: Configurable script to install a2modules package on specified server

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
pkg_file <- args[3]
install_type <- args[4]

# Install Packages --------------------------------------------------------

setwd(source_dir)

required_packages <- c('devtools')
installed_packages <- rownames(installed.packages())

for(p in required_packages){
  if(! p %in% installed_packages){
    stop(paste(p, "R package not installed. \n"))
  }
}

pkg_info <- read.csv(pkg_file, header = FALSE, sep = "|", col.names = c("package", "version", "repo"))

if (install_type == "offline") {
  
  for (row in 1:nrow(pkg_info)) {
    
    pkg_name <- as.character(pkg_info[row, "package"])
    vers <- as.character(pkg_info[row, "version"])
    repo <- as.character(pkg_info[row, "repo"])
    
    cat(format(Sys.time(), "%c"), "-", pkg_name, "install starting -------------------- \n\n")
    
    pkg_file_name <- paste(paste(pkg_name, vers, sep = "_"), ".tar.gz", sep = "")
    
    pkg_path <- paste(paste(source_dir, "/pkgs", sep ="/"), pkg_file_name, sep = "/")
    
    install.packages(pkgs = pkg_path, lib = dest_dir, repos = NULL)
    
    cat(format(Sys.time(), "%c"), "-", pkg_name, "install completed\n\n")
  }
  
} else {
  
  for (row in 1:nrow(pkg_info)) {
    
    cat(format(Sys.time(), "%c"), "-", pkg_name, "install starting -------------------- \n\n")
    
    pkg_name <- as.character(pkg_info[row, "package"])
    vers <- as.character(pkg_info[row, "version"])
    repo <- as.character(pkg_info[row, "repo"])
    
    devtools::install_version(package = pkg_name,
                              version = vers,
                              repos = repo,
                              lib.loc = dest_dir)
    
    cat(format(Sys.time(), "%c"), "-", pkg_name, "install completed\n\n")
  }
}

cat(format(Sys.time(), "%c"), "-", "a2sipr build starting -------------------- \n\n")

system("R CMD build a2sipr")

cat(format(Sys.time(), "%c"), "-", "a2sipr build completed\n\n")

cat(format(Sys.time(), "%c"), "-", "a2sipr install starting -------------------- \n\n")

install.packages(list.files(pattern = "a2sipr.*tar.gz"), lib = dest_dir, repos = NULL)

cat(format(Sys.time(), "%c"), "-", "a2sipr install completed\n\n")


cat(format(Sys.time(), "%c"), "-", "a2munge build starting -------------------- \n\n")

system("R CMD build a2munge")

cat(format(Sys.time(), "%c"), "-", "a2munge build completed\n\n")

cat(format(Sys.time(), "%c"), "-", "a2munge install starting -------------------- \n\n")

install.packages(list.files(pattern = "a2munge.*tar.gz"), lib = dest_dir, repos = NULL)

cat(format(Sys.time(), "%c"), "-", "a2munge install completed\n\n")


cat(format(Sys.time(), "%c"), "-", "a2modeler build starting -------------------- \n\n")

system("R CMD build a2modeler")

cat(format(Sys.time(), "%c"), "-", "a2modeler build completed\n\n")

cat(format(Sys.time(), "%c"), "-", "a2modeler install starting -------------------- \n\n")

install.packages(list.files(pattern = "a2modeler.*tar.gz"), lib = dest_dir, repos = NULL)

cat(format(Sys.time(), "%c"), "-", "a2modeler install completed\n\n")


cat(format(Sys.time(), "%c"), "-", "a2charter build starting -------------------- \n\n")

system("R CMD build a2charter")

cat(format(Sys.time(), "%c"), "-", "a2charter build completed\n\n")

cat(format(Sys.time(), "%c"), "-", "a2charter install starting -------------------- \n\n")

install.packages(list.files(pattern = "a2charter.*tar.gz"), lib = dest_dir, repos = NULL)

cat(format(Sys.time(), "%c"), "-", "a2charter install completed\n\n")
