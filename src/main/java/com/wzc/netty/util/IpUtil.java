package com.wzc.netty.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.wzc.netty.exception.BizException;
import com.wzc.netty.pojo.IpInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.regex.Pattern;

@Slf4j
public class IpUtil{

    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\|");

    private static final String IP_FILE_PATH = "ip/ip2region.db";

    private static final long INVALID_IP_ADDRESS = 2130706433L;

    /**
     * 解析ip地址
     *
     * @param ipStr 字符串类型ip 例:192.168.0.1
     * @return 返回结果形式(国家 | 区域 | 省份 | 城市 | ISP) 例 [中国, 0, 河北省, 衡水市, 电信]
     */
    public static IpInfo parse(String ipStr) {
        return parse(ipStr, null);
    }

    /**
     * 自定义解析ip地址
     *
     * @param ipStr 字符串类型ip 例:1970753539(经过转换后的)
     * @param index 想要获取的区间 例如:只想获取 省,市 index = [2,3]
     * @return 返回结果例 [北京,北京市]
     */
    public static IpInfo parse(String ipStr, int[] index) {
        try {
            //获得文件流时，因为读取的文件是在打好jar文件里面，不能直接通过文件资源路径拿到文件，但是可以在jar包中拿到文件流
            //ResourcePatternResolver的实现方法，可以匹配到各种部署时的各种文件类型例如war，jar，zip等等findPathMatchingResources
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(IP_FILE_PATH);
            Resource resource = resources[0];
            File target = new File(resource.getURI().getPath());
            try (InputStream is = resource.getInputStream();
                 FileOutputStream fos = new FileOutputStream(target)) {
                 IOUtils.copy(is, fos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Searcher searcher = new Searcher(String.valueOf(target), null, null);
            long ip = Searcher.checkIP(ipStr);
            if (ip == INVALID_IP_ADDRESS) {
                return null;
            }
            return parse(ip, index, searcher);
        } catch (Exception e) {
            log.error("ip解析为long错误,ipStr:[{}],错误信息:[{}]", ipStr, e.getMessage(), e);
            throw new BizException("系统异常!");
        }
    }

    /**
     * 自定义解析ip地址
     *
     * @param ip    ip Long类型ip
     * @param index 想要获取的区间 例如:只想获取 省,市 index = [2,3]
     * @return 返回结果例 [湖南省, 衡阳市]
     */
    public static IpInfo parse(Long ip, int[] index, Searcher searcher) {
        IpInfo ipInfo = new IpInfo();
        try {
            String region = searcher.search(ip);
            log.info("获取到的城市信息：" + region);
            String[] splitInfos = SPLIT_PATTERN.split(region);
            // 补齐5位
            if (splitInfos.length < 5) {
                splitInfos = Arrays.copyOf(splitInfos, 5);
            }
            ipInfo.setCountry(filterZero(splitInfos[0]));
            ipInfo.setRegion(filterZero(splitInfos[1]));
            ipInfo.setProvince(filterZero(splitInfos[2]));
            ipInfo.setCity(filterZero(splitInfos[3]));
            ipInfo.setIsp(filterZero(splitInfos[4]));
//            if (index == null) {
//                regionList = Arrays.asList(split);
//            } else {
//                for (int i : index) {
//                    regionList.add(split[i]);
//                }
//            }
            //关闭资源
            searcher.close();
        } catch (Exception e) {
            log.error("根据ip解析地址失败,ip:[{}],index[{}],报错信息:[{}]", ip, index, e.getMessage(), e);
            throw new RuntimeException("系统异常!");
        }
        return ipInfo;
    }

    public static String getIpAddress(FullHttpRequest request, ChannelHandlerContext ctx) {
        HttpHeaders headers = request.headers();
        // 1. 检查 X-Real-IP 头, 这个头通常是由负载均衡器或代理服务器添加的,用于记录客户端的真实 IP 地址。
        String ipAddress = headers.get("X-Real-IP");
        if (isValidIpAddress(ipAddress)) {
            return ipAddress;
        }
        // 2. 检查 x-forwarded-for 头
        ipAddress = headers.get("x-forwarded-for");
        if (isValidIpAddress(ipAddress)) {
            return ipAddress;
        }
        // 3. 检查 Proxy-Client-IP 头
        ipAddress = headers.get("Proxy-Client-IP");
        if (isValidIpAddress(ipAddress)) {
            return ipAddress;
        }
        // 4. 检查 WL-Proxy-Client-IP 头
        ipAddress = headers.get("WL-Proxy-Client-IP");
        if (isValidIpAddress(ipAddress)) {
            return ipAddress;
        }
        // 5. 检查 HTTP_CLIENT_IP 头
        ipAddress = headers.get("HTTP_CLIENT_IP");
        if (isValidIpAddress(ipAddress)) {
            return ipAddress;
        }
        // 6. 检查 HTTP_X_FORWARDED_FOR 头
        ipAddress = headers.get("HTTP_X_FORWARDED_FOR");
        if (isValidIpAddress(ipAddress)) {
            return ipAddress;
        }
        // 7. 如果客户端位于代理服务器后面,这种方式获取到的将是代理服务器的 IP 地址,而不是客户端的真实 IP 地址。
        if (StrUtil.isEmpty(ipAddress)) {
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            ipAddress = address.getAddress().getHostAddress();
        }
        return ipAddress;
    }

    private static boolean isValidIpAddress(String ipAddress) {
        return ipAddress != null && !ipAddress.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress);
    }

    /**
     * 数据过滤，因为 ip2Region 采用 0 填充的没有数据的字段
     */
    @Nullable
    private static String filterZero(@Nullable String info) {
        // null 或 0 返回 null
        if (info == null || BigDecimal.ZERO.toString().equals(info)) {
            return null;
        }
        return info;
    }

}
