package com.taobao.profile.instrument;

import com.taobao.profile.config.ProfFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.instrument.IllegalClassFormatException;

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
}
