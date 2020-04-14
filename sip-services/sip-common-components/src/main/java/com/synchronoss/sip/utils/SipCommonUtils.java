package com.synchronoss.sip.utils;

import com.google.json.JsonSanitizer;
import com.synchronoss.bda.sip.jwt.TokenParser;
import com.synchronoss.bda.sip.jwt.token.ProductModuleFeature;
import com.synchronoss.bda.sip.jwt.token.ProductModules;
import com.synchronoss.bda.sip.jwt.token.Products;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.exceptions.UnauthorizedException;
import com.synchronoss.sip.utils.Privileges.PrivilegeNames;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

public class SipCommonUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(SipCommonUtils.class);
  private static final String FEATURE_NAME = "My Analysis";
  private static final String MODULE_FEATURE_NAME = "Drafts";
  private static final int MIN_ANALYSIS_NAME_LENGTH = 1;
  private static final int MAX_ANALYSIS_NAME_LENGTH = 100;


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
    } catch (UnauthorizedException | IOException e) {
      LOGGER.error("Error occurred while fetching token", e);
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
  public static String getToken(final HttpServletRequest req) {
    String authHeader = null;
    if (!("OPTIONS".equals(req.getMethod()))) {
      authHeader = req.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new UnauthorizedException("Missing or invalid Authorization header.");
      }
      // The part after "Bearer "
      return authHeader.substring(7);
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
    boolean hasValidLength =
        name.length() >= MIN_ANALYSIS_NAME_LENGTH && name.length() <= MAX_ANALYSIS_NAME_LENGTH;
    if (hasValidLength) {
      if (name.matches("[`~!@#$%^&*()+={}|\"':;?/>.<,*:/?\\[\\]\\\\]")) {
        throw new IllegalArgumentException(
            "Analysis name must not consists of special characters except '- _'");
      }
    } else {
      throw new IllegalArgumentException(
          String.format(
              "analysisName %s is invalid - character count MUST be greater than or equal to %s and"
                  + " less than or equal to %s",
              name, MIN_ANALYSIS_NAME_LENGTH, MAX_ANALYSIS_NAME_LENGTH));
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
    return Stream.of(binString.split("")).mapToInt(Integer::parseInt).toArray();
  }

  /**
   * Validate privileges for the user.
   *
   * @param productList Products associated with the user
   * @param category Analysis request body category
   * @return validation response
   */
  public static Boolean validatePrivilege(
      List<Products> productList, Long category, PrivilegeNames privName) {
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
   * Validate privileges for the user.
   *
   * @param productList Products associated with the user
   * @param category Analysis request body category
   * @return validation response
   */
  public static Boolean validatePrivilege(
      ArrayList<Products> productList, Long category, PrivilegeNames privName,
      String moduleName) {
    Privileges priv = new Privileges();
    if (!CollectionUtils.isEmpty(productList)) {
      for (Products product : productList) {
        ArrayList<ProductModules> productModulesList =
            product.getProductModules() != null ? product.getProductModules() : new ArrayList<>();
        if (!CollectionUtils.isEmpty(productModulesList)) {
          for (ProductModules productModule : productModulesList) {
            ArrayList<ProductModuleFeature> prodModFeatureList =
                productModule.getProdModFeature() != null
                    && productModule.getProductModName().equals(moduleName)
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
      LOGGER.error("Invalid authentication token {}", authToken);
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

  /**
   * Util method to set Response UNAUTHORIZED.
   *
   * @param response HttpServletResponse
   * @return
   */
  public static HttpServletResponse setUnAuthResponse(HttpServletResponse response)
      throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response
        .sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    return response;
  }

  /**
   * Util method to set Response BAD REQUEST.
   *
   * @param response HttpServletResponse
   * @return
   */
  public static HttpServletResponse setBadRequest(HttpServletResponse response)
      throws IOException {
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response
        .sendError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());
    return response;
  }


  /**
   * Validate system level category from the ticket with analysis category.
   *
   * @param productList list of products
   * @param category    analysis category id
   * @return true if any system level category matched with the analysis category Id
   */
  public static boolean haveSystemCategory(List<Products> productList, Long category) {
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
                    if (category != null && category.equals(prodModSubFeature.getProdModFeatureID())
                        && prodModSubFeature.isSystemCategory()) {
                      return true;
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
   * Sanitize a given string to eliminate JSONInjection vulnerabilities.
   *
   * @param inputJsonString Json string to be sanitized
   * @return Sanitized Json string
   */
  public static String sanitizeJson(String inputJsonString) {
    String sanitizedString = JsonSanitizer.sanitize(inputJsonString);

    return sanitizedString;
  }

  /**
   * Encrypts a given string.
   *
   * @param secretKey for encryption
   * @param password Password to be encrypted
   * @return Encrypted password
   * @throws Exception In case of any error
   */
  public static String encryptPassword(SecretKey secretKey, String password) throws Exception {
    String encryptedPassword = null;

    SipObfuscation obfuscator = new SipObfuscation(secretKey);
    encryptedPassword = obfuscator.encrypt(password);
    return encryptedPassword;
  }

  /**
   * Decrypts a given encrypted string.
   *
   * @param secretKey for encryption
   * @param encryptedPassword Password to be decrypted
   * @return Decrypted password
   * @throws Exception In case of any error
   */
  public static String decryptPassword(SecretKey secretKey, String encryptedPassword)
      throws Exception {
    String decryptedPassword = null;

    SipObfuscation obfuscator = new SipObfuscation(secretKey);
    decryptedPassword = obfuscator.decrypt(encryptedPassword);

    return decryptedPassword;
  }
}
