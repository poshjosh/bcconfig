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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 23, 2018 12:54:41 PM
 */
public abstract class AbstractCompositeConfig<DATA_TYPE> extends AbstractConfig<DATA_TYPE> {

    private final List<Config<DATA_TYPE>> propertiesList;

    public AbstractCompositeConfig(ConfigService<DATA_TYPE> configService) throws IOException {
        this(configService, false);
    }
    
    public AbstractCompositeConfig(ConfigService<DATA_TYPE> configService, boolean allowDuplicates) throws IOException {
        this(configService.load().values(), configService.getTimePattern(), allowDuplicates);
    }
    
    public AbstractCompositeConfig(Collection<Config<DATA_TYPE>> configs) {
        this(configs, null);
    }
    
    public AbstractCompositeConfig(Collection<Config<DATA_TYPE>> configs, boolean allowDuplicates) {
        this(configs, null, allowDuplicates);
    }
    
    public AbstractCompositeConfig(Collection<Config<DATA_TYPE>> configs, String timePattern) {
        this(configs, timePattern, false);
    }
    
    public AbstractCompositeConfig(Collection<Config<DATA_TYPE>> configs, String timePattern, boolean allowDuplicates) {
        super(timePattern);
        final List<Config<DATA_TYPE>> list = new ArrayList(configs.size());
        final Set<String> allNames = new HashSet<>();
        for(Config config : configs) {
            if(!allowDuplicates) {
                final Set<String> names = config.getNames();
                for(String name : names) {
                    if(allNames.contains(name)) {
                        throw new UnsupportedOperationException("Property '"+name+"' is duplicated");
                    }
                }
                allNames.addAll(names);
            }
            list.add(config);
        }
        this.propertiesList = Collections.unmodifiableList(list);
    }

    /**
     * <b>Returns a copy</b>
     * @return A copy of the combination of all the {@link java.util.Properties Properties} 
     * which composes this config's data. Changes to the returned {@link java.util.Properties Properties}
     * are <b>NOT</b> reflected in this config.
     */
    @Override
    public abstract DATA_TYPE getData();

    @Override
    protected String doGet(String key, String defaultValue) {
        String val = null;
        for(Config props : propertiesList) {
            val = props.get(key, defaultValue);
            if(val != null) {
                break;
            }
        }
        return val == null ? defaultValue : val;
    }

    @Override
    protected Object doSet(String key, String value) {
        Object val = null;
        for(Config<DATA_TYPE> props : propertiesList) {
            if(props.getNames().contains(key)) {
                val = props.set(key, value);
                break;
            }
        }
        return val;
    }

    @Override
    public int size() {
        int total = 0;
        for(Config<DATA_TYPE> props : propertiesList) {
            total += props.size();
        }
        return total;
    }

    @Override
    public Set<String> getNames() {
        Set<String> set = new HashSet<>();
        for(Config<DATA_TYPE> props : propertiesList) {
            set.addAll(props.getNames());
        }
        return set;
    }

    public final List<Config<DATA_TYPE>> getPropertiesList() {
        final boolean returnCopy = false;
        return propertiesList;
    }
}
