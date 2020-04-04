package cn.t.server.dnsserver.protocol.handler;

import cn.t.server.dnsserver.protocol.Request;
import cn.t.server.dnsserver.protocol.Response;
import cn.t.server.dnsserver.util.MessageCodecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息处理器
 *
 * @author yj
 * @since 2020-03-04 17:37
 **/
public final class MessageHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandlerAdapter.class);

    private static final List<MessageHandler> messageHandlerList = new ArrayList<>();

    public static void handle(Request request, DatagramSocket serverDatagramSocket, InetAddress sourceINetAddress, int sourcePort) throws IOException {
        MessageHandler messageHandler = selectMessageHandler(request);
        if(messageHandler != null) {
            Object result =  messageHandler.handler(request);
            if(result != null) {
                if(result instanceof Response) {
                    byte[] responseBytes = MessageCodecUtil.encodeResponse((Response)result);
                    DatagramPacket packet = new DatagramPacket(responseBytes, responseBytes.length);
                    packet.setAddress(sourceINetAddress);
                    packet.setPort(sourcePort);
                    serverDatagramSocket.send(packet);
                } else if(result instanceof byte[]) {
                    byte[] responseBytes = ((byte[])result);
                    DatagramPacket packet = new DatagramPacket(responseBytes, responseBytes.length);
                    packet.setAddress(sourceINetAddress);
                    packet.setPort(sourcePort);
                    serverDatagramSocket.send(packet);
                } else {
                    logger.warn("未实现的编码类型: {}", result.getClass().getName());
                }
            } else {
                logger.info("消息处理结果返回值为空, 忽略");
            }
        } else {
            logger.warn("未能处理的消息: {}", request);
        }
    }
    private static MessageHandler selectMessageHandler(Request request) {
        for(MessageHandler messageHandler: messageHandlerList) {
            if(messageHandler.support(request)) {
                return messageHandler;
            }
        }
        return null;
    }
    static {
        messageHandlerList.add(new InternetIpV4DomainQueryHandler());
        messageHandlerList.add(new InternetIpV6DomainQueryHandler());
    }
}
