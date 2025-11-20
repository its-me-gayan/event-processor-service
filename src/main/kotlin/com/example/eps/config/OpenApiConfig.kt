package com.example.eps.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/20/25
 * Time: 10:49 PM
 */

@Configuration
class OpenApiConfig {

    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("event-service")
            .pathsToMatch("/**")
            .build()
    }

    @Bean
    fun customOpenAPI(): OpenAPI {
        val contact = Contact()
        contact.email = "slgayan1@gmail.com"
        contact.name = "Gayan Sanjeewa"
        contact.url = "https://www.s.id/hendisantika"
        return OpenAPI()
            .info(
                Info()
                    .title("Event Processing service")
                    .version("V1")
                    .description("")
                    .termsOfService("http://swagger.io/terms/")
                    .license(License().name("Apache 2.0").url("http://springdoc.org"))
                    .contact(contact)
            )
    }

}