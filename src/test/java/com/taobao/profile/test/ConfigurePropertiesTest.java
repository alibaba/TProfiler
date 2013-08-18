package com.taobao.profile.test;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.taobao.profile.config.ConfigureProperties;

public class ConfigurePropertiesTest {
  
  @Test
  public void testConfigureProperties(){
    Properties prop = new Properties();
    prop.put("log.file.path", "${user.home}/tprofiler.log");
    Properties properties  = new ConfigureProperties(prop, System.getProperties());
    Assert.assertEquals(properties.getProperty("log.file.path"), System.getProperty("user.home") + "/tprofiler.log" );
  }
}
