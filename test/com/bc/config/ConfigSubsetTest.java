package com.bc.config;

import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Josh
 */
public class ConfigSubsetTest {
    
    public ConfigSubsetTest() { }
    
    @BeforeClass
    public static void setUpClass() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }

    @Test
    public void testAll() {
System.out.println("Testing "+this.getClass().getName()+"#testAll()");
//        test(".."); // didn't work 
//        test("$$"; // didn't work
        test("."); 
        test("a..b"); 
        test("x"); 
        test("$"); 
        test("#"); 
        test("##"); 
        test("xx"); 
    }
    
    private void test(String separator) {
        ConfigGroup instance = new ConfigGroupImpl();
        Properties names = new Properties();
        names.setProperty("name"+separator+"first", "John");
        names.setProperty("name"+separator+"last", "Doe");
        Properties favs = new Properties();
        favs.setProperty("favorite"+separator+"color", "blue");
        favs.setProperty("favorite"+separator+"animal", "sheep,horse");
        instance.put("names.properties", new ConfigImpl(names, null));
        instance.put("favorites.properties", new ConfigImpl(favs, null));
        String subsetName = "name";
        Config subset = instance.subset("names.properties", subsetName, separator);
System.out.println("#subset("+subsetName+", "+separator+"): "+subset);        
        subsetName = "favorite";
        subset = instance.subset("favorites.properties", subsetName, separator);
System.out.println("#subset("+subsetName+", "+separator+"): "+subset);        
    }
}
