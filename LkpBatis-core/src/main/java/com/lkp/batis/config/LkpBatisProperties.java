package com.lkp.batis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "lkpbatis.server")
public class LkpBatisProperties {
    String ip;
    String port;
}
