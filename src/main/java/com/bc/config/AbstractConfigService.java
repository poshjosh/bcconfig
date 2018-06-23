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
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 28, 2016 12:00:49 AM
 */
public abstract class AbstractConfigService<DATA_TYPE> implements ConfigService<DATA_TYPE> {
    
    private transient final Logger logger = Logger.getLogger(AbstractConfigService.class.getName());

    private final boolean useCache;
    
    private final String timePattern;
    
    private final ConfigGroup<DATA_TYPE> cachedConfigs;
    
    private final ClassLoader classLoader;
    
    public AbstractConfigService(String timePattern) { 
        
        this(Thread.currentThread().getContextClassLoader(), new ConfigGroupImpl(), timePattern, true);
    }
    
    public AbstractConfigService(
            ClassLoader classLoader, ConfigGroup<DATA_TYPE> cache, 
            String timePattern, boolean useCache) {
        
        this.classLoader = Objects.requireNonNull(classLoader);
        
        this.cachedConfigs = Objects.requireNonNull(cache);
        
        this.timePattern = timePattern;
        
        this.useCache = useCache;
    }

    public abstract String [] getDefaultPaths(String filename);

    public abstract String getPath(String filename);
    
    public abstract void load(Properties props, String path) throws IOException;
    
    public abstract void store(DATA_TYPE data, String path) throws IOException;
    
    @Override
    public String loadFor(String filename, String key) throws IOException{
        Config properties = this.load((String)null, this.getPath(filename));
        return properties.get(key);
    }
    
    @Override
    public String loadFor(String filename, String key, String defaultValue) throws IOException{
        Config properties = this.load((String)null, this.getPath(filename));
        return properties.get(key, defaultValue);
    }
    
    @Override
    public String loadFor(String defaultfilename, String filename, String key, String defaultValue) throws IOException{
        Config properties = this.load(this.getDefaultPaths(defaultfilename), this.getPath(filename));
        return properties.get(key, defaultValue);
    }
    
    @Override
    public void storeFor(String filename, String key, String value) throws IOException{
        Config properties = this.load((String)null, this.getPath(filename));
        properties.set(key, value);
        this.storeByName(filename);
    }
    
    @Override
    public void storeFor(String defaultfilename, String filename, String key, String value) throws IOException{
        Config properties = this.load(this.getDefaultPaths(defaultfilename), this.getPath(filename));
        properties.set(key, value);
        this.storeByName(filename);
    }

    @Override
    public Config loadByName(String defaultFilename, String filename) throws IOException{
        return load(this.getDefaultPaths(defaultFilename), this.getPath(filename));
    }
    
    @Override
    public void storeByName(String filename) throws IOException {
        this.store(this.getPath(filename));
    }
    
    @Override
    public Config load(String defaultPath, String path) throws IOException {
        return load(defaultPath==null?new String[0]:new String[]{defaultPath}, path);
    }

    public void store(String path) throws IOException {

        if(!this.isUseCache()) {
            return;
        }

        final String name = this.getName(path);
        
        final Config<DATA_TYPE> config = this.getConfigs().get(name);
        
        final DATA_TYPE data = config == null ? null : config.getData();
        
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "{0} = {1}\n{2}", new Object[]{path, name, config});
        }
        
        if(data != null) {
            this.store(data, path);
        }
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
    
    public InputStream getResourceAsStream(String path) {
        return this.getClassLoader().getResourceAsStream(path);
    }
    
    public URL getResource(String resourcePath) {
        return this.getClassLoader().getResource(resourcePath);
    }
    
    public ClassLoader getClassLoader() {
        return this.classLoader;
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
    public ConfigGroup<DATA_TYPE> getConfigs() {
        final ConfigGroup<DATA_TYPE> configs;
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
    public final ConfigGroup<DATA_TYPE> getCachedConfigs() {
        return cachedConfigs;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "{useCache=" + useCache +
                ", timePattern: " + timePattern + "\nNames = " +
                (cachedConfigs == null ? null : cachedConfigs.keySet()) + '}';
    }
}