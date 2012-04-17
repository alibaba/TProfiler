/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.config;

import java.util.HashSet;
import java.util.Set;

/**
 * 包名过滤器,过滤注入或者不注入的Package
 * 
 * @author luqi
 * @since 2010-6-23
 */
public class ProfFilter {

	/**
	 * 注入Package集合
	 */
	private static Set<String> includePackage = new HashSet<String>();
	/**
	 * 不注入Package集合
	 */
	private static Set<String> excludePackage = new HashSet<String>();

	static {
		// 默认不注入的Package
		excludePackage.add("java");
		excludePackage.add("javax");
		excludePackage.add("sun");
		excludePackage.add("sunw");
		excludePackage.add("com/sun");

		excludePackage.add("org/xml");
		excludePackage.add("org/jboss");
		excludePackage.add("org/apache/xerces");
		excludePackage.add("org/objectweb/asm");
		// 不注入profile本身
		excludePackage.add("com/taobao/profile");
	}

	/**
	 * 
	 * @param className
	 */
	public static void addIncludeClass(String className) {
		String icaseName = className.toLowerCase().replace('.', '/');
		includePackage.add(icaseName);
	}

	/**
	 * 
	 * @param className
	 */
	public static void addExcludeClass(String className) {
		String icaseName = className.toLowerCase().replace('.', '/');
		excludePackage.add(icaseName);
	}

	/**
	 * 是否需要注入
	 * 
	 * @param className
	 * @return
	 */
	public static boolean IsNeedInject(String className) {
		String icaseName = className.toLowerCase().replace('.', '/');
		for (String v : includePackage) {
			if (icaseName.startsWith(v)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否不需要注入
	 * 
	 * @param className
	 * @return
	 */
	public static boolean IsNotNeedInject(String className) {
		String icaseName = className.toLowerCase().replace('.', '/');
		for (String v : excludePackage) {
			if (icaseName.startsWith(v)) {
				return true;
			}
		}
		return false;
	}
}
