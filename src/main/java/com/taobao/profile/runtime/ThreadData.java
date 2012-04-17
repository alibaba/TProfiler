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
 * 此类用来记录线程性能分析数据
 * 
 * @author xiaodu
 * @since 2010-6-23
 */
public class ThreadData {
	/**
	 * 性能分析数据
	 */
	public ProfStack<long[]> profileData = new ProfStack<long[]>();
	/**
	 * 栈帧
	 */
	public ProfStack<long[]> stackFrame = new ProfStack<long[]>();
	/**
	 * 当前栈深度
	 */
	public int stackNum = 0;

	/**
	 * 清空数据
	 */
	public void clear(){
		profileData.clear();
		stackFrame.clear();
		stackNum = 0;
	}
}
