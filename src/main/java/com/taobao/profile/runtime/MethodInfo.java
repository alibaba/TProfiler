/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.runtime;

/**
 * 方法信息对象
 * 
 * @author xiaodu
 * @since 2010-6-23
 */
public class MethodInfo {

	/**
	 * 类名
	 */
	private String mClassName;
	/**
	 * 方法名
	 */
	private String mMethodName;
	/**
	 * 文件名
	 */
	private String mFileName;
	/**
	 * 行号
	 */
	private int mLineNum;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return mClassName + ":" + mMethodName + ":" + mLineNum;
	}

	/**
	 * @return
	 */
	public String getMClassName() {
		return mClassName;
	}

	/**
	 * @param className
	 */
	public void setMClassName(String className) {
		mClassName = className;
	}

	/**
	 * @return
	 */
	public String getMMethodName() {
		return mMethodName;
	}

	/**
	 * @param methodName
	 */
	public void setMMethodName(String methodName) {
		mMethodName = methodName;
	}

	/**
	 * @return
	 */
	public String getMFileName() {
		return mFileName;
	}

	/**
	 * @param fileName
	 */
	public void setMFileName(String fileName) {
		mFileName = fileName;
	}

	/**
	 * @return
	 */
	public int getMLineNum() {
		return mLineNum;
	}

	/**
	 * @param lineNum
	 */
	public void setMLineNum(int lineNum) {
		mLineNum = lineNum;
	}

}
