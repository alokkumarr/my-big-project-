package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.id3.Id3TokenException;
import com.sncr.saw.security.app.id3.model.AuthorizationCodeDetails;
import com.sncr.saw.security.app.id3.model.Id3AuthenticationRequest;
import com.sncr.saw.security.app.id3.model.Id3ClientDetails;
import com.sncr.saw.security.app.id3.model.Id3ClientTicketDetails;
import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.repository.Id3Repository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Date;
import java.util.UUID;

@Repository
public class Id3RepositoryImpl implements Id3Repository {

  private final NSSOProperties nssoProperties;

  private final JdbcTemplate jdbcTemplate;

  private static final Logger logger = LoggerFactory.getLogger(Id3RepositoryImpl.class);

  @Autowired
  public Id3RepositoryImpl(JdbcTemplate jdbcTemplate, NSSOProperties nssoProperties) {
    this.jdbcTemplate = jdbcTemplate;
    this.nssoProperties = nssoProperties;
  }

  /**
   * Method to validate the Id3 request to make sure Domain and client_id whitelisted in SIP.
   *
   * @param masterLoginId
   * @param id3DomainName
   * @param clientId
   * @return
   */
  @Override
  public boolean validateId3Request(String masterLoginId, String id3DomainName, String clientId) {

    return true;
  }

  /**
   * This Method obtains the Authorization Code for Id3 user , domain and client-Id .
   *
   * @param authorizationCodeDetails
   * @return
   */
  @Override
  public String obtainAuthorizationCode(AuthorizationCodeDetails authorizationCodeDetails) {
    String sipTicketId = UUID.randomUUID().toString();
    authorizationCodeDetails.setSipTicketId(sipTicketId);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    Id3ClientDetails id3ClientDetails =
        fetchId3Details(
            authorizationCodeDetails.getMasterLoginId(),
            authorizationCodeDetails.getId3ClientId(),
            authorizationCodeDetails.getId3DomainName());
    String insertSql =
        "Insert into ID3_TICKET_DETAILS ( ID3_CLIENT_SYS_ID , "
            + "SIP_TICKET_ID, VALID_INDICATOR, CREATED_TIME,CREATED_BY)"
            + " values ( ?,?,true,sysdate(),?)";
    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(insertSql);
          ps.setLong(1, id3ClientDetails.getId3ClientSysId());
          ps.setString(2, sipTicketId);
          ps.setString(3, id3ClientDetails.getMasterLoginId());
          return ps;
        },
        keyHolder);
    authorizationCodeDetails.setTicketDetailsId(keyHolder.getKey().longValue());
    authorizationCodeDetails.setValidUpto(System.currentTimeMillis() + 2 * 60 * 1000);
    return Jwts.builder()
        .setSubject(authorizationCodeDetails.getMasterLoginId())
        .claim("ticket", authorizationCodeDetails)
        .setIssuedAt(new Date())
        .signWith(SignatureAlgorithm.HS256, nssoProperties.getJwtSecretKey())
        .compact();
  }

  /**
   * Validate the authorization code issued by SIP for authentication after SIP sso redirect.
   *
   * @param authorizationCode
   * @return
   */
  @Override
  public AuthorizationCodeDetails validateAuthorizationCode(
      String authorizationCode, Id3AuthenticationRequest id3AuthenticationRequest) {
    Claims ssoToken =
        Jwts.parser()
            .setSigningKey(nssoProperties.getJwtSecretKey())
            .parseClaimsJws(authorizationCode)
            .getBody();
    // Check if the code is valid
    AuthorizationCodeDetails authorizationCodeDetails =
        (AuthorizationCodeDetails) ssoToken.get("ticket");
    // mark the valid bydefault as false
    authorizationCodeDetails.setValid(false);
    boolean validity = false;
    String masterLoginId = null;
    if (authorizationCodeDetails != null) {
      validity = (authorizationCodeDetails.getValidUpto()) > (new Date().getTime());
      masterLoginId = authorizationCodeDetails.getMasterLoginId();
    }
    if (validity && masterLoginId != null) {
      String validateTicketsql =
          "select U.USER_ID ,U.ID3_ENABLED, U.ACTIVE_STATUS_IND AS USER_ACTIVE, ICD.ACTIVE_STATUS_IND AS ID3_CLIENT_ACTIVE ,"
              + " ITD.SIP_TICKET_ID, ITD.VALID_INDICATOR "
              + "from USERS U , CUSTOMERS C , ID3_CLIENT_DETAILS ICD , ID3_TICKET_DETAILS ITD "
              + "where U.USER_ID = ? "
              + "AND U.CUSTOMER_SYS_ID = C.CUSTOMER_SYS_ID "
              + "AND C.ID3_CLIENT_SYS_ID= ICD.ID3_CLIENT_SYS_ID "
              + "AND ICD.ID3_CLIENT_SYS_ID=ITD.ID3_CLIENT_SYS_ID "
              + "AND ICD.ID3_CLIENT_ID= ? "
              + "AND ICD.ID3_DOMAIN_NAME=?  "
              + "AND ITD.ID3_TICKET_DETAILS_SYS_ID= ?";

      Id3ClientTicketDetails id3ClientTicketDetails =
          jdbcTemplate.query(
              validateTicketsql,
              preparedStatement -> {
                preparedStatement.setString(1, authorizationCodeDetails.getMasterLoginId());
                preparedStatement.setString(2, authorizationCodeDetails.getId3ClientId());
                preparedStatement.setString(3, authorizationCodeDetails.getId3DomainName());
                preparedStatement.setLong(3, authorizationCodeDetails.getTicketDetailsId());
              },
              rs -> {
                Id3ClientTicketDetails id3ClientTicketDetails1 = null;
                if (rs.next()) {
                  id3ClientTicketDetails1 = new Id3ClientTicketDetails();
                  id3ClientTicketDetails1.setMasterLoginId(rs.getString("USER_ID"));
                  id3ClientTicketDetails1.setId3Enabled(rs.getBoolean("ID3_ENABLED"));
                  id3ClientTicketDetails1.setCustomerCode(rs.getString("CUSTOMER_CODE"));
                  id3ClientTicketDetails1.setUserActive(rs.getBoolean("USER_ACTIVE"));
                  id3ClientTicketDetails1.setId3ClientSysId(rs.getLong("ID3_CLIENT_SYS_ID"));
                  id3ClientTicketDetails1.setId3ClientActive(rs.getBoolean("ID3_CLIENT_ACTIVE"));
                  id3ClientTicketDetails1.setSipTicketId(rs.getString("SIP_TICKET_ID"));
                  id3ClientTicketDetails1.setValidIndicator(rs.getBoolean("VALID_INDICATOR"));
                }
                return id3ClientTicketDetails1;
              });
      if (id3ClientTicketDetails.isValidIndicator()
          && id3ClientTicketDetails.isId3Enabled()
          && id3ClientTicketDetails.isUserActive()
          && authorizationCodeDetails.getValidUpto() >= System.currentTimeMillis())
        logger.trace("Successfully validated request for user: " + masterLoginId);
      authorizationCodeDetails.setValid(true);
    }
    logger.info("Authentication failed request for user: " + masterLoginId);
    return authorizationCodeDetails;
  }

  /**
   * This Method will provide the mechanism to on board the ID3 clients in SIP for whitelisting.
   *
   * @param id3Request
   * @return
   */
  @Override
  public boolean onBoardId3client(Id3AuthenticationRequest id3Request) {
    throw new Id3TokenException("onBoardId3client method is not Yet implemented");
  }

  /**
   * Fetch Id3 details based on masterLoginId , id3ClientId , Id3 domain name.
   *
   * @param masterLoginId
   * @param id3ClientId
   * @param id3DomainName
   * @return Id3ClientDetails
   */
  private Id3ClientDetails fetchId3Details(
      String masterLoginId, String id3ClientId, String id3DomainName) {
    String fetchId3ClientSql =
        "SELECT U.USER_ID, U.ID3_ENABLED, C.CUSTOMER_CODE, U.ACTIVE_STATUS_IND AS USER_ACTIVE, "
            + "    ICD.ACTIVE_STATUS_IND AS ID3_CLIENT_ACTIVE, ICD.ID3_CLIENT_SYS_ID "
            + "FROM USERS U,CUSTOMERS C,ID3_CLIENT_DETAILS ICD "
            + "WHERE U.USER_ID = ? AND U.CUSTOMER_SYS_ID = C.CUSTOMER_SYS_ID "
            + "    AND C.ID3_CLIENT_SYS_ID = ICD.ID3_CLIENT_SYS_ID AND ICD.ID3_CLIENT_ID = ? "
            + "AND ICD.ID3_DOMAIN_NAME = ? ";
    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.ResultSet)
     */
    Id3ClientDetails id3ClientDetails =
        jdbcTemplate.query(
            fetchId3ClientSql,
            preparedStatement -> {
              preparedStatement.setString(1, masterLoginId);
              preparedStatement.setString(2, id3ClientId);
              preparedStatement.setString(3, id3DomainName);
            },
            rs -> {
              Id3ClientDetails id3ClientDetails1 = null;
              if (rs.next()) {
                id3ClientDetails1 = new Id3ClientDetails();
                id3ClientDetails1.setMasterLoginId(rs.getString("USER_ID"));
                id3ClientDetails1.setId3Enabled(rs.getBoolean("ID3_ENABLED"));
                id3ClientDetails1.setCustomerCode(rs.getString("CUSTOMER_CODE"));
                id3ClientDetails1.setUserActive(rs.getBoolean("USER_ACTIVE"));
                id3ClientDetails1.setId3ClientSysId(rs.getLong("ID3_CLIENT_SYS_ID"));
                id3ClientDetails1.setId3ClientActive(rs.getBoolean("ID3_CLIENT_ACTIVE"));
              }
              return id3ClientDetails1;
            });
    return id3ClientDetails;
  }
}