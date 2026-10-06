/*
 * Copyright 2023-2026 OpenAIRE AMKE & Athena Research and Innovation Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.athenarc.messaging.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearer";

    @Bean
    public OpenAPI openAPI(@Value("${project.version}") String version,
                           @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}") String issuerUri) {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Messaging System API")
                        .description("Messaging Service API")
                        .version(version)
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"))
                );

        if (StringUtils.hasText(issuerUri)) {
            openAPI.components(new Components().addSecuritySchemes(BEARER_SCHEME,
                            new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                    .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
        }
        return openAPI;
    }
}
