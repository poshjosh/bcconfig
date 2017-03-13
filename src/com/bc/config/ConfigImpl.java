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
public class ConfigImpl extends AbstractConfig {
    
    private static transient final Logger logger = Logger.getLogger(ConfigImpl.class.getName());

    private final Properties props;

    public ConfigImpl(Properties props, String timePattern) {
        super(timePattern);
        this.props = Objects.requireNonNull(props);
    }

    /**
     * @return The {@link java.util.Properties Properties} which contains this 
     * config's data. Changes to the returned {@link java.util.Properties Properties}
     * are reflected in this config.
     */
    @Override
    public Properties getProperties() {
        return props;
//        Properties output = new Properties();
//        Set<String> names = props.stringPropertyNames();
//        for(String name:names) {
//            output.setProperty(name, props.getProperty(name));
//        }
//        return output;
    }
    
    @Override
    public String doGetProperty(String key, String defaultValue) {
        String value = props.getProperty(key, defaultValue);
        return value;
    }
    
    @Override
    public Object doSetProperty(String key, String value) {
        final Object output = props.setProperty(key, value);
        return output;
    }

    @Override
    public int size() {
        return props.size();
    }

    @Override
    public Set<String> stringPropertyNames() {
        return props.stringPropertyNames();
    }
}
