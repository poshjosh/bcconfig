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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 23, 2018 3:25:26 AM
 */
public abstract class JsonConfigService extends AbstractConfigService<Map> {

    private transient final Logger logger = Logger.getLogger(JsonConfigService.class.getName());

    public JsonConfigService(String timePattern) {
        super(timePattern);
    }

    public JsonConfigService(ClassLoader classLoader, ConfigGroup<Map> cache, String timePattern, boolean useCache) {
        super(classLoader, cache, timePattern, useCache);
    }


    public abstract void load(Map props, String path) throws IOException;
    
    @Override
    public abstract void store(Map props, String path) throws IOException;

    @Override
    public Config<Map> load(String [] defaultPaths, String path) throws IOException {
        
        final String name = this.getName(path);
        
        Config<Map> output = null;
        
        final ConfigGroup<Map> cachedConfigs = this.getCachedConfigs();
        
        if(this.isUseCache()) {
            output = cachedConfigs.get(name);
        }
        
        if(output == null) {
            
            Map defaults = null;
            if(defaultPaths != null) {
                for(String defaultPath : defaultPaths) {
                    if(defaults == null) {
                        defaults = new LinkedHashMap();
                    }else{
                        defaults = new LinkedHashMap(defaults);
                    }
                    load(defaults, defaultPath);
                }
            }

            Map outputProps = new LinkedHashMap(defaults);
            load(outputProps, path);
            
            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "{0} = {1}\n{2}", new Object[]{path, name, outputProps});
            }
            
            output = new MapConfig(outputProps, this.getTimePattern());
            
            if(this.isUseCache()) {
                cachedConfigs.put(name, output);
            }
        }
        
        return output;
    }
}

