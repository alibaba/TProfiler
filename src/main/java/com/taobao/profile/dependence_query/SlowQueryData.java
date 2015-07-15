package com.taobao.profile.dependence_query;

import com.taobao.profile.runtime.ProfStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author weigao
 * @since 15/7/15
 */
public class SlowQueryData {
    /**
     * 性能分析数据
     */
    public ProfStack<RecordSlowQuery> profileData = new ProfStack<RecordSlowQuery>();

    /**
     * 性能分析数据
     */
    public Map<Integer,RecordSlowQuery> profileMap = new HashMap<Integer,RecordSlowQuery>();

    /**
     * 栈帧
     */
    public ProfStack<Object[]> stackFrame = new ProfStack<Object[]>();
    /**
     * 当前栈深度
     */
    public int stackNum = 0;

    /**
     * 清空数据
     */
    public void clear(){
        profileData.clear();
        stackFrame.clear();
        profileMap.clear();
        stackNum = 0;
    }
}
