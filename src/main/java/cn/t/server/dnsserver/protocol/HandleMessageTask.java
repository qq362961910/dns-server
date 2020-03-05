package cn.t.server.dnsserver.protocol;

import cn.t.server.dnsserver.protocol.handler.MessageHandlerAdapter;
import cn.t.server.dnsserver.util.MessageCodecUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * 消息处理器
 * @author yj
 * @since 2020-03-04 16:30
 **/
@Slf4j
public class HandleMessageTask implements Runnable {

    private DatagramSocket serverDatagramSocket;
    private final ByteBuffer byteBuffer;
    private InetAddress sourceINetAddress;
    private int sourcePort;

    @Override
    public void run() {
        Request request = MessageCodecUtil.decodeRequest(byteBuffer);
        if(request != null) {
            try {
                MessageHandlerAdapter.handle(request, serverDatagramSocket, sourceINetAddress, sourcePort);
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    public HandleMessageTask(DatagramSocket serverDatagramSocket, ByteBuffer byteBuffer, InetAddress sourceINetAddress, int sourcePort) {
        this.serverDatagramSocket = serverDatagramSocket;
        this.byteBuffer = byteBuffer;
        this.sourceINetAddress = sourceINetAddress;
        this.sourcePort = sourcePort;
    }
}
