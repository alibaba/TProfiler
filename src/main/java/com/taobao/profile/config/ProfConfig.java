/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 读取并保存配置
 * 
 * @author xiaodu
 * @since 2010-6-22
 */
public class ProfConfig {

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
	 * 构造方法
	 */
	public ProfConfig(String defaultPath) {
		String configPath = System.getProperty("profile.properties");
		if (configPath == null || configPath.isEmpty()) {
			parse(defaultPath);
		} else {
			File file = new File(configPath);
			if (file.exists()) {
				parseProperty(file);
			} else {
				parse(defaultPath);
			}
		}
	}

	/**
	 * 解析用户自定义配置文件
	 * 
	 * @param path
	 */
	private void parseProperty(File path) {

		Properties resource = new Properties();
		try {
			resource.load(new FileReader(path));
			String startProfTime = resource.getProperty("startProfTime");
			String endProfTime = resource.getProperty("endProfTime");
			String logFilePath = resource.getProperty("logFilePath");
			String methodFilePath = resource.getProperty("methodFilePath");
			String samplerFilePath = resource.getProperty("samplerFilePath");
			String includePackageStartsWith = resource.getProperty("includePackageStartsWith");
			String eachProfUseTime = resource.getProperty("eachProfUseTime");
			String eachProfIntervalTime = resource.getProperty("eachProfIntervalTime");
			String samplerIntervalTime = resource.getProperty("samplerIntervalTime");
			String excludePackageStartsWith = resource.getProperty("excludePackageStartsWith");
			String needNanoTime = resource.getProperty("needNanoTime");
			String ignoreGetSetMethod = resource.getProperty("ignoreGetSetMethod");
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析默认配置文件
	 * 
	 * @param configName
	 */
	private void parse(String configName) {
		try {
			ResourceBundle resource = ResourceBundle.getBundle(configName);
			String startProfTime = resource.getString("startProfTime");
			String endProfTime = resource.getString("endProfTime");
			String logFilePath = resource.getString("logFilePath");
			String methodFilePath = resource.getString("methodFilePath");
			String samplerFilePath = resource.getString("samplerFilePath");
			String includePackageStartsWith = resource.getString("includePackageStartsWith");
			String eachProfUseTime = resource.getString("eachProfUseTime");
			String eachProfIntervalTime = resource.getString("eachProfIntervalTime");
			String samplerIntervalTime = resource.getString("samplerIntervalTime");
			String excludePackageStartsWith = resource.getString("excludePackageStartsWith");
			String needNanoTime = resource.getString("needNanoTime");
			String ignoreGetSetMethod = resource.getString("ignoreGetSetMethod");
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
		} catch (Exception e) {
			e.printStackTrace();
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

}
