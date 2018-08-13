#!/usr/bin/env Rscript


# R Package Install Script ------------------------------------------------
# Description: Configurable script to install required SIP R packages. Installs
# all r packages in destination directory



# Docopt Command Line Arguments -------------------------------------------

doc <- "Usage: sip-r-package-install.R [options] [-h]
-p --packages PACKAGES path to R package dependencies config file
-l --lib LIBPATH R package library lib path
-d --dependecies DEPENDECIES logical flag to install dependecy packages [default: TRUE]
-r --repo REPO repo url [default: https://cloud.r-project.org]
"


# Pre-requistes - requires R prerequisite packages installed
required_packages <- c('docopt', 'jsonlite', 'devtools')
installed_packages <- rownames(installed.packages())

for(p in required_packages){
  if(! p %in% installed_packages){
    stop(paste(p, "R package not installed. you need it player. \n"))
  }
}

# Get commandline arguments
opt <- docopt::docopt(doc)

r_packages <- jsonlite::read_json(opt$packages)$packages
r_lib_path <- opt$lib
.dependencies <- opt$dependecies
repo <- opt$repo


# Install Packages --------------------------------------------------------

for(pck in names(r_packages)) {
  cat(format(Sys.time(), "%c"), "-", pck, "install starting -------------------- \n\n")
  devtools::install_version(package = pck,
                            version = r_packages[[pck]],
                            repos = repo,
                            lib = r_lib_path,
                            dependencies = .dependencies)
  cat(format(Sys.time(), "%c"), "-", pck, "install completed\n\n")
}

