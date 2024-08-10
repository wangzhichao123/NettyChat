package com.wzc.netty.config.properties;
import cn.hutool.core.util.StrUtil;
import com.wzc.netty.pojo.IpInfo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Ip2regionSearcher implements DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(Ip2regionSearcher.class);

    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\|");

    private Searcher searcher;

    public Ip2regionSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    @SneakyThrows
    public String searchStr(String ip) {
        return searcher.search(ip);
    }

    public IpInfo search(String ip) {
        String region = searchStr(ip);
        if (region == null) {
            return null;
        }
        String[] splitInfos = SPLIT_PATTERN.split(region);
        // 补齐5位
        if (splitInfos.length < 5) {
            splitInfos = Arrays.copyOf(splitInfos, 5);
        }
        IpInfo ipInfo = IpInfo.builder()
                .country(filterZero(splitInfos[0]))
                .region(filterZero(splitInfos[1]))
                .province(filterZero(splitInfos[2]))
                .city(filterZero(splitInfos[3]))
                .isp(filterZero(splitInfos[4]))
                .build();
        return ipInfo;
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Real-IP");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("x-forwarded-for");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.error("getIpAddress exception:", e);
                }
                assert inet != null;
                ipAddress = inet.getHostAddress();
            }
        }
        return StrUtil.subBefore(ipAddress, ",", false);
    }

    /**
     * 数据过滤，因为 ip2Region 采用 0 填充的没有数据的字段
     */
    @Nullable
    private String filterZero(@Nullable String info) {
        // null 或 0 返回 null
        if (info == null || BigDecimal.ZERO.toString().equals(info)) {
            return null;
        }
        return info;
    }

    @Override
    public void destroy() throws Exception {
        if (this.searcher != null) {
            this.searcher.close();
        }
    }

}
