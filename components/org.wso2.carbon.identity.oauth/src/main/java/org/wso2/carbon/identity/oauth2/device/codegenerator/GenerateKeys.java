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

package org.wso2.carbon.identity.oauth2.device.codegenerator;

import org.apache.commons.lang.RandomStringUtils;
import org.wso2.carbon.identity.oauth2.device.constants.Constants;

import java.security.SecureRandom;

/**
 * This class will be used to generate user code.
 */
public class GenerateKeys {

    public GenerateKeys() {

    }

    /**
     * This method is used to generate random string with fixed length.
     *
     * @param num Length of the random string.
     * @return Random string.
     */
    public static String getKey(int num) {

        return RandomStringUtils.random(num, 0, Constants.KEY_SET.length(), false, false,
                Constants.KEY_SET.toCharArray(), new SecureRandom());
    }
}
