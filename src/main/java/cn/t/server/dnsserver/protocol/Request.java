package cn.t.server.dnsserver.protocol;

import cn.t.server.dnsserver.constants.RecordClass;
import cn.t.server.dnsserver.constants.RecordType;

/**
 * @author yj
 * @since 2020-01-01 10:45
 **/
public class Request {
    private Header header;
    private String domain;
    private byte labelCount;
    private RecordType type;
    private RecordClass clazz;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public byte getLabelCount() {
        return labelCount;
    }

    public void setLabelCount(byte labelCount) {
        this.labelCount = labelCount;
    }

    public RecordType getType() {
        return type;
    }

    public void setType(RecordType type) {
        this.type = type;
    }

    public RecordClass getClazz() {
        return clazz;
    }

    public void setClazz(RecordClass clazz) {
        this.clazz = clazz;
    }
}
