/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.instrument;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.taobao.profile.Profiler;
import com.taobao.profile.runtime.MethodCache;

/**
 * ASM方法适配器
 * 
 * @author luqi
 * @since 2010-6-23
 */
public class ProfMethodAdapter extends MethodAdapter {
	/**
	 * 方法ID
	 */
	private int mMethodId = 0;

	/**
	 * @param visitor
	 * @param fileName
	 * @param className
	 * @param methodName
	 */
	public ProfMethodAdapter(MethodVisitor visitor, String fileName, String className, String methodName) {
		super(visitor);
		mMethodId = MethodCache.Request();
		MethodCache.UpdateMethodName(mMethodId, fileName, className, methodName);
		// 记录方法数
		Profiler.instrumentMethodCount.getAndIncrement();
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitCode()
	 */
	public void visitCode() {
		this.visitLdcInsn(mMethodId);
		this.visitMethodInsn(INVOKESTATIC, "com/taobao/profile/Profiler", "Start", "(I)V");
		super.visitCode();
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitLineNumber(int, org.objectweb.asm.Label)
	 */
	public void visitLineNumber(final int line, final Label start) {
		MethodCache.UpdateLineNum(mMethodId, line);
		super.visitLineNumber(line, start);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitInsn(int)
	 */
	public void visitInsn(int inst) {
		switch (inst) {
		case Opcodes.ARETURN:
		case Opcodes.DRETURN:
		case Opcodes.FRETURN:
		case Opcodes.IRETURN:
		case Opcodes.LRETURN:
		case Opcodes.RETURN:
		case Opcodes.ATHROW:
			this.visitLdcInsn(mMethodId);
			this.visitMethodInsn(INVOKESTATIC, "com/taobao/profile/Profiler", "End", "(I)V");
			break;
		default:
			break;
		}

		super.visitInsn(inst);
	}

}
