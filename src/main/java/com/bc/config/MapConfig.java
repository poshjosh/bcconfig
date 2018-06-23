/*
 * Copyright 2018 NUROX Ltd.
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
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 23, 2018 1:12:47 PM
 */
public class MapConfig extends AbstractConfig<Map> {
    
    private static transient final Logger logger = Logger.getLogger(ConfigImpl.class.getName());

    private final Map data;
    
    public MapConfig(Map data) {
        this(data, "EEE MMM dd HH:mm:ss z yyyy");
    }

    public MapConfig(Map data, String timePattern) {
        super(timePattern);
        this.data = Objects.requireNonNull(data);
    }

    /**
     * @return The Object usually {@link java.util.Properties Properties} or 
     * {@link java.util.Map Map} which contains this config's data. Changes to 
     * the returned data are reflected in this config.
     */
    @Override
    public Map getData() {
        final boolean copy = false;
        return data;
    }
    
    @Override
    public String doGet(String key, String defaultValue) {
        final String value = (String)data.getOrDefault(key, defaultValue);
        return value;
    }
    
    @Override
    public Object doSet(String key, String value) {
        final Object output = data.put(key, value);
        return output;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Set<String> getNames() {
        return data.keySet();
    }
}
