package com.taobao.profile.dependence_query;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author weigao
 * @since 15/7/15
 */
public class RecordSlowQuery {
    private String type;
    private long useTime;
    private Map<String, String> requestDesc;

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }


    public Map<String, String> getRequestDesc() {
        return requestDesc;
    }

    public void setRequestDesc(Map<String, String> requestDesc) {
        this.requestDesc = requestDesc;
    }

    private String map2Str() {
        if (requestDesc == null) {
            return "".intern();
        }

        Set<Map.Entry<String, String>> entries = requestDesc.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            sb.append(next.getKey());
            sb.append(" : ");
            sb.append(next.getValue());
            sb.append(";");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "RecordSlowQuery{" +
                "type=" + type +
                ",useTime=" + useTime +
                ", requestDesc=" + map2Str() +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
