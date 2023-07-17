package com.pyrosandro.common.error;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:common-error.properties"})
@ComponentScan(basePackages = "com.pyrosandro.common.error")
@AutoConfigureBefore(MessageSourceAutoConfiguration.class)
public class CommonErrorAutoConfiguration {
}
