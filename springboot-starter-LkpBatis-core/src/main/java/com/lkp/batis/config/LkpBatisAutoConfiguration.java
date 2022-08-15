package com.lkp.batis.config;

import com.lkp.batis.annotation.MyMapper;
import com.lkp.batis.connection.Executor;
import com.lkp.batis.connection.Encoder;
import com.lkp.batis.connection.Packager;
import com.lkp.batis.connection.RoundTripper;
import com.lkp.batis.connection.Transporter;
import com.lkp.batis.processor.AutoConfiguredMapperScannerRegister;
import com.lkp.batis.processor.MyMapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties(LkpBatisProperties.class)
public class LkpBatisAutoConfiguration {
    @Autowired
    LkpBatisProperties lkpBatisProperties;
    @Bean
    public Executor executor(LkpBatisProperties lkpBatisProperties) throws IOException {
        Encoder encoder = new Encoder();
        Transporter transporter = new Transporter(lkpBatisProperties.getIp(),lkpBatisProperties.getPort());
        Packager packager = new Packager(transporter, encoder);
        RoundTripper roundTripper = new RoundTripper(packager);
        return new Executor(roundTripper);
    }

    @Configuration
    @Import(AutoConfiguredMapperScannerRegister.class)
    @ConditionalOnMissingBean(MyMapperFactoryBean.class)
    public static class MyMapperScannerRegisterConfiguration{}
}
