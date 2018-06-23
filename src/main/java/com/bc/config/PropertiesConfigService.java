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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 23, 2018 2:41:48 AM
 */
public abstract class PropertiesConfigService extends AbstractConfigService<Properties> {

    private transient final Logger logger = Logger.getLogger(PropertiesConfigService.class.getName());

    public PropertiesConfigService(String timePattern) {
        super(timePattern);
    }

    public PropertiesConfigService(ClassLoader classLoader, ConfigGroup<Properties> cache, String timePattern, boolean useCache) {
        super(classLoader, cache, timePattern, useCache);
    }

    @Override
    public Config<Properties> load(String [] defaultPaths, String path) throws IOException {
        
        final String name = this.getName(path);
        
        Config<Properties> output = null;
        
        final ConfigGroup<Properties> cachedConfigs = this.getCachedConfigs();
        
        if(this.isUseCache()) {
            output = cachedConfigs.get(name);
        }
        
        if(output == null) {
            
            Properties defaults = null;
            if(defaultPaths != null) {
                for(String defaultPath : defaultPaths) {
                    if(defaults == null) {
                        defaults = new Properties();
                    }else{
                        defaults = new Properties(defaults);
                    }
                    load(defaults, defaultPath);
                }
            }

            Properties outputProps = new Properties(defaults);
            load(outputProps, path);
            
            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "{0} = {1}\n{2}", new Object[]{path, name, outputProps});
            }
            
            output = new ConfigImpl(outputProps, this.getTimePattern());
            
            if(this.isUseCache()) {
                cachedConfigs.put(name, output);
            }
        }
        
        return output;
    }

    @Override
    public void load(Properties props, String path) throws IOException {
        
        try(InputStream in = getInputStream(path)){

            logger.log(Level.FINER, "Loading properties from: {0}", path);           
            
            if(in == null) {
                throw new NullPointerException();
            }
            
            props.load(in);
            
            Level level = this.isUseCache() ? Level.INFO : Level.FINE;

            if(logger.isLoggable(level))            
            logger.log(level, "  From: {0}\nLoaded: {1}", new Object[]{path, props.stringPropertyNames()});           
        }
    }
    
    @Override
    public void store(Properties props, String path) throws IOException {
  
        try(OutputStream out = getOutputStream(path, false)){
            
            Level level = this.isUseCache() ? Level.INFO : Level.FINE;

            if(logger.isLoggable(level)) {
                logger.log(level, "Saving to: {0} properties:\n{1}", new Object[]{path, props.stringPropertyNames()}); 
            }
            logger.log(Level.FINER, "{0}", props);

            props.store(out, "Saved by: " + this.getClass().getSimpleName() + 
                    " @" + System.getProperty("user.name") + " on " + new Date());
        }
    }
}
