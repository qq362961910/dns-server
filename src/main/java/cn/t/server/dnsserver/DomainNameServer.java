package cn.t.server.dnsserver;

import cn.t.server.dnsserver.protocol.HandleMessageTask;
import cn.t.server.dnsserver.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * @author yj
 * @since 2020-03-04 16:01
 **/
@Slf4j
public class DomainNameServer implements Runnable {

    private final byte[] buffer = new byte[512];

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(53);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                InetAddress inetAddress = packet.getAddress();
                int port = packet.getPort();
                log.info("message from [{}:{}], {}B", inetAddress, port, packet.getLength());
                ThreadUtil.submitMessageHandleTask(new HandleMessageTask(Arrays.copyOfRange(buffer, 0, packet.getLength())));
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public static void main(String[] args) {
        new Thread(new DomainNameServer()).start();
    }
}
