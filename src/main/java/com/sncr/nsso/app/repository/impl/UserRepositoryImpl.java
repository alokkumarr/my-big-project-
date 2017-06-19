package com.sncr.nsso.app.repository.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.management.relation.RoleList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.sncr.nsso.app.repository.UserRepository;
import com.sncr.nsso.common.bean.ResetValid;
import com.sncr.nsso.common.bean.Role;
import com.sncr.nsso.common.bean.Ticket;
import com.sncr.nsso.common.bean.User;
import com.sncr.nsso.common.bean.Valid;
import com.sncr.nsso.common.bean.repo.CustomerProductModuleFeature;
import com.sncr.nsso.common.bean.repo.PasswordDetails;
import com.sncr.nsso.common.bean.repo.ProductModuleFeature;
import com.sncr.nsso.common.bean.repo.ProductModuleFeaturePrivileges;
import com.sncr.nsso.common.bean.repo.ProductModules;
import com.sncr.nsso.common.bean.repo.Products;
import com.sncr.nsso.common.bean.repo.TicketDetails;
import com.sncr.nsso.common.bean.repo.admin.role.RoleDetails;
import com.sncr.nsso.common.bean.repo.analysis.Analysis;
import com.sncr.nsso.common.bean.repo.analysis.AnalysisSummary;
import com.sncr.nsso.common.bean.repo.analysis.AnalysisSummaryList;
import com.sncr.nsso.common.util.Ccode;
import com.sncr.nsso.common.util.DateUtil;

/**
 * This class is used to do CRUD operations on the oracle data base having nsso
 * tables.
 * 
 * @author girija.sankar
 * 
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

	/**
	 * @author gsan0003
	 *
	 */

	private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Authenticates sso user.
	 * 
	 * @param masterLoginId
	 * @param password
	 * @return
	 */

	public boolean[] authenticateUser(String masterLoginId, String password) {
		boolean isAuthenticated = false;
		boolean isPasswordActive = false;
		boolean[] ret = { false, false };

		password = Ccode.cencode(password).trim();
		String pwd = password;
		String sql = "SELECT U.PWD_MODIFIED_DATE, C.PASSWORD_EXPIRY_DAYS " + "FROM USERS U, CUSTOMERS C "
				+ "WHERE U.USER_ID = ? AND U.ENCRYPTED_PASSWORD = ? " + " AND U.ACTIVE_STATUS_IND = '1' "
				+ "AND U.CUSTOMER_SYS_ID=C.CUSTOMER_SYS_ID";
		try {
			PasswordDetails passwordDetails = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, masterLoginId);
					preparedStatement.setString(2, pwd);
				}
			}, new UserRepositoryImpl.PwdDetailExtractor());

			if (passwordDetails != null) {
				isAuthenticated = true;
				if (!isPwdExpired(passwordDetails.getPwdModifiedDate(), passwordDetails.getPasswordExpiryDays())) {
					isPasswordActive = true;
				}
				ret[0] = isAuthenticated;
				ret[1] = isPasswordActive;
			}
		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while authenticating user : " + e.getMessage(), null, e);
		}

		return ret;
	}

	private boolean isPwdExpired(Date pwd_Modified_Date, int pwd_Expiration_Days) {
		String sysDate = DateUtil.getSysDate(); // This is in MM/dd/yyyy

		/*
		 * Date pwd_Expiration_Date = addDaystoDate(new Date(pwd_Modified_Date),
		 * Integer.parseInt(pwd_Expiration_Days));
		 */
		Date pwd_Expiration_Date = addDays(pwd_Modified_Date, pwd_Expiration_Days);

		int dateDiff = DateUtil.getNumberOfDays(DateUtil.getDateString(pwd_Expiration_Date, DateUtil.PATTERN_MMDDYYYY),
				sysDate);

		if (dateDiff > 0)// i.e sysdate > pwd_Expiration_Date
		{
			return true;
		} else {
			return false;
		}
	}

	private Date addDays(Date aDate, int daysToAdd) {
		Calendar calendar = Calendar.getInstance();

		if (aDate == null) {
			return null;
		}

		calendar.setTime(aDate);
		calendar.add(Calendar.DATE, daysToAdd);

		return calendar.getTime();
	}

	@Override
	public String rstchangePassword(String loginId, String newPass) {
		String message = null;
		// if new pass is != last 5 in pass history
		// change the pass
		// update pass history
		String encNewPass = Ccode.cencode(newPass).trim();
		String sql = "SELECT U.USER_SYS_ID FROM USERS U, CONTACT_INFO C, USER_CONTACT UC WHERE  U.USER_SYS_ID = UC.USER_SYS_ID "
				+ "AND UC.CONTACT_INFO_SYS_ID=C.CONTACT_INFO_SYS_ID AND U.USER_ID = ?";

		try {
			String userSysId = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, loginId);
				}
			}, new UserRepositoryImpl.StringExtractor("user_sys_id"));

			/*
			 * if(userSysId == null){ return
			 * "Email id does not matching to the User Id"; }
			 */

			sql = "SELECT PH.* FROM PASSWORD_HISTORY PH WHERE PH.USER_SYS_ID=? ORDER BY PH.DATE_OF_CHANGE DESC ";

			message = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, userSysId);
				}
			}, new UserRepositoryImpl.PasswordValidator(encNewPass));
			if (message != null && message.equals("valid")) {
				String sysId = System.currentTimeMillis() + "";

				sql = "INSERT INTO PASSWORD_HISTORY (PASSWORD_HISTORY_SYS_ID,USER_SYS_ID,PASSWORD,DATE_OF_CHANGE)"
						+ " VALUES(?,?,?,SYSDATE())";

				jdbcTemplate.update(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1, sysId);
						preparedStatement.setString(2, userSysId);
						preparedStatement.setString(3, encNewPass);
					}
				});

				sql = "UPDATE USERS U  SET U.ENCRYPTED_PASSWORD=? ,  "
						+ "U.PWD_MODIFIED_DATE=SYSDATE(),U.MODIFIED_BY ='CHANGE_PASSWORD' WHERE U.USER_SYS_ID=?";
				jdbcTemplate.update(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1, encNewPass);
						preparedStatement.setString(2, userSysId);
					}
				});
				message = null;
				/*
				 * sql =
				 * "UPDATE RESET_PWD_DTLS RS  SET RS.VALID=0, RS.INACTIVATED_DATE=SYSDATE() WHERE RS.USER_ID='"
				 * + loginId + "' AND RS.VALID=1"; jdbcTemplate.update(sql);
				 */
			}
		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while creating BO details for user " + e.getMessage(), loginId, null,
					e);
			message = "Error encountered while changing password.";
		}

		return message;
	}

	@Override
	public String changePassword(String loginId, String newPass, String oldPass) {
		String message = null;
		// if old password is correct
		// if new pass is != last 5 in pass history
		// change the pass
		// update pass history
		String encOldPass = Ccode.cencode(oldPass).trim();
		String encNewPass = Ccode.cencode(newPass).trim();
		String sql = "SELECT U.USER_SYS_ID" + " FROM USERS U" + " WHERE U.USER_ID = ?"
				+ " and  U.ENCRYPTED_PASSWORD = ?";

		try {
			String userSysId = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, loginId);
					preparedStatement.setString(2, encOldPass);
				}
			}, new UserRepositoryImpl.StringExtractor("user_sys_id"));

			if (userSysId == null) {
				message = "Value provided for old Password did not match.";
				return message;
			}
			sql = "select PH.* from PASSWORD_HISTORY PH where PH.user_sys_id=? order by PH.DATE_OF_CHANGE desc ";

			message = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, userSysId);
				}
			}, new UserRepositoryImpl.PasswordValidator(encNewPass));
			if (message != null && message.equals("valid")) {
				String sysId = System.currentTimeMillis() + "";

				sql = "insert into PASSWORD_HISTORY (PASSWORD_HISTORY_SYS_ID,USER_SYS_ID,PASSWORD,DATE_OF_CHANGE)"
						+ " values(?,?,?,sysdate())";

				jdbcTemplate.update(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1, sysId);
						preparedStatement.setString(2, userSysId);
						preparedStatement.setString(3, encNewPass);
					}
				});

				sql = "update USERS U  set U.ENCRYPTED_PASSWORD=?"
						+ " ,  U.PWD_MODIFIED_DATE=sysdate(),U.MODIFIED_BY ='change_password' where U.USER_SYS_ID=?";
				int i = jdbcTemplate.update(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1, encNewPass);
						preparedStatement.setString(2, userSysId);
					}
				});
				if (i == 1) {
					message = "Password Successfully Changed.";
				}
			}
		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while creating BO details for user " + e.getMessage(), loginId, null,
					e);
			message = "Error encountered while changing password.";
		}

		return message;
	}

	// new methods for ticket updation in DB

	/**
	 * inserts ticket into sso db
	 * 
	 * @param Ticket
	 * @throws Exception
	 * 
	 */
	@Override
	public void insertTicketDetails(Ticket ticket) throws Exception {
		int isValid = ticket.isValid() ? 1 : 0;
		try {
			String insertSql = "insert into TICKET(TICKET_ID,WINDOW_ID,MASTER_LOGIN_ID,USER_NAME,PRODUCT_CODE,ROLE_TYPE,CREATED_TIME,VALID_UPTO,VALID_INDICATOR,CREATED_DATE,UPDATED_DATE,INACTIVATED_DATE,DESCRIPTION) "
					+ "values (?,?,?,?,?,?,?,?,?,sysdate(),sysdate(),?,?)";
			// ticket.setRoleType("Basic");
			Object[] params = new Object[] { ticket.getTicketId(), ticket.getWindowId(), ticket.getMasterLoginId(),
					ticket.getUserFullName(), ticket.getDefaultProdID(), ticket.getRoleType(), ticket.getCreatedTime(),
					ticket.getValidUpto(), isValid, null, ticket.getValidityReason() };
			int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
					Types.VARCHAR, Types.BIGINT, Types.BIGINT, Types.SMALLINT, Types.DATE, Types.VARCHAR };
			jdbcTemplate.update(insertSql, params, types);

		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while adding ticket details for user " + e.getMessage(), null, e);
			throw e;
		}
	}

	@Override
	public void insertResetPasswordDtls(String userId, String randomHash, Long createdTime, long validUpto) {
		try {
			String insertSql = "insert into RESET_PWD_DTLS(RESET_PWD_DTLS_SYS_ID, USER_ID, RANDOM_HASHCODE,VALID, CREATED_TIME,VALID_UPTO,CREATED_DATE,INACTIVATED_DATE) "
					+ "values (?,?,?,?,?,?,sysdate(),?)";
			Object[] params = new Object[] { System.currentTimeMillis() + "", userId, randomHash, 1, createdTime,
					validUpto, null };
			int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.SMALLINT, Types.BIGINT,
					Types.BIGINT, Types.DATE };

			String sql = "UPDATE RESET_PWD_DTLS RS  SET RS.VALID=0, RS.INACTIVATED_DATE=SYSDATE() WHERE RS.USER_ID=? "
					+ " AND RS.VALID=1";
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, userId);
				}
			});

			jdbcTemplate.update(insertSql, params, types);

		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while adding secret code details for user " + e.getMessage(), null, e);
			throw e;
		}
	}

	@Override
	public ResetValid validateResetPasswordDtls(String randomHash) {
		try {
			String sql = "SELECT VALID_UPTO, USER_ID FROM RESET_PWD_DTLS  WHERE RANDOM_HASHCODE=? AND VALID=1";
			return jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, randomHash);
				}
			}, new UserRepositoryImpl.ResetValidityExtractor());
			// logger.info("secret code details inserted for user Id "+ userId);
		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while validating the reset password link for random key" + randomHash
					+ " : " + e.getMessage(), null, e);
			throw e;
		}
	}

	@Override
	public String verifyUserCredentials(String masterLoginId, String email, String fName) {
		String message = null;
		String sql = "select u.active_status_ind, u.first_name" + " from users u" + " where u.user_id = ?";

		try {
			User user = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, masterLoginId);
				}
			}, new UserRepositoryImpl.UserCredentialsExtractor());
			if (user == null) {
				message = "'User Name' provided is not identified in the system, please re-verify.";
				return message;
			} else if (user.getActiveStatusInd().equals("Inactive")) {
				message = "User is inactive, please contact administrator.";
				return message;
			} else if (user.getFirstName() != null && user.getFirstName() != null
					&& !user.getFirstName().equalsIgnoreCase(fName)) {
				message = "'First Name' provided is not identified in the system, please re-verify.";
				return message;
			}

			sql = "select ci.email from users u, user_contact uc, contact_info ci " + " where u.user_id=?"
					+ " and u.user_sys_id=uc.user_sys_id " + " and uc.contact_info_sys_id = ci.contact_info_sys_id  ";

			String emailFrmDB = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, masterLoginId);
				}
			}, new UserRepositoryImpl.StringExtractor("email"));
			if (!emailFrmDB.equals(email)) {
				message = "'Email Address' provided is not identified in the system, please re-verify.";
				return message;
			}
		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while resetting password for user " + e.getMessage(), null, e);
			message = "Error encountered while resetting password.";
		}
		return message;
	}

	@Override
	public String updateUserPass(String masterLoginId, String newPassEncrp) {
		String sql = "update users u set u.encrypted_password='" + newPassEncrp + "' "
				+ " , u.pwd_modified_date = sysdate() , u.date_of_change = sysdate(), u.modified_by ='reset_pass_req' where u.user_id='"
				+ masterLoginId + "'";
		String message = null;
		try {

			Integer count = jdbcTemplate.update(sql);

			if (count == 0) {
				message = "No user found for updating new password value.";
			}
		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while resetting password for user " + e.getMessage(), masterLoginId,
					null, e);
			message = "Error encountered while updating new password value.";
		}
		return message;
	}

	@Override
	public void invalidateTicket(String ticketId, String validityMessage) {
		try {
			String updateSql = "update TICKET set valid_indicator=0,inactivated_Date=sysdate(),DESCRIPTION=? where ticket_id=?";
			jdbcTemplate.update(updateSql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, validityMessage);
					preparedStatement.setString(2, ticketId);
				}
			});
			// logger.info("Ticket got invalidated for ticketId: " + ticketId);
		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while invalidating the ticket" + e.getMessage(), null, e);
			throw e;
		}

	}

	private class PwdDetailExtractor implements ResultSetExtractor<PasswordDetails> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.jdbc.core.ResultSetExtractor#extractData(java
		 * .sql.ResultSet)
		 */
		@Override
		public PasswordDetails extractData(ResultSet rs) throws SQLException, DataAccessException {
			PasswordDetails passwordDetails = null;
			if (rs.next()) {
				passwordDetails = new PasswordDetails();
				passwordDetails.setPwdModifiedDate(rs.getDate("PWD_MODIFIED_DATE"));
				passwordDetails.setPasswordExpiryDays(rs.getInt("PASSWORD_EXPIRY_DAYS"));
			}
			return passwordDetails;
		}
	}

	@Override
	public void prepareTicketDetails(User user, Boolean onlyDef) {
		String masterLoginId = user.getMasterLoginId();

		// TO DO: Modify the below queries to form a single Query

		// Generic User Details
		try {
			String sql = "SELECT U.USER_ID,U.USER_SYS_ID,U.FIRST_NAME,U.MIDDLE_NAME,U.LAST_NAME,C.COMPANY_NAME,C.CUSTOMER_SYS_ID,C.CUSTOMER_CODE,C.LANDING_PROD_SYS_ID,R.ROLE_CODE,R.ROLE_TYPE,R.DATA_SECURITY_KEY "
					+ "	FROM USERS U, CUSTOMERS C, ROLES R WHERE U.CUSTOMER_SYS_ID=C.CUSTOMER_SYS_ID AND R.ROLE_SYS_ID=U.ROLE_SYS_ID "
					+ "	AND C.ACTIVE_STATUS_IND = U.ACTIVE_STATUS_IND AND  U.ACTIVE_STATUS_IND = R.ACTIVE_STATUS_IND AND R.ACTIVE_STATUS_IND = 1 AND U.USER_ID=? ";
			TicketDetails ticketDetails = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, masterLoginId);
				}
			}, new UserRepositoryImpl.PrepareTicketExtractor());

			// Cust - Prod
			String sql3 = "SELECT DISTINCT P.PRODUCT_NAME,P.PRODUCT_DESC,P.PRODUCT_CODE,P.PRODUCT_SYS_ID,PV.PRIVILEGE_CODE FROM CUSTOMER_PRODUCTS CP JOIN PRODUCTS P "
					+ " ON(CP.PRODUCT_SYS_ID = P.PRODUCT_SYS_ID) JOIN `PRIVILEGES` PV ON(CP.CUST_PROD_SYS_ID=PV.CUST_PROD_SYS_ID) "
					+ " JOIN ROLES R ON(R.ROLE_SYS_ID=PV.ROLE_SYS_ID) where CP.PRODUCT_SYS_ID = P.PRODUCT_SYS_ID AND P.ACTIVE_STATUS_IND = CP.ACTIVE_STATUS_IND AND CP.ACTIVE_STATUS_IND = 1 AND "
					+ " PV.ACTIVE_STATUS_IND=1 AND PV.CUST_PROD_MOD_SYS_ID=0 AND R.ACTIVE_STATUS_IND = 1 AND CP.CUSTOMER_SYS_ID=? AND R.ROLE_CODE=?";

			ticketDetails.setProducts(jdbcTemplate.query(sql3, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, ticketDetails.getCustID());
					preparedStatement.setString(2, ticketDetails.getRoleCode());
				}
			}, new UserRepositoryImpl.PrepareProductExtractor()));

			// Cust - Prod - Modules
			String sql4 = "SELECT DISTINCT P.PRODUCT_CODE, M.MODULE_NAME, M.MODULE_DESC, M.MODULE_CODE, CPM.MODULE_URL, CPM.DEFAULT, CPM.CUST_PROD_MOD_SYS_ID, PV.PRIVILEGE_CODE FROM CUSTOMER_PRODUCT_MODULES CPM"
					+ " INNER JOIN USERS U ON (U.CUSTOMER_SYS_ID=CPM.CUSTOMER_SYS_ID) INNER JOIN PRODUCT_MODULES PM ON (CPM.PROD_MOD_SYS_ID=PM.PROD_MOD_SYS_ID)"
					+ " INNER JOIN CUSTOMER_PRODUCTS CP ON (CP.CUST_PROD_SYS_ID=CPM.CUST_PROD_SYS_ID) INNER JOIN CUSTOMERS C ON (C.CUSTOMER_SYS_ID=CP.CUSTOMER_SYS_ID)"
					+ " INNER JOIN MODULES M ON (M.MODULE_SYS_ID=PM.MODULE_SYS_ID) INNER JOIN PRODUCTS P ON (PM.PRODUCT_SYS_ID=P.PRODUCT_SYS_ID) JOIN `PRIVILEGES` PV ON(CP.CUST_PROD_SYS_ID=PV.CUST_PROD_SYS_ID AND CPM.CUST_PROD_MOD_SYS_ID=PV.CUST_PROD_MOD_SYS_ID) "
					+ " JOIN ROLES R ON(R.ROLE_SYS_ID=PV.ROLE_SYS_ID) "
					+ " WHERE upper(U.USER_ID)=? AND P.ACTIVE_STATUS_IND = CP.ACTIVE_STATUS_IND AND CP.ACTIVE_STATUS_IND = 1 AND PV.ACTIVE_STATUS_IND=1 AND PV.CUST_PROD_MOD_FEATURE_SYS_ID=0"
					+ " AND C.ACTIVE_STATUS_IND=1 AND P.ACTIVE_STATUS_IND=1 AND M.ACTIVE_STATUS_IND=1 AND R.ROLE_CODE=? AND R.ACTIVE_STATUS_IND = 1";

			/**
			 * if(onlyDef){ sql4 = sql4 + " AND CPM.DEFAULT = 1"; }
			 **/

			ArrayList<ProductModules> prodMods = jdbcTemplate.query(sql4, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, masterLoginId);
					preparedStatement.setString(2, ticketDetails.getRoleCode());
				}
			}, new UserRepositoryImpl.PrepareProdModExtractor());
			if (prodMods != null) {
				// ticketDetails.setProductModules(prodMods);
				// Cust - Prod - Modules - Features
				String sql5 = "SELECT DISTINCT U.USER_SYS_ID, U.CUSTOMER_SYS_ID ,C.CUSTOMER_SYS_ID ,CP.CUST_PROD_SYS_ID,CP.CUSTOMER_SYS_ID, CPMF.CUST_PROD_MOD_FEATURE_SYS_ID,CPMF.FEATURE_TYPE, PV.PRIVILEGE_CODE "
						+ ", P.PRODUCT_CODE ,M.MODULE_CODE ,CPMF.FEATURE_NAME,CPMF.FEATURE_DESC,CPMF.FEATURE_CODE,CPMF.DEFAULT_URL ,CPMF.DEFAULT FROM USERS U "
						+ "INNER JOIN CUSTOMERS  C ON (C.CUSTOMER_SYS_ID=U.CUSTOMER_SYS_ID) INNER JOIN CUSTOMER_PRODUCTS CP ON (CP.CUSTOMER_SYS_ID=C.CUSTOMER_SYS_ID) "
						+ "INNER JOIN CUSTOMER_PRODUCT_MODULES CPM ON (CPM.CUST_PROD_SYS_ID=CP.CUST_PROD_SYS_ID) INNER JOIN CUSTOMER_PRODUCT_MODULE_FEATURES CPMF "
						+ "ON (CPMF.CUST_PROD_MOD_SYS_ID=CPM.CUST_PROD_MOD_SYS_ID) INNER JOIN PRODUCTS P ON (P.PRODUCT_SYS_ID=CP.PRODUCT_SYS_ID) INNER JOIN "
						+ "PRODUCT_MODULES PM ON (PM.PROD_MOD_SYS_ID=CPM.PROD_MOD_SYS_ID) INNER JOIN MODULES M ON(M.MODULE_SYS_ID=PM.MODULE_SYS_ID) "
						+ "INNER JOIN `PRIVILEGES` PV "
						+ "ON (CPMF.CUST_PROD_MOD_FEATURE_SYS_ID=PV.CUST_PROD_MOD_FEATURE_SYS_ID) INNER JOIN ROLES R ON(R.ROLE_SYS_ID=U.ROLE_SYS_ID AND R.ROLE_SYS_ID=PV.ROLE_SYS_ID) "
						+ " WHERE UPPER(U.USER_ID)= ? "
						+ "AND CPMF.ACTIVE_STATUS_IND = 1  AND P.ACTIVE_STATUS_IND = M.ACTIVE_STATUS_IND AND "
						+ "CP.ACTIVE_STATUS_IND = PM.ACTIVE_STATUS_IND AND CP.ACTIVE_STATUS_IND = CPM.ACTIVE_STATUS_IND "
						+ "AND CPM.ACTIVE_STATUS_IND = CPMF.ACTIVE_STATUS_IND AND R.ROLE_CODE=? AND R.ACTIVE_STATUS_IND = 1 "
						+ "AND PV.ANALYSIS_SYS_ID=0";

				/**
				 * if(onlyDef){ sql5 = sql5 + " AND CPM.DEFAULT = 1 AND
				 * CPMF.DEFAULT = 1"; }
				 **/
				ArrayList<ProductModuleFeature> prodModFeatr = jdbcTemplate.query(sql5, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1, masterLoginId);
						preparedStatement.setString(2, ticketDetails.getRoleCode());
					}
				}, new UserRepositoryImpl.PrepareProdModFeatureExtractor());

				// Roles and privileges
				/**
				 * String sql2 = "SELECT DISTINCT P.PRIVILEGE_CODE,
				 * P.PRIVILEGE_NAME, P.PRIVILEGE_DESC, CPMF.FEATURE_NAME FROM
				 * CUSTOMER_PRODUCT_MODULE_FEATURES CPMF " + "INNER JOIN
				 * `PRIVILEGES` P ON (P.CUST_PROD_MOD_FEATURE_SYS_ID =
				 * CPMF.CUST_PROD_MOD_FEATURE_SYS_ID) INNER JOIN " +
				 * "ROLE_PRIVILEGES RP ON(RP.PRIVILEGE_SYS_ID =
				 * P.PRIVILEGE_SYS_ID AND RP.ACTIVE_STATUS_IND =
				 * P.ACTIVE_STATUS_IND) " + "INNER JOIN CUSTOMER_PRODUCT_MODULES
				 * CPM ON(CPM.CUST_PROD_MOD_SYS_ID = CPMF.CUST_PROD_MOD_SYS_ID)
				 * INNER JOIN " + "CUSTOMER_PRODUCTS CP ON(CP.CUST_PROD_SYS_ID =
				 * CPM.CUST_PROD_SYS_ID) INNER JOIN USERS U
				 * ON(U.ROLE_SYS_ID=RP.ROLE_SYS_ID AND U.CUSTOMER_SYS_ID =
				 * CP.CUSTOMER_SYS_ID) " + "INNER JOIN ROLES R
				 * ON(R.ROLE_SYS_ID=U.ROLE_SYS_ID and
				 * RP.ROLE_SYS_ID=R.ROLE_SYS_ID) INNER JOIN " + "`PRIVILEGES` PV
				 * ON (CPMF.CUST_PROD_MOD_FEATURE_SYS_ID=PV.
				 * CUST_PROD_MOD_FEATURE_SYS_ID) WHERE " + "CP.ACTIVE_STATUS_IND
				 * = CPM.ACTIVE_STATUS_IND AND CPM.ACTIVE_STATUS_IND =
				 * CPMF.ACTIVE_STATUS_IND " + "AND CPMF.ACTIVE_STATUS_IND =
				 * U.ACTIVE_STATUS_IND AND CPMF.ACTIVE_STATUS_IND = 1 AND
				 * UPPER(U.USER_ID)=?";
				 * 
				 * if(onlyDef){ sql2 = sql2 + " AND CPM.DEFAULT = 1 AND
				 * CPMF.DEFAULT = 1"; }
				 * 
				 * ArrayList<ProductModuleFeaturePrivileges> prodModFeatrPriv =
				 * jdbcTemplate.query(sql2, new PreparedStatementSetter() {
				 * public void setValues(PreparedStatement preparedStatement)
				 * throws SQLException { preparedStatement.setString(1,
				 * masterLoginId); } }, new
				 * UserRepositoryImpl.PrepareProdModFeaturePrivExtractor());
				 * 
				 * /**ArrayList<ProductModuleFeaturePrivileges>
				 * productModuleFeaturePrivilegesSorted = null;
				 * ArrayList<ProductModuleFeature> prodModFeatrSorted = null;
				 * ArrayList<ProductModuleFeatures> prodModFeatrs = new
				 * ArrayList<ProductModuleFeatures>(); ProductModuleFeatures
				 * prodModFeatures = null; for (int i = 0; i <
				 * ticketDetails.getProductModules().size(); i++) {
				 * prodModFeatrSorted = new ArrayList<ProductModuleFeature>();
				 * prodModFeatures = new ProductModuleFeatures(); for (int y =
				 * 0; y < prodModFeatr.size(); y++) { if
				 * (ticketDetails.getProductModules().get(i).getProdCode()
				 * .equals(prodModFeatr.get(y).getProdCode()) &&
				 * prodModFeatr.get(y).getProdModCode()
				 * .equals(ticketDetails.getProductModules().get(i).
				 * getProductModCode())) {
				 * 
				 * productModuleFeaturePrivilegesSorted = new
				 * ArrayList<ProductModuleFeaturePrivileges>(); for (int z = 0;
				 * z < prodModFeatrPriv.size(); z++) { if
				 * (prodModFeatr.get(y).getProdModFeatureName()
				 * .equals(prodModFeatrPriv.get(z).getProdModFeatrName())) {
				 * 
				 * productModuleFeaturePrivilegesSorted.add(prodModFeatrPriv.get
				 * (z));
				 * 
				 * }
				 * 
				 * } prodModFeatr.get(y).setProdModFeatrPriv(
				 * productModuleFeaturePrivilegesSorted);
				 * prodModFeatrSorted.add(prodModFeatr.get(y));
				 * 
				 * } }
				 * prodModFeatures.setProdCode(ticketDetails.getProductModules()
				 * .get(i).getProdCode());
				 * prodModFeatures.setProdModCode(ticketDetails.
				 * getProductModules().get(i).getProductModCode());
				 * prodModFeatures.setProdModDesc(ticketDetails.
				 * getProductModules().get(i).getProductModDesc());
				 * prodModFeatures.setProdModFeatrPriv(prodModFeatrSorted);
				 * prodModFeatures.setprodModName(ticketDetails.
				 * getProductModules().get(i).getProductModName());
				 * prodModFeatrs.add(prodModFeatures);
				 * 
				 * } ticketDetails.setProductModuleFeatures(prodModFeatrs);
				 **/
				ArrayList<ProductModuleFeature> productModuleFeatureParentSorted = null;
				ArrayList<ProductModuleFeature> productModuleFeatureChildSorted = null;
				ArrayList<ProductModuleFeature> prodModFeatrSorted = null;
				ArrayList<ProductModuleFeature> prodModFeatrChildSorted = null;
				ArrayList<ProductModules> prodModSorted = null;
				for (int i = 0; i < ticketDetails.getProducts().size(); i++) {
					prodModSorted = new ArrayList<ProductModules>();
					for (int x = 0; x < prodMods.size(); x++) {
						if (ticketDetails.getProducts().get(i).getProductCode().equals(prodMods.get(x).getProdCode())) {
							prodModFeatrSorted = new ArrayList<ProductModuleFeature>();
							productModuleFeatureParentSorted = new ArrayList<ProductModuleFeature>();
							productModuleFeatureChildSorted = new ArrayList<ProductModuleFeature>();
							for (int y = 0; y < prodModFeatr.size(); y++) {
								if (prodModFeatr.get(y).getProdModFeatureType().split("_")[0].equals("PARENT")) {
									productModuleFeatureParentSorted.add(prodModFeatr.get(y));
								} else if (prodModFeatr.get(y).getProdModFeatureType().split("_")[0].equals("CHILD")) {
									productModuleFeatureChildSorted.add(prodModFeatr.get(y));
								}
							}

							for (int y = 0; y < productModuleFeatureParentSorted.size(); y++) {
								prodModFeatrChildSorted = new ArrayList<ProductModuleFeature>();
								for (int z = 0; z < productModuleFeatureChildSorted.size(); z++) {
									if (productModuleFeatureParentSorted.get(y).getProdModFeatureType().split("_")[1]
											.equals(productModuleFeatureChildSorted.get(z).getProdModFeatureType()
													.split("_")[1])) {
										prodModFeatrChildSorted.add(productModuleFeatureChildSorted.get(z));
									}

								}
								productModuleFeatureParentSorted.get(y)
										.setProductModuleSubFeatures(prodModFeatrChildSorted);
							}

							for (int y = 0; y < productModuleFeatureParentSorted.size(); y++) {

								if (ticketDetails.getProducts().get(i).getProductCode()
										.equals(productModuleFeatureParentSorted.get(y).getProdCode())
										&& productModuleFeatureParentSorted.get(y).getProdModCode()
												.equals(prodMods.get(x).getProductModCode())) {

									/**
									 * productModuleFeaturePrivilegesSorted =
									 * new
									 * ArrayList<ProductModuleFeaturePrivileges>
									 * (); for (int z = 0; z <
									 * prodModFeatrPriv.size(); z++) { if
									 * (prodModFeatr.get(y).
									 * getProdModFeatureName()
									 * .equals(prodModFeatrPriv.get(z).
									 * getProdModFeatrName())) {
									 * 
									 * productModuleFeaturePrivilegesSorted.add(
									 * prodModFeatrPriv.get(z));
									 * 
									 * }
									 * 
									 * }
									 * prodModFeatr.get(y).setProdModFeatrPriv(
									 * productModuleFeaturePrivilegesSorted);
									 **/
									prodModFeatrSorted.add(productModuleFeatureParentSorted.get(y));

								}
							}
							prodMods.get(x).setProdModFeature(prodModFeatrSorted);
							prodModSorted.add(prodMods.get(x));
						}
					}
					ticketDetails.getProducts().get(i).setProductModules(prodModSorted);
				}

			}
			if (ticketDetails != null) {
				user.setTicketDetails(ticketDetails);
			}

		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while preparing the Ticket Details for user " + e.getMessage(), null,
					e);
		}
	}

	@Override
	public Ticket getTicketDetails(String ticketId) {
		Ticket ticket = null;
		String sql = "SELECT MASTER_LOGIN_ID, PRODUCT_CODE, ROLE_TYPE, USER_NAME, WINDOW_ID FROM TICKET WHERE TICKET_ID=?";
		try {
			ticket = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, ticketId);
				}
			}, new UserRepositoryImpl.TicketDetailExtractor());
		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while get Ticket Details for ticketId : " + e.getMessage(), null, e);
		}

		return ticket;

	}

	private class PrepareTicketExtractor implements ResultSetExtractor<TicketDetails> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.jdbc.core.ResultSetExtractor#extractData(java
		 * .sql.ResultSet)
		 */
		@Override
		public TicketDetails extractData(ResultSet rs) throws SQLException, DataAccessException {
			TicketDetails ticketDetails = null;
			String firstName = null;
			String lastName = null;
			String middleName = null;

			while (rs.next()) {
				ticketDetails = new TicketDetails();
				ticketDetails.setCompName(rs.getString("company_name"));
				ticketDetails.setCustID(rs.getString("customer_sys_id"));
				ticketDetails.setCustCode(rs.getString("customer_code"));
				ticketDetails.setRoleType(rs.getString("role_type"));
				ticketDetails.setRoleCode(rs.getString("role_code"));
				ticketDetails.setLandingProd(rs.getString("landing_prod_sys_id"));
				ticketDetails.setDataSKey(rs.getString("data_security_key"));
				ticketDetails.setUserId(rs.getLong("user_sys_id"));
				if (firstName == null) {
					firstName = rs.getString("first_name");
					lastName = rs.getString("last_name");
					middleName = rs.getString("middle_name");
				}
				String name = null;
				if (firstName != null) {
					name = firstName != null ? firstName.trim() : firstName;
				}
				if (middleName != null) {
					name = name + " " + (middleName != null ? middleName.trim() : middleName);
				}
				if (lastName != null) {
					name = name + " " + (lastName != null ? lastName.trim() : lastName);
				}
				ticketDetails.setUserFullName(name);
			}
			return ticketDetails;
		}
	}

	private class PrepareProductExtractor implements ResultSetExtractor<ArrayList<Products>> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.jdbc.core.ResultSetExtractor#extractData(java
		 * .sql.ResultSet)
		 */
		@Override
		public ArrayList<Products> extractData(ResultSet rs) throws SQLException, DataAccessException {
			Products products = null;
			ArrayList<Products> prodList = new ArrayList<Products>();

			while (rs.next()) {
				products = new Products();
				products.setProductCode(rs.getString("product_code"));
				products.setProductDesc(rs.getString("product_desc"));
				products.setProductName(rs.getString("product_name"));
				products.setProductID(rs.getString("product_sys_id"));
				products.setPrivilegeCode(rs.getLong("privilege_code"));
				prodList.add(products);
			}
			return prodList;
		}
	}

	private class PrepareProdModExtractor implements ResultSetExtractor<ArrayList<ProductModules>> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.jdbc.core.ResultSetExtractor#extractData(java
		 * .sql.ResultSet)
		 */
		@Override
		public ArrayList<ProductModules> extractData(ResultSet rs) throws SQLException, DataAccessException {
			ProductModules productModules = null;
			ArrayList<ProductModules> prodModList = new ArrayList<ProductModules>();

			while (rs.next()) {
				productModules = new ProductModules();
				productModules.setProdCode(rs.getString("product_code"));
				productModules.setProductModCode(rs.getString("module_code"));
				productModules.setProductModDesc(rs.getString("module_desc"));
				productModules.setProductModName(rs.getString("module_name"));
				productModules.setProductModID(rs.getString("cust_prod_mod_sys_id"));
				productModules.setModuleURL(rs.getString("module_url"));
				productModules.setDefaultMod(rs.getString("default"));
				productModules.setPrivilegeCode(rs.getLong("privilege_code"));
				prodModList.add(productModules);
			}
			return prodModList;
		}
	}

	private class PrepareProdModFeatureExtractor implements ResultSetExtractor<ArrayList<ProductModuleFeature>> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.jdbc.core.ResultSetExtractor#extractData(java
		 * .sql.ResultSet)
		 */
		@Override
		public ArrayList<ProductModuleFeature> extractData(ResultSet rs) throws SQLException, DataAccessException {
			ProductModuleFeature productModulesFeatr = null;
			ArrayList<ProductModuleFeature> prodModFeaList = new ArrayList<ProductModuleFeature>();

			while (rs.next()) {
				productModulesFeatr = new ProductModuleFeature();
				productModulesFeatr.setProdModFeatureDesc(rs.getString("feature_desc"));
				productModulesFeatr.setProdCode(rs.getString("product_code"));
				productModulesFeatr.setProdModCode(rs.getString("module_code"));
				productModulesFeatr.setProdModFeatureName(rs.getString("feature_name"));
				productModulesFeatr.setProdModFeatrCode(rs.getString("feature_code"));
				productModulesFeatr.setDefaultURL(rs.getString("default_url"));
				productModulesFeatr.setDefaultFeature(rs.getString("default"));
				productModulesFeatr.setPrivilegeCode(rs.getLong("privilege_code"));
				productModulesFeatr.setProdModFeatureID(rs.getLong("cust_prod_mod_feature_sys_id"));
				productModulesFeatr.setProdModFeatureType(rs.getString("feature_type"));
				prodModFeaList.add(productModulesFeatr);
			}
			return prodModFeaList;
		}
	}

	/*
	 * private class PrepareProdModFeaturePrivExtractor implements
	 * ResultSetExtractor<ArrayList<ProductModuleFeaturePrivileges>> {
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.jdbc.core.ResultSetExtractor#extractData(java
	 * .sql.ResultSet)
	 * 
	 * @Override public ArrayList<ProductModuleFeaturePrivileges>
	 * extractData(ResultSet rs) throws SQLException, DataAccessException {
	 * ProductModuleFeaturePrivileges productModulesFeatrPriv = null;
	 * ArrayList<ProductModuleFeaturePrivileges> prodModFeaPrivList = new
	 * ArrayList<ProductModuleFeaturePrivileges>();
	 * 
	 * while (rs.next()) { productModulesFeatrPriv = new
	 * ProductModuleFeaturePrivileges();
	 * productModulesFeatrPriv.setPrivCode(rs.getString("privilege_code"));
	 * productModulesFeatrPriv.setProdModFeatrName(rs.getString("feature_name"))
	 * ; productModulesFeatrPriv.setPrivDesc(rs.getString("privilege_desc"));
	 * productModulesFeatrPriv.setPrivName(rs.getString("privilege_name"));
	 * prodModFeaPrivList.add(productModulesFeatrPriv); } return
	 * prodModFeaPrivList; } }
	 */

	public class UserCredentialsExtractor implements ResultSetExtractor<User> {

		@Override
		public User extractData(ResultSet rs) throws SQLException, DataAccessException {
			User user = null;
			String firstName = null;
			if (rs.next()) {
				user = new User();
				firstName = rs.getString("first_name") != null ? rs.getString("first_name").trim()
						: rs.getString("first_name");
				user.setFirstName(firstName);
				if (rs.getInt("ACTIVE_STATUS_ID") == 1) {
					user.setActiveStatusInd("Active");
				} else {
					user.setActiveStatusInd("Inactive");
				}
			}
			return user;
		}

	}

	public class EmailExtractor implements ResultSetExtractor<String> {
		@Override
		public String extractData(ResultSet rs) throws SQLException, DataAccessException {
			String result = null;
			Boolean userAvail = false;
			if (rs.next()) {
				result = rs.getString("email");
				userAvail = true;
			}
			if (result == null || result.equals("")) {
				if (userAvail) {
					return "no email";
				} else {
					return "Invalid";
				}
			} else {
				return result;
			}
		}
	}

	public class ResetValidityExtractor implements ResultSetExtractor<ResetValid> {
		@Override
		public ResetValid extractData(ResultSet rs) throws SQLException, DataAccessException {
			ResetValid resetValid = new ResetValid();
			String message = "Reset password link has expired ";
			resetValid.setValid(false);
			if (rs.next()) {
				Long validUpto = rs.getLong("valid_upto");
				String userId = rs.getString("user_id");
				if (validUpto >= System.currentTimeMillis()) {
					resetValid.setValid(true);
					resetValid.setMasterLoginID(userId);
					message = "valid Link";
				}
			}
			resetValid.setValidityReason(message);
			return resetValid;
		}
	}

	public class StringExtractor implements ResultSetExtractor<String> {
		private String fieldName;

		public StringExtractor(String fieldName) {
			this.fieldName = fieldName;
		}

		@Override
		public String extractData(ResultSet rs) throws SQLException, DataAccessException {
			String result = null;
			if (rs.next()) {
				result = rs.getString(fieldName) != null ? rs.getString(fieldName).trim() : rs.getString(fieldName);
			}
			return result;
		}
	}

	public class TicketDetailExtractor implements ResultSetExtractor<Ticket> {

		@Override
		public Ticket extractData(ResultSet rs) throws SQLException, DataAccessException {
			Ticket ticket = null;
			if (rs.next()) {
				ticket = new Ticket();
				ticket.setMasterLoginId(rs.getString("MASTER_LOGIN_ID"));
				ticket.setDefaultProdID(rs.getString("PRODUCT_CODE"));
				ticket.setRoleType(rs.getString("ROLE_TYPE"));
				ticket.setUserFullName(rs.getString("USER_NAME"));
				ticket.setWindowId(rs.getString("WINDOW_ID"));
			}
			return ticket;
		}

	}

	public class PasswordValidator implements ResultSetExtractor<String> {
		String encNewPass = null;

		public PasswordValidator(String encNewPass) {
			this.encNewPass = encNewPass;
		}

		@Override
		public String extractData(ResultSet rs) throws SQLException, DataAccessException {
			String password = null;
			int counter = 0;
			while (rs.next() && counter <= 4) {
				password = rs.getString("PASSWORD") != null ? rs.getString("PASSWORD").trim()
						: rs.getString("PASSWORD");
				if (password.equals(encNewPass)) {
					return "New password should not match to the last 5 password !!";
				}
				counter = counter + 1;
			}
			return "valid";
		}

	}

	@Override
	public String getUserEmailId(String userId) {
		String message = null;
		String sql = "select ci.email from USERS u, USER_CONTACT uc, CONTACT_INFO ci " + " where u.user_id=?"
				+ " and u.user_sys_id=uc.user_sys_id " + " and uc.contact_info_sys_id = ci.contact_info_sys_id  ";

		try {
			return jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, userId);
				}
			}, new UserRepositoryImpl.EmailExtractor());
		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered get User Email while resetting password for user " + e.getMessage(),
					null, e);
			message = "Error encountered while resetting password.";
		}
		return message;
	}

	@Override
	public boolean createAnalysis(Analysis analysis) {

		String sql = "INSERT INTO ANALYSIS (CUST_PROD_MOD_FEATURE_SYS_ID, ANALYSIS_ID, "
				+ "ANALYSIS_NAME, CREATED_BY,  CREATED_DATE, ACTIVE_STATUS_IND ) "
				+ "VALUES ( ?, ?, ?, ?, SYSDATE(), 1 ); ";
		try {
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, analysis.getFeatureId());
					preparedStatement.setLong(2, analysis.getAnalysisId());
					preparedStatement.setString(3, analysis.getAnalysisName());
					preparedStatement.setString(4, analysis.getUserId());
				}
			});
		} catch (Exception e) {
			logger.error("Exception encountered while creating a new Artifact " + e.getMessage(), null, e);
		}
		return true;
	}

	@Override
	public AnalysisSummaryList getAnalysisByFeatureID(Long featureId) {
		AnalysisSummaryList analysisSummaryList = new AnalysisSummaryList();
		List<AnalysisSummary> listOfAnalysisSummery = new ArrayList<AnalysisSummary>();
		try {
			String sql = "select * from ANALYSIS A " + "where A.CUST_PROD_MOD_FEATURE_SYS_ID = " + featureId
			/*
			 * +
			 * " (select CUST_PROD_MOD_FEATURE_SYS_ID  from customer_product_module_features cpmf "
			 * + "where cpmf.FEATURE_CODE='" + featureId+"' ) "
			 */
					+ " AND A.ACTIVE_STATUS_IND = 1 ";
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

			for (Map row : rows) {
				AnalysisSummary analysisSummary = new AnalysisSummary();
				analysisSummary.setAnalysisId((Long) row.get("ANALYSIS_ID"));
				analysisSummary.setAnalysisName((String) row.get("ANALYSIS_NAME"));
				analysisSummary.setActiveStatusInd((Integer) row.get("ACTIVE_STATUS_IND"));
				analysisSummary.setFeatureId((Long) row.get("CUST_PROD_MOD_FEATURE_SYS_ID"));
				analysisSummary.setCreatedBy((String) row.get("CREATED_BY"));
				analysisSummary.setCreatedDate(
						DateUtil.convertStringToDate(row.get("CREATED_DATE").toString(), "yyyy-MM-dd HH:mm:ss"));
				analysisSummary.setModifiedBy((String) row.get("MODIFIED_BY"));

				analysisSummary.setInactivatedBy((String) row.get("INACTIVATED_BY"));
				if (row.get("MODIFIED_DATE") != null) {
					analysisSummary.setModifiedDate(
							DateUtil.convertStringToDate(row.get("MODIFIED_DATE").toString(), "yyyy-MM-dd HH:mm:ss"));
				}

				if (row.get("INACTIVATED_DATE") != null) {
					analysisSummary.setInactivatedDate(DateUtil
							.convertStringToDate(row.get("INACTIVATED_DATE").toString(), "yyyy-MM-dd HH:mm:ss"));
				}
				listOfAnalysisSummery.add(analysisSummary);
			}
			analysisSummaryList.setValid(true);
			analysisSummaryList.setValidityMessage("Artifacts List Successfully Populated.");
			analysisSummaryList.setArtifactSummaryList(listOfAnalysisSummery);
			return analysisSummaryList;
		} catch (Exception e) {
			logger.error("Exception encountered while getting a list of Artifacts " + e.getMessage(), null, e);
			analysisSummaryList.setValid(false);
			analysisSummaryList.setError("Error encountered while getting a list of Artifacts.");
			analysisSummaryList.setArtifactSummaryList(listOfAnalysisSummery);
			return analysisSummaryList;
		}

	}

	@Override
	public boolean updateAnalysis(Analysis analysis) {

		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE ANALYSIS SET ANALYSIS_NAME =?, CUST_PROD_MOD_FEATURE_SYS_ID= ?, "
				+ " MODIFIED_DATE=SYSDATE(), MODIFIED_BY=? ");
		sql.append(" WHERE ANALYSIS_ID = ? ");

		try {
			jdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, analysis.getAnalysisName());
					preparedStatement.setLong(2, analysis.getFeatureId());
					preparedStatement.setString(3, analysis.getUserId());
					preparedStatement.setLong(4, analysis.getAnalysisId());
				}
			});

		} catch (Exception e) {
			logger.error("Exception encountered while updating an Artifact " + e.getMessage(), null, e);
		}
		return true;
	}

	@Override
	public boolean deleteAnalysis(Analysis analysis) {
		String sql = "UPDATE ANALYSIS SET ACTIVE_STATUS_IND = 0 ,INACTIVATED_DATE=SYSDATE(), INACTIVATED_BY=? "
				+ " WHERE ANALYSIS_ID = ? ";
		try {
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, analysis.getUserId());
					preparedStatement.setLong(2, analysis.getAnalysisId());
				}
			});
			return true;
		} catch (Exception e) {
			logger.error("Exception encountered while deleting the Analysis " + e.getMessage(), null, e);
		}
		return true;
	}

	@Override
	public ArrayList<User> getUsers(Long customerId) {
		ArrayList<User> userList = null;
		String sql = "SELECT U.USER_SYS_ID, U.USER_ID, U.EMAIL, R.ROLE_NAME, R.ROLE_SYS_ID,  U.CUSTOMER_SYS_ID, U.FIRST_NAME, U.MIDDLE_NAME, U.LAST_NAME,"
				+ " U.ACTIVE_STATUS_IND FROM USERS U, ROLES R WHERE U.CUSTOMER_SYS_ID = R.CUSTOMER_SYS_ID AND U.ROLE_SYS_ID = R.ROLE_SYS_ID AND U.CUSTOMER_SYS_ID=?";
		try {
			userList = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, customerId);
				}
			}, new UserRepositoryImpl.UserDetailExtractor());
		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while get Ticket Details for ticketId : " + e.getMessage(), null, e);
		}

		return userList;
	}

	public class UserDetailExtractor implements ResultSetExtractor<ArrayList<User>> {

		@Override
		public ArrayList<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
			User user = null;
			ArrayList<User> userList = new ArrayList<User>();
			while (rs.next()) {
				user = new User();
				user.setMasterLoginId(rs.getString("USER_ID"));
				user.setUserId(rs.getLong("USER_SYS_ID"));
				user.setEmail(rs.getString("EMAIL"));
				user.setRoleName(rs.getString("ROLE_NAME"));
				user.setRoleId(rs.getLong("ROLE_SYS_ID"));
				user.setFirstName(rs.getString("FIRST_NAME"));
				user.setLastName(rs.getString("LAST_NAME"));
				user.setMiddleName(rs.getString("MIDDLE_NAME"));
				user.setCustomerId(rs.getLong("CUSTOMER_SYS_ID"));
				if (rs.getInt("ACTIVE_STATUS_IND") == 1) {
					user.setActiveStatusInd("Active");
				} else {
					user.setActiveStatusInd("Inactive");
				}

				userList.add(user);
			}
			return userList;
		}
	}

	@Override
	public Valid addUser(User user) {
		Valid valid = new Valid();
		String sql = "INSERT INTO USERS (USER_ID, EMAIL, ROLE_SYS_ID, CUSTOMER_SYS_ID, ENCRYPTED_PASSWORD, "
				+ "FIRST_NAME, MIDDLE_NAME, LAST_NAME, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY ) "
				+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE(), ? ); ";
		try {
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, user.getMasterLoginId());
					preparedStatement.setString(2, user.getEmail());
					preparedStatement.setLong(3, user.getRoleId());
					preparedStatement.setLong(4, user.getCustomerId());
					preparedStatement.setString(5, Ccode.cencode(user.getPassword()).trim());
					preparedStatement.setString(6, user.getFirstName());
					preparedStatement.setString(7, user.getMiddleName());
					preparedStatement.setString(8, user.getLastName());
					preparedStatement.setString(9, user.getActiveStatusInd());
					preparedStatement.setString(10, user.getMasterLoginId());
				}
			});
		} catch (DuplicateKeyException e) {
			logger.error("Exception encountered while creating a new user " + e.getMessage(), null, e);
			valid.setValid(false);
			valid.setError("User already Exists!");
			return valid;
		} catch (Exception e) {
			logger.error("Exception encountered while creating a new user " + e.getMessage(), null, e);
			valid.setValid(false);
			valid.setError(e.getMessage());
			return valid;
		}
		valid.setValid(true);
		return valid;
	}

	@Override
	public boolean updateUser(User user) {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE USERS SET EMAIL = ?, ROLE_SYS_ID = ? ");
		if (user.getPassword() != null) {
			sql.append(",ENCRYPTED_PASSWORD = '" + Ccode.cencode(user.getPassword()).trim() + "'");
			sql.append(",PWD_MODIFIED_DATE = SYSDATE()");
		}

		sql.append(",FIRST_NAME = ?, MIDDLE_NAME = ?, LAST_NAME = ?, ACTIVE_STATUS_IND = ?,"
				+ " MODIFIED_DATE = SYSDATE(), MODIFIED_BY = ? WHERE USER_SYS_ID = ?");

		try {
			jdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, user.getEmail());
					preparedStatement.setLong(2, user.getRoleId());
					preparedStatement.setString(3, user.getFirstName());
					preparedStatement.setString(4, user.getMiddleName());
					preparedStatement.setString(5, user.getLastName());
					preparedStatement.setInt(6, Integer.parseInt(user.getActiveStatusInd()));
					preparedStatement.setString(7, user.getMasterLoginId());
					preparedStatement.setLong(8, user.getUserId());
				}
			});
		} catch (Exception e) {
			logger.error("Exception encountered while updating user " + e.getMessage(), null, e);
			return false;
		}
		return true;
	}

	@Override
	public boolean deleteUser(Long userId, String masterLoginId) {
		String sql = "UPDATE USERS SET ACTIVE_STATUS_IND = 0, INACTIVATED_DATE=SYSDATE(), INACTIVATED_BY=?  "
				+ " WHERE USER_SYS_ID = ?";
		try {
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, masterLoginId);
					preparedStatement.setLong(2, userId);

				}
			});
		} catch (Exception e) {
			logger.error("Exception encountered while deleting user " + e.getMessage(), null, e);
			return false;
		}
		return true;
	}

	@Override
	public List<Role> getRolesDropDownList(Long customerId) {
		ArrayList<Role> rolesList = null;
		String sql = "SELECT R.ROLE_SYS_ID, R.ROLE_NAME FROM ROLES R "
				+ " WHERE R.CUSTOMER_SYS_ID = ? AND ACTIVE_STATUS_IND=1;";
		try {
			rolesList = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, customerId);
				}
			}, new UserRepositoryImpl.rolesDDDetailExtractor());
		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while get Ticket Details for ticketId : " + e.getMessage(), null, e);
		}

		return rolesList;
	}

	public class rolesDDDetailExtractor implements ResultSetExtractor<ArrayList<Role>> {

		@Override
		public ArrayList<Role> extractData(ResultSet rs) throws SQLException, DataAccessException {
			Role role = null;
			ArrayList<Role> roleList = new ArrayList<Role>();
			while (rs.next()) {
				role = new Role();
				role.setRoleName(rs.getString("ROLE_NAME"));
				role.setRoleId(rs.getLong("ROLE_SYS_ID"));
				roleList.add(role);
			}
			return roleList;
		}
	}

	@Override
	public ArrayList<RoleDetails> getRoles(Long customerId) {
		ArrayList<RoleDetails> roleList = null;
		long roleId;
		String sql = "SELECT R.ROLE_SYS_ID, R.CUSTOMER_SYS_ID, R.DATA_SECURITY_KEY, R.ROLE_NAME, R.ROLE_DESC,  R.ROLE_TYPE, R.ACTIVE_STATUS_IND"
				+ "  FROM ROLES R WHERE R.CUSTOMER_SYS_ID=?";
		try {
			roleList = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, customerId);
				}
			}, new UserRepositoryImpl.roleDetailExtractor());

			Long featureSysId;
			ArrayList<CustomerProductModuleFeature> cpmf = new ArrayList<CustomerProductModuleFeature>();

			// Get the CUST, PROD, MOD details

			String sql2 = "SELECT DISTINCT CPM.CUST_PROD_MOD_SYS_ID, CPM.CUST_PROD_SYS_ID FROM CUSTOMER_PRODUCT_MODULES CPM "
					+ "INNER JOIN PRODUCT_MODULES PM ON (CPM.PROD_MOD_SYS_ID=PM.PROD_MOD_SYS_ID) "
					+ "INNER JOIN CUSTOMER_PRODUCTS CP ON (CP.CUST_PROD_SYS_ID=CPM.CUST_PROD_SYS_ID) "
					+ "INNER JOIN CUSTOMERS C ON (C.CUSTOMER_SYS_ID=CP.CUSTOMER_SYS_ID) "
					+ "INNER JOIN MODULES M ON (M.MODULE_SYS_ID=PM.MODULE_SYS_ID) INNER JOIN PRODUCTS P ON "
					+ "(PM.PRODUCT_SYS_ID=P.PRODUCT_SYS_ID) "
					+ "WHERE C.CUSTOMER_SYS_ID=? AND P.ACTIVE_STATUS_IND = CP.ACTIVE_STATUS_IND AND "
					+ "CP.ACTIVE_STATUS_IND = 1 "
					+ "AND C.ACTIVE_STATUS_IND=1 AND P.ACTIVE_STATUS_IND=1 AND M.ACTIVE_STATUS_IND=1 AND M.MODULE_CODE = 'ANLYS00001';";

			cpmf = jdbcTemplate.query(sql2, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, customerId);

				}
			}, new UserRepositoryImpl.CPMFDetailExtractor());

			// use above id's to check if feature exists,
			// if no create feature, get the feature sys id and create
			// privilege
			// if yes check if My Analysis priv exists, if not create

			String sql3 = "SELECT CPMF.CUST_PROD_MOD_FEATURE_SYS_ID from customer_product_module_features CPMF "
					+ "where CUST_PROD_MOD_SYS_ID = ? AND FEATURE_NAME = 'MY ANALYSIS'";

			for (int y = 0; y < roleList.size(); y++) {
				roleId = roleList.get(y).getRoleSysId();
				for (int i = 0; i < cpmf.size(); i++) {
					Long custProdMod = cpmf.get(i).getCustProdModSysId();
					Long custProd = cpmf.get(i).getCustProdSysId();
					featureSysId = getFeatureSysId(sql3, custProdMod);
					Long roleSysId = roleId;
					if (featureSysId != 0) {
						Long custProdModFeatr = featureSysId;
						// check if priv exists
						String sql4 = "select * from privileges where CUST_PROD_SYS_ID=? AND CUST_PROD_MOD_SYS_ID=?"
								+ " AND	 CUST_PROD_MOD_FEATURE_SYS_ID=? ANd ROLE_SYS_ID=?";

						Boolean privExists = jdbcTemplate.query(sql4, new PreparedStatementSetter() {
							public void setValues(PreparedStatement preparedStatement) throws SQLException {
								preparedStatement.setLong(1, custProd);
								preparedStatement.setLong(2, custProdMod);
								preparedStatement.setLong(3, custProdModFeatr);
								preparedStatement.setLong(4, roleSysId);
							}

						}, new UserRepositoryImpl.PrivDetailExtractor());

						if (privExists) {
							roleList.get(y).setMyAnalysis(true);
							roleList.get(y).setMyAnalysisPrev(true);
						} else {
							roleList.get(y).setMyAnalysis(false);
							roleList.get(y).setMyAnalysisPrev(false);
						}

					}

				}

			}

		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while get Ticket Details for ticketId : " + e.getMessage(), null, e);
		}

		return roleList;
	}

	public class PrivDetailExtractor implements ResultSetExtractor<Boolean> {

		@Override
		public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
			Boolean featureSysId = false;
			while (rs.next()) {
				featureSysId = true;
			}
			return featureSysId;
		}
	}

	public class roleDetailExtractor implements ResultSetExtractor<ArrayList<RoleDetails>> {

		@Override
		public ArrayList<RoleDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {

			RoleDetails role = null;
			ArrayList<RoleDetails> roleList = new ArrayList<RoleDetails>();
			while (rs.next()) {
				role = new RoleDetails();
				if (rs.getLong("ACTIVE_STATUS_IND") == 1) {
					role.setActiveStatusInd("Active");
				} else {
					role.setActiveStatusInd("Inactive");
				}
				role.setCustSysId(rs.getLong("CUSTOMER_SYS_ID"));
				role.setDsk(rs.getString("DATA_SECURITY_KEY"));
				role.setRoleDesc(rs.getString("ROLE_DESC"));
				role.setRoleName(rs.getString("ROLE_NAME"));
				role.setRoleSysId(rs.getLong("ROLE_SYS_ID"));
				role.setRoleType(rs.getString("ROLE_TYPE"));

				if (rs.getInt("ACTIVE_STATUS_IND") == 1) {
					role.setActiveStatusInd("Active");
				} else {
					role.setActiveStatusInd("Inactive");
				}

				roleList.add(role);
			}
			return roleList;
		}
	}

	@Override
	public List<Role> getRoletypesDropDownList() {
		ArrayList<Role> rolesList = null;
		String sql = "SELECT R.ROLES_TYPES_SYS_ID, R.ROLES_TYPE_NAME FROM ROLES_TYPES R ";

		try {
			rolesList = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
				}
			}, new UserRepositoryImpl.roleTypeDetailExtractor());
		} catch (DataAccessException de) {
			logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
			throw de;
		} catch (Exception e) {
			logger.error("Exception encountered while get Ticket Details for ticketId : " + e.getMessage(), null, e);
		}

		return rolesList;
	}

	public class roleTypeDetailExtractor implements ResultSetExtractor<ArrayList<Role>> {

		@Override
		public ArrayList<Role> extractData(ResultSet rs) throws SQLException, DataAccessException {
			Role role = null;
			ArrayList<Role> roleList = new ArrayList<Role>();
			while (rs.next()) {
				role = new Role();
				role.setRoleName(rs.getString("ROLES_TYPE_NAME"));
				role.setRoleId(rs.getLong("ROLES_TYPES_SYS_ID"));
				roleList.add(role);
			}
			return roleList;
		}
	}

	@Override
	public Valid addRole(RoleDetails role) {
		Valid valid = new Valid();
		Long roleId;
		Long featureSysId;
		ArrayList<CustomerProductModuleFeature> cpmf = new ArrayList<CustomerProductModuleFeature>();
		String sql = "INSERT INTO ROLES (CUSTOMER_SYS_ID, ROLE_NAME, ROLE_CODE, ROLE_DESC, ROLE_TYPE, "
				+ "DATA_SECURITY_KEY, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY ) "
				+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, SYSDATE(), ? ); ";
		StringBuffer roleCode = new StringBuffer();
		roleCode.append(role.getCustomerCode()).append("_").append(role.getRoleName()).append("_")
				.append(role.getRoleType());
		try {
			// Add the role
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {

					preparedStatement.setLong(1, role.getCustSysId());
					preparedStatement.setString(2, role.getRoleName());
					preparedStatement.setString(3, roleCode.toString());
					preparedStatement.setString(4, role.getRoleDesc());
					preparedStatement.setString(5, role.getRoleType());
					if (role.getDsk() != null) {
						preparedStatement.setString(6, role.getDsk());
					} else {
						preparedStatement.setString(6, "NA");
					}

					preparedStatement.setLong(7, Integer.parseInt(role.getActiveStatusInd()));
					preparedStatement.setString(8, role.getMasterLoginId());
				}
			});

			if (role.getMyAnalysis()) {

				// Get Added Role Sys Id

				String sql1 = "SELECT R.ROLE_SYS_ID FROM ROLES R WHERE R.ROLE_NAME=?";

				roleId = jdbcTemplate.query(sql1, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1, role.getRoleName());
					}
				}, new UserRepositoryImpl.roleIdDetailExtractor());

				// Get the CUST, PROD, MOD details

				String sql2 = "SELECT DISTINCT CPM.CUST_PROD_MOD_SYS_ID, CPM.CUST_PROD_SYS_ID FROM CUSTOMER_PRODUCT_MODULES CPM "
						+ "INNER JOIN PRODUCT_MODULES PM ON (CPM.PROD_MOD_SYS_ID=PM.PROD_MOD_SYS_ID) "
						+ "INNER JOIN CUSTOMER_PRODUCTS CP ON (CP.CUST_PROD_SYS_ID=CPM.CUST_PROD_SYS_ID) "
						+ "INNER JOIN CUSTOMERS C ON (C.CUSTOMER_SYS_ID=CP.CUSTOMER_SYS_ID) "
						+ "INNER JOIN MODULES M ON (M.MODULE_SYS_ID=PM.MODULE_SYS_ID) INNER JOIN PRODUCTS P ON "
						+ "(PM.PRODUCT_SYS_ID=P.PRODUCT_SYS_ID) "
						+ "WHERE C.CUSTOMER_SYS_ID=? AND P.ACTIVE_STATUS_IND = CP.ACTIVE_STATUS_IND AND "
						+ "CP.ACTIVE_STATUS_IND = 1 "
						+ "AND C.ACTIVE_STATUS_IND=1 AND P.ACTIVE_STATUS_IND=1 AND M.ACTIVE_STATUS_IND=1 AND M.MODULE_CODE = 'ANLYS00001';";

				cpmf = jdbcTemplate.query(sql2, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setLong(1, role.getCustSysId());

					}
				}, new UserRepositoryImpl.CPMFDetailExtractor());

				// use above id's to check if feature exists,
				// if no create feature, get the feature sys id and create
				// privilege
				// if yes check if My Analysis priv exists, if not create

				String sql3 = "SELECT CPMF.CUST_PROD_MOD_FEATURE_SYS_ID from customer_product_module_features CPMF "
						+ "where CUST_PROD_MOD_SYS_ID = ? AND FEATURE_NAME = 'MY ANALYSIS'";

				for (int i = 0; i < cpmf.size(); i++) {
					Long custProdMod = cpmf.get(i).getCustProdModSysId();
					Long custProd = cpmf.get(i).getCustProdSysId();
					featureSysId = getFeatureSysId(sql3, custProdMod);

					if (featureSysId != 0) {
						Long custProdModFeatr = featureSysId;
						// create priv
						insertMyAnalysisPrivileges(role, roleId, custProdMod, custProd, custProdModFeatr);

					} else {
						// create feature
						String sql6 = "INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_SYS_ID, "
								+ "DEFAULT_URL, FEATURE_NAME, FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,DEFAULT, "
								+ "ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY) "
								+ " VALUES (?, '/', 'MY ANALYSIS', 'My Analysis', ?, ?,'1', '1', sysdate(), ?) ";

						StringBuffer feature_Code = new StringBuffer();
						feature_Code.append("MYANALYSIS_").append(custProdMod);
						StringBuffer feature_type = new StringBuffer();
						feature_type.append("PARENT_").append(feature_Code);
						jdbcTemplate.update(sql6, new PreparedStatementSetter() {
							public void setValues(PreparedStatement preparedStatement) throws SQLException {
								preparedStatement.setLong(1, custProdMod);
								preparedStatement.setString(2, feature_Code.toString());
								preparedStatement.setString(3, feature_type.toString());
								preparedStatement.setString(4, role.getMasterLoginId());
							}
						});
						Long newFeatureId = getFeatureSysId(sql3, custProdMod);
						insertMyAnalysisPrivileges(role, roleId, custProdMod, custProd, newFeatureId);
					}

				}

			}

		} catch (DuplicateKeyException e) {
			logger.error("Exception encountered while creating a new user " + e.getMessage(), null, e);
			valid.setValid(false);
			valid.setError("User already Exists!");
			return valid;
		} catch (Exception e) {
			logger.error("Exception encountered while creating a new user " + e.getMessage(), null, e);
			valid.setValid(false);
			valid.setError(e.getMessage());
			return valid;
		}
		valid.setValid(true);
		return valid;
	}

	private void insertMyAnalysisPrivileges(RoleDetails role, Long roleId, Long custProdMod, Long custProd,
			Long custProdModFeatr) {

		String sql3 = "select PRIVILEGE_SYS_ID from privileges where ROLE_SYS_ID=?";
		Boolean privExists = jdbcTemplate.query(sql3, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setLong(1, roleId);
			}

		}, new UserRepositoryImpl.PrivDetailExtractor());

		if (privExists == null || !privExists) {

			String sql5 = "INSERT INTO PRIVILEGES (CUST_PROD_SYS_ID, CUST_PROD_MOD_SYS_ID, "
					+ "CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, "
					+ "ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY) "
					+ " VALUES (?, ?, '0', ?, '0', '128', 'All', '1', sysdate(), ?) ";

			jdbcTemplate.update(sql5, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, custProd);
					preparedStatement.setLong(2, custProdMod);
					preparedStatement.setLong(3, roleId);
					preparedStatement.setString(4, role.getMasterLoginId());
				}
			});

			String sql6 = "INSERT INTO PRIVILEGES (CUST_PROD_SYS_ID, CUST_PROD_MOD_SYS_ID, "
					+ "CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, "
					+ "ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY) "
					+ "VALUES (?, '0', '0', ?, '0', '128', 'All', '1', sysdate(), ?)";

			jdbcTemplate.update(sql6, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, custProd);
					preparedStatement.setLong(2, roleId);
					preparedStatement.setString(3, role.getMasterLoginId());
				}

			});
		}
		String sql4 = "INSERT INTO PRIVILEGES (CUST_PROD_SYS_ID, CUST_PROD_MOD_SYS_ID, "
				+ "CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, "
				+ "ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY) VALUES ( ?, ?, ?, ?, '0', '128', 'All', '1', sysdate(), ?) ";

		jdbcTemplate.update(sql4, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setLong(1, custProd);
				preparedStatement.setLong(2, custProdMod);
				preparedStatement.setLong(3, custProdModFeatr);
				preparedStatement.setLong(4, roleId);
				preparedStatement.setString(5, role.getMasterLoginId());
			}
		});
	}

	private Long getFeatureSysId(String sql3, Long custProdMod) {
		return jdbcTemplate.query(sql3, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setLong(1, custProdMod);

			}
		}, new UserRepositoryImpl.MyAnalysisDetailExtractor());
	}

	public class roleIdDetailExtractor implements ResultSetExtractor<Long> {

		@Override
		public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
			Long role = null;
			while (rs.next()) {
				role = rs.getLong("ROLE_SYS_ID");
			}
			return role;
		}
	}

	public class CPMFDetailExtractor implements ResultSetExtractor<ArrayList<CustomerProductModuleFeature>> {

		@Override
		public ArrayList<CustomerProductModuleFeature> extractData(ResultSet rs)
				throws SQLException, DataAccessException {
			ArrayList<CustomerProductModuleFeature> alcpmf = new ArrayList<CustomerProductModuleFeature>();
			CustomerProductModuleFeature cpmf = null;
			while (rs.next()) {
				cpmf = new CustomerProductModuleFeature();
				cpmf.setCustProdSysId(rs.getLong("CUST_PROD_SYS_ID"));
				cpmf.setCustProdModSysId(rs.getLong("CUST_PROD_MOD_SYS_ID"));
				alcpmf.add(cpmf);
			}
			return alcpmf;
		}
	}

	public class MyAnalysisDetailExtractor implements ResultSetExtractor<Long> {

		@Override
		public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
			Long featureSysId = 0L;
			while (rs.next()) {
				featureSysId = rs.getLong("CUST_PROD_MOD_FEATURE_SYS_ID");
			}
			return featureSysId;
		}
	}

	@Override
	public boolean deleteRole(Long roleId, String masterLoginId) {

		String sql2 = "DELETE FROM ROLES " + " WHERE ROLE_SYS_ID = ?";
		try {
			jdbcTemplate.update(sql2, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, roleId);

				}
			});
		} catch (Exception e) {
			logger.error("Exception encountered while deleting role " + e.getMessage(), null, e);
			return false;
		}
		return true;
	}

	@Override
	public boolean updateRole(RoleDetails role) {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE ROLES SET CUSTOMER_SYS_ID = ?, ROLE_NAME = ?, ROLE_CODE = ?, ROLE_DESC=?, ROLE_TYPE=?, "
				+ " ACTIVE_STATUS_IND=?, MODIFIED_DATE = SYSDATE(), MODIFIED_BY = ?, DATA_SECURITY_KEY = ? WHERE ROLE_SYS_ID = ?");
		StringBuffer roleCode = new StringBuffer();
		roleCode.append(role.getCustomerCode()).append("_").append(role.getRoleName()).append("_")
				.append(role.getRoleType());
		try {
			jdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, role.getCustSysId());
					preparedStatement.setString(2, role.getRoleName());
					preparedStatement.setString(3, roleCode.toString());
					preparedStatement.setString(4, role.getRoleDesc());
					preparedStatement.setString(5, role.getRoleType());
					preparedStatement.setInt(6, Integer.parseInt(role.getActiveStatusInd()));
					preparedStatement.setString(7, role.getMasterLoginId());
					preparedStatement.setString(8, role.getDsk());
					preparedStatement.setLong(9, role.getRoleSysId());
				}
			});

		} catch (Exception e) {
			logger.error("Exception encountered while updating role " + e.getMessage(), null, e);
			return false;
		}
		return true;
	}

	@Override
	public boolean checkUserExists(Long roleId) {
		Boolean userExists;
		String sql1 = "SELECT * FROM USERS " + " WHERE ROLE_SYS_ID = ?";
		try {
			userExists = jdbcTemplate.query(sql1, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, roleId);
				}
			}, new UserRepositoryImpl.UserExistsExtractor());
		} catch (Exception e) {
			logger.error("Exception encountered while updating role " + e.getMessage(), null, e);
			return false;
		}
		return userExists;
	}

	public class UserExistsExtractor implements ResultSetExtractor<Boolean> {

		@Override
		public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
			Boolean userExists = false;
			while (rs.next()) {
				userExists = true;
			}
			return userExists;
		}
	}

	@Override
	public boolean checkPrivExists(Long roleId) {
		Boolean privExists;
		String sql1 = "SELECT * FROM PRIVILEGES " + " WHERE ROLE_SYS_ID = ?";
		try {
			privExists = jdbcTemplate.query(sql1, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, roleId);
				}
			}, new UserRepositoryImpl.PrivExistsExtractor());
		} catch (Exception e) {
			logger.error("Exception encountered while updating role " + e.getMessage(), null, e);
			return false;
		}
		return privExists;
	}

	public class PrivExistsExtractor implements ResultSetExtractor<Boolean> {

		@Override
		public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
			Boolean privExists = false;
			while (rs.next()) {
				privExists = true;
			}
			return privExists;
		}
	}
}
