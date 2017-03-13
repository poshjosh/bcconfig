/*
 * Copyright 2016 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.config;

import java.util.Map;

/**
 * A {@link com.bc.config.ConfigGroup} contains multiple {@link com.bc.config.Config} instances.
 * @author Chinomso Bassey Ikwuagwu on Jul 18, 2016 11:43:36 AM
 */
public interface ConfigGroup extends Map<String, Config>{

    Config get(String filename);
    
    /**
     * @param filename
     * @param subset_name
     * @param separator
     * @return The subset of properties with prefix <tt>subset_name + separator</tt> 
     * for {@link com.bc.config.Config Config} loaded from the 
     * specified filename.
     * @see com.bc.config.Config#subset(java.lang.String, java.lang.String) 
     */
    Config subset(String filename, String subset_name, String separator);
    
    String getPropertyFor(String filename, String key);

    String getPropertyFor(String filename, String key, String defaultValue);

    Object setPropertyFor(String filename, String key, String value);
}
