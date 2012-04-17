/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.analysis;

import java.math.BigDecimal;

/**
 * 数学计算工具方法
 * 
 * @author shutong.dy
 * @since 2012-1-11
 */
public class Math {

	private Math() {
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static long add(long v1, long v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.add(b2).longValue();
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static long div(long v1, long v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.divide(b2, 0, BigDecimal.ROUND_HALF_UP).longValue();
	}
}
