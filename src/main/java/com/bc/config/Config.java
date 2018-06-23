package com.bc.config;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Represents a view of {@link java.util.Properties}
 * <p>
 * Within the <tt>.properties</tt> file, any property may be reference by name thus:
 * </p>
 * <code><pre>
 * name = John
 * greeting = Hello ${name}
 * </pre></code>
 * @author Chinomso Ikwuagwu
 */
public interface Config<DATA_TYPE> {

    Map<String, Object> toMap();
    
    Map<String, Object> toMap(Set<String> names);
    
    /**
     * @return The Object usually {@link java.util.Properties Properties} or 
     * {@link java.util.Map Map} which contains this config's data. Changes to 
     * the returned data are reflected in this config.
     */
    DATA_TYPE getData();

    String get(String key);

    String get(String key, String defaultValue);

    Object set(String key, String value);
    
    Set<String> getNames();

    String[] getArray(String key);

    String[] getArray(String key, String separator);

    String[] getArray(String key, String[] defaultValue);

    String[] getArray(String key, String[] defaultValue, String separator);

    Boolean getBoolean(String key);
    
    Boolean getBoolean(String key, Boolean defaultValue);

    boolean getBoolean(String key, boolean defaultValue);

    Collection<String> getCollection(String key);

    Collection<String> getCollection(String key, Collection defaultValue);

    Double getDouble(String key);

    double getDouble(String key, double defaultValue);

    Float getFloat(String key);

    float getFloat(String key, float defaultValue);

    Integer getInt(String key);

    int getInt(String key, int defaultValue);

    Long getLong(String key);

    long getLong(String key, long defaultValue);

    Map getMap(String key, String separator);

    Map getMap(String key, Map defaultValue, String separator);

    Short getShort(String key);

    short getShort(String key, short defaultValue);

    String getString(String key);

    String getString(String key, String defaultValue);

    Calendar getTime(String key, Calendar defaultValue) throws ParseException;

    /**
     * @param key
     * @return The time represented by <tt>key</tt> as contained in the config
     *         document of this object, or null if no such time is specified.
     * @throws java.text.ParseException
     */
    Calendar getTime(String key) throws ParseException;

    String getTimePattern();

    String[] setArray(String key, String[] arr);

    String[] setArray(String key, String[] arr, String separator);

    Boolean setBoolean(String key, boolean b);

    String[] setCollection(String key, Collection arr);

    String[] setCollection(String key, Collection arr, String separator);

    Double setDouble(String key, double d);

    Float setFloat(String key, float i);

    Integer setInt(String key, int i);

    Long setLong(String key, long l);

    Map setMap(String key, Map value, String separator);

    Short setShort(String key, short i);

    String setString(String key, String value);

    Calendar setTime(String key, Calendar date) throws ParseException;
    
    int size();
    
    /**
     * <p>
     * <b>Note</b>: Using multiple dots (e.g <tt>..</tt>) or dollar signs 
     * (e.g <tt>$$</tt>) as separator leads to unexpected results. Best practice 
     * is to use a single dot <tt>(.)</tt>, hash <tt>(#)</tt>, underscore 
     * <tt>(_)</tt> or dollar sign <tt>($)</tt> as separator.
     * </p>
     * <p>
     * <div><b>Generally given inputs:</b></div>
     * <div>subset_name = name</div>
     * <div>separator = .</div>
     * </p>
     * <p>
     * <div>
     * If this object contains:
     * </div>
     * <div><tt>name.first = John</tt></div>
     * <div><tt>name.last = Doe</tt></div>
     * </p>
     * <p>
     * <div>The following output <tt>properties</tt> instance is returned:</div>
     * <div><tt>first = John</tt></div>
     * <div><tt>last = Doe</tt></div>
     * </p>
     * @param subset_name
     * @param separator
     * @return The subset properties
     */
    Config<DATA_TYPE> subset(String subset_name, String separator);
}
