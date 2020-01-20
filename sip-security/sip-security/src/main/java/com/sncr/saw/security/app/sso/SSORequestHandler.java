package com.sncr.saw.security.app.sso;

import com.sncr.saw.security.app.id3.model.Id3AuthenticationRequest;
import com.sncr.saw.security.app.id3.service.ValidateId3IdentityToken;
import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.common.bean.RefreshToken;
import com.sncr.saw.security.common.bean.User;
import com.sncr.saw.security.app.service.TicketHelper;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Service
public class SSORequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(SSORequestHandler.class);

    private final NSSOProperties nSSOProperties;
    private final TicketHelper tHelper;

    @Autowired
    public SSORequestHandler(NSSOProperties nSSOProperties, TicketHelper tHelper) {
        this.nSSOProperties = nSSOProperties;
        this.tHelper = tHelper;
    }

    @Autowired
    private ValidateId3IdentityToken validateId3IdentityToken;


    public SSOResponse processSSORequest(String token ) {
        logger.info("Request received to process single sign-on");
        Claims ssoToken = Jwts.parser().setSigningKey(nSSOProperties.getSsoSecretKey())
                .parseClaimsJws(token).getBody();
        // Check if the Token is valid
        Set<Map.Entry<String, Object>> entrySet = ((Map<String, Object>) ssoToken.get("ticket")).entrySet();
        boolean validity = false;
        String masterLoginId = null;
       for (Map.Entry<String, Object> pair : entrySet) {
            if (pair.getKey().equals("validUpto")) {
                validity = Long.parseLong(pair.getValue().toString()) > (new Date().getTime());
            }
            if (pair.getKey().equals("masterLoginId")) {
                masterLoginId = pair.getValue().toString();
            }
        }
        if (validity && masterLoginId!=null) {
           logger.trace("Successfully validated single sign-on request for user: "+masterLoginId);
          return createSAWToken(masterLoginId, false);
        }
        logger.info("Authentication failed single sign-on request for user: "+masterLoginId);
        return null;
    }

    public SSOResponse processId3SSORequest(String token , Id3AuthenticationRequest id3Request ) {
        logger.info("Request received to process single sign-on with Id3");

        SSOResponse ssoResponse = null;
        // Check if the Token is valid
         String masterLoginId = validateId3IdentityToken.validateToken(token,id3Request);
        if (masterLoginId!=null) {
            logger.trace("Successfully validated single sign-on request for user: "+masterLoginId);
            ssoResponse = createSAWToken(masterLoginId, true);
        }
        logger.trace("Authentication failed single sign-on request for user: "+masterLoginId);
        return ssoResponse;
    }

    /**
     * Create New sip token for the master login Id.
     * @param masterLoginId
     * @return
     */
    public SSOResponse createSAWToken(String masterLoginId, Boolean id3Enabled) {
        logger.info("Ticket will be created for SSO request");
        logger.info("Token Expiry :" + nSSOProperties.getValidityMins());
        Ticket ticket;
        User user = new User();
        user.setId3Enabled(id3Enabled);
        user.setMasterLoginId(masterLoginId);
        String atoken;
        String rToken;
        SSOResponse ssoResponse = new SSOResponse();
        ticket = new Ticket();
        ticket.setMasterLoginId(masterLoginId);
        ticket.setValid(true);
        RefreshToken newRToken;
        try {
            user.setValidMins((nSSOProperties.getValidityMins() != null
                    ? Long.parseLong(nSSOProperties.getValidityMins()) : 60));
            ticket = tHelper.createTicket(user, false);
            newRToken = new RefreshToken();
            newRToken.setValid(true);
            newRToken.setMasterLoginId(masterLoginId);
            newRToken
                    .setValidUpto(System.currentTimeMillis() + (nSSOProperties.getRefreshTokenValidityMins() != null
                            ? Long.parseLong(nSSOProperties.getRefreshTokenValidityMins()) : 1440) * 60 * 1000);
        } catch (DataAccessException de) {
            logger.error("Exception occurred creating ticket ", de, null);
            ticket.setValid(false);
            ticket.setValidityReason("Database error. Please contact server Administrator.");
            ticket.setError(de.getMessage());
            atoken =
                    Jwts.builder().setSubject(masterLoginId).claim("ticket", ticket)
                            .setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, nSSOProperties.getJwtSecretKey()).compact();
            ssoResponse.setaToken(atoken);
            return ssoResponse;
        } catch (Exception e) {
            logger.error("Exception occurred creating ticket ", e, null);
            ticket.setValid(false);
            return null;
        }

        atoken = Jwts.builder().setSubject(masterLoginId).claim("ticket", ticket).setIssuedAt(new Date())
                        .signWith(SignatureAlgorithm.HS256, nSSOProperties.getJwtSecretKey()).compact();
        rToken = Jwts.builder().setSubject(masterLoginId).claim("ticket", newRToken).setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, nSSOProperties.getJwtSecretKey()).compact();
      ssoResponse.setaToken(atoken);
      ssoResponse.setrToken(rToken);
      return ssoResponse;
    }

    /**
     * Set SSO cookies for the limited expiry time.
     * @param response
     * @param authorizationCode
     * @param id3Request
     */
    public void setSsoCookies(HttpServletResponse response , String authorizationCode ,Id3AuthenticationRequest id3Request)
    {
        Cookie sessionID = new Cookie("sessionID", authorizationCode);
        sessionID.setMaxAge(2 * 60); // expires in 2 minutes
        sessionID.setHttpOnly(true);
        sessionID.setSecure(true);
        Cookie domainName = new Cookie("domainName", id3Request.getDomainName());
        domainName.setMaxAge(2 * 60); // expires in 2 minutes
        domainName.setHttpOnly(true);
        domainName.setSecure(true);
        Cookie clientId = new Cookie("clientId", id3Request.getClientId());
        clientId.setMaxAge(2 * 60); // expires in 2 minutes
        clientId.setHttpOnly(true);
        clientId.setSecure(true);
        response.addCookie(sessionID);
        response.addCookie(domainName);
        response.addCookie(clientId);

    }
}
