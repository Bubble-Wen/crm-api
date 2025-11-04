package com.crm.utils;

import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssClientConfig {
    @Value("${aliyun.oss.endpoint}")
    String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    String accessKeySecret;

    @Bean
    public OSSClient createOssClient() {
        return (OSSClient) new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }
}
