/*******************************************************************************
 Filename:  V26__ADD_SYSTEM_CATEGORY.SQL
 Purpose:   Add system category column in customer_product_module_features table to mark system level folder.
 Date:      26-12-2019
********************************************************************************/

	/*******************************************************************************
	 ALTER Table Scripts Starts
	********************************************************************************/

	ALTER TABLE CUSTOMER_PRODUCT_MODULE_FEATURES
    	 ADD COLUMN IF NOT EXISTS SYSTEM_CATEGORY tinyint(4) AFTER `ACTIVE_STATUS_IND`;

    /*******************************************************************************
	 ALTER Table Scripts Ends
	********************************************************************************/

    UPDATE CUSTOMER_PRODUCT_MODULE_FEATURES SET SYSTEM_CATEGORY = 0;

/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/