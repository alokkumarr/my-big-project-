package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.controller.ServerResponseMessages;
import com.sncr.saw.security.app.repository.DataSecurityKeyRepository;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.dsk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DataSecurityKeyRepositoryDaoImpl implements
    DataSecurityKeyRepository {

    private static final Logger logger = LoggerFactory
        .getLogger(CustomerProductModuleFeatureRepositoryDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataSecurityKeyRepositoryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public DskValidity addSecurityGroups(SecurityGroups securityGroups,String createdBy,Long custId) {
        DskValidity valid = new DskValidity();
        String addSql = "INSERT INTO `SEC_GROUP` " +
            "(`CUSTOMER_SYS_ID`,`SEC_GROUP_NAME`,`Description`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`) "
            + "VALUES (?,?,?,1,now(),?)";
        securityGroups.setSecurityGroupName(securityGroups.getSecurityGroupName().trim());
        securityGroups.setDescription(securityGroups.getDescription().trim());

        if (custId == null || custId == 0)  {
            valid.setValid(false);
            valid.setValidityMessage("Customer Id Can't be null or 0");
            return  valid;
        }
        if ((securityGroups.getSecurityGroupName() == null || securityGroups.getSecurityGroupName().isEmpty()) && (securityGroups.getDescription() == null || securityGroups.getDescription().isEmpty())  ) {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.FEILDS_NULL_OR_EMPTY);
            valid.setError(ServerResponseMessages.FEILDS_NULL_OR_EMPTY);
            return valid;
        }
        if (securityGroups.getSecurityGroupName() == null || securityGroups.getSecurityGroupName().isEmpty())   {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.GROUP_NULL_OR_EMPTY);
            valid.setError(ServerResponseMessages.GROUP_NULL_OR_EMPTY);
            return valid;
        }
        else if (this.isGroupNameExists(securityGroups.getSecurityGroupName(),custId)){
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.GROUP_NAME_EXISTS);
            valid.setError(ServerResponseMessages.GROUP_NAME_EXISTS);
            return valid;
        }
        else if (securityGroups.getSecurityGroupName().length() > 255 ) {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.GROUP_NAME_LONG);
            valid.setError(ServerResponseMessages.GROUP_NAME_LONG);
            return valid;
        }
        else if ( securityGroups.getDescription().length() > 255 )  {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.DESCRIPTION_NAME_LONG);
            return valid;
        }
        else {
            try{
                int insertResult = jdbcTemplate.update(addSql,ps -> {
                    ps.setLong(1,custId);
                    ps.setString(2,securityGroups.getSecurityGroupName());
                    ps.setString(3,securityGroups.getDescription());
                    ps.setString(4,createdBy);
                });

                logger.trace(insertResult + " : " + ServerResponseMessages.SEC_GROUP_ADDED);
                valid.setValid(true);
                valid.setGroupId(this.getSecurityGroupSysId(securityGroups.getSecurityGroupName(),custId));
                valid.setGroupName(securityGroups.getSecurityGroupName());
                valid.setDescription(securityGroups.getDescription());
                valid.setValidityMessage(ServerResponseMessages.SEC_GROUP_ADDED);
                return valid;
                // Here we need not to assign default user to newly created Group name. By default it should be left unassigned.
            }
            catch (Exception e) {
                logger.error(e.getMessage());
                valid.setValid(false);
                valid.setValidityMessage("Error in creating Security Group");
                valid.setError("Error in creating Security Group");
                return valid;
            }
        }
    }

    /**
     * is Group Name exists in TABLE
     * @param groupName
     * @return
     */
    public Boolean isGroupNameExists(String groupName, Long custId)  {
        List<String> groupNames = this.getAllGroupNameList(custId);
        for (String temp : groupNames)  {
            if(temp.equalsIgnoreCase(groupName))    {
                return true;
            }
        }
        return false;
    }

    /**
     * is Group Name exists in TABLE
     * @param groupName
     * @return
     */
    public String getGroupNameForGid (Long groupId,String groupName)  {
        String fetchSql = "SELECT SEC_GROUP_NAME FROM SEC_GROUP WHERE SEC_GROUP_SYS_ID = ?";
        String name = jdbcTemplate.query(fetchSql,
            preparedStatement -> { preparedStatement.setLong(1,groupId);},
            resultSet -> {
                String temp = null;
                while (resultSet.next()) {
                    temp = resultSet.getString("SEC_GROUP_NAME");
                }
                return temp;
            });
        return name;
    }

    /**
     * is Description Name exists in TABLE
     * @param descName
     * @return
     */
    public Boolean isDescExists(String groupName,String descName)  {
        String fetchSql = "SELECT Description FROM SEC_GROUP WHERE SEC_GROUP_NAME = ?";
        String desc = jdbcTemplate.query(fetchSql,
            preparedStatement -> { preparedStatement.setString(1,groupName);},
            resultSet -> {
                String temp = null;
                while (resultSet.next()) {
                    temp = resultSet.getString("DESCRIPTION");
                }
                return temp;
            });

        if ( desc.equalsIgnoreCase(descName) )  {
            return true;
        }
        else { return false; }
    }


    /**
     * Get List of GroupNames from SEC_GROUP
     * @return GroupName List
     */
    public List<String> getAllGroupNameList(Long custId)  {
        String fetchSql = "SELECT SEC_GROUP_NAME FROM SEC_GROUP WHERE ACTIVE_STATUS_IND = 1 AND CUSTOMER_SYS_ID = ? ";

        List<String> groupNames = jdbcTemplate.query(fetchSql,
            preparedStatement -> { preparedStatement.setLong(1,custId); },
            resultSet -> {
                List<String> nameList = new ArrayList<>();
                while (resultSet.next()) {
                    nameList.add(resultSet.getString("SEC_GROUP_NAME"));
                }
                return nameList;
            });

        return groupNames;
    }

    @Override
    public DskValidity updateSecurityGroups(Long securityGroupId, List<String> oldNewGroups,Long custId) {
        DskValidity valid = new DskValidity();
        String updateSql = "UPDATE SEC_GROUP SET SEC_GROUP_NAME = ?, DESCRIPTION = ? WHERE SEC_GROUP_SYS_ID = ?";
        if ( (securityGroupId == null || securityGroupId <= 0)) {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.GROUP_ID_NULL_EMPTY);
            valid.setError(ServerResponseMessages.GROUP_ID_NULL_EMPTY);
            return valid;
        }
        if((oldNewGroups.get(0) == null || oldNewGroups.get(0).trim().isEmpty())
            && (oldNewGroups.get(1) ==null || oldNewGroups.get(1).trim().isEmpty()) ) {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.FEILDS_NULL_OR_EMPTY);
            valid.setError(ServerResponseMessages.FEILDS_NULL_OR_EMPTY);
            return valid;
        }
        if (oldNewGroups.get(0) == null || oldNewGroups.get(0).trim().isEmpty()) {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.GROUP_NULL_OR_EMPTY);
            valid.setError(ServerResponseMessages.GROUP_NULL_OR_EMPTY);
            return valid;
        }
        else if (this.isGroupNameExists(oldNewGroups.get(0).trim(),custId) && this.isDescExists(oldNewGroups.get(0).trim(),oldNewGroups.get(1).trim())){
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.FIELDS_EXISTS);
            valid.setError(ServerResponseMessages.FIELDS_EXISTS);
            return valid;
        }
        else if ( !(this.getGroupNameForGid(securityGroupId,oldNewGroups.get(0).trim()).equalsIgnoreCase(oldNewGroups.get(0).trim())) && this.isGroupNameExists(oldNewGroups.get(0).trim(),custId) )    {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.GROUP_NAME_EXISTS);
            valid.setError(ServerResponseMessages.GROUP_NAME_EXISTS);
            return valid;
        }
        else if (oldNewGroups.get(0).trim().length() > 255) {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.GROUP_NAME_LONG);
            valid.setError(ServerResponseMessages.GROUP_NAME_LONG);
            return valid;
        }
        else if (oldNewGroups.get(1).trim().length() > 255) {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.DESCRIPTION_NAME_LONG);
            valid.setError(ServerResponseMessages.DESCRIPTION_NAME_LONG);
            return valid;
        }
        else {
            try{
                int updateResult = jdbcTemplate.update(updateSql,ps-> {
                    ps.setString(1,oldNewGroups.get(0).trim());
                    ps.setString(2,oldNewGroups.get(1).trim());
                    ps.setLong(3, securityGroupId);
                });
                valid.setValid(true);
                valid.setGroupId(this.getSecurityGroupSysId(oldNewGroups.get(0).trim(),custId));
                valid.setGroupName(oldNewGroups.get(0).trim());
                valid.setDescription(oldNewGroups.get(1).trim());
                valid.setValidityMessage(ServerResponseMessages.SEC_GROUP_UPDATED);
                logger.trace(updateResult + ServerResponseMessages.SEC_GROUP_UPDATED);
                return valid;
            }
            catch (Exception e) {
                logger.error(e.getMessage());
                valid.setValid(false);
                valid.setValidityMessage("Error in Updating Group Name ");
                valid.setError("Error in Updating Group Name");
                return valid;
            }
        }
    }

    @Override
    public Valid deleteSecurityGroups(Long securityGroupId) {
        Valid valid = new Valid();
        String delSql = "DELETE FROM SEC_GROUP WHERE SEC_GROUP_SYS_ID = ?";
        /** NOTE: Deleting a row from SEC_GROUP will inturn deletes corresponding reference rows in child tables.
         * That is, SEC_GROUP_DSK_ATTRIBUTE and SEC_GROUP_DSK_VALUE. So, no need of deleting its references in other tables.
         **/
        if ( securityGroupId <= 0 || securityGroupId == null)   {
            logger.error(ServerResponseMessages.GROUP_ID_NULL_EMPTY);
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.GROUP_ID_NULL_EMPTY);
            valid.setError(ServerResponseMessages.GROUP_ID_NULL_EMPTY);
            return valid;
        }
        else{
            try{
                int deleteResult = jdbcTemplate.update(delSql,ps -> {
                    ps.setLong(1,securityGroupId);
                });
                if(unAssignGroupFromUser(securityGroupId))  {
                    logger.trace(deleteResult + ServerResponseMessages.SEC_GROUP_DELETED);
                    valid.setValidityMessage(ServerResponseMessages.SEC_GROUP_DELETED);
                    valid.setValid(true);
                    return valid;
                }
                else {
                    logger.error(ServerResponseMessages.UNASSIGN_GROUP_FROM_USER);
                    valid.setValidityMessage(ServerResponseMessages.UNASSIGN_GROUP_FROM_USER);
                    valid.setError(ServerResponseMessages.UNASSIGN_GROUP_FROM_USER);
                    valid.setValid(false);
                    return valid;
                }
                // Note : This is intentional here to update SEC_GROUP_SYS_ID as null in USERS, Whenever we delete a security group,
            }
            catch (Exception e) {
                logger.error(e.getMessage());
                valid.setValid(false);
                valid.setValidityMessage("Error in deleting Security Group !!");
                valid.setError(ServerResponseMessages.UNASSIGN_GROUP_FROM_USER);
                return valid;
            }
        }
    }

    /**
     * Function to update user table to set SEC_GROUP_SYS_ID as null in case of corresponding group deletion.
     * @param secGroupSysId
     * @return
     */
    public Boolean unAssignGroupFromUser(Long secGroupSysId) {
        String updateSql = "UPDATE USERS SET SEC_GROUP_SYS_ID = null WHERE SEC_GROUP_SYS_ID = ? ";
        if ( secGroupSysId <= 0 || secGroupSysId == null)   {
            logger.error("security group Sys Id can't be null or empty!!");
            return false;
        }
        else {
            try{
                int updateRes = jdbcTemplate.update(updateSql, ps -> {
                    ps.setLong(1,secGroupSysId);
                });
                logger.trace(updateRes + "User Table updated ");
                return true;
            }
            catch (Exception e){
                logger.error(e.getMessage());
                return false;
            }
        }
    }

    @Override
    public List<SecurityGroups> fetchSecurityGroupNames(Long custId) {
        String fetchSql = "SELECT SEC_GROUP_SYS_ID,SEC_GROUP_NAME,DESCRIPTION FROM SEC_GROUP WHERE ACTIVE_STATUS_IND = 1 AND CUSTOMER_SYS_ID = ? ";

        List<SecurityGroups> groupNames = jdbcTemplate.query(fetchSql,
            preparedStatement -> { preparedStatement.setLong(1,custId); },
            resultSet -> {
            List<SecurityGroups> nameList = new ArrayList<>();
            while (resultSet.next()) {
                SecurityGroups securityGroups = new SecurityGroups();
                securityGroups.setSecGroupSysId(resultSet.getLong("SEC_GROUP_SYS_ID"));
                securityGroups.setSecurityGroupName(resultSet.getString("SEC_GROUP_NAME"));
                securityGroups.setDescription(resultSet.getString("DESCRIPTION"));
                nameList.add(securityGroups);
            }
            return nameList;
        });

        return groupNames;
    }

    /**
     * Function returns corresponding sec_group_sys_id when group name is given.
     * @param securityGroupName
     * @return
     */
    public Long getSecurityGroupSysId(String securityGroupName, Long custId) {
        Long groupSysId = null;
        String fetchSql = "SELECT SEC_GROUP_SYS_ID FROM SEC_GROUP WHERE SEC_GROUP_NAME = ? AND CUSTOMER_SYS_ID = ? ";
        try {
            String fetchedGroupSysId = jdbcTemplate.query(fetchSql,ps -> {
                    ps.setString(1,securityGroupName);
                    ps.setLong(2,custId);
                },
                resultSet -> {
                    String val = null;
                    if(resultSet.next())
                    {
                        val = resultSet.getString("SEC_GROUP_SYS_ID");
                    }
                    return val;
                });
            groupSysId = Long.valueOf(fetchedGroupSysId);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
        return groupSysId;
    }

    /**
     * Functions returns corresponding AttributeSysId
     * @param securityGroupSysId
     * @param attributeName
     * @return
     */
    public Long getSecurityGroupDskAttributeSysId(Long securityGroupSysId,String attributeName) {
        Long attributeSysId = null;
        String fetchSql = "SELECT SEC_GROUP_DSK_ATTRIBUTE_SYS_ID FROM SEC_GROUP_DSK_ATTRIBUTE WHERE SEC_GROUP_SYS_ID = ? AND ATTRIBUTE_NAME = ?";
        try {
            Long fetchedAttributeSysId = jdbcTemplate.query(fetchSql,ps -> {
                    ps.setLong(1,securityGroupSysId);
                    ps.setString(2,attributeName);
                },
                resultSet -> {
                    Long val = null;
                    if(resultSet.next())
                    {
                        val = resultSet.getLong("SEC_GROUP_DSK_ATTRIBUTE_SYS_ID");
                    }
                    return val;
                });
            attributeSysId = fetchedAttributeSysId;
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
        return attributeSysId;
    }

    /**
     * Function returns the list of Attribute sys id for corresponding security group.
     * @param securityGroupSysId
     * @return
     */
    public List<Long> getSecurityGroupDskAttributeSysIdList(Long securityGroupSysId) {
        List<Long> attributeSysId = new ArrayList<>();
        String fetchSql = "SELECT SEC_GROUP_DSK_ATTRIBUTE_SYS_ID FROM SEC_GROUP_DSK_ATTRIBUTE WHERE SEC_GROUP_SYS_ID = ?";
        try {
            List<String> fetchedAttributeSysId = jdbcTemplate.query(fetchSql,ps -> {
                    ps.setLong(1,securityGroupSysId);
                },
                resultSet -> {
                    List<String> val = new ArrayList<>();
                    while (resultSet.next())
                    {
                        val.add(resultSet.getString("SEC_GROUP_DSK_ATTRIBUTE_SYS_ID"));
                    }
                    return val;
                });
//            attributeSysId = fetchedAttributeSysId;
            fetchedAttributeSysId.stream().forEach(str -> {attributeSysId.add(Long.valueOf(str));});
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
        return attributeSysId;
    }


    @Override
    public Valid addSecurityGroupDskAttributeValues(Long securityGroupId, AttributeValues attributeValues) {
        Valid valid = new Valid();
        Long groupAttrSysId ;
        if(securityGroupId == null || securityGroupId <= 0 )    {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.GROUP_ID_NULL_EMPTY);
            valid.setError(ServerResponseMessages.GROUP_ID_NULL_EMPTY);
            return valid;
        }
        else if ( attributeValues.getAttributeName() == null)   {
            valid.setValid(false);
            valid.setValidityMessage("Attribute Name can't be null");
            return valid;
        }
        else if ( attributeValues.getValue() == null)  {
            valid.setValid(false);
            valid.setValidityMessage("Value can't be null");
            return valid;
        }
        else if ( (attributeValues.getAttributeName().isEmpty()) && (attributeValues.getValue().isEmpty()) )    {
            valid.setValid(false);
            valid.setValidityMessage(" Fields Can't be null or empty!! ");
            valid.setError("Fields Can't be null or empty!!");
            return valid;
        }
        else if ( attributeValues.getAttributeName() == null || attributeValues.getAttributeName().isEmpty() )   {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.ATTRIBUTE_NULL_OR_EMPTY);
            valid.setError(ServerResponseMessages.ATTRIBUTE_NULL_OR_EMPTY);
            return valid;
        }
        else if ( attributeValues.getValue() == null || attributeValues.getValue().isEmpty())  {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.VALUE_NULL_OR_EMPTY);
            valid.setError(ServerResponseMessages.VALUE_NULL_OR_EMPTY);
            return valid;
        }
        else if ( attributeValues.getAttributeName().trim().length() > 100 )    {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.ATTRIBUTE_NAME_LONG);
            valid.setError(ServerResponseMessages.ATTRIBUTE_NAME_LONG);
            return valid;
        }
        else if ( attributeValues.getValue().trim().length() > 45 ) {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.VALUE_TOO_LONG);
            valid.setError(ServerResponseMessages.VALUE_TOO_LONG);
            return valid;
        }
        else {
             groupAttrSysId = this.getSecurityGroupDskAttributeSysId(securityGroupId,attributeValues.getAttributeName());
             attributeValues.setAttributeName(attributeValues.getAttributeName().trim());
        }
        /**
         * Note : Here, we are checking whether the Attribute name exists for the respective Group, if So, we are responding with an error saying the attribute name already exists.
         * In future this could be solved directly by adding constraint in Table definition and also here am keeping in my that the DB needs to Altered to add Customer relationship with DSK's.
         */
        if (groupAttrSysId == null) {
            if(securityGroupId != null)  {
                String addSql = "INSERT INTO `sec_group_dsk_attribute` " +
                    "(`SEC_GROUP_SYS_ID`,`ATTRIBUTE_NAME`) "
                    + "VALUES (?,?)";
                String addValueSql = "INSERT INTO `sec_group_dsk_value` " +
                    "(`SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`,`DSK_VALUE`) "
                    + "VALUES (?,?)";;
                try{
                    int addResult  = jdbcTemplate.update(addSql,ps -> {
                        ps.setLong(1,securityGroupId);
                        ps.setString(2,attributeValues.getAttributeName());
                    });
                    logger.trace(addResult + " Attribute added to table SEC_GROUP_DSK_ATTRIBUTE.");

                    Long attributeSysId = this.getSecurityGroupDskAttributeSysId(securityGroupId,attributeValues.getAttributeName());
                    if ( attributeSysId != null)    {
                        int addValResult = jdbcTemplate.update(addValueSql, ps -> {
                            ps.setLong(1,attributeSysId);
                            ps.setString(2,attributeValues.getValue());
                        });
                        logger.trace(addValResult + ServerResponseMessages.ATTRIBUTE_VALUE_ADDED + " to  SEC_GROUP_DSK_VALUE.");
                        valid.setValid(true);
                        valid.setValidityMessage(ServerResponseMessages.ATTRIBUTE_VALUE_ADDED);
                        return valid;
                    }
                    else { logger.error(ServerResponseMessages.ATTRIBUTE_ID_NULL); }
                }
                catch (Exception e) {
                    logger.error(e.getMessage());
                    valid.setValidityMessage("Error in adding Attribute value");
                    valid.setError("Error in adding Attribute value");
                    valid.setValid(false);
                }
                return valid;
            }
            else {
                logger.error(ServerResponseMessages.CANT_GET_GROUP_ID);
                valid.setValidityMessage(ServerResponseMessages.CANT_GET_GROUP_ID);
                valid.setError(ServerResponseMessages.CANT_GET_GROUP_ID);
                valid.setValid(false);
                return valid;
            }
        }
        else    {
            logger.error(ServerResponseMessages.ATTRIBUTE_NAME_EXISTS);
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.ATTRIBUTE_NAME_EXISTS);
            valid.setError(ServerResponseMessages.ATTRIBUTE_NAME_EXISTS);
            return valid;
        }
    }

    @Override
    public List<String> fetchSecurityGroupDskAttributes(Long securityGroupId) {
        List<String> attributeNames = null;
        if (securityGroupId != null)  {
            try{
                String fetchSql = "SELECT ATTRIBUTE_NAME FROM sec_group_dsk_attribute WHERE SEC_GROUP_SYS_ID = ?";

                attributeNames = jdbcTemplate.query(fetchSql,
                    preparedStatement -> {preparedStatement.setLong(1,securityGroupId);},
                    resultSet -> {
                        List<String> nameList = new ArrayList<>();
                        while (resultSet.next()) {
                            nameList.add(resultSet.getString("ATTRIBUTE_NAME"));
                        }
                        return nameList;
                    });
            }
            catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        else {
            logger.error(ServerResponseMessages.GROUP_ID_NULL_EMPTY);
        }
        return attributeNames;
    }

    @Override
    public Valid deleteSecurityGroupDskAttributeValues(List<String> dskList) {
        Valid valid =  new Valid();
        Long groupSysId = Long.parseLong(dskList.get(0).trim());
        Long groupAttributeSysId = this.getSecurityGroupDskAttributeSysId(groupSysId,dskList.get(1).trim());
        if (dskList.get(0).trim().isEmpty() || dskList.get(0) == null )  {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.GROUP_ID_NULL_EMPTY);
            valid.setError(ServerResponseMessages.GROUP_ID_NULL_EMPTY);
            return valid;
        }
        if (  dskList.get(1) == null || dskList.get(1).trim().isEmpty() )  {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.ATTRIBUTE_NULL_OR_EMPTY);
            valid.setError(ServerResponseMessages.ATTRIBUTE_NULL_OR_EMPTY);
            return valid;
        }
        else if ( dskList.get(0).trim().length() > 255 )    {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.GROUP_NAME_LONG);
            valid.setError(ServerResponseMessages.GROUP_NAME_LONG);
            return valid;
        }
        else if ( dskList.get(1).trim().isEmpty() || dskList.get(1) == null )   {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.ATTRIBUTE_NULL_OR_EMPTY);
            valid.setError(ServerResponseMessages.ATTRIBUTE_NULL_OR_EMPTY);
            return valid;
        }
        else if ( dskList.get(1).trim().length() > 100 )    {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.ATTRIBUTE_NAME_LONG);
            valid.setError(ServerResponseMessages.ATTRIBUTE_NAME_LONG);
            return valid;
        }
        else if (groupAttributeSysId == null || groupSysId == null)    {
            valid.setValid(false);
            valid.setValidityMessage("Field no longer exists!! Please refresh the page.");
            valid.setError("Field no longer exists!! Please refresh the page");
            return valid;
        }
        else if (groupSysId != null) {
            try{
                String delSql = "DELETE FROM sec_group_dsk_attribute WHERE SEC_GROUP_DSK_ATTRIBUTE_SYS_ID = ? ";
                /**
                    Note : Deleting Attribute row from sec_group_dsk_attribute inturn deletes corresponding value from sec_group_dsk_value,
                 Since we have defined a relation between the tables; So deleting other row from DSK_VALUE table is not required here.
                 **/
                int delResult = jdbcTemplate.update(delSql, ps -> {
                    ps.setLong(1,groupAttributeSysId);
                });
                logger.trace(delResult + " Attribute " + dskList.get(1).trim() + " successfully removed");
                valid.setValid(true);
                valid.setValidityMessage(" Attribute " + dskList.get(1).trim() + " successfully removed");
                return valid;
            }
            catch (Exception e) {
                logger.error(e.getMessage());
                valid.setValid(false);
                valid.setValidityMessage("Error in deleting Attribute !!");
                valid.setError("Error in deleting Attribute");
                return valid;
            }
        }
        else {
            logger.error("Error in deleting Attribute");
            valid.setValid(false);
            valid.setValidityMessage("Error in deleting Attribute");
            valid.setError("Error in deleting Attribute");
            return valid;
        }
    }

    @Override
    public List<DskDetails> fetchDskAllAttributeValues(Long securityGroupId) {
        List<DskDetails> dskValueList = null;
        String fetchSql = "SELECT sg.CREATED_BY as CREATED_BY, sg.CREATED_DATE as CREATED_DATE, sga.ATTRIBUTE_NAME as ATTRIBUTE_NAME, sgv.DSK_VALUE as VALUE FROM SEC_GROUP sg, sec_group_dsk_attribute sga, sec_group_dsk_value sgv WHERE sg.SEC_GROUP_SYS_ID = ? AND sg.SEC_GROUP_SYS_ID = sga.SEC_GROUP_SYS_ID AND sga.SEC_GROUP_DSK_ATTRIBUTE_SYS_ID = sgv.SEC_GROUP_DSK_ATTRIBUTE_SYS_ID ";
        if(securityGroupId != null || securityGroupId != 0) {
            try{
                dskValueList = jdbcTemplate.query(fetchSql, ps -> {
                    ps.setLong(1,securityGroupId);
                },resultSet -> {
                    List<DskDetails> tempList = new ArrayList<>();
                    while (resultSet.next())    {
                        DskDetails dskDetails = new DskDetails();
                        dskDetails.setCreated_by(resultSet.getString("CREATED_BY"));
                        dskDetails.setCreated_date(resultSet.getString("CREATED_DATE"));
                        dskDetails.setAttributeName(resultSet.getString("ATTRIBUTE_NAME"));
                        dskDetails.setValue(resultSet.getString("VALUE"));
                        tempList.add(dskDetails);
                    }
                    return tempList;
                });
            }
            catch (Exception e){
                logger.error(e.getMessage());
            }

            return dskValueList;
        }
        else {
            return null;
        }
    }



    @Override
    public Valid updateUser(String securityGroupName, Long userSysId, Long custId) {
        Valid valid = new Valid();
        Long securityGroupSysId = this.getSecurityGroupSysId(securityGroupName,custId);
        String updateSql = "UPDATE USERS SET SEC_GROUP_SYS_ID = ? WHERE USER_SYS_ID = ? ";
        if( securityGroupName == null || securityGroupName.isEmpty() || userSysId <= 0 || userSysId == null)   {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.FEILDS_NULL_OR_EMPTY);
            valid.setError(ServerResponseMessages.FEILDS_NULL_OR_EMPTY);
            return valid;
        }
        else if ( securityGroupName.equalsIgnoreCase( "-1") )   {
            try{
                // If Group name is removed from User. We are identifying this action by accepting -1 as part of name,So We need to set sec_group_sys_id as null in users table.
                int updateRes = jdbcTemplate.update(updateSql, ps -> {
                    ps.setObject(1, null);
                    ps.setLong(2,userSysId);
                });
                logger.trace(updateRes + "User Table updated ");
                valid.setValid(true);
                valid.setValidityMessage(ServerResponseMessages.SEC_GROUP_REMOVED);
                return valid;
            }
            catch (Exception e){
                logger.error(e.getMessage());
                valid.setValid(false);
                valid.setValidityMessage("error in updating group user");
                valid.setError("error in updating group user");
                return valid;
            }
        }
        else {
            try{
                int updateRes = jdbcTemplate.update(updateSql, ps -> {
                    ps.setLong(1,securityGroupSysId);
                    ps.setLong(2,userSysId);
                });
                logger.trace(updateRes + "User Table updated ");
                valid.setValid(true);
                valid.setValidityMessage(securityGroupName + " : " +ServerResponseMessages.SEC_GROUP_UPDATED);
                return valid;
            }
            catch (Exception e){
                logger.error(e.getMessage());
                valid.setValid(false);
                valid.setValidityMessage("Error in updating group user");
                valid.setError("Error in updating group user");
                return valid;
            }
        }
    }

    @Override
    public List<UserAssignment> getAllUserAssignments(Long custId) {
        String fetchSql = " Select distinct u.USER_ID as LoginID," +
                            " u.USER_SYS_ID as UserSysId," +
                            " r.role_Name as Role," +
                            " u.FIRST_NAME as FirstName," +
                            " u.LAST_NAME as LastName," +
                            " u.email as Email," +
                            " CASE when u.ACTIVE_STATUS_IND = 0 Then 'INACTIVE' ELSE 'ACTIVE' END as Status," +
                            " sg.sec_group_name as Group_name " +
                            " From USERS u" +
                            " inner join " +
                            " ROLES r " +
                            " on (u.ROLE_SYS_ID = r.ROLE_SYS_ID AND u.customer_sys_id = r.customer_sys_id AND u.customer_sys_id = ?) " +
                            " inner join customers c " +
                            " on (u.customer_sys_id = c.customer_sys_id AND r.customer_sys_id = c.customer_sys_id AND c.ACTIVE_STATUS_IND = 1) " +
                            " left outer join" +
                            " SEC_GROUP sg " +
                            " on (u.customer_sys_id = sg.customer_sys_id AND u.sec_group_sys_id = sg.sec_group_sys_id)";

        /** NOTE : The below commented code (sql) in replacement of above sql can lists out only user assignments who have associated security groups.
                If in future, if there is a requirement like to list only user assignments who have associated security groups. we can directly replace this sql.
         **/

        /** " Select distinct u.USER_ID as LoginID," +
         " u.USER_SYS_ID as UserSysId," +
         " r.role_Name as Role," +
         " u.FIRST_NAME as FirstName," +
         " u.LAST_NAME as LastName," +
         " u.email as Email," +
         " CASE when u.ACTIVE_STATUS_IND = 0 Then 'INACTIVE' ELSE 'ACTIVE' END as Status," +
         " sg.sec_group_name as Group_name " +
         " From USERS u" +
         " inner join " +
         " ROLES r " +
         " on (u.ROLE_SYS_ID = r.ROLE_SYS_ID AND u.customer_sys_id = r.customer_sys_id AND u.customer_sys_id = ?) " +
         " inner join customers c " +
         " on (u.customer_sys_id = c.customer_sys_id AND r.customer_sys_id = c.customer_sys_id AND c.ACTIVE_STATUS_IND = 1) " +
         " inner join" +
         " SEC_GROUP sg " +
         " on (u.customer_sys_id = sg.customer_sys_id AND u.sec_group_sys_id = sg.sec_group_sys_id)"; **/


        List<UserAssignment> userAssignmentsList = new ArrayList<>();
        try{
            userAssignmentsList = jdbcTemplate.query(fetchSql, ps -> { ps.setLong(1,custId );}, reultSet -> {
                List<UserAssignment> tempList = new ArrayList<>();
                while(reultSet.next())  {
                    UserAssignment userAssignment = new UserAssignment();
                    userAssignment.setUserSysId(reultSet.getLong("UserSysId"));
                    userAssignment.setLoginId(reultSet.getString("LoginID"));
                    userAssignment.setRole(reultSet.getString("Role"));
                    userAssignment.setFirstName(reultSet.getString("FirstName"));
                    userAssignment.setLastName(reultSet.getString("LastName"));
                    userAssignment.setEmail(reultSet.getString("Email"));
                    userAssignment.setStatus(reultSet.getString("Status"));
                    userAssignment.setGroupName(reultSet.getString("Group_name"));
                    tempList.add(userAssignment);
                }
                logger.trace("Success in reading User Assignments");
                return tempList;
            });

        }
        catch (Exception e){
            logger.error(e.getMessage());
        }
        return userAssignmentsList;
    }

    @Override
    public Valid updateAttributeValues(Long securityGroupId, AttributeValues attributeValues) {
        Valid valid =  new Valid();
        attributeValues.setAttributeName(attributeValues.getAttributeName().trim());
        attributeValues.setValue(attributeValues.getValue().trim());
        if ( securityGroupId <= 0 || securityGroupId == null )  {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.GROUP_ID_NULL_EMPTY);
            valid.setError(ServerResponseMessages.GROUP_ID_NULL_EMPTY);
            return valid;
        }
        if ( (attributeValues.getAttributeName() == null || attributeValues.getAttributeName().isEmpty()) && (attributeValues.getValue().isEmpty() || attributeValues.getValue() == null))   {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.ATTRIBUTE_NULL_OR_EMPTY);
            valid.setError(ServerResponseMessages.ATTRIBUTE_NULL_OR_EMPTY);
            return valid;
        }
        if ( attributeValues.getValue() == null || attributeValues.getValue().isEmpty() )   {
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.VALUE_NULL_OR_EMPTY);
            valid.setError(ServerResponseMessages.VALUE_NULL_OR_EMPTY);
            return valid;
        }
        else {
            String updateSql = "Update sec_group_dsk_value SET DSK_VALUE = ? where SEC_GROUP_DSK_ATTRIBUTE_SYS_ID = ? ";
            Long groupSysId = securityGroupId;
            Long attributeSysId = this.getSecurityGroupDskAttributeSysId(groupSysId,attributeValues.getAttributeName());
            try{
                int updateRes = jdbcTemplate.update(updateSql, ps -> {
                    ps.setString(1,attributeValues.getValue());
                    ps.setLong(2,attributeSysId);
                });
                logger.trace(updateRes + ServerResponseMessages.ATTRIBUTE_VALUE_UPDATED);
                valid.setValid(true);
                valid.setValidityMessage(ServerResponseMessages.ATTRIBUTE_VALUE_UPDATED);
                return valid;
            }
            catch (Exception e) {
                logger.error(e.getMessage());
                valid.setValid(false);
                valid.setValidityMessage("Error in updating value !!");
                valid.setError("Error in updating value");
                return valid;
            }
        }
    }

}
