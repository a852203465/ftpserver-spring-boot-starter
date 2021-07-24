package com.github.darkjrong.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ftp 加载配置
 * @author Rong.Jia
 * @date 2019/10/17 00:27
 */
@Configuration
@ConditionalOnClass({FtpServerProperties.class})
@EnableConfigurationProperties(FtpServerProperties.class)
@ConditionalOnProperty(prefix = "ftp", name = "enabled", havingValue = "true")
public class FtpServerAutoConfiguration {

    private final FtpServerProperties ftpServerProperties;

    public FtpServerAutoConfiguration(final FtpServerProperties ftpServerProperties) {
        this.ftpServerProperties = ftpServerProperties;
    }

    @Bean
    public FtpServerFactoryBean ftpServerFactoryBean() {
        final FtpServerFactoryBean factoryBean = new FtpServerFactoryBean();
        factoryBean.setProperties(this.ftpServerProperties);
        return factoryBean;
    }

}
