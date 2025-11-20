package com.example.eps.service.impl

import com.example.eps.model.dto.CountryInfoDto
import com.example.eps.service.CountryEnrichmentService
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/20/25
 * Time: 12:06 AM
 */
@Service
class CountryEnrichmentServiceImpl(
    private val webClient : WebClient,
    @Value("\${external.countries.url}") private val url:String
    ): CountryEnrichmentService {

    private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)


    @Retryable(maxAttempts = 3, backoff = Backoff(delay = 2000))
    override fun fetchCountryInfo(alpha2: String): CountryInfoDto {
        val formattedUrl = url.replace("{code}", alpha2)
        println("Calling external API: {} "+formattedUrl)
        val response = webClient.get()
            .uri(formattedUrl)
            .retrieve()
            .bodyToMono(String::class.java)
            .block(Duration.ofSeconds(5))
        val jsonNode = mapper.readTree(response)
        if (!jsonNode.isArray || jsonNode.size() == 0) {
            throw RuntimeException("Invalid country API response")
        }

        val countryNode = jsonNode[0]

        return CountryInfoDto.from(
            countryName = countryNode.path("name").path("common").asText(),
            isIndependent = countryNode.path("independent").asBoolean(false),
            isUnMember = countryNode.path("unMember").asBoolean(false),
            capital = countryNode.path("capital").firstOrNull()?.asText() ?: "",
            region = countryNode.path("region").asText(),
            population = countryNode.path("population").asDouble(0.0)
        )
    }
}