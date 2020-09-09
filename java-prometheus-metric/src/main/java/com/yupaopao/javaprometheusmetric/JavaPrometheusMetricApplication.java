package com.yupaopao.javaprometheusmetric;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JavaPrometheusMetricApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaPrometheusMetricApplication.class, args);
    }
    //在启动主类中添加Bean ，此配置是监控 jvm 的
    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(@Value("${spring.application.name}") String applicationName){
//        Common tags可以被定义在registry级别，并且会被添加到每个监控系统的报告中
        return registry -> registry.config().commonTags("application",applicationName);
    }
}


