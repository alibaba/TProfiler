package com.taobao.profile.dependence_query;

import com.taobao.profile.Profiler;
import com.taobao.profile.runtime.MethodCache;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;

/**
 * @author weigao
 * @since 15/5/25
 */
public abstract class IMethodAdapter extends MethodAdapter {

    /**
     * 方法ID
     */
    protected int mMethodId = 0;

    public IMethodAdapter(MethodVisitor methodVisitor,String fileName, String className, String methodName) {
        super(methodVisitor);
        mMethodId = MethodCache.Request();
        MethodCache.UpdateMethodName(mMethodId, fileName, className, methodName);
        // 记录方法数
        Profiler.instrumentMethodCount.getAndIncrement();
    }

}
