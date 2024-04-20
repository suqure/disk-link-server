package ltd.finelink.tool.disk.utils;

import java.lang.reflect.Method;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class IPUtil {
 

    private static DbSearcher searcher;
    private static Method method;

    static {
        // load db 
        Resource resource = new ClassPathResource("ip2region.db"); 
		try {
	        DbConfig config = new DbConfig();
	        searcher = new DbSearcher(config, IOUtils.toByteArray(resource.getInputStream()));
	        method = searcher.getClass().getMethod("memorySearch", String.class);
		} catch (Exception e) {
			 log.warn("[ip2region] Error: initialization failed: {}", e.getMessage());
		}
        
    }

    public static String getCityInfo(String ip) {
        if (ip == null || !Util.isIpAddress(ip)) {
            return null;
        }
        try {
            return ((DataBlock) method.invoke(searcher, ip)).getRegion();
        } catch (Exception e) {
        	log.warn("[ip2region] get city info error: {}",e.getMessage());
        }
        return null;
    }
    
    public static String getRealIp(FullHttpRequest request) {
		String ip = request.headers().get("request-remote-host");
		if (ip == null || ip.length() == 0 || StringUtils.isEmpty(ip)) {
			ip = request.headers().get("X-Forwarded-For");
		}
		if (ip == null || ip.length() == 0 || StringUtils.isEmpty(ip)) {
			ip = request.headers().get("X-Forwarded-For");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.headers().get("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.headers().get("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.headers().get("HTTP_X_CLUSTER_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.headers().get("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.headers().get("HTTP_X_FORWARDED");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.headers().get("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.headers().get("HTTP_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.headers().get("HTTP_FORWARDED");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.headers().get("HTTP_VIA");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.headers().get("REMOTE_ADDR");
		}

		if (ip != null && ip.indexOf(",") != -1) {
			String[] ipWithMultiProxy = ip.split(",");
			for (String eachIpSegement : ipWithMultiProxy) {
				if (!"unknown".equalsIgnoreCase(eachIpSegement)) {
					ip = eachIpSegement;
					break;
				}
			}
		}
		return ip;
	}
     
}
