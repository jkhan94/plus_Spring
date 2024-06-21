package com.sparta.easyspring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// MyselectshopApplication의 JPAAuditing 때문에 테스트가 안 돼서 config 추가
@Configuration // 아래 설정을 등록하여 활성화 합니다.
@EnableJpaAuditing // 시간 자동 변경이 가능하도록 합니다.
public class JpaConfig {
}
