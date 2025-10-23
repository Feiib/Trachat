package com.fei.aitravelassistant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.autoconfigure.mcp.server.MpcServerAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//exclude = PgVectorStoreAutoConfiguration.class
@SpringBootApplication(exclude = {MpcServerAutoConfiguration.class})
@MapperScan("com.fei.aitravelassistant.mapper")
public class FeiAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeiAiAgentApplication.class, args);
    }

}
