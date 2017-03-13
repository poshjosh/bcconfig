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

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 19, 2016 10:49:25 PM
 */
public class ConfigTestBase {

    public static final String[] KEYS = {
        "security.algorithm", 
        "boolean.property", "long.property", "double.property", 
        "string.property", "date.property", "array.property", 
        "security.encryptionKey", "path.property", "regex.property"
    };

    public Config print(ConfigGroup config, String key) throws ParseException {
        
        Config props = this.getView(config, key);

        this.print(props, key);
        
        return props;
    }
    
    public Config printSubset(ConfigGroup config, String subsetFilename, String subsetName) {
        
        Config subset = config.subset(subsetFilename, subsetName, ".");
        
System.out.println("subset('"+subsetFilename+"', '"+subsetName+"', ','): "+subset);  
        
        return subset;
    }
    
    public void print(Config view, String key) throws ParseException {
System.out.println(key + " = " + view.getProperty(key));  
        if(key.contains("boolean")) {
System.out.println(key + " = " + view.getBoolean(key));            
        }else if(key.contains("int")) {
System.out.println(key + " = " + view.getLong(key));            
        }else if(key.contains("long")) {
System.out.println(key + " = " + view.getInt(key));            
        }else if(key.contains("float")) {
System.out.println(key + " = " + view.getFloat(key));            
        }else if(key.contains("double")) {
System.out.println(key + " = " + view.getDouble(key));            
        }else if(key.contains("string")) {
System.out.println(key + " = " + view.getString(key));            
        }else if(key.contains("date")) {
            Calendar time = view.getTime(key);
System.out.println(key + " = " + (time == null ? null : time.getTime()));            
        }else if(key.contains("array")) {
            String [] array = view.getArray(key);
System.out.println(key + " = " + (array == null ? null : Arrays.asList(array)));            
        }else if(key.contains("collection")) {
            Collection<String> collection = view.getCollection(key);
System.out.println(key + " = " + collection);            
        }else {
System.out.println(key + " = " + view.getString(key));                        
        }
    }
    public Config getView(ConfigGroup configGroup, String key) {
        
        Config output;
        
        if(key.startsWith("general.")) {
            output = configGroup.get("general.properties");
        }else if(key.startsWith("security.")) {
            output = configGroup.get("security.properties");
        }else{
            output = null;
            String [] prefixesForGeneral = {"boolean.", "long.", "double.", "string", "date.", "array.", "path.", "regex."};
            for(String prefix:prefixesForGeneral) {
                if(key.startsWith(prefix)) {
                    output = configGroup.get("general.properties");
                    break;
                }
            }
        }
        if(output == null) {
            throw new UnsupportedOperationException("Unexpected key: "+key);
        }
        return output;
    }
    
    public ConfigService getConfigurationService() {
        //Sun Jan 15 23:25:15 WAT 2017
        final String timePattern = "dd MMMM yyyy";
        
        final String propertiesDir = System.getProperty("user.home") + 
        "/Documents/NetBeansProjects/bcconfig/test/META-INF/properties"; 
        
        final String defaultPropertiesDir = propertiesDir  + "/defaults";
     
        return this.getConfigurationService(timePattern, defaultPropertiesDir, propertiesDir);
    }
    
    public ConfigService getConfigurationService(
        String timePattern, String defaultPropertiesDir, String propertiesDir) {
        
        return new DirConfigService(defaultPropertiesDir, propertiesDir, timePattern);
    }
}
