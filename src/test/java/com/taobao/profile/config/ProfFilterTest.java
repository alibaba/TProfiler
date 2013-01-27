package com.taobao.profile.config;

import com.taobao.profile.Profiler;
import org.junit.Assert;
import org.junit.Test;

public class ProfFilterTest {
    @Test
    public void testIsNeedInject() throws Exception {
        ProfFilter.addIncludeClass("a");
        Assert.assertTrue(ProfFilter.isNeedInject("a"));
        Assert.assertFalse(ProfFilter.isNeedInject("b"));
    }

    @Test
    public void testIsNotNeedInject() throws Exception {
        ProfFilter.addExcludeClass("a");
        Assert.assertTrue(ProfFilter.isNotNeedInject("a"));
        Assert.assertFalse(ProfFilter.isNotNeedInject("b"));
    }

    @Test
    public void testIsNotNeedInjectClassLoader() throws Exception {
        ProfFilter.addExcludeClassLoader("a");
        Assert.assertTrue(ProfFilter.isNotNeedInjectClassLoader("a"));
        Assert.assertFalse(ProfFilter.isNotNeedInjectClassLoader("b"));
    }
}
