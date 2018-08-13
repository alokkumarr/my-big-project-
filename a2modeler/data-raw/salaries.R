
# File to Add Salaries Dataset -------------------------------------------
library(dplyr)
library(tidyr)
library(tibble)
library(babynames)

salaries <- read.csv("~/salaries.csv")


# Format Data -------------------------------------------------------------

salaries <- salaries %>%
  select(employee_name = EmployeeName,
         job_title = JobTitle,
         year = Year,
         base_pay = BasePay,
         overtime_pay = OvertimePay,
         other_pay = OtherPay,
         total_pay = TotalPay,
         benefits = Benefits,
         total = TotalPayBenefits
         )

# Add Gender and Job Family Features
salaries <- salaries %>%
  separate(employee_name, c("first_name", "last_name"), sep = " ") %>%
  mutate_at(c("first_name", "last_name"), tolower) %>%
  left_join(
    babynames %>%
      group_by(sex, name) %>%
      summarise_at("n", sum) %>%
      ungroup() %>%
      group_by(name) %>%
      filter(n == max(n)) %>%
      ungroup() %>%
      mutate(name = tolower(name)),
    by = c("first_name" = "name")
  ) %>%
  replace_na(list(benefits = 0, sex = "U")) %>%
  select(first_name, last_name, gender_guess = sex, job_title, year,
         base_pay, overtime_pay, other_pay, total_pay, benefits, total) %>%
  mutate(police   = ifelse(grepl("police", tolower(job_title)), 1, 0),
         fire     = ifelse(grepl("fire", tolower(job_title)), 1, 0),
         medical  = ifelse(grepl("medical", tolower(job_title)), 1, 0)) %>%
  as.tibble()



# Save to Data folder -----------------------------------------------------

save(salaries, file = 'data/salaries.rdata', compress = 'xz')

