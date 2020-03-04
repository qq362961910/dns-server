package cn.t.server.dnsserver.protocol.handler;

import cn.t.server.dnsserver.constants.RecordClass;
import cn.t.server.dnsserver.constants.RecordType;
import cn.t.server.dnsserver.protocol.Header;
import cn.t.server.dnsserver.protocol.Record;
import cn.t.server.dnsserver.protocol.Request;
import cn.t.server.dnsserver.protocol.Response;
import cn.t.server.dnsserver.util.FlagUtil;
import cn.t.server.dnsserver.util.MessageCodecUtil;
import cn.t.util.common.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yj
 * @since 2020-01-01 11:37
 **/
@Slf4j
public class InternetIpV4DomainQueryHandler implements MessageHandler {

    private Ipv4DomainHelper ipv4DomainHelper = new Ipv4DomainHelper();

    @Override
    public boolean support(Request request) {
        //class: internet && type: A
        return request != null && RecordClass.IN == request.getClazz() && RecordType.A == request.getType();
    }

    @Override
    public Object handler(Request request) throws IOException {
        String domain = request.getDomain();
        //读取配置域名
        String ip = ipv4DomainHelper.getCustomDomainMapping(domain);
        if(!StringUtil.isEmpty(ip)) {
            log.info("===================================== domain : {} use local dns config, response ip: {} =====================================", domain, ip);
            Response response = new Response();
            response.setLabelCount(request.getLabelCount());
            response.setDomain(domain);
            response.setType(request.getType());
            response.setClazz(request.getClazz());
            List<Record> recordList = new ArrayList<>();
            response.setRecordList(recordList);
            //record
            Record record = new Record();
            record.setOffset((short)(0xC000 | 12));
            record.setRecordType(RecordType.A);
            record.setRecordClass(RecordClass.IN);
            record.setTtl(20);
            record.setValue(ip);
            recordList.add(record);

            Header header = request.getHeader();
            short flag = header.getFlags();
            flag = FlagUtil.markResponse(flag);
            //如果客户端设置建议递归查询
            if(FlagUtil.isRecursionResolve(header.getFlags())) {
                flag = FlagUtil.markRecursionSupported(flag);
            }
            header.setFlags(flag);
            header.setAnswerCount((short)recordList.size());
            response.setHeader(header);
            return response;
        } else {
            log.info("domain: {} is not config in file, use local resolver", domain);
            //加载
            try {
                InetAddress[] addresses = InetAddress.getAllByName(domain);
                log.info("domain: {} resolved by local resolver, record size: {}", domain, addresses.length);
                //因为域名字符的限制(最大为63)所以byte字节的高两位始终为00，所以使用高两位使用11表示使用偏移量来表示对应的域名,10和01两种状态被保留
                //前面内容都是定长，所以偏移量一定是从12开始算起
                Response response = new Response();
                response.setLabelCount(request.getLabelCount());
                response.setDomain(domain);
                response.setType(request.getType());
                response.setClazz(request.getClazz());
                List<Record> recordList = new ArrayList<>();
                response.setRecordList(recordList);
                for(InetAddress inetAddress: addresses) {
                    if(inetAddress instanceof Inet4Address) {
                        Record record = new Record();
                        record.setOffset((short)(0xC000 | 12));
                        record.setRecordType(RecordType.A);
                        record.setRecordClass(RecordClass.IN);
                        record.setTtl(1);
                        record.setValue(inetAddress.getHostAddress());
                        recordList.add(record);
                    }
                }
                Header header = request.getHeader();
                short flag = header.getFlags();
                flag = FlagUtil.markResponse(flag);
                //如果客户端设置建议递归查询
                if(FlagUtil.isRecursionResolve(header.getFlags())) {
                    flag = FlagUtil.markRecursionSupported(flag);
                }
                header.setFlags(flag);
                header.setAnswerCount((short)recordList.size());
                response.setHeader(header);
                return response;
            } catch (UnknownHostException e) {
                log.warn(String.format("本地路由解析域名失败, domain: %s", domain), e);
                log.info("domain: {} cannot be resolved by local resolver, use 114.114.114.114", domain);
                InetAddress dnsServerAddress = InetAddress.getByName("114.114.114.114");
                DatagramSocket internetSocket = new DatagramSocket();
                byte[] data = MessageCodecUtil.encodeRequest(request);
                DatagramPacket internetSendPacket = new DatagramPacket(data, data.length, dnsServerAddress, 53);
                internetSocket.send(internetSendPacket);
                byte[] receivedData = new byte[512];
                DatagramPacket internetReceivedPacket = new DatagramPacket(receivedData, receivedData.length);
                internetSocket.receive(internetReceivedPacket);
                return Arrays.copyOfRange(receivedData, 0, internetReceivedPacket.getLength())
            }
        }
    }

}
