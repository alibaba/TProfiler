/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile;

import java.lang.instrument.Instrumentation;

import com.taobao.profile.instrument.ProfTransformer;

/**
 * TProfiler入口
 * 
 * @author luqi
 * @since 2010-6-23
 */
public class Main {

	/**
	 * @param args
	 * @param inst
	 */
	public static void premain(String args, Instrumentation inst) {
		inst.addTransformer(new ProfTransformer());
		Manager.instance().startupThread();
	}
}
