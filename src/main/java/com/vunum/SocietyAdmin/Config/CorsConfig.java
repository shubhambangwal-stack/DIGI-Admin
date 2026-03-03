package com.vunum.SocietyAdmin.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    /**
     * CORS CONFIG TO SETUP WEBSOCKETS
     * REMOVE SOME ORIGINS AFTER DEPLOYMENT
     *
     * @return FILTERED SOURCE
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("http://localhost:5173");
        config.addAllowedOriginPattern("https://digi-immo.netlify.app");
        config.addAllowedOriginPattern("https://administrator.digiimmo.eu");
        config.addAllowedOriginPattern("https://benevolent-entremet-6f415e.netlify.app");
        config.addAllowedOriginPattern("http://192.168.0.120:8082");
        config.addAllowedOriginPattern("http://localhost:3000");
        config.addAllowedOriginPattern("http://localhost:3000/");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
