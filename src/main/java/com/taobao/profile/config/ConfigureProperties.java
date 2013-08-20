package com.taobao.profile.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.taobao.profile.utils.Utilities;
import com.taobao.profile.utils.VariableNotFoundException;

/**
 * <p>用于加载配置文件的properties类，与java默认的不同，在调用get方法返回value时，会对value
 * 会检查是否存在变量(如：${user.home})，如果存在，会将变量替换成具体的值。
 * <p>该类使用dectorator设计模式
 * 
 * <p>示例文件内容(profile.properties)：
 * <pre>
 *   logFileName = tprofiler.log
 *   methodFileName = tmethod.log
 *   samplerFileName = tsampler.log
 *   
 *   startProfTime = 9:00:00
 *   endProfTime = 11:00:00
 *   eachProfUseTime = 5
 *   eachProfIntervalTime = 50
 *   samplerIntervalTime = 20
 *   port = 50000
 *   debugMode = false
 *   needNanoTime = false
 *   ignoreGetSetMethod = true
 *   
 *   logFilePath = ${user.home}/logs/${logFileName}
 *   methodFilePath = ${user.home}/logs/${methodFileName}
 *   samplerFilePath = ${user.home}/logs/${samplerFileName}
 *   
 *   excludeClassLoader = org.eclipse.osgi.internal.baseadaptor.DefaultClassLoader
 *   includePackageStartsWith = com.taobao;com.taobao.common
 *   excludePackageStartsWith = com.taobao.sketch;org.apache.velocity;com.alibaba;com.taobao.forest.domain.dataobject
 * </pre>
 * <p>未例代码：
 * <pre>
 *   Properties properties = new Properties();
 *   InputStream in = getClass().getClassLoader().getResourceAsStream("profile.properties");
 *   properties.load(in);
 *
 *   Properties context = new Properties(System.getProperties());
 *   context.putAll(System.getProperties());
 *   context.putAll(properties);
 *   try{
 *     ConfigureProperties configureProperties = new ConfigureProperties(properties, context);
 *     String logFilePath = configureProperties.getProperty("logFilePath");
 *     Assert.assertEquals(logFilePath, System.getProperty("user.home") + "/logs/tprofiler.log");
 *   }finally{
 *     in.close();
 *   }
 * </pre>
 * @author manlge
 * @since 2013-08-18
 */
public class ConfigureProperties extends Properties {
  
  /**
   * 
   */
  private static final long serialVersionUID = -3868173073422671544L;

  private Properties delegate;
  
  private Properties context;
  
  
  public ConfigureProperties(Properties delegate, Properties context) {
    super();
    this.delegate = delegate;
    this.context = context;
  }

  public Object setProperty(String key, String value) {
    return delegate.setProperty(key, value);
  }

  public void load(Reader reader) throws IOException {
    delegate.load(reader);
  }

  public int size() {
    return delegate.size();
  }

  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  public Enumeration<Object> keys() {
    return delegate.keys();
  }

  public Enumeration<Object> elements() {
    return delegate.elements();
  }

  public void load(InputStream inStream) throws IOException {
    delegate.load(inStream);
  }

  public boolean contains(Object value) {
    return delegate.contains(value);
  }

  public boolean containsValue(Object value) {
    return delegate.containsValue(value);
  }

  public boolean containsKey(Object key) {
    return delegate.containsKey(key);
  }

  public Object get(Object key) {
    return delegate.get(key);
  }

  public Object put(Object key, Object value) {
    return delegate.put(key, value);
  }

  public Object remove(Object key) {
    return delegate.remove(key);
  }

  public void putAll(Map<? extends Object, ? extends Object> t) {
    delegate.putAll(t);
  }

  public void clear() {
    delegate.clear();
  }

  public Object clone() {
    return delegate.clone();
  }

  public String toString() {
    return delegate.toString();
  }

  public Set<Object> keySet() {
    return delegate.keySet();
  }

  public Set<java.util.Map.Entry<Object, Object>> entrySet() {
    return delegate.entrySet();
  }

  public void save(OutputStream out, String comments) {
    delegate.save(out, comments);
  }

  public void store(Writer writer, String comments) throws IOException {
    delegate.store(writer, comments);
  }

  public Collection<Object> values() {
    return delegate.values();
  }

  public void store(OutputStream out, String comments) throws IOException {
    delegate.store(out, comments);
  }

  public boolean equals(Object o) {
    return delegate.equals(o);
  }

  public int hashCode() {
    return delegate.hashCode();
  }

  public void loadFromXML(InputStream in) throws IOException,
    InvalidPropertiesFormatException {
    delegate.loadFromXML(in);
  }

  public void storeToXML(OutputStream os, String comment) throws IOException {
    delegate.storeToXML(os, comment);
  }

  public void storeToXML(OutputStream os, String comment, String encoding)
    throws IOException {
    delegate.storeToXML(os, comment, encoding);
  }

  public String getProperty(String key) {
    String value = delegate.getProperty(key);
    try {
      return Utilities.repleseVariables(value, context);
    } catch (VariableNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public String getProperty(String key, String defaultValue) {
    String value = delegate.getProperty(key, defaultValue);
    try {
      return Utilities.repleseVariables(value, context);
    } catch (VariableNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public Enumeration<?> propertyNames() {
    return delegate.propertyNames();
  }

  public Set<String> stringPropertyNames() {
    return delegate.stringPropertyNames();
  }

  public void list(PrintStream out) {
    delegate.list(out);
  }

  public void list(PrintWriter out) {
    delegate.list(out);
  }
  
  
}
