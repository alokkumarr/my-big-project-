/*******************************************************************************
 Filename:  V18__ADD_JV_FLAG_CUST_CODE.SQL
 Purpose:   Add JV FLAG in customer table and filter by customer code flag in CONFIG_VAL table
            to support multi tenancy features
 Date:      19-09-2019
********************************************************************************/

	/*******************************************************************************
	 ALTER Table Scripts Starts
	********************************************************************************/

	ALTER TABLE CUSTOMERS
    	 ADD COLUMN IF NOT EXISTS IS_JV_CUSTOMER tinyint(4) AFTER `DOMAIN_NAME`;

	ALTER TABLE CONFIG_VAL
    	 ADD COLUMN IF NOT EXISTS FILTER_BY_CUSTOMER_CODE tinyint(4) AFTER `CREATED_BY`;


    /*******************************************************************************
	 ALTER Table Scripts Ends
	********************************************************************************/

    UPDATE CUSTOMERS SET IS_JV_CUSTOMER = 1;
    UPDATE CONFIG_VAL SET FILTER_BY_CUSTOMER_CODE = 0;

/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/
