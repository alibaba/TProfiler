package com.taobao.profile.config;

import org.junit.Assert;
import org.junit.Test;

public class ProfConfigTest {
    @Test
    public void defaultConfigShouldWork() throws Exception {
        ProfConfig profile = new ProfConfig("profile");
        Assert.assertEquals("com.taobao;com.taobao.common", profile.getIncludePackageStartsWith());
    }
}
