package com.taobao.profile.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.taobao.profile.config.ConfigureProperties;

public class ConfigurePropertiesTest {
  
  @Test
  public void testConfigureProperties(){
    Properties prop = new Properties();
    prop.put("file.name", "tprofiler.log");
    prop.put("log.file.path", "${user.home}/${file.name}");
    Properties context = System.getProperties();
    context.putAll(prop);
    
    Properties properties  = new ConfigureProperties(prop, context);
    Assert.assertEquals(properties.getProperty("log.file.path"), System.getProperty("user.home") + "/tprofiler.log" );
  }
  
  @Test
  public void testConfigure() throws IOException{
    Properties properties = new Properties();
    InputStream in = getClass().getClassLoader().getResourceAsStream("profile.properties");
    properties.load(in);

    Properties context = new Properties(System.getProperties());
    context.putAll(System.getProperties());
    context.putAll(properties);
    try{
      ConfigureProperties configureProperties = new ConfigureProperties(properties, context);
      String logFilePath = configureProperties.getProperty("logFilePath");
      Assert.assertEquals(logFilePath, System.getProperty("user.home") + "/logs/tprofiler.log");
    }finally{
      in.close();
    }
  }
}
