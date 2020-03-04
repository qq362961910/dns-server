package cn.t.server.dnsserver.protocol;

import cn.t.server.dnsserver.constants.RecordClass;
import cn.t.server.dnsserver.constants.RecordType;
import lombok.Data;

@Data
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
}
