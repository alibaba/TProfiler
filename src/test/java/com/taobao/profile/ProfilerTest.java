package com.taobao.profile;

import com.taobao.profile.runtime.ProfStack;
import com.taobao.profile.runtime.ThreadData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProfilerTest {

    private int methodId;
    private int subMethodId;

    @Before
    public void setUp() throws Exception {
        Profiler.clearData();
        methodId = 1;
        subMethodId = 2;
        enableProfiler(true);
    }

    @Test
    public void disableProfilerShouldWork() throws Exception {
        enableProfiler(false);
        profile();
        ThreadData threadData = getThreadData();
        assertEmpty(threadData);
    }

    @Test
    public void profilerShouldWork() throws Exception {
        profile();
        ThreadData threadData = getThreadData();
        Assert.assertEquals(1, threadData.profileData.size());
        long[] profileData = threadData.profileData.pop();
        Assert.assertEquals(methodId, profileData[0]);
        Assert.assertTrue(profileData[2] > 0);
    }

    private void profile() throws InterruptedException {
        Profiler.Start(methodId);
        wait100ms();
        Profiler.End(methodId);
    }

    private void wait100ms() throws InterruptedException {
        synchronized (this) {
            this.wait(100);
        }
    }

    @Test
    public void nanoTimeIfSet() throws Exception {
        Manager.NEED_NANO_TIME = true;
        profile();
        ThreadData threadData = getThreadData();
        long[] profileData = threadData.profileData.pop();
        Assert.assertTrue(profileData[2] > 100 * 1000);
    }

    @Test
    public void unmatchedEndShouldWork() throws Exception {
        Profiler.End(methodId);
        ThreadData threadData = getThreadData();
        assertEmpty(threadData);
    }

    @Test
    public void nestCallShouldWork() throws Exception {
        Profiler.Start(methodId);
        Profiler.Start(subMethodId);
        wait100ms();
        Profiler.End(subMethodId);
        Profiler.End(methodId);
        ProfStack<long[]> profileData = getThreadData().profileData;
        Assert.assertEquals(subMethodId, profileData.elementAt(0)[0]);
        Assert.assertEquals(methodId, profileData.elementAt(1)[0]);
    }

    @Test
    public void profileShouldStopWhenProfileDataIsTooMany() throws Exception {
        ProfStack<long[]> profileData = getThreadData().profileData;
        //TODO magic number
        for (int i = 0; i < 20001; i++) {
            profileData.push(new long[]{1, 1, 1});
        }
        profile();
        Assert.assertEquals(20001, profileData.size());
    }

    private void assertEmpty(ThreadData threadData) {
        Assert.assertTrue(null == threadData ||
                (0 == threadData.profileData.size() + threadData.stackFrame.size()));
    }

    private void enableProfiler(boolean b) {
        Manager.instance().setProfileFlag(b);
    }

    private ThreadData getThreadData() {
        return Profiler.threadProfile[((int) Thread.currentThread().getId())];
    }
}
