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

import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Subsets are not editable
 * @author Chinomso Bassey Ikwuagwu on Jul 18, 2016 7:02:42 PM
 */
public class ConfigSubset extends ConfigImpl {

    public ConfigSubset(Properties props, String timePattern) {
        super(props, timePattern);
    }

    public ConfigSubset(Config parent, String subset_name, String separator) {
        
        super(new Properties(),  parent.getTimePattern());
       
        final String prefix = subset_name + separator;
        
        StringBuilder reused = new StringBuilder();
        
        final String regex = Pattern.matches("\\p{Punct}", separator) ? "["+separator+"]" : separator;
        
Logger logger = Logger.getLogger(this.getClass().getName());
final Level FINE = Level.FINE;
final Level FINER = Level.FINER;

        Set<String> prop_names = parent.stringPropertyNames();

if(logger.isLoggable(FINE))            
logger.log(FINE, "Extracting subset: {0} from property names: {1}", 
new Object[]{subset_name, prop_names});

        for(String prop_name:prop_names) {

            reused.setLength(0);
            
            if(prop_name.startsWith(prefix)) {

                String [] parts = prop_name.split(regex); 

//XLogger.getInstance().log(Level.FINER, "\"{0}\".split(\"{1}\") = {2}", 
//this.getClass(), prop_name, regex, parts==null?null:Arrays.toString(parts));

                if(parts != null && parts.length > 1) {
                    
                    int start = 1; // ignore the first part, start at 1 rather than 0
                    for(int i=start; i<parts.length; i++) {
                        reused.append(parts[i]);
                        if(i < parts.length -1) {
                            reused.append(separator);
                        }
                    }
                }

if(logger.isLoggable(FINER))                    
logger.log(FINER, "For subset: {0}, updating: {1} to {2}", 
new Object[]{subset_name, prop_name, reused});

                if(reused.length() > 0) {
                    
                    super.setProperty(reused.toString(), parent.getProperty(prop_name));
                }
            }
        }

logger.log(FINE, "Subset has {0} properties", this.size());
    }
    
    @Override
    public Object setProperty(String key, String value) {
        throw new UnsupportedOperationException("Read only");
    }
}
