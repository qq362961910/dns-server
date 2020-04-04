package cn.t.server.dnsserver.protocol;

import java.util.List;

public class Response extends Request {

    private List<Record> recordList;

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }
}
