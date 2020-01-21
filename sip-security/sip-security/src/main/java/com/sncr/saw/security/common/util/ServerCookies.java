package com.sncr.saw.security.common.util;

import javax.servlet.http.HttpServletResponse;

/** This is the utility class to set the server cookies. */
public class ServerCookies {

    /**
     * Set cookies to HttpServletResponse .
     * @param name
     * @param value
     * @param domain
     * @param maxAge
     * @param secure
     * @param httpOnly
     * @param response
     */
  public static void setCookie(
      String name,
      String value,
      String domain,
      int maxAge,
      boolean secure,
      boolean httpOnly,
      HttpServletResponse response) {
    StringBuffer cookieBuf = new StringBuffer();
    appendCookieValue(cookieBuf, name, value, domain, maxAge, secure, httpOnly);
    String cookie = cookieBuf.toString();
    response.addHeader("set-cookie", cookie);
  }

    /**
     * Prepare cookie values .
     * @param headerBuf
     * @param name
     * @param value
     * @param domain
     * @param maxAge
     * @param isSecure
     * @param httpOnly
     */
  private static void appendCookieValue(
      StringBuffer headerBuf,
      String name,
      String value,
      String domain,
      int maxAge,
      boolean isSecure,
      boolean httpOnly) {
    StringBuffer buf = new StringBuffer();
    // Servlet implementation checks name
    buf.append(name);
    buf.append("=");
    buf.append(value);

    /*// Add domain information, if present
    if (domain != null) {
      buf.append("; Domain=");
     // buf.append(domain);
        buf.append("http://13.126.137.89");
    }


    if (maxAge > 0) {
      buf.append("; Max-Age=");
      buf.append(maxAge);
    }

    // Secure
    if (isSecure) {
      buf.append("; Secure");
    }*/

    /*// HttpOnly
    if (httpOnly) {
      buf.append("; HttpOnly");
    }*/
      // Path=path
     //
          buf.append("; Path=");
          buf.append("/");
     // }
    headerBuf.append(buf);
  }
}
