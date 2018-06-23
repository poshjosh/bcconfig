package com.bc.config;

import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Represents a view of {@link java.util.Properties}
 * <p>
 * Within the <tt>.properties</tt> file, any property may be reference by name thus:
 * </p>
 * <code><pre>
 * name = John
 * greeting = Hello ${name}
 * </pre></code>
 * @author Chinomso Bassey Ikwuagwu on Jul 18, 2016 4:32:02 PM
 */
public class ConfigImpl extends AbstractConfig<Properties> {
    
    private static transient final Logger logger = Logger.getLogger(ConfigImpl.class.getName());

    private final Properties data;
    
    public ConfigImpl(Properties data) {
        this(data, "EEE MMM dd HH:mm:ss z yyyy");
    }

    public ConfigImpl(Properties data, String timePattern) {
        super(timePattern);
        this.data = Objects.requireNonNull(data);
    }

    /**
     * @return The Object usually {@link java.util.Properties Properties} or 
     * {@link java.util.Map Map} which contains this config's data. Changes to 
     * the returned data are reflected in this config.
     */
    @Override
    public Properties getData() {
        final boolean copy = false;
        return data;
    }
    
    @Override
    public String doGet(String key, String defaultValue) {
        final String value = data.getProperty(key, defaultValue);
        return value;
    }
    
    @Override
    public Object doSet(String key, String value) {
        final Object output = data.setProperty(key, value);
        return output;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Set<String> getNames() {
        return data.stringPropertyNames();
    }
}
