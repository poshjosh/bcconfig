package com.bc.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link com.bc.config.ConfigGroup} contains multiple {@link com.bc.config.Config} instances
 * @author Josh
 */
public class ConfigGroupImpl<DATA_TYPE> 
        extends HashMap<String, Config<DATA_TYPE>> 
        implements ConfigGroup<DATA_TYPE>, Serializable {
    
    private transient final Logger logger = Logger.getLogger(ConfigGroupImpl.class.getName());

    public ConfigGroupImpl() { }

    public ConfigGroupImpl(String key, Properties value) {
        this(key, new ConfigImpl(value, "EEE MMM dd HH:mm:ss z yyyy"));
    }
    
    public ConfigGroupImpl(String key, Properties value, String timePattern) {
        this(key, new ConfigImpl(value, timePattern));
    }
    
    public ConfigGroupImpl(String key, Config value) {
        this.put(key, value);
    }

    @Override
    public Config get(String filename) {
        final Config config = super.get(filename); 
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Filename: {0}, config:\n{1}", 
                    new Object[]{filename, config});
        }
        return config;
    }

    /**
     * @param filename
     * @param subset_name
     * @param separator
     * @return The subset of properties with prefix <tt>subset_name + separator</tt> 
     * for {@link com.bc.config.Config Config} loaded from the 
     * specified filename.
     * @see com.bc.config.Config#subset(java.lang.String, java.lang.String) 
     */
    @Override
    public Config subset(String filename, String subset_name, String separator) {
        
        Config props = this.get(filename);
        
        return props.subset(subset_name, separator);
    }
    
    @Override
    public String getFor(String filename, String key) {
        return this.getFor(filename, key, null);
    }

    @Override
    public String getFor(String filename, String key, String defaultValue) {
        Config properties = this.get(filename);
        String value = properties.get(key, defaultValue);
        return value;
    }

    @Override
    public Object setFor(String filename, String key, String value) {
        Config properties = this.get(filename);
        return properties.set(key, value);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": " + this.keySet();
    }
}

