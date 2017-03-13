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

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 27, 2016 11:38:22 PM
 */
public class SimpleConfigService extends AbstractConfigService {

    private transient final Logger logger = Logger.getLogger(SimpleConfigService.class.getName());
    
    private final String [] defaultPaths;
    
    private final String [] paths;

    public SimpleConfigService(
            String defaultPath, String path) { 
        
        this(defaultPath, path, "EEE MMM dd HH:mm:ss z yyyy");
    }
    
    public SimpleConfigService(
            String defaultPath, String path,  String timePattern) { 

        this(new String[]{defaultPath}, new String[]{path}, timePattern);
    }
    
    public SimpleConfigService(
            String [] defaultPaths, String [] paths) { 
        
        this(defaultPaths, paths, "EEE MMM dd HH:mm:ss z yyyy");
    }
    
    public SimpleConfigService(
            String [] defaultPaths, String [] paths,  String timePattern) { 
        
        this(defaultPaths, paths, timePattern, true);
    }
    
    public SimpleConfigService(
            String [] defaultPaths, String [] paths,  String timePattern, boolean useCache) { 
        
        this(new ConfigGroupImpl(), defaultPaths, paths, timePattern, useCache);
    }

    public SimpleConfigService(
            ConfigGroup configuration,
            String [] defaultPaths, String [] paths,  String timePattern) { 
        
        this(configuration, defaultPaths, paths, timePattern, true);
    }
    
    public SimpleConfigService(
            ConfigGroup configuration,
            String [] defaultPaths, String [] paths,  String timePattern, boolean useCache) { 
        
        super(configuration, timePattern, useCache);
        
        this.defaultPaths = defaultPaths;
        
        this.paths = Objects.requireNonNull(paths);
        
if(logger.isLoggable(Level.INFO))        
logger.log(Level.INFO, "  Defaults: {0}\nProperties: {1}", 
new Object[]{this.defaultPaths==null?null:Arrays.toString(defaultPaths), Arrays.toString(paths)});
    }

    @Override
    public ConfigGroup load() throws IOException {
    
        final ConfigGroup output = this.isUseCache() ? this.getCachedConfigs() : new ConfigGroupImpl();
        
        for(int i=0; i<this.paths.length; i++) {
            
            final String defaultPath = defaultPaths == null ? null : defaultPaths[i];
            final String path = paths[i];
            
if(logger.isLoggable(Level.CONFIG))            
logger.log(Level.CONFIG, "Loading: {0} over {1}", new Object[]{path, defaultPath}); 

            // If use cache is true add the loaded properties to the cache
            //
            final Config config = this.load(defaultPath, path);
            
            output.put(this.getName(path), config);
        }
        
        return output;
    }
    
    @Override
    public void store() throws IOException {
        
        final ConfigGroup configGroup = this.getCachedConfigs();
        
        for(String path : this.paths) {
            
            final String fileName = this.getName(path);
            
            Config config = configGroup.get(fileName);
            
            this.store(config.getProperties(), path);
        }
    }
    
    @Override
    public String getDefaultPath(String filename) {
        if(filename == null) {
            return null;
        }
        String defaultPath = this.getPathForName(this.defaultPaths, filename);
        return defaultPath;
    }

    @Override
    public String getPath(String filename) {
        Objects.requireNonNull(filename);
        String path = this.getPathForName(paths, filename);
        return path;
    }
    
    private String getPathForName(String [] arr, String name) {
        for(String e:arr) {
            if(e.endsWith(name)) {
                return e;
            }
        }
        throw new NullPointerException();
    }
}
/**
 * 
    @Override
    protected Config doMerge() throws IOException {
        
        Properties all = new Properties();
        
        for(int i=0; i<this.paths.length; i++) {
            
            String defaultPath = defaultPaths == null ? null : defaultPaths[i];
            String path = paths[i];
            
if(logger.isLoggable(Level.CONFIG))            
logger.log(Level.CONFIG, "Loading: {0} over {1}", new Object[]{path, defaultPath}); 

            // If use cache is true add the loaded properties to the cache
            //
            Config config = this.load(defaultPath, path);
            
            Set<String> keys = config.getProperties().stringPropertyNames();
            
            for(String key:keys) {
                
                if(all.getProperty(key) != null) {
                
                    throw new UnsupportedOperationException("Property '"+key+"' is duplicated in file: "+path+", with defaults: "+defaultPath);
                }
                
                all.setProperty(key, config.getProperty(key));
            }
        }
        
        return new ConfigImpl(all, this.getTimePattern());
    }
 * 
 */