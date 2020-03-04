package cn.t.server.dnsserver.protocol;

import lombok.Data;

import java.util.List;

@Data
public class Response extends Request {
    private List<Record> recordList;
}
