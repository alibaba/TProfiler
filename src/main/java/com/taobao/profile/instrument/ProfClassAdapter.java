/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.instrument;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;

import com.taobao.profile.Manager;

/**
 * ASM类配置器
 * 
 * @author luqi
 * @since 2010-6-23
 */
public class ProfClassAdapter extends ClassAdapter {
	/**
	 * 类名
	 */
	private String mClassName;
	/**
	 * 文件名
	 */
	private String mFileName = null;
	/**
	 * 字段对应方法列表
	 */
	private List<String> fieldNameList = new ArrayList<String>();

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visit(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
	}

	/**
	 * @param visitor
	 * @param theClass
	 */
	public ProfClassAdapter(ClassVisitor visitor, String theClass) {
		super(visitor);
		this.mClassName = theClass;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitSource(java.lang.String, java.lang.String)
	 */
	public void visitSource(final String source, final String debug) {
		super.visitSource(source, debug);
		mFileName = source;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitField(int, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		String up = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
		String getFieldName = "get" + up;
		String setFieldName = "set" + up;
		String isFieldName = "is" + up;
		fieldNameList.add(getFieldName);
		fieldNameList.add(setFieldName);
		fieldNameList.add(isFieldName);

		return super.visitField(access, name, desc, signature, value);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public MethodVisitor visitMethod(int arg, String name, String descriptor, String signature, String[] exceptions) {
		if (Manager.isIgnoreGetSetMethod()) {
			if (fieldNameList.contains(name)) {
				return super.visitMethod(arg, name, descriptor, signature, exceptions);
			}
		}
		// 静态区域不注入
		if ("<clinit>".equals(name)) {
			return super.visitMethod(arg, name, descriptor, signature, exceptions);
		}

		MethodVisitor mv = super.visitMethod(arg, name, descriptor, signature, exceptions);
		MethodAdapter ma = new ProfMethodAdapter(mv, mFileName, mClassName, name);
		return ma;
	}

}
