package com.taobao.profile.instrument;

import com.taobao.profile.Manager;
import com.taobao.profile.Profiler;
import com.taobao.profile.config.ProfFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

public class ProfTransformerTest {
    private String className = "com/taobao/profile/instrument/TestClass";

    private static class MockClassLoader extends ClassLoader {
        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

    @Before
    public void setUp() throws Exception {
        //TODO: DailyRollingFileWriter need this. need to decouple logger.
        Manager.METHOD_LOG_PATH = "test.log";

        ProfFilter.clear();
        Manager.instance().setProfileFlag(true);
    }

    @Test
    public void transformShouldNotWorkWhenClassIsNotIncluded() throws Exception {
        Class transformedClass = transformTestClass();
        Assert.assertFalse(isTestMethodInjected(transformedClass));
    }

    @Test
    public void transformShouldNotWorkWhenClassIsIncludedAndExcluded() throws Exception {
        ProfFilter.addIncludeClass(className);
        ProfFilter.addExcludeClass(className);
        Class transformedClass = transformTestClass();
        Assert.assertFalse(isTestMethodInjected(transformedClass));
    }

    @Test
    public void methodInjectionShouldWork() throws Exception {
        ProfFilter.addIncludeClass(className);
        Class transformedClass = transformTestClass();
        Assert.assertTrue(isTestMethodInjected(transformedClass));
    }

    private byte[] buildTestClassBytes() {
        //TestClass
        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, "com/taobao/profile/instrument/TestClass", null, "java/lang/Object", null);

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "testMethod", "()V", null, new String[]{"java/lang/InterruptedException"});
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, null);
            Label l3 = new Label();
            mv.visitTryCatchBlock(l2, l3, l2, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitInsn(MONITORENTER);
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(new Long(1000L));
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "wait", "(J)V");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(MONITOREXIT);
            mv.visitLabel(l1);
            Label l4 = new Label();
            mv.visitJumpInsn(GOTO, l4);
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_FULL, 2, new Object[]{"com/taobao/profile/instrument/TestClass", "java/lang/Object"}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitVarInsn(ASTORE, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(MONITOREXIT);
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l4);
            mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    private Class transformTestClass() throws IllegalClassFormatException {
        byte[] bytes = buildTestClassBytes();
        byte[] transformedBytes = new ProfTransformer().transform(new MockClassLoader(), className, null, null, bytes);
        Class transformedClass = new MockClassLoader().defineClass(className.replaceAll("/", "."), transformedBytes);
        return transformedClass;
    }

    private boolean isTestMethodInjected(Class transformedClass) throws Exception {
        Method testMethod = transformedClass.getDeclaredMethod("testMethod", new Class[]{});
        Object instance = transformedClass.newInstance();
        int profileCountBefore = getThreadProfileCount();
        testMethod.invoke(instance);
        return getThreadProfileCount() > profileCountBefore;
    }

    private int getThreadProfileCount() {
        int threadId = (int) Thread.currentThread().getId();
        return null == Profiler.threadProfile[threadId] ? 0 : Profiler.threadProfile[threadId].profileData.size();
    }
}
