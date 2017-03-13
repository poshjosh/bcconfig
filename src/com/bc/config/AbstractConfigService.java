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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 28, 2016 12:00:49 AM
 */
public abstract class AbstractConfigService implements ConfigService {
    
    private transient final Logger logger = Logger.getLogger(AbstractConfigService.class.getName());

    private final boolean useCache;
    
    private final String timePattern;
    
    private final ConfigGroup cachedConfigs;
    
    public AbstractConfigService(String timePattern, boolean useCache) { 
        
        this(new ConfigGroupImpl(), timePattern, useCache);
    }
    
    public AbstractConfigService(
            ConfigGroup cache, String timePattern, boolean useCache) { 
        
        this.cachedConfigs = Objects.requireNonNull(cache);
        
        this.timePattern = timePattern;
        
        this.useCache = useCache;
    }

    public abstract String getDefaultPath(String filename);

    public abstract String getPath(String filename);
    
    @Override
    public String loadPropertyFor(String filename, String key) throws IOException{
        Config properties = this.load((String)null, this.getPath(filename));
        return properties.getProperty(key);
    }
    
    @Override
    public String loadPropertyFor(String filename, String key, String defaultValue) throws IOException{
        Config properties = this.load((String)null, this.getPath(filename));
        return properties.getProperty(key, defaultValue);
    }
    
    @Override
    public String loadPropertyFor(String defaultfilename, String filename, String key, String defaultValue) throws IOException{
        Config properties = this.load(this.getDefaultPath(defaultfilename), this.getPath(filename));
        return properties.getProperty(key, defaultValue);
    }
    
    @Override
    public void storePropertyFor(String filename, String key, String value) throws IOException{
        Config properties = this.load((String)null, this.getPath(filename));
        properties.setProperty(key, value);
        this.storeByName(filename);
    }
    
    @Override
    public void storePropertyFor(String defaultfilename, String filename, String key, String value) throws IOException{
        Config properties = this.load(this.getDefaultPath(defaultfilename), this.getPath(filename));
        properties.setProperty(key, value);
        this.storeByName(filename);
    }

    @Override
    public Config loadByName(String defaultFilename, String filename) throws IOException{
        return load(this.getDefaultPath(defaultFilename), this.getPath(filename));
    }
    
    @Override
    public void storeByName(String filename) throws IOException {
        this.store(this.getPath(filename));
    }
    
    @Override
    public Config load(String defaultPath, String path) throws IOException {

        final String name = this.getName(path);
        
        Config output = null;
        
        if(this.isUseCache()) {
            output = cachedConfigs.get(name);
        }
        
        if(output == null) {
            
            Properties defaults = null;
            if(defaultPath != null) {
                defaults = new Properties();
                load(defaults, defaultPath);
            }

            Properties outputProps = new Properties(defaults);
            load(outputProps, path);
            
            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "{0} = {1}\n{2}", new Object[]{path, name, outputProps});
            }
            
            output = new ConfigImpl(outputProps, this.timePattern);
            
            if(this.useCache) {
                cachedConfigs.put(name, output);
            }
        }
        
        return output;
    }

    public void load(Properties props, String path) throws IOException {

        try(InputStream in = getInputStream(path)){

logger.log(Level.FINER, "Loading properties from: {0}", path);           
            
            if(in == null) {
                throw new NullPointerException();
            }
            
            props.load(in);
            
Level level = this.useCache ? Level.INFO : Level.FINE;

if(logger.isLoggable(level))            
logger.log(level, "From: {0} loaded properties:\n{1}", new Object[]{path, props.stringPropertyNames()});           
        }
    }

    public void store(String path) throws IOException {

        if(!this.isUseCache()) {
            return;
        }

        final String name = this.getName(path);
        
        final Config config = this.getConfigs().get(name);
        
        final Properties props = config == null ? null : config.getProperties();
        
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "{0} = {1}\n{2}", new Object[]{path, name, config});
        }
        
        if(props != null) {
            this.store(props, path);
        }
    }
    
    public void store(Properties props, String path) throws IOException {
  
        try(OutputStream out = getOutputStream(path, false)){
            
Level level = this.useCache ? Level.INFO : Level.FINE;

if(logger.isLoggable(level)) {
    logger.log(level, "Saving to: {0} properties:\n{1}", new Object[]{path, props.stringPropertyNames()}); 
}
logger.log(Level.FINER, "{0}", props);

            props.store(out, "Saved by: " + this.getClass().getSimpleName() + 
                    " @" + System.getProperty("user.name") + " on " + new Date());
        }
    }
    
    public InputStream getResourceAsStream(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }
    
    public URL getResource(String resourcePath) {
        return Thread.currentThread().getContextClassLoader().getResource(resourcePath);
    }
    
    public InputStream getInputStream(String path) throws FileNotFoundException {
        InputStream in = this.getResourceAsStream(path);
        if(in == null) {
            in = new FileInputStream(path);
        }
logger.log(Level.FINER, "InputStream: {0}", in);
        return in;
    }
    
    public OutputStream getOutputStream(String path, boolean append) throws FileNotFoundException {
        OutputStream out;
        try {
            out = new FileOutputStream(path, append);
        } catch (FileNotFoundException e) {
            try {
                Path resPath = this.getResourcePath(path, null);
                if(resPath == null) {
                    out = null;
                }else{
                    out = new FileOutputStream(resPath.toString(), false);
                }
            } catch (URISyntaxException use) {
                throw e;
            }
        }
        return out;
    }
    
    public String getName(String path) {
        return Paths.get(path).getFileName().toString();
    }
    
    public Path getResourcePath(String resourcePath, Path outputIfNone) throws URISyntaxException {
        
        final URL url = this.getResource(resourcePath);
        
        logger.log(Level.FINE, "Resolved resource: {0} to URL: {1}", new Object[]{resourcePath, url});
        
        final URI uri = url.toURI();
        
        return this.getPath(uri, outputIfNone);
    }
    
    public Path getPath(URI uri, Path outputIfNone) {
        Path output;
        try{
            output = Paths.get(uri);
        }catch(java.nio.file.FileSystemNotFoundException fsnfe) {
            
            logger.log(Level.WARNING, "For URI: "+uri, fsnfe);
            
            final Map<String, String> env = Collections.singletonMap("create", "true");
            
            try(FileSystem fileSystem = FileSystems.newFileSystem(uri, env)) {
                
                output = Paths.get(uri);
                    
            }catch(IOException ioe) {
                
                logger.log(Level.WARNING, "Exception creating FileSystem for: "+uri, ioe);
                
                output = outputIfNone;
            }
        }
        
        logger.log(Level.FINE, "Resolved URI: {0} to Path: {1}", new Object[]{uri, output});
        
        return output;
    }

    @Override
    public ConfigGroup getConfigs() {
        final ConfigGroup configs;
        if(this.isUseCache()) {
            configs = this.getCachedConfigs();
        }else{
            try{
                configs = this.load();
            }catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
        return configs;
    }

    @Override
    public final String getTimePattern() {
        return timePattern;
    }
    
    @Override
    public final boolean isUseCache() {
        return useCache;
    }

    @Override
    public final ConfigGroup getCachedConfigs() {
        return cachedConfigs;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "{useCache=" + useCache +
                ", timePattern: " + timePattern + "\nNames = " +
                (cachedConfigs == null ? null : cachedConfigs.keySet()) + '}';
    }
}