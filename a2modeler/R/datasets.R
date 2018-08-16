#' Available Model Methods for Modeler Objects
#'
#' A dataset containing the avaialble model methods for each modeler type
#'
#' @format A data frame with 24 rows and 5 variables:
#'  \describe{
#'   \item{type}{modeler type}
#'   \item{method}{model method function name}
#'   \item{name}{name of method}
#'   \item{package}{R package backend}
#'   \item{class}{method class membership. can be more than 1} ...
#'   }
"model_methods"



#' Salaries of San Fransico Municipal Workers
#'
#' A dataset containing the San Fransico Municipal workers salaries
#'
#' @format A data frame with 148686 rows and 14 variables:
#'  \describe{
#'   \item{first_name}{first name}
#'   \item{last_name}{last name}
#'   \item{gender_guess}{gender guess based on first name}
#'   \item{job_title}{job title}
#'   \item{year}{employment year}
#'   \item{base_pay}{base salary}
#'   \item{overtime_pay}{overtime pay}
#'   \item{other_pay}{other pay}
#'   \item{total_pay}{base, overtime and other pay total}
#'   \item{benefits}{total benefit cost}
#'   \item{total}{total pay plus benefits}
#'   \item{police}{flag for police based on job title}
#'   \item{fire}{flag for firefighter based on job title}
#'   \item{medical}{flag for medical professional based on job title}...
#'   }
"salaries"
