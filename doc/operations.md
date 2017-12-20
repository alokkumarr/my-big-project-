# Onboarding a customer

We can execute the jar file of command line tools and add saw-security-classes jar file in its classpath as follows:

    java -jar saw-security-command-line-tool-2.jar -cp saw-security-2-classes.jar

We can also utilise customer_onboard.sh script in order to execute the command with current environment setup.

    cd /opt/bda/saw-security/bin/
    bash customer_onboard.sh

Internally above command executes following:

    java -Dspring.config.location=/opt/bda/saw-security/conf/application.properties -Dlogging.config=/opt/bda/saw-security/conf/logback.xml -Dquartz.properties.location=/opt/bda/saw-security/conf -jar /opt/bda/saw-security/saw-security-command-line-tool-2.jar -cp /opt/bda/saw-security/saw-security-2-classes.jar --server.port=9999

Note that the versions may differ.

Features of spring boot shell:
1. Type in "help" and it will show you all the available commands
2. Tab based auto completion is supported.


    shell:>help
    AVAILABLE COMMANDS

    Built-In Commands
            clear: Clear the shell screen.
            exit, quit: Exit the shell.
            help: Display help about available commands.
            script: Read and execute commands from a file.
            stacktrace: Display the full stacktrace of the last error.    

    Saw Security Shell
            dummy-command: Dummy Command
            onboard-customer: Onboard the customer
    
    shell:>


Once you are inside the shell, type in onboard-customer and it will start the process of creating customer and related products/components in the system.

In below example, it starts with showing you which products are present in system and asks for basic customer information.


    shell:>onboard-customer
    Customer information:
    1
    {PRODUCT_SYS_ID=1, PRODUCT_NAME=MCT Insights}
    {PRODUCT_SYS_ID=2, PRODUCT_NAME=SnT Insighjts}
    {PRODUCT_SYS_ID=3, PRODUCT_NAME=Smart Care Insights}
    {PRODUCT_SYS_ID=4, PRODUCT_NAME=SAW Demo}
    {PRODUCT_SYS_ID=5, PRODUCT_NAME=Channel Insights}
    ====== CUSTOMERS TABLE ======
    Enter CUSTOMER_CODE: sync10
    Enter COMPANY_NAME: sync10
    Enter COMPANY_BUSINESS: sync10
    Enter LANDING_PROD_SYS_ID: 4
    Enter DOMAIN_NAME: sync10.com
    Generated CUSTOMER_SYS_ID: 16


In this case the generated customer_sys_id is 16. It continues to show product information as we need to associate these products with customers, in my case I chose 4 which is for saw demo.
    
    {PRODUCT_SYS_ID=1, PRODUCT_NAME=MCT Insights}
    {PRODUCT_SYS_ID=2, PRODUCT_NAME=SnT Insighjts}
    {PRODUCT_SYS_ID=3, PRODUCT_NAME=Smart Care Insights}
    {PRODUCT_SYS_ID=4, PRODUCT_NAME=SAW Demo}
    {PRODUCT_SYS_ID=5, PRODUCT_NAME=Channel Insights}
    ====== CUSTOMER_PRODUCTS TABLE ======
    Enter PRODUCT_SYS_ID: 4
    class org.springframework.jdbc.support.GeneratedKeyHolder
    11
    Generated CUST_PROD_SYS_ID: 11

In this example the generated customer product linkage ID is 11. It continues with displaying modules of all products, sicne we chose saw demo i.e. 4 in previous case. It makes sense to select modules of that product only. i.e. in this case either 4, 7 or 8.

    {PROD_MOD_SYS_ID=1, PRODUCT_NAME=MCT Insights, MODULE_NAME=OBSERVE}
    {PROD_MOD_SYS_ID=2, PRODUCT_NAME=SnT Insighjts, MODULE_NAME=OBSERVE}
    {PROD_MOD_SYS_ID=3, PRODUCT_NAME=Smart Care Insights, MODULE_NAME=OBSERVE}
    {PROD_MOD_SYS_ID=4, PRODUCT_NAME=SAW Demo, MODULE_NAME=ANALYZE}
    {PROD_MOD_SYS_ID=5, PRODUCT_NAME=Channel Insights, MODULE_NAME=OBSERVE}
    {PROD_MOD_SYS_ID=6, PRODUCT_NAME=MCT Insights, MODULE_NAME=ANALYZE}
    {PROD_MOD_SYS_ID=7, PRODUCT_NAME=SAW Demo, MODULE_NAME=ALERT}
    {PROD_MOD_SYS_ID=8, PRODUCT_NAME=SAW Demo, MODULE_NAME=OBSERVE}
    ====== CUSTOMER_PRODUCT_MODULES TABLE ======
    Enter PROD_MOD_SYS_ID (from above shown values): 
    4
    Enter more? (yes/no): no

It continues with displaying that it's creating the relationships and admin role in background followed by creating admin user for the customer.


    ====== CUSTOMER_PRODUCT_MODULE_FEATURES TABLE ======
    ====== ROLES TABLE for Admin Role ======
    ====== USERS TABLE for ADMIN USER ======
    Enter MASTER_LOGIN: 
    pawan@sync10.com
     Enter EMAIL: pawan@sync10.com
    Enter PASSWORD: synchronoss
    Enter FIRST_NAME: 
    Pawan
    Enter MIDDLE_NAME: 
    D
    Enter LAST_NAME: 
    Tejwani
    Generated User ID for current user is: 40
    shell:>

Note: This file has been moved to [rendered documentation]
(../saw-dist/src/main/asciidoc/saw-operations/index.adoc).