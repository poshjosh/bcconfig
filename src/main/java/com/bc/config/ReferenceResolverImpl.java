/*
 * Copyright 2018 NUROX Ltd.
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

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chinomso Bassey Ikwuagwu on May 11, 2018 3:58:14 PM
 */
public class ReferenceResolverImpl implements Serializable, ReferenceResolver {

    private final UnaryOperator<String> context;
    private final Pattern referencePattern;
    private final Map<String, String> sysEnv;
    private final Properties sysProps;

    public ReferenceResolverImpl() {
        this((key) -> key);
    }
    
    public ReferenceResolverImpl(UnaryOperator<String> context) {
        this(context, "\\$\\{(.+?)\\}");
    }

    public ReferenceResolverImpl(UnaryOperator<String> context, String pattern) {
        this(context, Pattern.compile(pattern));
    }
    
    public ReferenceResolverImpl(UnaryOperator<String> context, Pattern pattern) {
        this.context = Objects.requireNonNull(context);
        this.referencePattern = Objects.requireNonNull(pattern);
        this.sysEnv = System.getenv();
        this.sysProps = System.getProperties();
    }
    
    /**
     * References are indicated as such: <code>${name}</code>. So that the actual 
     * value referenced by <code>'name'</code> will be used to replace the text
     * <code>${name}</code>. Reference values are sourced in the following order:
     * <ol>
     *   <li>From the {@link com.bc.config.ReferenceResolverImpl#getContext() context}</li>
     *   <li>From system environment</li>
     *   <li>From system properties</li>
     * </ol>
     * @param input
     * @return The input with resolved references replaced with actual values
     */
    @Override
    public String resolve(String input) {
        return this.resolve(this.context, input);
    }
    
    @Override
    public String resolve(UnaryOperator<String> context, String input) {
      Matcher matcher = referencePattern.matcher(input);
      StringBuffer buff = null;
      while(matcher.find()) {
        String key = matcher.group(1);
        String val = context.apply(key);
        if(val == null) {
            val = sysEnv.get(key);
        }
        if(val == null) {
          val = sysProps.getProperty(key);
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
        output = input;
      }
  //System.out.println(" Input: "+value);    
  //System.out.println("Output: "+output);    
      return output;
    }

    /**
     * @bug 001 When we append C:\Users\USER to the StringBuffer we got C:UsersUSER
     * This bug fix is only a temporary measure
     * @param val
     * @return 
     */
    private String applyBugFix001(String val) {
        final boolean buggyIfTrue = false;
        final char replacement = buggyIfTrue ? File.separatorChar : '/';
        return val.replace('\\', replacement);
    }

    @Override
    public final UnaryOperator<String> getContext() {
        return context;
    }

    @Override
    public final Pattern getReferencePattern() {
        return referencePattern;
    }
}
