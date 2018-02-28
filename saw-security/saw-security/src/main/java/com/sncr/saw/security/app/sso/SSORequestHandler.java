package com.sncr.saw.security.app.sso;

import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.common.bean.RefreshToken;
import com.sncr.saw.security.common.bean.Ticket;
import com.sncr.saw.security.common.bean.User;
import com.sncr.saw.security.common.util.TicketHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Set;

@Service
public class SSORequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(SSORequestHandler.class);

    private final UserRepository userRepository;
    private final NSSOProperties nSSOProperties;

    @Autowired
    public SSORequestHandler(UserRepository userRepository, NSSOProperties nSSOProperties) {
        this.userRepository = userRepository;
        this.nSSOProperties = nSSOProperties;
    }

    public SSOResponse processSSORequest(String token) {
        logger.info("Request received to process single sign-on");
        Claims ssoToken = Jwts.parser().setSigningKey(nSSOProperties.getSsoSecretKey())
                .parseClaimsJws(token).getBody();
        // Check if the Token is valid
        Set<Map.Entry<String, Object>> entrySet = ((Map<String, Object>) ssoToken.get("ticket")).entrySet();
        Boolean validity = false;
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
           logger.info("Successfully validated single sign-on request for user: "+masterLoginId);
          return createSAWToken(masterLoginId);
        }
        logger.info("Authentication failed single sign-on request for user: "+masterLoginId);
        return null;
    }

    private SSOResponse createSAWToken(String masterLoginId) {
        logger.info("Ticket will be created for SSO request");
        logger.info("Token Expiry :" + nSSOProperties.getValidityMins());
        Ticket ticket;
        User user = new User();
        user.setMasterLoginId(masterLoginId);
        String atoken;
        String rToken;
        SSOResponse ssoResponse = new SSOResponse();
        TicketHelper tHelper = new TicketHelper(userRepository);
        ticket = new Ticket();
        ticket.setMasterLoginId(masterLoginId);
        ticket.setValid(false);
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
            ticket.setValidityReason("Database error. Please contact server Administrator.");
            ticket.setError(de.getMessage());
            atoken =
                    Jwts.builder().setSubject(masterLoginId).claim("ticket", ticket)
                            .setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact();
            ssoResponse.setaToken(atoken);
            return ssoResponse;
        } catch (Exception e) {
            logger.error("Exception occurred creating ticket ", e, null);
            return null;
        }
        /*
           To Do : Currently secret key for saw token is hardcoded as "sncrsaw2" in all places,
           this needs to be changed and make as configurable properties.
         */
        atoken = Jwts.builder().setSubject(masterLoginId).claim("ticket", ticket).setIssuedAt(new Date())
                        .signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact();
        rToken = Jwts.builder().setSubject(masterLoginId).claim("ticket", newRToken).setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact();
      ssoResponse.setaToken(atoken);
      ssoResponse.setrToken(rToken);
      return ssoResponse;
    }
}
