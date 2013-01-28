package com.taobao.profile.instrument;

import com.taobao.profile.config.ProfFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.instrument.IllegalClassFormatException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProfTransformerTest {
    private byte[] emptyBytes;

    private static class MockClassLoader extends ClassLoader {

    }

    private static class MockClass {
    }

    @Before
    public void setUp() throws Exception {
        emptyBytes = new byte[]{};
        ProfFilter.clear();
    }

    private byte[] mockTransform(String className, byte[] classBytes) throws IllegalClassFormatException {
        return new ProfTransformer() {
            @Override
            protected byte[] transform(String className, byte[] classfileBuffer) {
                return new byte[]{1};
            }
        }.transform(
                new MockClassLoader(),
                className,
                MockClass.class,
                null,
                classBytes
        );
    }

    @Test
    public void transformShouldNotWorkWhenClassIsNotIncluded() throws Exception {
        byte[] ret = mockTransform("a", emptyBytes);
        Assert.assertEquals(emptyBytes, ret);
    }

    @Test
    public void transformShouldNotWorkWhenClassIsIncludedAndExcluded() throws Exception {
        ProfFilter.addIncludeClass("a");
        ProfFilter.addExcludeClass("a");
        byte[] ret = mockTransform("a", emptyBytes);
        Assert.assertEquals(emptyBytes, ret);
    }

    @Test
    public void transformShouldWorkWhenClassIsIncluded() throws Exception {
        //TODO We need seperate ProfFilter, otherwise the tests failed.
        ProfFilter.addIncludeClass("a");
        byte[] ret = mockTransform("a", emptyBytes);
        Assert.assertFalse(emptyBytes.equals(ret));
    }

    @Test
    public void transformShouldUseProfClassAdapterToTransferClass() throws Exception {
        ClassNode cn = new ClassNode();
        cn.name = "TestClass";
        cn.methods.add(new MethodNode(Opcodes.ACC_PUBLIC, "testMethod", "()V", null, null));
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(classWriter);
        byte[] bytes = classWriter.toByteArray();

        ProfFilter.addIncludeClass("TestClass");
        final AtomicBoolean isTestMethodVisited = new AtomicBoolean(false);
        new ProfTransformer() {
            @Override
            protected ClassAdapter getClassAdapter(String className, ClassWriter writer) {
                return new ClassAdapter(writer) {
                    @Override
                    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
                        if ("testMethod".equals(s)) {
                            isTestMethodVisited.getAndSet(true);
                        }
                        return super.visitMethod(i, s, s1, s2, strings);
                    }
                };
            }
        }.transform(new MockClassLoader(), "TestClass", null, null, bytes);

        Assert.assertTrue(isTestMethodVisited.get());
    }
}
