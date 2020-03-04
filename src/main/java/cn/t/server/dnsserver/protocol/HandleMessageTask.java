package cn.t.server.dnsserver.protocol;

import cn.t.server.dnsserver.util.MessageCodecUtil;

import java.nio.ByteBuffer;

/**
 * 消息处理器
 * @author yj
 * @since 2020-03-04 16:30
 **/
public class HandleMessageTask implements Runnable {

    private final ByteBuffer byteBuffer;

    @Override
    public void run() {
        Request request = MessageCodecUtil.decodeRequest(byteBuffer);
        if(request != null) {

        }
    }

    public HandleMessageTask(byte[] message) {
        this.byteBuffer = ByteBuffer.wrap(message);
    }
}
