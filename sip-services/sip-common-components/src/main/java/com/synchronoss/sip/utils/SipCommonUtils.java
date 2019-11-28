package com.synchronoss.sip.utils;

import com.synchronoss.bda.sip.jwt.TokenParser;
import com.synchronoss.bda.sip.jwt.token.ProductModuleFeature;
import com.synchronoss.bda.sip.jwt.token.ProductModules;
import com.synchronoss.bda.sip.jwt.token.Products;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.sip.utils.Privileges.PrivilegeNames;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class SipCommonUtils {

  private static final Logger logger = LoggerFactory.getLogger(SipCommonUtils.class);
  private static final String FEATURE_NAME = "My Analysis";
  private static final String MODULE_FEATURE_NAME = "Drafts";

  /**
   * This method to validate jwt token then return the validated ticket for further processing.
   *
   * @param request HttpServletRequest
   * @return Ticket
   */
  public static Ticket getTicket(HttpServletRequest request) {
    Ticket ticket = null;
    try {
      String token = getToken(request);
      ticket = TokenParser.retrieveTicket(token);
    } catch (IllegalAccessException | IOException e) {
      logger.error("Error occurred while fetching token", e);
    }
    return ticket;
  }

  /**
   * Get JWT token details.
   *
   * @param req http Request
   * @return String
   * @throws IllegalAccessException If Authorization not found
   */
  public static String getToken(final HttpServletRequest req) throws IllegalAccessException {
    String authHeader = null;
    if (!("OPTIONS".equals(req.getMethod()))) {
      authHeader = req.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new IllegalAccessException("Missing or invalid Authorization header.");
      }
      return authHeader.substring(7); // The part after "Bearer "
    }
    return authHeader;
  }

  /**
   * Validates the Name for file, analysis etc.
   *
   * @param name name
   */
  public static void validateName(String name) {
    if (name.isEmpty()) {
      throw new IllegalArgumentException("analysisName must not be null");
    }
    // validate name length and avoid any invalid specific symbol for file name
    boolean hasValidLength = name.length() >= 1 && name.length() <= 30;
    if (hasValidLength) {
      if (name.matches("[`~!@#$%^&*()+={}|\"':;?/>.<,*:/?\\[\\]\\\\]")) {
        throw new IllegalArgumentException(
            "Analysis name must not consists of special characters except '- _'");
      }
    } else {
      throw new IllegalArgumentException(
          String.format(
              "analysisName %s is invalid - character count MUST be greater than or equal to 1 and "
                  + "less than or equal to 30",
              name));
    }
  }

  /**
   * Functions returns the binary equivalent given a decimal num.
   *
   * @param n decimal integer
   * @return binary integer data
   */
  public static int[] decToBinary(Long n) {
    String binString = Long.toBinaryString(n);
    binString = binString.length() < 16 ? "00000000".concat(binString) : binString;

    binString.toCharArray();
    final int[] privCode = Stream.of(binString.split("")).mapToInt(Integer::parseInt).toArray();

    return privCode;
  }

  /**
   * Validate privileges for the user.
   *
   * @param productList Products associated with the user
   * @param category Analysis request body category
   * @return validation response
   */
  public static Boolean validatePrivilege(
      ArrayList<Products> productList, Long category, PrivilegeNames privName) {
    Privileges priv = new Privileges();
    if (!CollectionUtils.isEmpty(productList)) {
      for (Products product : productList) {
        ArrayList<ProductModules> productModulesList =
            product.getProductModules() != null ? product.getProductModules() : new ArrayList<>();
        if (!CollectionUtils.isEmpty(productModulesList)) {
          for (ProductModules productModule : productModulesList) {
            ArrayList<ProductModuleFeature> prodModFeatureList =
                productModule.getProdModFeature() != null
                    ? productModule.getProdModFeature()
                    : new ArrayList<>();
            if (!CollectionUtils.isEmpty(prodModFeatureList)) {
              for (ProductModuleFeature productModuleFeature : prodModFeatureList) {
                ArrayList<ProductModuleFeature> productModuleSubFeatureList =
                    productModuleFeature.getProductModuleSubFeatures() != null
                        ? productModuleFeature.getProductModuleSubFeatures()
                        : new ArrayList<>();
                if (!CollectionUtils.isEmpty(productModuleSubFeatureList)) {
                  for (ProductModuleFeature prodModSubFeature : productModuleSubFeatureList) {
                    if (category != null && prodModSubFeature.getProdModFeatureID() == category) {
                      Long privCode = prodModSubFeature.getPrivilegeCode();
                      return priv.isPriviegePresent(privName, privCode);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    return false;
  }

  /**
   * Validate the auth token.
   *
   * @param authToken authorization token
   * @return Boolean flag
   */
  public static boolean authValidation(String authToken) {
    if (authToken == null || !authToken.startsWith("Bearer ")) {
      logger.error("Invalid authentication token {}", authToken);
      return false;
    }
    return true;
  }

  /**
   * checks for Private Category.
   *
   * @param ticket Ticket
   * @return Long Category Id for User.
   */
  public static Long checkForPrivateCategory(Ticket ticket) {
    final Long[] privateCategory = {null};
    if (ticket != null && !CollectionUtils.isEmpty(ticket.getProducts())) {
      ticket
          .getProducts()
          .forEach(
              product -> {
                if (product != null && !CollectionUtils.isEmpty(product.getProductModules())) {
                  product
                      .getProductModules()
                      .forEach(
                          prodMod -> {
                            if (prodMod != null
                                && !CollectionUtils.isEmpty(prodMod.getProdModFeature())) {
                              prodMod
                                  .getProdModFeature()
                                  .forEach(
                                      prodModFeat -> {
                                        if (prodModFeat != null
                                            && !CollectionUtils.isEmpty(
                                                prodModFeat.getProductModuleSubFeatures())
                                            && FEATURE_NAME.equalsIgnoreCase(
                                                prodModFeat.getProdModFeatureName())) {
                                          prodModFeat
                                              .getProductModuleSubFeatures()
                                              .forEach(
                                                  prodModSubFeat -> {
                                                    if (prodModSubFeat != null
                                                        && prodModSubFeat.getProdModFeatureName()
                                                            != null
                                                        && MODULE_FEATURE_NAME.equalsIgnoreCase(
                                                            prodModSubFeat
                                                                .getProdModFeatureName())) {
                                                      privateCategory[0] =
                                                          prodModSubFeat.getProdModFeatureID();
                                                    }
                                                  });
                                        }
                                      });
                            }
                          });
                }
              });
    }
    return privateCategory[0];
  }
}
