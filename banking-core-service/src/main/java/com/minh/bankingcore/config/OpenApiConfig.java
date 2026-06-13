package com.minh.bankingcore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI bankingCoreOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mini Core Banking - Core API")
                        .description("APIs for account, transfer, transaction, ledger, outbox, and audit flow.")
                        .version("v1"));
    }
}
