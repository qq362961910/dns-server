package cn.t.server.dnsserver.util;

import cn.t.util.io.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author yj
 * @since 2020-01-01 11:55
 **/
public class Ipv4DomainUtil {

    private static final Logger logger = LoggerFactory.getLogger(Ipv4DomainUtil.class);

    private static final Properties properties;

    static {
        properties = tryIpv4DomainMappingConfiguration();
    }

    private static Properties tryIpv4DomainMappingConfiguration() {
        Properties properties = new Properties();
        try (
            InputStream is = FileUtil.getResourceInputStream(Ipv4DomainUtil.class, "/ipv4-domain-mapping.properties")
        ) {
            if(is == null) {
                logger.error("ipv4配置文件未找到: {}", "ipv4-domain-mapping.properties");
            } else {
                properties.load(is);
            }
        } catch (IOException e) {
            logger.error("", e);
        }
        return properties;
    }

    /**
     * 根据domain查询ip
     * @param domain 域名
     * @return ip
     */
    public static String getCustomDomainMapping(String domain) {
        return properties.getProperty(domain);
    }
}
