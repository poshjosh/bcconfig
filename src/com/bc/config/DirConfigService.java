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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 18, 2016 11:10:41 AM
 */
public class DirConfigService extends AbstractConfigService {
    
    private static transient final Logger logger = Logger.getLogger(DirConfigService.class.getName());
    
    private final String defaultPropertiesDir;
    
    private final String propertiesDir;
    
    private final FilenameFilter filenameFilter;
    
    public DirConfigService(
            String defaultPropertiesDir, String propertiesDir, String timePattern) { 
        this(
            new ConfigGroupImpl(),
            defaultPropertiesDir, propertiesDir, timePattern
        );
    }
    
    public DirConfigService(
            ConfigGroup configuration, 
            String defaultPropertiesDir, String propertiesDir, String timePattern) { 
        this(
                configuration, defaultPropertiesDir, propertiesDir,
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".properties");
                    }
                }, timePattern, true
        );
    }

    public DirConfigService(
            String defaultPropertiesDir, String propertiesDir, 
            FilenameFilter filenameFilter, String timePattern, boolean useCache) { 
        this(
                new ConfigGroupImpl(),
                defaultPropertiesDir, propertiesDir,
                filenameFilter, timePattern, useCache
        );
    }
    
    public DirConfigService(ConfigGroup configuration,
            String defaultPropertiesDir, String propertiesDir, 
            FilenameFilter filenameFilter,  String timePattern, boolean useCache) {
        this(
                Thread.currentThread().getContextClassLoader(),
                configuration,
                defaultPropertiesDir, propertiesDir,
                filenameFilter, timePattern, useCache
        );
    } 
    
    public DirConfigService(
            ClassLoader classLoader, ConfigGroup configuration,
            String defaultPropertiesDir, String propertiesDir, 
            FilenameFilter filenameFilter,  String timePattern, boolean useCache) { 
        
        super(classLoader, configuration, timePattern, useCache);
        
        this.defaultPropertiesDir = defaultPropertiesDir;
        
        this.propertiesDir = Objects.requireNonNull(propertiesDir);
        
        this.filenameFilter = filenameFilter;
        
if(logger.isLoggable(Level.INFO))        
logger.log(Level.INFO, "  Defaults: {0}\nProperties: {1}", 
new Object[]{this.defaultPropertiesDir, this.propertiesDir});
    }

    @Override
    public ConfigGroup load() throws IOException {
    
        final ConfigGroup output = this.isUseCache() ? this.getCachedConfigs() : new ConfigGroupImpl();
        
        final String dirString = this.defaultPropertiesDir == null ? this.propertiesDir : this.defaultPropertiesDir;
        
        final String [] fileNames = this.getFileNames(dirString);

logger.log(Level.INFO, "Loading properties files from: {0}", dirString);

        for(String name:fileNames) {
            
logger.log(Level.CONFIG, "Loading: {0}", name); 

            // If use cache is true add the loaded properties to the cache
            //
            final Config config = this.loadByName(name, name);
            
            output.put(name, config);
        }
        
        return output;
    }
    
    @Override
    public void store() throws IOException {
        
        final String [] fileNames = this.getFileNames(propertiesDir);
        
        final ConfigGroup configGroup = this.getCachedConfigs();
        
        for(String fileName : fileNames) {
            
            Config config = configGroup.get(fileName);
            
            this.store(config.getProperties(), this.getPath(fileName));
        }
    }

    @Override
    public String [] getDefaultPaths(String filename) {
        if(filename == null) {
            return null;
        }
        if(this.defaultPropertiesDir == null) {
            return null;
        }else{
            return new String[]{this.defaultPropertiesDir + File.separatorChar + filename};
        }
    }

    @Override
    public String getPath(String filename) {
        Objects.requireNonNull(filename);
        return this.propertiesDir + File.separatorChar + filename;
    }

    public final FilenameFilter getFilenameFilter() {
        return this.filenameFilter;
    }
    
    public final String getPropertiesDir() {
        return this.propertiesDir;
    }

    public final String getDefaultPropertiesDir() {
        return this.defaultPropertiesDir;
    }
    
    public String [] getFileNames(String dirString) throws FileNotFoundException {
        
        final File dir = new File(dirString);
        
        if(!dir.exists()) {
            throw new FileNotFoundException(dirString);
        }
        
        final String [] output = this.getFilenameFilter() == null ?
                dir.list() : dir.list(this.getFilenameFilter());

        return output;
    }

    public File [] getFiles(String dirString) throws FileNotFoundException {
        
        final File dir = new File(dirString);
        
        if(!dir.exists()) {
            throw new FileNotFoundException(dirString);
        }
        
        
        final File [] output = this.getFilenameFilter() == null ?
                dir.listFiles() : dir.listFiles(this.getFilenameFilter());

        return output;
    }
}
/**
 * 
    @Override
    protected Config doMerge() throws IOException {
        
        final String dirString = this.defaultPropertiesDir == null ? this.propertiesDir : this.defaultPropertiesDir;
        
        final String [] fileNames = this.getFileNames(dirString);

        Properties all = new Properties();
        
logger.log(Level.INFO, "Merging all properties file in: {0}", dirString);
        
        for(String name:fileNames) {
            
logger.log(Level.CONFIG, "Merging: {0}", name); 

            // If use cache is true add the loaded properties to the cache
            //
            Config props = this.loadByName(name, name);
            
            Set<String> keys = props.getProperties().stringPropertyNames();
            
            for(String key:keys) {
                
                if(all.getProperty(key) != null) {
                
                    throw new UnsupportedOperationException("Property '"+key+"' is duplicated in file: "+name);
                }
                
                all.setProperty(key, props.getProperty(key));
            }
        }
        
        return new ConfigImpl(all, this.getTimePattern());
    }
 * 
 */
