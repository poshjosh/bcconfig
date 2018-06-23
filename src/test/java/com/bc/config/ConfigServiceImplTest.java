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
import java.text.ParseException;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Josh
 */
public class ConfigServiceImplTest extends ConfigTestBase {
    
    public ConfigServiceImplTest() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }

    @Test    
    public void testSave() throws ParseException, IOException {
        
        System.out.println("===================== testSave =====================");
        
        ConfigService configSvc = this.getConfigurationService();
//        final String dir = System.get("user.home") + 
//        "/Documents/NetBeansProjects/bcconfig/src/test/resources/META-INF/properties"; 
//        ConfigService configSvc = new SimpleConfigService(
//                dir + "/defaults/security.properties",
//                dir + "/security.properties");
        
        ConfigGroup configGroup = configSvc.load();
        
System.out.println("Configuration\n"+configGroup);

        String filename = "security.properties";
        
        Config config = configGroup.get(filename);
        
        final String key = "addition_"+System.currentTimeMillis();
        
        config.set(key, String.valueOf(new Date()));
        
        configSvc.storeByName(filename);
        
        this.printSubset(configGroup, filename, "security");

        final Config m1 = new CompositeConfig(this.getConfigurationService());
        
        String val = m1.get(key, null);
System.out.println(key+" = " + val);        
        
        configSvc = this.getConfigurationService();
        
        configSvc.storeFor(filename, key, "");
        
        final Config m2 = new CompositeConfig(this.getConfigurationService());
        
        val = m2.get(key, null);
System.out.println(key+" = " + val);        
    }
    
//    @Test
    public void testGet() throws ParseException, IOException {
        
        System.out.println("===================== testGet =====================");
        
        ConfigService configSvc = this.getConfigurationService();
        
        ConfigGroup config = configSvc.load();
        
System.out.println("Configuration\n"+config);

        for(String key:KEYS) {
            
            Config props = this.getView(config, key);
            
            this.print(props, key);
        }
    }

//    @Test    
    public void testSubset() throws IOException {
        
        System.out.println("===================== testSubset =====================");
        
        ConfigService configSvc = this.getConfigurationService();
        
        ConfigGroup config = configSvc.load();
        
System.out.println("Configuration\n"+config);
        
        String subsetFilename = "security.properties";
        String subsetName = "security";
        
        Config securitySubset = this.printSubset(config, subsetFilename, subsetName);
        
        String key = "algorithm";
        String val= "abc";
System.out.println("PropertiesSubset#setProperty(key"+","+val+")");
        try{
            securitySubset.set(key, val);
            securitySubset.setArray(key, new String[]{});
            securitySubset.setBoolean(key, true);
            securitySubset.setDouble(key, 0);
            securitySubset.setString(key, val);
            
            fail("PropertySubsets should not be modifiable");
            
        }catch(Exception e) { }
        
        securitySubset = this.printSubset(config, subsetFilename, subsetName);
        
        String filename = subsetFilename;
        
        Config security = config.get(filename);
        
        assertNotEquals(securitySubset, security);
        
        key = "security.algorithm";
        
System.out.println("Properties("+filename+")#setProperty("+key+", "+val+")");
        security.setString(key, val);
        
        securitySubset = this.printSubset(config, subsetFilename, subsetName);
        
System.out.println("Configuration#setPropertyFor("+subsetFilename+", "+key+", "+val+")");        
        config.setFor(subsetFilename, key, val);
        
System.out.println("ConfigurationService#storeByName("+subsetFilename+")");        
        configSvc.storeByName(subsetFilename); 
        
        security = this.printSubset(config, subsetFilename, subsetName);
        
        val = "def";
    
System.out.println("ConfigurationService#storePropertyFor("+subsetFilename+", "+key+", "+val+")");        
        configSvc.storeFor(subsetFilename, key, val);
        
System.out.println("ConfigurationService#storeByName("+subsetFilename+")");        
        configSvc.storeByName(subsetFilename); 
        
        security = this.printSubset(config, subsetFilename, subsetName);
    }
    
//    @Test    
    public void testMerge() throws ParseException, IOException {
        
        System.out.println("===================== testGet for Merged =====================");
        
        ConfigService configSvc = this.getConfigurationService();
        
        ConfigGroup config = configSvc.load();
        
System.out.println("Configuration\n"+config);

        Config merged = new CompositeConfig(config.values(), configSvc.getTimePattern());

        for(String key:KEYS) {

            this.print(merged, key);
        }

        config.clear(); // Note this
        
        String double1 = "double.property1";
        double double1Value = 12.34;
        String double2 = "double.property2";
        double double2Value = 56.78;
        
        String filename = "general.properties";
        
        if(!config.isEmpty()) {
System.out.println("Configuration#setPropertyFor("+filename+", "+double1+", "+double1Value+")");        
            config.setFor("general.properties", double1, Double.toString(double1Value));
        }
        
System.out.println("ConfigurationService#storePropertyFor("+filename+", "+double2+", "+double2Value+")");        
        configSvc.storeFor("general.properties", double2, Double.toString(double2Value));
        
        Config generalSubset = this.printSubset(config, filename, "double");
        
        assertTrue(double1Value == generalSubset.getDouble("property1"));
        
        assertTrue(double2Value == generalSubset.getDouble("property2"));        
    }    

}
