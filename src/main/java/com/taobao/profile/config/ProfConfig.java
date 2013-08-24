/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.taobao.profile.utils.VariableNotFoundException;

/**
 * 读取并保存配置
 * 
 * @author xiaodu
 * @since 2010-6-22
 */
public class ProfConfig {
  
  /**
   * 配置文件名
   */
  private static final String CONFIG_FILE_NAME = "profile.properties";

  /**
   * 默认的配置文件路径，~/.tprofiler/profile.properties
   */
  private File DEFAULT_PROFILE_PATH = new File(System.getProperty("user.home"), "/.tprofiler/" + CONFIG_FILE_NAME);


	/**
	 * 开始profile时间
	 */
	private String startProfTime;

	/**
	 * 结束profile时间
	 */
	private String endProfTime;

	/**
	 * log文件路径
	 */
	private String logFilePath;

	/**
	 * method文件路径
	 */
	private String methodFilePath;

	/**
	 * sampler文件路径
	 */
	private String samplerFilePath;

	/**
	 * 不包括的ClassLoader
	 */
	private String excludeClassLoader;

	/**
	 * 包括的包名
	 */
	private String includePackageStartsWith;

	/**
	 * 不包括的包名
	 */
	private String excludePackageStartsWith;

	/**
	 * 每次profile用时
	 */
	private int eachProfUseTime = -1;

	/**
	 * 两次profile间隔时间
	 */
	private int eachProfIntervalTime = -1;

	/**
	 * 两次sampler间隔时间
	 */
	private int samplerIntervalTime = -1;

	/**
	 * 是否用纳秒采集
	 */
	private boolean needNanoTime;

	/**
	 * 是否忽略get/set方法
	 */
	private boolean ignoreGetSetMethod;

	/**
	 * 是否进入调试模式
	 */
	private boolean debugMode;

    /**
     * Socket端口号配置
     */
    private int port;

	/**
	 * 构造方法
	 */
	public ProfConfig() {
	  
	  //此时配置文件中的debug参数还未读取，因此使用-Dtprofiler.debug=true来读取，用于开发时调试
	  boolean debug = "true".equalsIgnoreCase(System.getProperty("tprofiler.debug")); 
	  /*
	   * 查找顺序：
	   * 1. 系统参数-Dprofile.properties=/path/profile.properties
	   * 2. 当前文件夹下的profile.properties
	   * 3. 用户文件夹~/.tprofiler/profile.properties，如：/home/manlge/.tprofiler/profile.properties
	   * 4. 默认jar包中的profile.properties
	   */
	  String specifiedConfigFileName = System.getProperty(CONFIG_FILE_NAME);
	  File configFiles[] = {
			  specifiedConfigFileName == null ? null : new File(specifiedConfigFileName), 
					  new File(CONFIG_FILE_NAME), 
					  DEFAULT_PROFILE_PATH
	  };

	  for (File file : configFiles){
		  if (file != null && file.exists() && file.isFile()) {
			  if (debug){
				  System.out.println(String.format("load configuration from \"%s\".", file.getAbsolutePath()));
			  }
			  parseProperty(file);
			  return;
		  }
	  }
		//加载默认配置
    if (debug){
      System.out.println(String.format("load configuration from \"%s\".", DEFAULT_PROFILE_PATH.getAbsolutePath()));
    }
    try {
    	extractDefaultProfile();
    	parseProperty(DEFAULT_PROFILE_PATH);
    } catch (IOException e) {
    	throw new RuntimeException("error load config file " + DEFAULT_PROFILE_PATH, e);
    }

	}

	/**
	 * 解压默认的配置文件到~/.tprofiler/profile.properties，作为模板，以便用户编辑
	 * @throws IOException
	 */
	private void extractDefaultProfile() throws IOException {
	  /*
	   * 这里采用stream进行复制，而不是采用properties.load和save，主要原因为以下2点：
	   * 1. 性能，stream直接复制快，没有properties解析过程(不过文件较小，解析开销可以忽略)
	   * 2. properties会造成注释丢失，该文件作为模板提供给用户，包含注释信息
	   */
    InputStream in = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME));
    OutputStream out = null;
    try{
      File profileDirectory = DEFAULT_PROFILE_PATH.getParentFile();
      if (!profileDirectory.exists()){
        profileDirectory.mkdirs();
      }
      out = new BufferedOutputStream(new FileOutputStream(DEFAULT_PROFILE_PATH));
      byte[] buffer = new byte[1024];
      for (int len = -1; (len = in.read(buffer)) != -1;){
        out.write(buffer, 0, len);
      }
    }finally{
      in.close();
      out.close();
    }
  }

  /**
	 * 解析用户自定义配置文件
	 * @param path
	 */
	private void parseProperty(File path) {
		Properties properties = new Properties();
		try {
			properties.load(new FileReader(path)); //配置文件原始内容，未进行变量替换
			
			//变量查找上下文，采用System.properties和配置文件集合
			Properties context = new Properties(); 
			context.putAll(System.getProperties());
			context.putAll(properties);
			
			//加载配置
      loadConfig(new ConfigureProperties(properties, context));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

  /**
   * 加载配置
   * @param properties
   */
  private void loadConfig(Properties properties) throws VariableNotFoundException {
    String startProfTime = properties.getProperty("startProfTime");
    String endProfTime = properties.getProperty("endProfTime");
    String logFilePath = properties.getProperty("logFilePath");
    String methodFilePath = properties.getProperty("methodFilePath");
    String samplerFilePath = properties.getProperty("samplerFilePath");
    String includePackageStartsWith = properties.getProperty("includePackageStartsWith");
    String eachProfUseTime = properties.getProperty("eachProfUseTime");
    String eachProfIntervalTime = properties.getProperty("eachProfIntervalTime");
    String samplerIntervalTime = properties.getProperty("samplerIntervalTime");
    String excludePackageStartsWith = properties.getProperty("excludePackageStartsWith");
    String needNanoTime = properties.getProperty("needNanoTime");
    String ignoreGetSetMethod = properties.getProperty("ignoreGetSetMethod");
    String excludeClassLoader = properties.getProperty("excludeClassLoader");
    String debugMode = properties.getProperty("debugMode");
          String port = properties.getProperty("port");
          setPort(port == null ? 50000 : Integer.valueOf(port));
    setDebugMode("true".equals(debugMode));
    setExcludeClassLoader(excludeClassLoader);
    setExcludePackageStartsWith(excludePackageStartsWith);
    setEndProfTime(endProfTime);
    setIncludePackageStartsWith(includePackageStartsWith);
    setLogFilePath(logFilePath);
    setMethodFilePath(methodFilePath);
    setSamplerFilePath(samplerFilePath);
    setStartProfTime(startProfTime);
    setNeedNanoTime("true".equals(needNanoTime));
    setIgnoreGetSetMethod("true".equals(ignoreGetSetMethod));
    if (eachProfUseTime == null) {
    	setEachProfUseTime(5);
    } else {
    	setEachProfUseTime(Integer.valueOf(eachProfUseTime.trim()));
    }
    if (eachProfIntervalTime == null) {
    	setEachProfIntervalTime(50);
    } else {
    	setEachProfIntervalTime(Integer.valueOf(eachProfIntervalTime.trim()));
    }
    if (samplerIntervalTime == null) {
    	setSamplerIntervalTime(10);
    } else {
    	setSamplerIntervalTime(Integer.valueOf(samplerIntervalTime.trim()));
    }
  }


	/**
	 * @return
	 */
	public String getStartProfTime() {
		return startProfTime;
	}

	/**
	 * @param startProfTime
	 */
	public void setStartProfTime(String startProfTime) {
		this.startProfTime = startProfTime;
	}

	/**
	 * @return
	 */
	public String getEndProfTime() {
		return endProfTime;
	}

	/**
	 * @param endProfTime
	 */
	public void setEndProfTime(String endProfTime) {
		this.endProfTime = endProfTime;
	}

	/**
	 * @return
	 */
	public String getLogFilePath() {
		return logFilePath;
	}

	/**
	 * @param logFilePath
	 */
	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	/**
	 * @return the methodFilePath
	 */
	public String getMethodFilePath() {
		return methodFilePath;
	}

	/**
	 * @param methodFilePath
	 *            the methodFilePath to set
	 */
	public void setMethodFilePath(String methodFilePath) {
		this.methodFilePath = methodFilePath;
	}

	/**
	 * @return
	 */
	public String getIncludePackageStartsWith() {
		return includePackageStartsWith;
	}

	/**
	 * @param includePackageStartsWith
	 */
	public void setIncludePackageStartsWith(String includePackageStartsWith) {
		this.includePackageStartsWith = includePackageStartsWith;
	}

	/**
	 * @return
	 */
	public int getEachProfUseTime() {
		return eachProfUseTime;
	}

	/**
	 * @param eachProfUseTime
	 */
	public void setEachProfUseTime(int eachProfUseTime) {
		this.eachProfUseTime = eachProfUseTime;
	}

	/**
	 * @return
	 */
	public int getEachProfIntervalTime() {
		return eachProfIntervalTime;
	}

	/**
	 * @param eachProfIntervalTime
	 */
	public void setEachProfIntervalTime(int eachProfIntervalTime) {
		this.eachProfIntervalTime = eachProfIntervalTime;
	}

	/**
	 * @return
	 */
	public String getExcludePackageStartsWith() {
		return excludePackageStartsWith;
	}

	/**
	 * @param excludePackageStartsWith
	 */
	public void setExcludePackageStartsWith(String excludePackageStartsWith) {
		this.excludePackageStartsWith = excludePackageStartsWith;
	}

	/**
	 * @return
	 */
	public boolean isNeedNanoTime() {
		return needNanoTime;
	}

	/**
	 * @param needNanoTime
	 */
	public void setNeedNanoTime(boolean needNanoTime) {
		this.needNanoTime = needNanoTime;
	}

	/**
	 * @return
	 */
	public boolean isIgnoreGetSetMethod() {
		return ignoreGetSetMethod;
	}

	/**
	 * @param ignoreGetSetMethod
	 */
	public void setIgnoreGetSetMethod(boolean ignoreGetSetMethod) {
		this.ignoreGetSetMethod = ignoreGetSetMethod;
	}

	/**
	 * @param samplerFilePath
	 *            the samplerFilePath to set
	 */
	public void setSamplerFilePath(String samplerFilePath) {
		this.samplerFilePath = samplerFilePath;
	}

	/**
	 * @param samplerIntervalTime
	 *            the samplerIntervalTime to set
	 */
	public void setSamplerIntervalTime(int samplerIntervalTime) {
		this.samplerIntervalTime = samplerIntervalTime;
	}

	/**
	 * @return the samplerFilePath
	 */
	public String getSamplerFilePath() {
		return samplerFilePath;
	}

	/**
	 * @return the samplerIntervalTime
	 */
	public int getSamplerIntervalTime() {
		return samplerIntervalTime;
	}

	/**
	 * @return the excludeClassLoader
	 */
	public String getExcludeClassLoader() {
		return excludeClassLoader;
	}

	/**
	 * @param excludeClassLoader the excludeClassLoader to set
	 */
	public void setExcludeClassLoader(String excludeClassLoader) {
		this.excludeClassLoader = excludeClassLoader;
	}

	/**
	 * @return the debugMode
	 */
	public boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * @param debugMode the debugMode to set
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
