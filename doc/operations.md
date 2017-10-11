# Introduction

This document describes how to install and configure SAW Security in
an environment.

# Prerequisites

Before starting an installation of SAW Security ensure the following
is provided:

- A mail relay host, used to send out password reset emails.  The mail
  relay host is configured in the SAW environment configuration

# Installing

To install SAW Security, use the SAW bundle package which coordinates
installation of SAW modules.  See the SAW bundle package Operations
Guide for closer details about installing.

# Configuring

At the moment there are no SAW Security parameters to configure.

# DSK Feature

SAW platform feature for row level filtering using a data security key (DSK).

#### Pre-Requisite:

DSK configured columns should be present in ALL of the data objects/artifacts referenced in the metrics

#### Steps to create DSK in SAW-security

##### Step 1: 
Create the Security Group in SEC_GROUP table.
    
    ########################## Create SEC_GROUP samaple script ##############################
    INSERT INTO `SEC_GROUP` (`SEC_GROUP_SYS_ID`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`)     VALUES ('1', '1', '2017-10-04', 'system');
    
##### Step 2: 
Create DSK attribute (fields/columns name) for corresponding security group (SEC_GROUP created in step 1).
    
    ########################## DSK Attribute Sample script #############################
    INSERT INTO `sec_group_dsk_attribute` (`SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`, `SEC_GROUP_SYS_ID`, `ATTRIBUTE_NAME`) VALUES ('1', '1', 'SESSION_ID');
    INSERT INTO `sec_group_dsk_attribute` (`SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`, `SEC_GROUP_SYS_ID`, `ATTRIBUTE_NAME`) VALUES ('2', '1', 'CONTENT_CLASS');
    
##### Step 3: 
Create DSK values for corresponding DSK attribute (DSK Attribute created in step 2).

    ########################## DSK Value sample script ###############################
    INSERT INTO `sec_group_dsk_value` (`SEC_GROUP_DSK_VALUE_SYS_ID`, `SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`, `DSK_VALUE`) VALUES ('1', '1', 'AFF2948C-DCFF-4944-8553-51435518AF67');
    INSERT INTO `sec_group_dsk_value` (`SEC_GROUP_DSK_VALUE_SYS_ID`, `SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`, `DSK_VALUE`) VALUES ('2', '1', '945ca612-d3ad-4e6e-9c92-7cff86730235');
    INSERT INTO `sec_group_dsk_value` (`SEC_GROUP_DSK_VALUE_SYS_ID`, `SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`, `DSK_VALUE`) VALUES ('3', '2', 'VIDEOS'); 
    
##### Step 4: 
Map the SEC_GROUP to users to apply the DSK filter.
    
    ########################## Update User with DSK script ##############################
    UPDATE USERS SET SEC_GROUP_SYS_ID = '3' WHERE USER_ID = 'analyst.dsk.mct.report';

### Important Note:  
If any metrics contains more than one data object as analysis for report then DSK attribute should be configured with dataObjectName.columnName. Example: For MCT_SESSION data object, DSK Attribute name should be MCT_SESSION.SESSION_ID.