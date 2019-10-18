package com.synchronoss.bda.sip;

import com.synchronoss.bda.sip.exception.SipNotProcessedSipEntityException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;




public class SipXssRequestFilter extends GenericFilterBean {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;
    logger.trace("Logging Request  {} : {}", req.getMethod(), req.getRequestURL());
    String uri = req.getRequestURL().toString();
    Boolean isValid = ESAPI.validator().isValidURI("URL", uri, true);
    logger.trace("isValid URI: " + isValid);
    if (req.getQueryString() != null) {
      Map<String, List<String>> canonicalizedMap = splitQuery(req.getQueryString());
      Set<Entry<String, List<String>>> query = canonicalizedMap.entrySet();
      Iterator<Entry<String, List<String>>> itr = query.iterator();
      while (itr.hasNext()) {
        Entry<String, List<String>> e = itr.next();
        String key = (String) e.getKey();
        List<String> valueString = canonicalizedMap.get(key);
        for (String data : valueString) {
          logger.trace("query parameter value: " + data);

          Boolean queryIsValid =
              ESAPI.validator().isValidInput("Validating query parameter value for intrusion", data,
                  "SafeString", data.length(), false);

          if (!queryIsValid) {
            queryIsValid =
                ESAPI.validator().isValidInput("Validating query parameter value for intrusion",
                    data, "Digit", data.length(), false);
            logger.trace("queryIsValid Query Parameter: " + queryIsValid);
          }
          if (!queryIsValid) {
            res.sendError(org.apache.http.HttpStatus.SC_UNAUTHORIZED);
            logger.trace(
                "Check for cross-site scripting: reflected in query parameter value: " + data);
            throw new SipNotProcessedSipEntityException("Check for cross-site scripting: "
                + "reflected in query parameter value: " + data);
          }
        }
      }
    }
    if (!isValid) {
      logger.info("Cross-site scripting: reflected: " + !isValid);
      res.sendError(org.apache.http.HttpStatus.SC_UNAUTHORIZED);
      throw new SipNotProcessedSipEntityException("Cross-site scripting: reflected: " + !isValid);
    }
    chain.doFilter(request, response);
    logger.trace("Logging Response :{}", res.getContentType());

  }

  private Map<String, List<String>> splitQuery(String uri) throws UnsupportedEncodingException {
    final Map<String, List<String>> queryPairs = new LinkedHashMap<String, List<String>>();
    final String[] pairs = uri.split("&");
    for (String pair : pairs) {
      final int idx = pair.indexOf("=");
      final String key = idx > 0 ? pair.substring(0, idx) : pair;
      if (!queryPairs.containsKey(key)) {
        queryPairs.put(key, new LinkedList<String>());
      }
      final String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null;
      queryPairs.get(key).add(value);
    }
    return queryPairs;
  }
  
  

}
