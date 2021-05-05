package com.pierre.serialnumbersgenerator.config;

import com.pierre.serialnumbersgenerator.model.Settings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneratorConfigurer {

    @Bean
    @ConfigurationProperties("settings.ui")
    public Settings uiSettings() {

        return new Settings();
    }

    @Bean
    @ConfigurationProperties("settings.api")
    public Settings apiSettings() {

        return new Settings();
    }
}
