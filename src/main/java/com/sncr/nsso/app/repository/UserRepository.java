package com.sncr.nsso.app.repository;

import java.util.List;

import com.sncr.nsso.common.bean.Analysis;
import com.sncr.nsso.common.bean.AnalysisSummaryList;
import com.sncr.nsso.common.bean.ResetValid;
import com.sncr.nsso.common.bean.Ticket;
import com.sncr.nsso.common.bean.User;

public interface UserRepository {
	void insertTicketDetails(Ticket ticket) throws Exception;
	boolean[] authenticateUser(String masterLoginId, String password);
	void prepareTicketDetails(User user, Boolean onlyDef);
	void invalidateTicket(String ticketId, String validityMessage);
	String verifyUserCredentials(String masterLoginId, String eMail,
			String firstName);
	String updateUserPass(String masterLoginId, String newPassEncrp);
	Ticket getTicketDetails(String ticketId);
	String changePassword(String loginId, String newPass, String oldPass);
	String rstchangePassword(String loginId, String newPass);
	String getUserEmailId(String userId);
	void insertResetPasswordDtls(String userId, String randomHash,
			Long createdTime, long validUpto);
	ResetValid validateResetPasswordDtls(String randomHash);
	boolean createAnalysis (Analysis analysis);
	boolean updateAnalysis(Analysis analysis);
	boolean deleteAnalysis(Analysis analysis);
	AnalysisSummaryList getAnalysisByFeatureID (Long featureId);
	List<User> getUsers(Long customerId);
}
