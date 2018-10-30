package com.sncr.saw.security.app.controller;

public class ServerResponseMessages {
    public static final String ADD_GROUPS_WITH_NON_ADMIN_ROLE = "Only Admin's can Add Security Groups";
    public static final String MODIFY_GROUP_WITH_NON_ADMIN_ROLE = "Only Admin's can Modify Security Groups";
    public static final String DELETE_GROUP_WITH_NON_ADMIN_ROLE = "Only Admin's can Delete Security Groups";
    public static final String ADD_ATTRIBUTES_WITH_NON_ADMIN_ROLE = "Only Admin's can Add Attributes ";
    public static final String MODIFY_ATTRIBUTES_WITH_NON_ADMIN_ROLE = "Only Admin's can Modify Attributes ";
    public static final String DELETE_ATTRIBUTES_WITH_NON_ADMIN_ROLE = "Only Admin's can DELETE Attributes ";
    public static final String MODIFY_USER_GROUPS_WITH_NON_ADMIN_ROLE = "Only Admin's can Modify user - groups ";


    public static final String SEC_GROUP_ADDED = "Security Group Created Successfully";
    public static final String SEC_GROUP_UPDATED = "Group added Successfully to User";
    public static final String SEC_GROUP_DELETED = "Security Group Deleted Successfully";
    public static final String SEC_GROUP_REMOVED = "Group successfully removed from user";

    public static final String ATTRIBUTE_VALUE_ADDED = "Attribute and value Added Successfully";
    public static final String ATTRIBUTE_VALUE_UPDATED = "Value Updated Successfully";
    public static final String ATTRIBUTE_VALUE_DELETED = "Attribute Deleted Successfully";

    public static final String GROUP_NAME_EXISTS = "Group Name already exists";
    public static final String ATTRIBUTE_NAME_EXISTS = "Attribute Name already exists";
    public static final String FIELDS_EXISTS = "Fields already exists";

    public static final String GROUP_NAME_LONG = "Group Name too Long, Range(1-255)";
    public static final String ATTRIBUTE_NAME_LONG = "Attribute Name too Long, Range(1-100)";
    public static final String VALUE_TOO_LONG = "Value too Long, Range(1-45)";
    public static final String DESCRIPTION_NAME_LONG = "Description Name too Long, Range(1-255)";

    public static final String FEILDS_NULL_OR_EMPTY = "Parameters can't be null or empty";
    public static final String ATTRIBUTE_NULL_OR_EMPTY = "Attribute Name can't be null or empty";
    public static final String VALUE_NULL_OR_EMPTY = "Value can't be null or empty";
    public static final String GROUP_NULL_OR_EMPTY = "Group Name can't be null or empty";
    public static final String GROUP_ID_NULL_EMPTY = "Group Id can't be NULL, 0 or negative";

    public static final String ATTRIBUTE_ID_NULL = "Attribute Id is NULL";
    public static final String CANT_GET_GROUP_ID = "Couldn't able to get Sys Id";

    public static final String UNASSIGN_GROUP_FROM_USER = "Failed to un-assign Group from User";
}
