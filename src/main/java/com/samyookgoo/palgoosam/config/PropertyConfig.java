package com.samyookgoo.palgoosam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource("classpath:properties/env.production.properties")
})
public class PropertyConfig {
}
