package cn.t.server.dnsserver.util;

import cn.t.server.dnsserver.constants.RecordClass;
import cn.t.server.dnsserver.constants.RecordType;
import cn.t.server.dnsserver.exception.ForbidServiceException;
import cn.t.server.dnsserver.protocol.Header;
import cn.t.server.dnsserver.protocol.Record;
import cn.t.server.dnsserver.protocol.Request;
import cn.t.server.dnsserver.protocol.Response;
import cn.t.util.common.CollectionUtil;
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

    /**
     * response编码
     */
    public static byte[] encodeResponse(Response response) {
        Header header = response.getHeader();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //1.transaction id
        buffer.putShort(header.getTransID());
        //2.flag
        buffer.putShort(header.getFlags());
        //3.question count(固定写1)
        buffer.putShort((short) 1);
        //4.answer count
        buffer.putShort((short)(CollectionUtil.isEmpty(response.getRecordList()) ? 0 : response.getRecordList().size()));
        //5.authoritative count(固定写0)
        buffer.putShort((short)0);
        //6.additional count(固定写0)
        buffer.putShort((short)0);
        //7.query body
        //7.1 domain
        String[] elements = response.getDomain().split("\\.");
        for(String ele: elements) {
            //长度
            buffer.put((byte)ele.length());
            //value
            buffer.put(ele.getBytes());
        }
        //结束补0
        buffer.put((byte)0);
        //7.2 type
        buffer.putShort(response.getType().value);
        //7.3 class
        buffer.putShort(response.getClazz().value);

        //8.answer body
        for(Record record: response.getRecordList()) {
            //8.1 offset
            //todo
//            buffer.putShort(record.getOffset());
            //8.2 type
            buffer.putShort(record.getRecordType().value);
            //8.3 class
            buffer.putShort(record.getRecordClass().value);
            //8.4 ttl
            buffer.putInt(record.getTtl());
            //8.5 value
            if(RecordType.A == record.getRecordType()) {
                //值为字符串的ip地址
                buffer.putShort((short)4);
                String ip = record.getValue();
                String[] ipElements = ip.split("\\.");
                for(String part : ipElements) {
                    try {
                        buffer.put((byte)Short.parseShort(part));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            } else if(RecordType.CNAM == record.getRecordType()) {
                //值为别名
                ByteBuffer cnameBuffer = ByteBuffer.allocate(64);
                String[] cNameElements = record.getValue().split("\\.");
                for(String cNamePart: cNameElements) {
                    //长度
                    cnameBuffer.put((byte)cNamePart.length());
                    //value
                    cnameBuffer.put(cNamePart.getBytes());
                }
                buffer.put(cnameBuffer.array());
            }
        }
        buffer.flip();
        int len = buffer.limit() - buffer.position();
        byte[] bytes = new byte[len];
        buffer.get(bytes);
        return bytes;
    }

}
