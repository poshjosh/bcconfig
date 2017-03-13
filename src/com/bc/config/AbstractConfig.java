/*
 * Copyright 2017 NUROX Ltd.
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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a view of {@link java.util.Properties}
 * <p>
 * Within the <tt>.properties</tt> file, any property may be reference by name thus:
 * </p>
 * <code><pre>
 * name = John
 * greeting = Hello ${name}
 * </pre></code>
 * @author Chinomso Bassey Ikwuagwu on Feb 25, 2017 11:01:04 AM
 */
public abstract class AbstractConfig 
        implements Config, Serializable {
    
    private transient static final Logger logger = Logger.getLogger(AbstractConfig.class.getName());

    private final String timePattern;
    
    public AbstractConfig() {
        this(null);
    }
    
    public AbstractConfig(String timePattern) {
        this.timePattern = timePattern;
    }

    @Override
    public final String getTimePattern() {
        return timePattern;
    }
    
    protected abstract String doGetProperty(String key, String defaultValue);
    
    protected abstract Object doSetProperty(String key, String value);

    protected String doGetProperty(String key) {
        return this.doGetProperty(key, null);
    }

    @Override
    public String getProperty(String key) {
        String value = this.doGetProperty(key);
        return value == null ? null : resolveReferences(value);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String value = this.doGetProperty(key, defaultValue);
        return value == null ? null : resolveReferences(value);
    }

    private Pattern referencePattern;
    private String resolveReferences(String value) {
      if(referencePattern == null) {
        referencePattern = Pattern.compile("\\$\\{(.+?)\\}");
      }
      Matcher matcher = referencePattern.matcher(value);
      StringBuffer buff = null;
      while(matcher.find()) {
        String key = matcher.group(1);
        String val = this.doGetProperty(key);
        if(val == null) {
          val = System.getProperty(key);
        }
        if(val != null) {
            
// @bug 001 see bugFix method for description          
          val = applyBugFix001(val);
        
          if(buff == null) {
            buff = new StringBuffer();
          }
          matcher.appendReplacement(buff, val);
        }
      }
      String output;
      if(buff != null) {
        matcher.appendTail(buff);
        output = buff.toString();
      }else{
        output = value;
      }
  //System.out.println(" Input: "+value);    
  //System.out.println("Output: "+output);    
      return output;
    }

    // @bug 001 When we append C:\Users\USER to the StringBuffer we got C:UsersUSER
    // This bug fix is only a temporary measure
    private String applyBugFix001(String val) {
//      return val.replace('\\', File.separatorChar); This didn't work (At least on windows system)
        return val.replace('\\', '/');
    }

    @Override
    public Object setProperty(String key, String value) {
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Setting: {0}={1} in config:\n{2}", 
                    new Object[]{key, value, this});
        }
        final Object output = this.doSetProperty(key, value);
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "After setting {0} = {1}, previous value: {2}, config:\n{3}", 
                    new Object[]{key, value, output, this});
        }
        return output;
    }

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
    @Override
    public Config subset(String subset_name, String separator) {
       
        return new ConfigSubset(this, subset_name, separator);
    }
    
    @Override
    public Boolean setBoolean(String key, boolean b) {
        Object obj = setProperty(key, Boolean.toString(b));
        return (obj == null) ? null : Boolean.parseBoolean(obj.toString().trim());
    }

    @Override
    public Boolean getBoolean(String key) {
        String s = getProperty(key);
        return s == null ? null : Boolean.valueOf(s.trim());
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String s = getProperty(key);
        return s == null ? defaultValue : Boolean.parseBoolean(s.trim());
    }

    @Override
    public Short setShort(String key, short i) {
        Long l = setLong(key, i);
        return l == null ? null : l.shortValue();
    }

    @Override
    public Short getShort(String key) {
        Long l = getLong(key);
        return l == null ? null : l.shortValue();
    }

    @Override
    public short getShort(String key, short defaultValue) {
        return (short)getLong(key, defaultValue);
    }

    @Override
    public Integer setInt(String key, int i) {
        Long l = setLong(key, i);
        return l == null ? null : l.intValue();
    }

    @Override
    public Integer getInt(String key) {
        Long l = getLong(key);
        return l == null ? null : l.intValue();
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return (int)getLong(key, defaultValue);
    }

    @Override
    public Long setLong(String key, long l) {
        Object obj = setProperty(key, ""+l);
        return this.longValueOf(obj, null);
    }

    @Override
    public Long getLong(String key) {
        String value = getProperty(key);
        return this.longValueOf(value, null);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        String value = getProperty(key);
        return this.longValueOf(value, defaultValue);
    }

    @Override
    public Float setFloat(String key, float i) {
        Double d = setDouble(key, i);
        return (d == null) ? null : d.floatValue();
    }

    @Override
    public Float getFloat(String key) {
        Double d = getDouble(key);
        return d == null ? null : d.floatValue();
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return (float)getDouble(key, defaultValue);
    }

    @Override
    public Double setDouble(String key, double d) {
        Object obj = setProperty(key, ""+d);
        return this.doubleValueOf(obj, null);
    }

    @Override
    public Double getDouble(String key) {
        String val = getProperty(key);
        return doubleValueOf(val, null);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        String val = getProperty(key);
        return doubleValueOf(val, defaultValue);
    }
    
    private Long longValueOf(Object longString, Long defaultValue) {
        if(longString == null) {
            return defaultValue;
        }else{
            String value = longString.toString();
            value = value.trim();
            return Long.valueOf(value);
        }
    }
    
    private Double doubleValueOf(Object doubleString, Double defaultValue) {
        if(doubleString == null) {
            return defaultValue;
        }else{
            String value = doubleString.toString();
            value = value.trim();
//            if(!value.endsWith("d") && !value.endsWith("D")) {
//                value = value + 'd';
//            }
            return Double.valueOf(value);
        }
    }

    @Override
    public String setString(String key, String value) {
        return (String)this.setProperty(key, value);
    }

    @Override
    public String getString(String key) {
        return this.getProperty(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return this.getProperty(key, defaultValue);
    }

    @Override
    public Map setMap(String key, Map value, String separator) {
        QueryParametersConverter c = new QueryParametersConverter(separator);
        Object obj = setProperty(key, c.convert(value));
        return (obj == null) ? null : c.reverse(obj.toString());
    }

    @Override
    public Map getMap(String key, String separator) {
        String s = getProperty(key);
        return s == null ? null : new QueryParametersConverter(separator).reverse(s);
    }

    @Override
    public Map getMap(String key, Map defaultValue, String separator) {
        String  s = getProperty(key);
        return s == null ? defaultValue : new QueryParametersConverter(separator).reverse(s);

    }
    
    @Override
    public Calendar setTime(String key, Calendar date) throws ParseException {

        SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance();
        sdf.applyPattern(this.timePattern);

        String dateStr = sdf.format(date.getTime());

        Object obj = setProperty(key, dateStr);

        return (obj != null) ? parseTime(obj.toString(), this.timePattern) : null;
    }

    @Override
    public Calendar getTime(String key, Calendar defaultValue) throws ParseException {
        Calendar cal = getTime(key);
        return (cal == null) ? defaultValue : cal;
    }

    /**
     * @param key
     * @return The time represented by <tt>key</tt> as contained in the config
     *         document of this object, or null if no such time is specified.
     * @throws java.text.ParseException
     */
    @Override
    public Calendar getTime(String key) throws ParseException {

        String timeStr = getProperty(key);
        
Logger logger = Logger.getLogger(this.getClass().getName());
if(logger.isLoggable(Level.FINER))
logger.log(Level.FINER, "Time designation: {0}, Time : {1}", new Object[]{key, timeStr});

        return parseTime(timeStr, this.timePattern);
    }

    public static Calendar parseTime(String timeStr, String pattern) throws ParseException {

        SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance();

        sdf.applyPattern(pattern);

        if (timeStr == null || timeStr.equals("")) {
            return null;
        }else {
            if(timeStr.length()>3 && !timeStr.contains(":")) {
                timeStr+=" 00:00:00"; // Add time part
            }

            Calendar time = Calendar.getInstance(); // Today
            Date date = sdf.parse(timeStr);
            time.setTime(date);

            return time;
        }
    }

    @Override
    public String [] setArray(String key, String [] arr) {
        return setArray(key, arr, ",");
    }
    
    @Override
    public String [] setArray(String key, String [] arr, String separator) {
        String value = "";
        for(int i=0; i<arr.length; i++) {
            if(i < arr.length-1) {
                value += (arr[i] + separator);
            }else{
                value += arr[i];
            }
        }

        if(value.length() < 1) return null;

        Object obj = setProperty(key, value);

        return (obj != null) ? obj.toString().split(separator) : null;
    }
    
    @Override
    public String [] getArray(String key) {
        return getArray(key, ",");
    }
    
    @Override
    public String [] getArray(String key, String separator) {
        String val = getProperty(key);
        return val == null ? null : val.split(separator);
    }

    @Override
    public String [] getArray(String key, String [] defaultValue) {
        return getArray(key, defaultValue, ",");
    }
    
    @Override
    public String [] getArray(String key, String [] defaultValue, String separator) {
        String value = getProperty(key);
        return (value == null || value.length() < 1) ? defaultValue : value.split(separator);
    }

    @Override
    public String [] setCollection(String key, Collection arr) {
        return setCollection(key, arr, ",");
    }
    
    @Override
    public String [] setCollection(String key, Collection arr, String separator) {
        String value = "";
        Iterator iter = arr.iterator();
        while(iter.hasNext()) {
            value += (iter.next() + separator);
        }

        if(value.length() < 1) return null;

        Object obj = setProperty(key, value);

        return (obj != null) ? obj.toString().split(separator) : null;
    }

    @Override
    public Collection<String> getCollection(String key) {
        return Arrays.asList(this.getArray(key));
    }
    
    @Override
    public Collection<String> getCollection(String key, Collection defaultValue) {
        String value = getProperty(key);
        return (value == null || value.length() < 1) ? defaultValue : getCollection(key);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.timePattern);
        hash = 17 * hash + Objects.hashCode(this.getProperties());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConfigImpl other = (ConfigImpl) obj;
        if (!Objects.equals(this.timePattern, other.getTimePattern())) {
            return false;
        }
        if (!Objects.equals(this.getProperties(), other.getProperties())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString()+"{" + "timePattern=" + timePattern + ", props=" + this.getProperties() + '}';
    }
}
