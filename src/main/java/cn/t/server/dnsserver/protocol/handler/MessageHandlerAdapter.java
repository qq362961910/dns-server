package cn.t.server.dnsserver.protocol.handler;

import cn.t.server.dnsserver.protocol.Request;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息处理器
 *
 * @author yj
 * @since 2020-03-04 17:37
 **/
@Slf4j
public class MessageHandlerAdapter {
    private final List<MessageHandler> messageHandlerList = new ArrayList<>();
    public Object handle(Request request) {
        MessageHandler messageHandler = selectMessageHandler(request);
        if(messageHandler != null) {
            return messageHandler.handler(request);
        } else {
            log.warn("未能处理的消息: {}", request);
            return null;
        }
    }
    private MessageHandler selectMessageHandler(Request request) {
        for(MessageHandler messageHandler: messageHandlerList) {
            if(messageHandler.support(request)) {
                return messageHandler;
            }
        }
        return null;
    }

    public MessageHandlerAdapter() {
        messageHandlerList.add(new InternetIpV4DomainQueryHandler());
    }
}
