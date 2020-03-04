package cn.t.server.dnsserver.protocol;

import cn.t.server.dnsserver.constants.RecordClass;
import cn.t.server.dnsserver.constants.RecordType;
import lombok.Data;

/**
 * @author yj
 * @since 2020-01-01 10:45
 **/
@Data
public class Request {
    private Header header;
    private String domain;
    private byte labelCount;
    private RecordType type;
    private RecordClass clazz;
}
