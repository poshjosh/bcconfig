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

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 18, 2016 11:11:13 AM
 */
public interface ConfigService<DATA_TYPE> {

    ConfigGroup<DATA_TYPE> getCachedConfigs();
    
    ConfigGroup<DATA_TYPE> getConfigs();
    
    String getTimePattern();
        
    boolean isUseCache();
    
    ConfigGroup<DATA_TYPE> load() throws IOException;
    
    Config<DATA_TYPE> load(String defaultPath, String path) throws IOException;
    
    Config<DATA_TYPE> load(String [] defaultPaths, String path) throws IOException;

    Config<DATA_TYPE> loadByName(String defaultFilename, String filename) throws IOException;

    String loadFor(String filename, String key) throws IOException;

    String loadFor(String filename, String key, String defaultValue) throws IOException;

    String loadFor(String defaultfilename, String filename, String key, String defaultValue) throws IOException;

    void store() throws IOException;
    
    void storeByName(String filename) throws IOException;

    void storeFor(String filename, String key, String value) throws IOException;

    void storeFor(String defaultfilename, String filename, String key, String value) throws IOException;
}
