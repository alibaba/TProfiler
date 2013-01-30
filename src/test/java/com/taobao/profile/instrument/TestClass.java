package com.taobao.profile.instrument;

/**
 * @author ikari_shinji
 * use "java -classpath asm-all-4.1.jar org.objectweb.asm.util.ASMifier com/taobao/profile/instrument/TestClass.class"
 * to generate byte code
 */
public class TestClass {
    public void testMethod() throws InterruptedException {
        synchronized (this) {
            this.wait(1000l);
        }
    }
}
