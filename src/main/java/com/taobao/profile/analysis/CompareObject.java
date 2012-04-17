/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.analysis;

/**
 * 可排序数据对象
 * 
 * @author shutong.dy
 * @since 2012-2-14
 */
public class CompareObject implements Comparable<CompareObject> {
	private String threadId;
	private String threadName;
	private String threadState;
	private String methodName;
	private int count;

	@Override
	public int compareTo(CompareObject o) {
		return o.count - this.count;
	}

	/**
	 * @return the threadId
	 */
	public String getThreadId() {
		return threadId;
	}

	/**
	 * @param threadId
	 *            the threadId to set
	 */
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	/**
	 * @return the threadName
	 */
	public String getThreadName() {
		return threadName;
	}

	/**
	 * @param threadName
	 *            the threadName to set
	 */
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	/**
	 * @return the threadState
	 */
	public String getThreadState() {
		return threadState;
	}

	/**
	 * @param threadState
	 *            the threadState to set
	 */
	public void setThreadState(String threadState) {
		this.threadState = threadState;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName
	 *            the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

}
