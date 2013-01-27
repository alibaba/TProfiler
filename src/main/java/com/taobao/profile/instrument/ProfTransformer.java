/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 */
package com.taobao.profile.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.taobao.profile.Manager;
import com.taobao.profile.Profiler;
import com.taobao.profile.config.ProfFilter;

/**
 * 自定义ClassFileTransformer,用于转换类字节码
 *
 * @author luqi
 * @since 2010-6-23
 */
public class ProfTransformer implements ClassFileTransformer {

	/* (non-Javadoc)
	 * @see java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader, java.lang.String, java.lang.Class, java.security.ProtectionDomain, byte[])
	 */
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
	        ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (ProfFilter.isNotNeedInjectClassLoader(loader.getClass().getName())) {
			return classfileBuffer;
		}
		if (!ProfFilter.isNeedInject(className)) {
			return classfileBuffer;
		}
		if (ProfFilter.isNotNeedInject(className)) {
			return classfileBuffer;
		}
		if (Manager.instance().isDebugMode()) {
			System.out.println(" ---- TProfiler Debug: " + loader.getClass().getName() + " ---- " + className);
		}
        return transform(className, classfileBuffer);
    }

    protected byte[] transform(String className, byte[] classfileBuffer) {
        // 记录注入类数
        Profiler.instrumentClassCount.getAndIncrement();
        try {
            ClassReader reader = new ClassReader(classfileBuffer);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassAdapter adapter = new ProfClassAdapter(writer, className);
            reader.accept(adapter, 0);
            // 生成新类字节码
            return writer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            // 返回旧类字节码
            return classfileBuffer;
        }
    }
}
