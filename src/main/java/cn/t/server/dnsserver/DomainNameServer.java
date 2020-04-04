package cn.t.server.dnsserver;

import cn.t.server.dnsserver.protocol.HandleMessageTask;
import cn.t.server.dnsserver.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author yj
 * @since 2020-03-04 16:01
 **/
public class DomainNameServer {

    private static final Logger logger = LoggerFactory.getLogger(DomainNameServer.class);

    private static final byte[] buffer = new byte[512];

    public static void main(String[] args) throws Exception {
        //修改java内置dns服务器地址
//        SystemPropertiesLoader.loadDefaultProperties();
        DatagramSocket socket = new DatagramSocket(53);
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            InetAddress inetAddress = packet.getAddress();
            int port = packet.getPort();
            byte[] content = Arrays.copyOfRange(buffer, 0, packet.getLength());
            logger.info("message from [{}:{}], {}B", inetAddress, port, content.length);
            ThreadUtil.submitMessageHandleTask(new HandleMessageTask(socket, ByteBuffer.wrap(content), inetAddress, port));
        }
    }
}
