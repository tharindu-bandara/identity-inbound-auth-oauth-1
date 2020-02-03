/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.oauth2.validators;

import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;

/**
 * Refresh token validator.
 */
public class RefreshTokenValidator implements OAuth2TokenValidator {

    public static final String TOKEN_TYPE = "refresh_token";

    @Override
    public boolean validateAccessDelegation(OAuth2TokenValidationMessageContext messageContext)
            throws IdentityOAuth2Exception {

        // We do not validate access delegation for refresh token.
        return true;
    }

    @Override
    public boolean validateScope(OAuth2TokenValidationMessageContext messageContext) throws IdentityOAuth2Exception {

        // We do not validate scopes for refresh token. Because refresh token does not have the concept of scopes.
        // It is there to get an access token. Refresh token should not act like an access token.
        return true;
    }

    @Override
    public boolean validateAccessToken(OAuth2TokenValidationMessageContext validationReqDTO)
            throws IdentityOAuth2Exception {

        // These validations are done in a higher level. Can't move them here yet because of backward compatibility
        // issues.
        return true;
    }

    @Override
    public String getTokenType() {

        return "Refresh";
    }
}
