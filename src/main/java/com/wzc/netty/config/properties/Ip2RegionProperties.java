package com.wzc.netty.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = Ip2RegionProperties.PREFIX)
public class Ip2RegionProperties {
    public static final String PREFIX = "ip2region";
    private boolean enabled = false;
    private String dbFile = "ip/ip2region.xdb";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDbFile() {
        return dbFile;
    }

    public void setDbFile(String dbFile) {
        this.dbFile = dbFile;
    }

}
