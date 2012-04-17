/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.analysis;

import java.util.Stack;

/**
 * 可排序数据对象
 * 
 * @author shutong.dy
 * @since 2012-1-11
 */
public class TimeSortData implements Comparable<TimeSortData> {

	public float max;
	public float min;
	public long size = 0;
	public long sum = 0;
	public String methodName;
	public Stack<Long> valueStack = new Stack<Long>();

	/**
	 * @return
	 */
	private long getValue() {
		for (long v : valueStack) {
			sum = Math.add(sum, v);
		}
		return sum;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(TimeSortData o) {
		if (this.getValue() > o.getValue()) {
			return -1;
		} else {
			return 1;
		}
	}
}
