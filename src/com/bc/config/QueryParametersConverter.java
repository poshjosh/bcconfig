package com.bc.config;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @(#)QueryParametersConverter.java   25-Dec-2013 02:35:40
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class QueryParametersConverter implements Serializable {
    
    /**
     * If empty strings are allowed they are appended as empty String. This is 
     * because if <tt>a=</tt> is split we get a single element Array. The second
     * part is considered an empty String
     */
    private boolean emptyStringsAllowed;
    
    /**
     * When there are multiple separators, the separator at this index
     * will be used to divide a pair.<br/>
     * Given the input: abc=1=d. <br/>
     * Considering the first '=' as separator we have. <tt>abc : 1=d</tt><br/>
     * Considering the second '=' as separator we have. <tt>abc=1 : d</tt><br/>
     * If this value is less than 0, then first separator will be used. If it is 
     * greater then the index of the last separator then the last separator will 
     * be used.
     */
    private int separatorIndex;
    
    private String separator;
    
    public QueryParametersConverter() { 
        this("&");
    }
    
    public QueryParametersConverter(String separator) { 
        this(false, separator);
    }
    
    public QueryParametersConverter(boolean emptyStringsAllowed, String separator) { 
        this.emptyStringsAllowed = emptyStringsAllowed;
        this.separator = separator;
    }

    public QueryParametersConverter(boolean emptyStringsAllowed, 
            int separatorIndex, String separator) { 
        this.emptyStringsAllowed = emptyStringsAllowed;
        this.separatorIndex = separatorIndex;
        this.separator = separator;
    }

    /**
     * @see #toMap(java.lang.String) 
     */
    public Map<String, String> reverse(String input) {
        return this.toMap(input);
    }
    
    /**
     * @param input The query string of format <tt>key_0=val_0&key_1=val_1...</tt>
     * to convert into a map with corresponding keys and values.
     * @return The Map representation of the keys and values in the input
     * query string
     */
    public Map<String, String> toMap(String input) {
        
        if(input == null) {
            throw new NullPointerException();
        }
        
Logger logger = Logger.getLogger(this.getClass().getName());
Level level = Level.FINER;

if(logger.isLoggable(level))
logger.log(level, "{0}. Separator: {1}, Empty strings allowed: {2}, Query: {3}", 
        new Object[]{separator, emptyStringsAllowed, input}); 

        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        String [] queryPairs = input.split(separator);

        for(int i=0; i<queryPairs.length; i++) {
            
logger.log(level, "Pair[{0}]: {1}", new Object[]{i, queryPairs[i]});

            String [] parts = queryPairs[i].split("=");
            
logger.log(level, "Pair[{0}]: {1}", new Object[]{i, parts==null?null:Arrays.toString(parts)});

            String key;
            String val;
            
            if(parts.length == 0) {
                continue;
            }else if (parts.length == 1) {
                if(this.isEmptyStringsAllowed()) {
                    key = parts[0];
                    val = ""; //We prefer an empty String to null -> Query standards
                }else{
                    continue;
                }
            }else if(parts.length == 2) {
                key = parts[0];
                val = parts[1];
            }else{
                if(separatorIndex < 0) {
                    separatorIndex = 0;
                }
                if(separatorIndex >= parts.length-1) {
                    separatorIndex = parts.length - 2;
                }

                StringBuilder builder = new StringBuilder();
                for(int partIndex=0; partIndex<separatorIndex+1; partIndex++) {
                    builder.append(parts[partIndex]);
                    if(partIndex < separatorIndex) {
                        builder.append('=');
                    }
                }
                key = builder.toString();
                
                builder.setLength(0);
                for(int partIndex=separatorIndex+1; partIndex<parts.length; partIndex++) {
                    builder.append(parts[partIndex]);
                    if(partIndex < parts.length-1) {
                        builder.append('=');
                    }
                }
                val = builder.toString();
            }
            
            result.put(
                    this.reverseKey(key.trim()), 
                    this.reverseValue(val==null?null:val.trim()));
        }
        
logger.log(level, "{0}. Output: {1}", result);        

        return result;
    }
    
    public String reverseKey(String key) {
        return key;
    }
    
    public String reverseValue(String val) {
        return val;
    }
    
    public String convert(Map params) {
        return this.toQueryString(params);
    }
    
    /**
     * Converts the key/value pairs in the input map to a query string
     * for the format <tt>key_0=val_0&key_1=val_1...</tt>
     * @param params The Map whose key/value pairs will be converted to a
     * query String.
     * @return The query string representation of the input Map. 
     */
    public String toQueryString(Map params) {
        
        StringBuilder builder = new StringBuilder();
        
        Iterator iter = params.entrySet().iterator();
        
        do {
            
            if (!iter.hasNext()) {
                break;
            }
            
            java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            
            builder.append(this.convertKey(key));
            builder.append('=');
            builder.append(this.convertValue(val));
            
            if (iter.hasNext()) {
                builder.append(separator);
            }        
        } while (true);
        
        return builder.toString();
    }
    
    public Object convertKey(Object key) {
        return key;
    }
    
    public Object convertValue(Object val) {
        return val;
    }

    public int getSeparatorIndex() {
        return separatorIndex;
    }

    public void setSeparatorIndex(int separatorIndex) {
        this.separatorIndex = separatorIndex;
    }
    
    public boolean isEmptyStringsAllowed() {
        return emptyStringsAllowed;
    }

    public String getSeparator() {
        return separator;
    }
}
