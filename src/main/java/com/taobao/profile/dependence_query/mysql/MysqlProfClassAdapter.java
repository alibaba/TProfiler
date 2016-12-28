package com.taobao.profile.dependence_query.mysql;

import com.taobao.profile.dependence_query.IClassAdapter;
import com.taobao.profile.dependence_query.IMethodAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Mysql ASM类配置器
 * 
 * @author weigao
 * @since 2015-6-1
 */
public class MysqlProfClassAdapter extends IClassAdapter {

	/**
	 * @param visitor
	 * @param theClass
	 */
	public MysqlProfClassAdapter(ClassVisitor visitor, String theClass) {
		super(visitor, theClass);
	}


	/**
	 * 记录Mysql方法
	 * @param mv
	 * @param name
	 * @param descriptor
	 * @return
	 */
	private IMethodAdapter record(final MethodVisitor mv, final String name, final String descriptor) {
		return new IMethodAdapter(mv,"",name,descriptor){
			public void visitCode() {

				Label sqlIsNull = new Label();
				Label endIf = new Label();


				mv.visitInsn(Opcodes.ACONST_NULL);
				mv.visitVarInsn(Opcodes.ASTORE, 30);

				mv.visitVarInsn(Opcodes.ALOAD, 2);
				mv.visitJumpInsn(Opcodes.IFNULL, sqlIsNull);

				mv.visitVarInsn(Opcodes.ALOAD, 2);
				mv.visitVarInsn(Opcodes.ASTORE, 30);
				mv.visitJumpInsn(Opcodes.GOTO, endIf);

				mv.visitLabel(sqlIsNull);

				mv.visitVarInsn(Opcodes.ALOAD, 4);
				mv.visitJumpInsn(Opcodes.IFNULL, endIf);

				mv.visitTypeInsn(Opcodes.NEW, "java/lang/String");
				mv.visitInsn(Opcodes.DUP);
				mv.visitVarInsn(Opcodes.ALOAD, 4);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/mysql/jdbc/Buffer", "getByteBuffer", "()[B");
				mv.visitLdcInsn("utf-8");
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/String", "<init>", "([BLjava/lang/String;)V");
				mv.visitVarInsn(Opcodes.ASTORE, 30);

				mv.visitLabel(endIf);

				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitFieldInsn(Opcodes.GETFIELD, "com/mysql/jdbc/ConnectionImpl", "host", "Ljava/lang/String;");

				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitFieldInsn(Opcodes.GETFIELD, "com/mysql/jdbc/ConnectionImpl", "port", "I");

				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitFieldInsn(Opcodes.GETFIELD, "com/mysql/jdbc/ConnectionImpl", "database", "Ljava/lang/String;");

				mv.visitVarInsn(Opcodes.ALOAD, 30);

				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/taobao/profile/Profiler", "start4Mysql", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V");
				mv.visitCode();
			}

			public void visitInsn(int inst) {
				finishRecord(mv, inst);
			}
		};
	}

	/**
	 * 记录完成
	 * @param mv
	 * @param inst
	 */
	private void finishRecord(MethodVisitor mv,int inst){
		switch (inst) {
			case Opcodes.ARETURN:
			case Opcodes.DRETURN:
			case Opcodes.FRETURN:
			case Opcodes.IRETURN:
			case Opcodes.LRETURN:
			case Opcodes.RETURN:
			case Opcodes.ATHROW:
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/taobao/profile/Profiler", "end4Mysql", "()V");
				break;
			default:
				break;
		}

		mv.visitInsn(inst);
	}


	/***
	 * 依据方法名称返回方法的注入器；
	 * @param mv	原始的MethodVisitor对象；用于方法和修改方法
	 * @param name	方法名称
	 *
	 * @return
	 */
	private IMethodAdapter getMethodAdapter(MethodVisitor mv,String name,String descriptor){
		if(mClassName.startsWith(MysqlProfFilter.MysqlPath)){
			if("execSQL".equalsIgnoreCase(name)){
//					System.out.println(descriptor);
				if("(Lcom/mysql/jdbc/StatementImpl;Ljava/lang/String;ILcom/mysql/jdbc/Buffer;IIZLjava/lang/String;[Lcom/mysql/jdbc/Field;Z)Lcom/mysql/jdbc/ResultSetInternalMethods;".equalsIgnoreCase(descriptor)){
					return record(mv, name, descriptor);
				}
			}
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public MethodVisitor visitMethod(int arg, String name, String descriptor, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(arg, name, descriptor, signature, exceptions);

		IMethodAdapter methodAdapter = getMethodAdapter(mv,name,descriptor);
		if(methodAdapter==null){
			return mv;
		}
		return methodAdapter;
	}

}
