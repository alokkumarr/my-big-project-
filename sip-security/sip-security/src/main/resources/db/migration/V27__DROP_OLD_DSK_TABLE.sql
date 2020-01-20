/*******************************************************************************
 Filename:  V27__DROP_OLD_DSK_TABLE
 Purpose:   Delete the old dsk attributes table which are no longer used.
 Date:      13-01-2020
********************************************************************************/

DELETE FROM SEC_GROUP_DSK_ATTRIBUTE;

DROP TABLE IF EXISTS SEC_GROUP_DSK_VALUE;
DROP TABLE IF EXISTS SEC_GROUP_DSK_ATTRIBUTE;

/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/