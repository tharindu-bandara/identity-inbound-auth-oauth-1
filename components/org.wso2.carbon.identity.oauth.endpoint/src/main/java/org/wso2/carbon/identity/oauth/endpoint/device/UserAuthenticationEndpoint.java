/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.identity.oauth.endpoint.device;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.wso2.carbon.identity.application.authentication.framework.model.CommonAuthRequestWrapper;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.oauth.cache.AppInfoCache;
import org.wso2.carbon.identity.oauth.common.exception.InvalidOAuthClientException;
import org.wso2.carbon.identity.oauth.dao.OAuthAppDO;
import org.wso2.carbon.identity.oauth.endpoint.authz.OAuth2AuthzEndpoint;
import org.wso2.carbon.identity.oauth.endpoint.exception.InvalidRequestParentException;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.device.api.DeviceAuthService;
import org.wso2.carbon.identity.oauth2.device.constants.Constants;
import org.wso2.carbon.identity.oauth2.device.model.DeviceFlowDO;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Rest implementation for device authentication flow.
 */
@Path("/device")
public class UserAuthenticationEndpoint {

    private static final Log log = LogFactory.getLog(UserAuthenticationEndpoint.class);

    private OAuth2AuthzEndpoint oAuth2AuthzEndpoint = new OAuth2AuthzEndpoint();
    private DeviceFlowDO deviceFlowDO = new DeviceFlowDO();
    private DeviceAuthService deviceAuthService;

    public void setDeviceAuthService(DeviceAuthService deviceAuthService) {

        this.deviceAuthService = deviceAuthService;
    }

    @POST
    @Path("/")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/html")
    public Response deviceAuthorize(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws URISyntaxException, InvalidRequestParentException, IdentityOAuth2Exception, IOException {

        String userCode = request.getParameter(Constants.USER_CODE);
        if (StringUtils.isBlank(userCode)) {
            if (log.isDebugEnabled()) {
                log.debug("user_code is missing in the request.");
            }
            response.sendRedirect(IdentityUtil.
                    getServerURL("/authenticationendpoint/device.do?error=invalidRequest", false, false));
            return null;
        }
        String clientId = deviceAuthService.getClientId(userCode);
        if (StringUtils.isNotBlank(clientId) && StringUtils.equals(getUserCodeStatus(userCode), Constants.PENDING)) {

            setCallbackURI(clientId);
            deviceAuthService.setAuthenticationStatus(userCode);
            CommonAuthRequestWrapper commonAuthRequestWrapper = new CommonAuthRequestWrapper(request);
            commonAuthRequestWrapper.setParameter(Constants.CLIENT_ID, clientId);
            commonAuthRequestWrapper.setParameter(Constants.RESPONSE_TYPE, Constants.RESPONSE_TYPE_DEVICE);
            commonAuthRequestWrapper.setParameter(Constants.REDIRECTION_URI, deviceFlowDO.getCallbackUri());
            if (getScope(userCode) != null) {
                String scope = String.join(Constants.SEPARATED_WITH_SPACE, getScope(userCode));
                commonAuthRequestWrapper.setParameter(Constants.SCOPE, scope);
            }
            commonAuthRequestWrapper.setParameter(Constants.NONCE, userCode);
            return oAuth2AuthzEndpoint.authorize(commonAuthRequestWrapper, response);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Incorrect user_code: " + userCode);
            }
            response.sendRedirect(IdentityUtil
                    .getServerURL("/authenticationendpoint/device.do?error=invalidUserCode", false, false));
            return null;
        }
    }

    /**
     * Get the scopes from the database.
     *
     * @param userCode User code that has delivered to the device.
     * @return Scopes
     * @throws IdentityOAuth2Exception
     */
    private String[] getScope(String userCode) throws IdentityOAuth2Exception {

        return deviceAuthService.getScope(userCode);
    }

    /**
     * Get the user code status.
     *
     * @param userCode User code that has delivered to the device.
     * @return Status
     * @throws IdentityOAuth2Exception
     */
    private String getUserCodeStatus(String userCode) throws IdentityOAuth2Exception {

        return deviceAuthService.getStatus(userCode);
    }

    /**
     * This method is used to generate the redirection URI.
     *
     * @param appName Service provider name.
     * @return Redirection URI
     */
    private String getRedirectionURI(String appName) throws URISyntaxException {

        String pageURI = IdentityUtil.getServerURL("/authenticationendpoint/device_success.do",
                false, false);
        URIBuilder uriBuilder = new URIBuilder(pageURI);
        uriBuilder.addParameter(Constants.APP_NAME, appName);
        return uriBuilder.build().toString();
    }

    /**
     * This method is used to set the callback uri. If there is no value it will set a default value.
     *
     * @param clientId Consumer key of the application.
     * @throws IdentityOAuth2Exception
     */
    private void setCallbackURI(String clientId) throws IdentityOAuth2Exception {

        try {
            OAuthAppDO oAuthAppDO;
            oAuthAppDO = OAuth2Util.getAppInformationByClientId(clientId);
            String redirectURI = oAuthAppDO.getCallbackUrl();
            if (StringUtils.isBlank(redirectURI)) {
                String appName = oAuthAppDO.getApplicationName();
                redirectURI = getRedirectionURI(appName);
                deviceAuthService.setCallbackUri(clientId, redirectURI);
                AppInfoCache.getInstance().clearCacheEntry(clientId);
            }
            deviceFlowDO.setCallbackUri(redirectURI);
        } catch (InvalidOAuthClientException | URISyntaxException | IdentityOAuth2Exception e) {
            String errorMsg = String.format("Error when getting app details for client id : %s", clientId);
            throw new IdentityOAuth2Exception(errorMsg, e);
        }
    }
}
