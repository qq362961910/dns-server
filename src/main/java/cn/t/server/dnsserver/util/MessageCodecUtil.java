package cn.t.server.dnsserver.util;

import cn.t.server.dnsserver.constants.RecordClass;
import cn.t.server.dnsserver.constants.RecordType;
import cn.t.server.dnsserver.exception.ForbidServiceException;
import cn.t.server.dnsserver.protocol.Header;
import cn.t.server.dnsserver.protocol.Request;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * 消息编解码工具
 *
 * @author yj
 * @since 2020-03-04 17:02
 **/
@Slf4j
public final class MessageCodecUtil {

    /**
     * request解码
     */
    public static Request decodeRequest(ByteBuffer messageBuffer) {
        //解析头部
        //报文Id
        short id = messageBuffer.getShort();
        //报文标志
        short flag = messageBuffer.getShort();
        //查询问题区域的数量
        short queryDomainCount = messageBuffer.getShort();
        //回答区域的数量
        short answerCount = messageBuffer.getShort();
        //授权区域的数量
        short authoritativeNameServerCount = messageBuffer.getShort();
        //附加区域的数量
        short additionalRecordsCount = messageBuffer.getShort();

        //服务检查
        if(!FlagUtil.isQuery(flag)) {
            throw new ForbidServiceException("flag只支持查询");
        }
        if(!FlagUtil.isForwardDirection(flag)) {
            throw new ForbidServiceException("flag只支持正向查询");
        }

        //解析报文体
        if(queryDomainCount > 0) {
            StringBuilder domainBuilder = new StringBuilder();
            byte count;
            byte labelCount = 0;
            while (messageBuffer.remaining() > 0 && (count = messageBuffer.get()) > 0) {
                byte[] partDomain = new byte[count];
                messageBuffer.get(partDomain);
                domainBuilder.append(new String(partDomain)).append(".");
                labelCount++;
            }
            if(domainBuilder.length() > 0) {
                domainBuilder.deleteCharAt(domainBuilder.length() - 1);
            }
            String domain = domainBuilder.toString();
            short type = messageBuffer.getShort();
            short clazz = messageBuffer.getShort();
            //header
            Header header = new Header();
            header.setTransID(id);
            header.setQueryDomainCount(queryDomainCount);
            header.setAnswerCount(answerCount);
            header.setAuthoritativeNameServerCount(authoritativeNameServerCount);
            header.setAdditionalRecordsCount(additionalRecordsCount);
            //message
            Request request = new Request();
            request.setHeader(header);
            request.setDomain(domain);
            request.setLabelCount(labelCount);
            request.setType(RecordType.getRecordType(type));
            request.setClazz(RecordClass.getRecordClass(clazz));
            return request;
        } else {
            log.warn("query domain count is 0");
            return null;
        }
    }

    /**
     * request编码
     */
    public static byte[] encodeRequest(Request request) {
        Header header = request.getHeader();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //1.transaction id
        buffer.putShort(header.getTransID());
        //2.flag
        buffer.putShort(header.getFlags());
        //3.question count(固定写1)
        buffer.putShort((short) 1);
        //4.answer count
        buffer.putShort((short) 0);
        //5.authoritative count(固定写0)
        buffer.putShort((short)0);
        //6.additional count(固定写0)
        buffer.putShort((short)0);
        //7.query body
        String[] elements = request.getDomain().split("\\.");
        for(String ele: elements) {
            //长度
            buffer.put((byte)ele.length());
            //value
            buffer.put(ele.getBytes());
        }
        //结束补0
        buffer.put((byte)0);
        //2.type
        buffer.putShort(request.getType().value);
        //3.class
        buffer.putShort(request.getClazz().value);
        buffer.flip();
        int len = buffer.limit() - buffer.position();
        byte[] bytes = new byte[len];
        buffer.get(bytes);
        return bytes;
    }

}
