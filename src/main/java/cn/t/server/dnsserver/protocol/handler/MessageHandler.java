package cn.t.server.dnsserver.protocol.handler;


import cn.t.server.dnsserver.protocol.Request;

public interface MessageHandler {
    boolean support(Request request);
    Object handler(Request request);
}
