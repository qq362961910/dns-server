package cn.t.server.dnsserver.protocol.handler;


import cn.t.server.dnsserver.protocol.Request;

import java.io.IOException;

public interface MessageHandler {
    boolean support(Request request);
    Object handler(Request request) throws IOException;
}
