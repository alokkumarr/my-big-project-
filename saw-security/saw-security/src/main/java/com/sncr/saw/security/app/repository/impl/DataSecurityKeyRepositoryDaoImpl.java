package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.DataSecurityKeyRepository;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.dsk.AttributeValues;
import com.sncr.saw.security.common.bean.repo.dsk.DskDetails;
import com.sncr.saw.security.common.bean.repo.dsk.SecurityGroups;
import com.sncr.saw.security.common.bean.repo.dsk.UserAssignment;
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
    public Valid addSecurityGroups(SecurityGroups securityGroups,String createdBy) {
        Valid valid = new Valid();
        String addSql = "INSERT INTO `SEC_GROUP` " +
            "(`SEC_GROUP_NAME`,`Description`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`) "
            + "VALUES (?,?,1,now(),?)";

        if (this.isGroupNameExists(securityGroups.getSecurityGroupName())){
            valid.setValid(false);
            valid.setValidityMessage("Group Name already Exists !!");
        }
        else {
            try{
                int insertResult = jdbcTemplate.update(addSql,ps -> {
                    ps.setString(1,securityGroups.getSecurityGroupName());
                    ps.setString(2,securityGroups.getDescription());
                    ps.setString(3,createdBy);
                });
                logger.trace(insertResult + " Security Group created successfully.");
                valid.setValid(true);
                valid.setValidityMessage("Security Group created successfully.");
                // Here we need not to assign default user to newly created Group name. By default it should be left unassigned.
            }
            catch (Exception e) {
                logger.error(e.getMessage());
                valid.setValid(false);
                valid.setValidityMessage("Error in creating Security Group");
            }
        }
        return valid;
    }

    /**
     * is Group Name exists in TABLE
     * @param groupName
     * @return
     */
    public Boolean isGroupNameExists(String groupName)  {
        List<String> groupNames = this.getAllGroupNameList();
        Boolean flag = false ;

        for (String temp : groupNames)  {
            if(temp.equalsIgnoreCase(groupName))    {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * Get List of GroupNames from SEC_GROUP
     * @return GroupName List
     */
    public List<String> getAllGroupNameList()  {
        String fetchSql = "SELECT SEC_GROUP_NAME FROM SEC_GROUP WHERE ACTIVE_STATUS_IND = 1";

        List<String> groupNames = jdbcTemplate.query(fetchSql,
            preparedStatement -> {},
            resultSet -> {
                List<String> nameList = new ArrayList<>();
                while (resultSet.next()) {
                    SecurityGroups securityGroups = new SecurityGroups();
                    nameList.add(resultSet.getString("SEC_GROUP_NAME"));
                }
                return nameList;
            });

        return groupNames;
    }

    @Override
    public Valid updateSecurityGroups(List<String> oldNewGroups) {
        Valid valid = new Valid();
        Long groupSysID = this.getSecurityGroupSysId(oldNewGroups.get(2));
        String updateSql = "UPDATE SEC_GROUP SET SEC_GROUP_NAME = ?, DESCRIPTION = ? WHERE SEC_GROUP_SYS_ID = ?";
        if (this.isGroupNameExists(oldNewGroups.get(0))){
            valid.setValid(false);
            valid.setValidityMessage("Group Name already Exists !!");
        }
        else {
            try{
                int updateResult = jdbcTemplate.update(updateSql,ps-> {
                    ps.setString(1,oldNewGroups.get(0));
                    ps.setString(2,oldNewGroups.get(1));
                    ps.setLong(3, groupSysID);
                });
                valid.setValid(true);
                valid.setValidityMessage("Security Group updated successfully");
                logger.trace(updateResult + "Security Group updated successfully");
            }
            catch (Exception e) {
                logger.error(e.getMessage());
                valid.setValid(false);
                valid.setValidityMessage("Error in Updating Group Name ");
            }
        }

        return valid;
    }

    @Override
    public Boolean deleteSecurityGroups(String securityGroupName) {
        Long securityGroupSysId = this.getSecurityGroupSysId(securityGroupName);
        String delSql = "DELETE FROM SEC_GROUP WHERE SEC_GROUP_SYS_ID = ?";
        /** NOTE: Deleting a row from SEC_GROUP will inturn deletes corresponding reference rows in child tables.
         * That is, SEC_GROUP_DSK_ATTRIBUTE and SEC_GROUP_DSK_VALUE. So, no need of deleting its references in other tables.
         **/
        try{
            int deleteResult = jdbcTemplate.update(delSql,ps -> {
                ps.setLong(1,securityGroupSysId);
            });
            logger.trace(deleteResult + "Security Group deleted successfully");
            return this.unAssignGroupFromUser(securityGroupSysId);
            // Note : This is intentional here to update SEC_GROUP_SYS_ID as null in USERS, Whenever we delete a security group,
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Function to update user table to set SEC_GROUP_SYS_ID as null in case of corresponding group deletion.
     * @param secGroupSysId
     * @return
     */
    public Boolean unAssignGroupFromUser(Long secGroupSysId) {
        String updateSql = "UPDATE USERS SET SEC_GROUP_SYS_ID = null WHERE SEC_GROUP_SYS_ID = ? ";
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

    @Override
    public List<SecurityGroups> fetchSecurityGroupNames() {
        String fetchSql = "SELECT SEC_GROUP_NAME,DESCRIPTION FROM SEC_GROUP WHERE ACTIVE_STATUS_IND = 1";

        List<SecurityGroups> groupNames = jdbcTemplate.query(fetchSql,
            preparedStatement -> {},
            resultSet -> {
            List<SecurityGroups> nameList = new ArrayList<>();
            while (resultSet.next()) {
                SecurityGroups securityGroups = new SecurityGroups();
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
    public Long getSecurityGroupSysId(String securityGroupName) {
        Long groupSysId = null;
        String fetchSql = "SELECT SEC_GROUP_SYS_ID FROM SEC_GROUP WHERE SEC_GROUP_NAME = ?";
        try {
            String fetchedGroupSysId = jdbcTemplate.query(fetchSql,ps -> {
                    ps.setString(1,securityGroupName);
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
            String fetchedAttributeSysId = jdbcTemplate.query(fetchSql,ps -> {
                    ps.setLong(1,securityGroupSysId);
                    ps.setString(2,attributeName);
                },
                resultSet -> {
                    String val = null;
                    if(resultSet.next())
                    {
                        val = resultSet.getString("SEC_GROUP_DSK_ATTRIBUTE_SYS_ID");
                    }
                    return val;
                });
            attributeSysId = Long.valueOf(fetchedAttributeSysId);
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
    public Boolean addSecurityGroupDskAttributeValues(AttributeValues attributeValues) {

        Long groupSysId = this.getSecurityGroupSysId(attributeValues.getSecurityGroupName());

        if(groupSysId != null)  {
            String addSql = "INSERT INTO `sec_group_dsk_attribute` " +
                "(`SEC_GROUP_SYS_ID`,`ATTRIBUTE_NAME`) "
                + "VALUES (?,?)";
            String addValueSql = "INSERT INTO `sec_group_dsk_value` " +
                "(`SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`,`DSK_VALUE`) "
                + "VALUES (?,?)";;
            try{
                int addResult  = jdbcTemplate.update(addSql,ps -> {
                    ps.setLong(1,groupSysId);
                    ps.setString(2,attributeValues.getAttributeName());
                });
                logger.trace(addResult + " Attribute added to table SEC_GROUP_DSK_ATTRIBUTE.");

                Long attributeSysId = this.getSecurityGroupDskAttributeSysId(groupSysId,attributeValues.getAttributeName());
                if ( attributeSysId != null)    {
                    int addValResult = jdbcTemplate.update(addValueSql, ps -> {
                        ps.setLong(1,attributeSysId);
                        ps.setString(2,attributeValues.getValue());
                    });
                    logger.trace(addValResult + " Attribute value added to table SEC_GROUP_DSK_VALUE.");
                }
                else { logger.error("attributeSysId is NULL"); }
            }
            catch (Exception e) {
                logger.error(e.getMessage());
                return false;
            }
            return true;
        }
        else {
            logger.error("Couldn't able to get Sys Id");
            return false;
        }
    }

    @Override
    public List<String> fetchSecurityGroupDskAttributes(String securityGroupName) {
        Long secGroupSysId = this.getSecurityGroupSysId(securityGroupName);
        List<String> attributeNames = null;
        if (secGroupSysId != null)  {
            try{
                String fetchSql = "SELECT ATTRIBUTE_NAME FROM sec_group_dsk_attribute WHERE SEC_GROUP_SYS_ID = ?";

                attributeNames = jdbcTemplate.query(fetchSql,
                    preparedStatement -> {preparedStatement.setLong(1,secGroupSysId);},
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
            logger.error("secGroupSysId is null");
        }
        return attributeNames;
    }

    @Override
    public Boolean deleteSecurityGroupDskAttributeValues(List<String> dskList) {
        Long groupSysId = this.getSecurityGroupSysId(dskList.get(0));
        Long groupAttributeSysId = this.getSecurityGroupDskAttributeSysId(groupSysId,dskList.get(1));
        if (groupSysId != null) {
            try{
                String delSql = "DELETE FROM sec_group_dsk_attribute WHERE SEC_GROUP_DSK_ATTRIBUTE_SYS_ID = ? ";
                /**
                    Note : Deleting Atrribute row from sec_group_dsk_attribute inturn deletes corresponding value from sec_group_dsk_value,
                 Since we have defined a relation between the tables; So deleting other row from DSK_VALUE table is not required here.
                 **/
                int delResult = jdbcTemplate.update(delSql, ps -> {
                    ps.setLong(1,groupAttributeSysId);
                });
                logger.trace(delResult + " Attribute " + dskList.get(1) + " sucessfully removed");
                return true;
            }
            catch (Exception e) {
                logger.error(e.getMessage());
                return false;
            }
        }
        else {
            logger.error("Couldn't able to get Group Sys Id");
            return false;
        }
    }

    @Override
    public List<DskDetails> fetchDskAllAttributeValues(String secuityGroupName) {
        List<DskDetails> dskValueList = null;
        String fetchSql = "SELECT sg.CREATED_BY as CREATED_BY, sg.CREATED_DATE as CREATED_DATE, sga.ATTRIBUTE_NAME as ATTRIBUTE_NAME, sgv.DSK_VALUE as VALUE FROM SEC_GROUP sg, sec_group_dsk_attribute sga, sec_group_dsk_value sgv WHERE sg.SEC_GROUP_NAME = ? AND sg.SEC_GROUP_SYS_ID = sga.SEC_GROUP_SYS_ID AND sga.SEC_GROUP_DSK_ATTRIBUTE_SYS_ID = sgv.SEC_GROUP_DSK_ATTRIBUTE_SYS_ID ;";

        try{
            dskValueList = jdbcTemplate.query(fetchSql, ps -> {
                ps.setString(1,secuityGroupName);
            },resultSet -> {
                List<DskDetails> tempList = new ArrayList<>();
                while (resultSet.next())    {
                    DskDetails dskDetails = new DskDetails();
                    dskDetails.setSecurityGroupName(secuityGroupName);
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



    @Override
    public Boolean updateUser(String securityGroupName, String userId) {
        Long securityGroupSysId = this.getSecurityGroupSysId(securityGroupName);
        String updateSql = "UPDATE USERS SET SEC_GROUP_SYS_ID = ? WHERE USER_ID = ? ";
        try{
            int updateRes = jdbcTemplate.update(updateSql, ps -> {
               ps.setLong(1,securityGroupSysId);
               ps.setString(2,userId);
            });
            logger.trace(updateRes + "User Table updated ");
            return true;
        }
        catch (Exception e){
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public List<UserAssignment> getAlluserAssignments() {
        String fetchSql = "SELECT u.USER_ID as LoginID," +
            " r.role_Name as Role," +
            " u.FIRST_NAME as FirstName," +
            " u.LAST_NAME as LastName, u.email as Email," +
            " sg.SEC_GROUP_Name as GroupName," +
            " CASE when u.ACTIVE_STATUS_IND = 0 Then 'INACTIVE' ELSE 'ACTIVE' END as Status" +
            " FROM " +
            "USERS u, Roles r, SEC_GROUP sg " +
            "where u.ROLE_SYS_ID = r.ROLE_SYS_ID AND u.sec_group_sys_id = sg.sec_group_sys_id";

        List<UserAssignment> userAssignmentsList = new ArrayList<>();
        try{
            userAssignmentsList = jdbcTemplate.query(fetchSql, ps -> {}, reultSet -> {
                List<UserAssignment> tempList = new ArrayList<>();
                while(reultSet.next())  {
                    UserAssignment userAssignment = new UserAssignment();
                    userAssignment.setLoginId(reultSet.getString("LoginID"));
                    userAssignment.setRole(reultSet.getString("Role"));
                    userAssignment.setFirstName(reultSet.getString("FirstName"));
                    userAssignment.setLastName(reultSet.getString("LastName"));
                    userAssignment.setEmail(reultSet.getString("Email"));
                    userAssignment.setStatus(reultSet.getString("Status"));
                    userAssignment.setGroupName(reultSet.getString("GroupName"));
                    tempList.add(userAssignment);
                }
                logger.trace("Success in reading User Assginments");
                return tempList;
            });

        }
        catch (Exception e){
            logger.error(e.getMessage());
        }
        return userAssignmentsList;
    }

    @Override
    public Boolean updateAttributeValues(AttributeValues attributeValues) {
        String updateSql = "Update sec_group_dsk_value SET DSK_VALUE = ? where SEC_GROUP_DSK_ATTRIBUTE_SYS_ID = ? ";
        Long groupSysId = this.getSecurityGroupSysId(attributeValues.getSecurityGroupName());
        Long attributeSysId = this.getSecurityGroupDskAttributeSysId(groupSysId,attributeValues.getAttributeName());
        try{
            int updateRes = jdbcTemplate.update(updateSql, ps -> {
               ps.setString(1,attributeValues.getValue());
               ps.setLong(2,attributeSysId);
            });
            logger.trace("Value Updated successfully");
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }
}
