/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.discovery;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit test covering OIDProviderRequest class.
 */
public class OIDProviderRequestTest {

    private OIDProviderRequest oidProviderRequest = new OIDProviderRequest();

    @Test
    public void testGetandSetTenantDomain() throws Exception {

        String tenantDomain = "tenantDomain";
        oidProviderRequest.setTenantDomain("tenantDomain");
        String tenantDomain1 = oidProviderRequest.getTenantDomain();
        assertEquals(tenantDomain1, tenantDomain, "Error while retrieving tenant domain.");
    }

    @Test
    public void testGetandSetUri() throws Exception {

        String uri = "uri";
        OIDProviderRequest oidProviderRequest = new OIDProviderRequest();
        oidProviderRequest.setUri("uri");
        String uri1 = oidProviderRequest.getUri();
        assertEquals(uri1, uri, "Error while retrieving uri");
    }
}
