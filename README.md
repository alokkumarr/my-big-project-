# Introduction

This is the Synchronoss Analytics Workbench (SAW) Security component.
It provides authentication services to SAW applications.  The project
is further described on its [Confluence page].

[Confluence page]: https://confluence.synchronoss.net:8443/display/BDA/SAW+-+Security

# Operations

See the [operations guide](doc/operations.md) for details about
operations and monitoring.

# Install saw security locally
To start with saw security on local machine we will need below software to be installed which are as follows:
 1. Java 8.
 2. Maven 3.x
 3. MariaDB 5.5
 4. Spring Tool Suite or equivalent 
 
*Note : If you are using Mac install `homebrew`*

Java
---- 
> brew update
> brew cask install java

Maven
-----
> brew update
> brew install maven

MariaDB
-------
> brew update
> brew install mariadb@5.5
> brew services start mariadb or mysql.service start
	>  [mysql_secure_installation](https://mariadb.com/kb/en/library/mysql_secure_installation/)
 `mysql -u root -p -h localhost`
	`CREATE USER 'saw'@'localhost' IDENTIFIED BY 'saw';`
	`CREATE DATABASE saw_security;`
	`GRANT ALL PRIVILEGES ON saw_security.* TO 'saw'@'localhost';`

Data Dump
---------
Use the data dump provided below location
> /saw-security/src/main/resources/db/data/data-dump.sql
`mysql -u saw -p saw_security < data-dump.sql`

After above steps, you are set with tables & data on saw_security databases. you should see below tables
+----------------------------------+
| Tables_in_saw_security           |
+----------------------------------+
| analysis                         |
| contact_info                     |
| customer_product_module_features |
| customer_product_modules         |
| customer_products                |
| customers                        |
| modules                          |
| password_history                 |
| privilege_codes                  |
| privilege_group_codes            |
| privilege_groups                 |
| privileges                       |
| product_modules                  |
| products                         |
| reset_pwd_dtls                   |
| role_privileges                  |
| roles                            |
| roles_type                       |
| rs_hl_initial_setup              |
| rs_hl_statictbl                  |
| schema_version                   |
| sec_group                        |
| sec_group_dsk_attribute          |
| sec_group_dsk_value              |
| ticket                           |
| user_contact                     |
| users                            |
+----------------------------------+
Connect local MariaDB instance
------------------------------

 - Open  `/src/main/resources/application.properties` 
 - set `spring.datasource.url=jdbc:mariadb://localhost:3306/saw_security`
 - set `spring.datasource.username=saw`
 - set  `flyway.baselineOnMigrate=false`
 - set `spring.datasource.password=6cqMCz4dKXWF2ada9vhCkA==` 
		> for the password we need to use the util class in source code
		> Ccode.java to generate the password   Run Ccode.java passing your
		> actual `password` as program argument in this case it is `saw` so  
		> the above `6cqMCz4dKXWF2ada9vhCkA==` has been generated.

  

Run SAW-Security
----------------

 1. Run as `Spring boot application` from Spring Tool Suite
 2. Open browser & enter
    http://localhost:9000/saw-security/swagger-ui.html

  

	

