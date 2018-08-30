
#' Install R Package Function
#'
#' Installs list of R packages. Checks for pre-installation and correct version
#'
#' @param packages list of R packges. name of element should be package name.
#'   version is element value
#' @param lib_path optional input for library path
#' @param dependecies option to install package dependecies
#' @param repo repo url to download from
#' @export
sip_package_install <- function(packages,
                                lib_path = NULL,
                                dependecies = TRUE,
                                repo = "https://cloud.r-project.org" ) {

  checkmate::assert_list(packages)
  checkmate::assert_flag(dependecies)
  checkmate::assert_character(repo)

  if(is.null(lib_path)){
    lib_path <- .libPaths()[1]
  }
  checkmate::assert_directory(lib_path)

  installed_packages <- utils::installed.packages(lib_path)[, c("Package", "Version")]

  # For each package check if pre-installed with correct version, otherwise install
  for (pkg in names(packages)) {
    install_pkg <- dplyr::filter(installed_packages, Package == pkg)
    if (nrow(install_pkg) > 1) {
      if (packages[[pkg]] != install_pkg$Version) {
        update <- TRUE
      } else {
        update <- FALSE
      }
    } else{
      update <- TRUE
    }
    
    if (update) {
      try(utils::install.packages(pck,
                                  lib = lib_path,
                                  dependencies = dependecies,
                                  repos = repo),
          TRUE)
    }
  }
}
