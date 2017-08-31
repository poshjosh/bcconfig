/*
 * Copyright 2017 NUROX Ltd.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Presents a combined view of multiple {@link com.bc.config.Config Config}s. 
 * <b>Note:</b> A property must <b>NOT</b> be repeated across the multiple
 * {@link com.bc.config.Config Config}s constituting this object.
 * The combined {@link com.bc.config.Config Config} propagates changes to the 
 * specific {@link com.bc.config.Config Config} containing the property being
 * set by the change.
 * <p>
 * <b>Note:</b>
 * The {@link #getProperties()} method returns a copy combining the properties 
 * from all the constituting {@link com.bc.config.Config Config}s. Hence changes 
 * to the properties returned by that method will not be reflected in the actual 
 * {@link com.bc.config.Config Config} which contains the data.
 * </p>
 * @author Chinomso Bassey Ikwuagwu on Feb 25, 2017 10:48:14 AM
 */
public class CompositeConfig extends AbstractConfig {

    private final List<Properties> propsList;

    public CompositeConfig(ConfigService configService) throws IOException {
        this(configService, false);
    }
    
    public CompositeConfig(ConfigService configService, boolean allowDuplicates) throws IOException {
        this(configService.load().values(), configService.getTimePattern(), allowDuplicates);
    }
    
    public CompositeConfig(Collection<Config> configs) {
        this(configs, null);
    }
    
    public CompositeConfig(Collection<Config> configs, boolean allowDuplicates) {
        this(configs, null, allowDuplicates);
    }
    
    public CompositeConfig(Collection<Config> configs, String timePattern) {
        this(configs, timePattern, false);
    }
    
    public CompositeConfig(Collection<Config> configs, String timePattern, boolean allowDuplicates) {
        super(timePattern);
        final List<Properties> list = new ArrayList(configs.size());
        final Set<String> allNames = new HashSet<>();
        for(Config config : configs) {
            final Properties props = config.getProperties();
            if(!allowDuplicates) {
                final Set<String> names = props.stringPropertyNames();
                for(String name : names) {
                    if(allNames.contains(name)) {
                        throw new UnsupportedOperationException("Property '"+name+"' is duplicated");
                    }
                }
                allNames.addAll(names);
            }
            list.add(props);
        }
        this.propsList = Collections.unmodifiableList(list);
    }

    @Override
    protected String doGetProperty(String key, String defaultValue) {
        String val = null;
        for(Properties props : propsList) {
            val = props.getProperty(key, defaultValue);
            if(val != null) {
                break;
            }
        }
        return val == null ? defaultValue : val;
    }

    @Override
    protected Object doSetProperty(String key, String value) {
        Object val = null;
        for(Properties props : propsList) {
            if(props.stringPropertyNames().contains(key)) {
                val = props.setProperty(key, value);
                break;
            }
        }
        return val;
    }

    /**
     * <b>Returns a copy</b>
     * @return A copy of the combination of all the {@link java.util.Properties Properties} 
     * which composes this config's data. Changes to the returned {@link java.util.Properties Properties}
     * are <b>NOT</b> reflected in this config.
     */
    @Override
    public Properties getProperties() {
        Properties all = new Properties();
        for(Properties props : propsList) {
            for(String key : props.stringPropertyNames()) {
                all.setProperty(key, props.getProperty(key));
            }
        }
        return all;
    }

    @Override
    public int size() {
        int total = 0;
        for(Properties props : propsList) {
            total += props.size();
        }
        return total;
    }

    @Override
    public Set<String> stringPropertyNames() {
        Set<String> set = new HashSet<>();
        for(Properties props : propsList) {
            set.addAll(props.stringPropertyNames());
        }
        return set;
    }
}
