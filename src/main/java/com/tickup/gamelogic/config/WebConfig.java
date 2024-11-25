package com.tickup.gamelogic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 잠시 cors 설정 끄기 위한 파일 ( 통합 시 삭제 해야 함 )
@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // API 경로
                        .allowedOrigins("http://localhost:3000") // 허용할 프론트엔드 도메인
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                        .allowedHeaders("*") // 모든 헤더 허용
                        .allowCredentials(true); // 쿠키/인증 정보 허용
            }
        };
    }
}
