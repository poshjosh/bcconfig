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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 19, 2016 10:48:11 PM
 */
public class ConfigImplTest extends ConfigTestBase {

    public ConfigImplTest() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }

    @Test
    public void testGet() throws ParseException, IOException {
        
        System.out.println("===================== testGet =====================");
        
        ConfigService configSvc = this.getConfigurationService();
        
        ConfigGroup config = configSvc.load(); 
        
System.out.println("Configuration\n"+config);

        for(String key:KEYS) {
            
            Config props = this.getView(config, key);
            
            System.out.println("Printing subset: long.");
            final Config subsetLong = props.subset("long", ".");
            System.out.println(subsetLong.getData());
            System.out.println("Printing subset: general.");
            final Config subsetGene = props.subset("general", ".");
            System.out.println(subsetGene.getData());
        }
    }
}
