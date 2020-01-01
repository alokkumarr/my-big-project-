package com.sncr.saw.security.app.id3.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public class OpenIdConnectToken {

    @JsonProperty("access_token")
    private String accessToken = null;
    @JsonProperty("token_type")
    private TokenTypeEnum tokenType = null;
    @JsonProperty("expires_in")
    private Long expiresIn = null;
    @JsonProperty("refresh_token")
    private String refreshToken = null;
    @JsonProperty("id_token")
    private String idToken = null;
    @JsonProperty("scope")
    private String scope = null;

    public OpenIdConnectToken accessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    /**
     * Provides access to APIs when included in the Authorization header, (ie &#x27;Authorization&amp;#58; Bearer &lt;access_token&gt;&#x27;)
     *
     * @return accessToken
     **/
  //  @Schema(description = "Provides access to APIs when included in the Authorization header, (ie 'Authorization&#58; Bearer <access_token>')")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public OpenIdConnectToken tokenType(TokenTypeEnum tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    /**
     * Always &#x27;Bearer&#x27;
     *
     * @return tokenType
     **/
   // @Schema(description = "Always 'Bearer'")
    public TokenTypeEnum getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenTypeEnum tokenType) {
        this.tokenType = tokenType;
    }

    public OpenIdConnectToken expiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    /**
     * The lifetime in seconds of the access token.
     * minimum: 0
     *
     * @return expiresIn
     **/
   // @Schema(description = "The lifetime in seconds of the access token.")
    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public OpenIdConnectToken refreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    /**
     * Can be used to obtain new access tokens.
     *
     * @return refreshToken
     **/
   // @Schema(description = "Can be used to obtain new access tokens.")
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public OpenIdConnectToken idToken(String idToken) {
        this.idToken = idToken;
        return this;
    }

    /**
     * Signed JWT containing identity claims of the authenticated user.
     *
     * @return idToken
     **/
  //  @Schema(description = "Signed JWT containing identity claims of the authenticated user.")
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public OpenIdConnectToken scope(String scope) {
        this.scope = scope;
        return this;
    }

    /**
     * Space delimited list of scopes applicable to this access token.
     *
     * @return scope
     **/
   // @Schema(description = "Space delimited list of scopes applicable to this access token.")
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OpenIdConnectToken openidConnectTokenResponse = (OpenIdConnectToken) o;
        return Objects.equals(this.accessToken, openidConnectTokenResponse.accessToken) &&
            Objects.equals(this.tokenType, openidConnectTokenResponse.tokenType) &&
            Objects.equals(this.expiresIn, openidConnectTokenResponse.expiresIn) &&
            Objects.equals(this.refreshToken, openidConnectTokenResponse.refreshToken) &&
            Objects.equals(this.idToken, openidConnectTokenResponse.idToken) &&
            Objects.equals(this.scope, openidConnectTokenResponse.scope);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(accessToken, tokenType, expiresIn, refreshToken, idToken, scope);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OpenidConnectTokenResponse {\n");

        sb.append("    accessToken: ").append(toIndentedString(accessToken)).append("\n");
        sb.append("    tokenType: ").append(toIndentedString(tokenType)).append("\n");
        sb.append("    expiresIn: ").append(toIndentedString(expiresIn)).append("\n");
        sb.append("    refreshToken: ").append(toIndentedString(refreshToken)).append("\n");
        sb.append("    idToken: ").append(toIndentedString(idToken)).append("\n");
        sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    /**
     * Always &#x27;Bearer&#x27;
     */
    public enum TokenTypeEnum {
        BEARER("Bearer");

        private String value;

        TokenTypeEnum(String value) {
            this.value = value;
        }

        @JsonCreator
        public static TokenTypeEnum fromValue(String text) {
            for (TokenTypeEnum b : TokenTypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
