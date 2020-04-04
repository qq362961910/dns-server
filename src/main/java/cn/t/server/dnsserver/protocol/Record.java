package cn.t.server.dnsserver.protocol;

import cn.t.server.dnsserver.constants.RecordClass;
import cn.t.server.dnsserver.constants.RecordType;

public class Record {

    /**
     * 记录类型
     * */
    private RecordType recordType;

    /**
     * 记录class
     * */
    private RecordClass recordClass;

    /**
     * time to live(秒)
     * */
    private int ttl;

    /**
     * 值
     * */
    private String value;

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public RecordClass getRecordClass() {
        return recordClass;
    }

    public void setRecordClass(RecordClass recordClass) {
        this.recordClass = recordClass;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
