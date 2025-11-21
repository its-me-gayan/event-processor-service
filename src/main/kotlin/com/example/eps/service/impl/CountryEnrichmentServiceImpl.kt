package com.example.eps.service.impl

import com.example.eps.model.dto.CountryInfoDto
import com.example.eps.service.CountryEnrichmentService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.Duration

/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/20/25
 * Time: 12:06 AM
 */
@Service
class CountryEnrichmentServiceImpl(
    private val webClient: WebClient,
    @Value("\${external.countries.url}") private val url: String,
    @Value("\${external.countries.timeout:5}") private val timeoutSeconds: Long,
    private val objectMapper: ObjectMapper
) : CountryEnrichmentService {

    private val logger = KotlinLogging.logger {}

    @Retryable(
        maxAttemptsExpression = "\${external.countries.retry:3}",
        backoff = Backoff(delayExpression = "\${external.countries.back-off-delay:2000}"),
        retryFor = [WebClientResponseException::class, RuntimeException::class]
    )
    override fun fetchCountryInfo(countryCode: String): CountryInfoDto {
        logger.info { "Starting country info fetch for country code: $countryCode" }

        return try {
            val response = callExternalApi(countryCode)
            val countryInfo = parseApiResponse(response, countryCode)
            logger.info { "Successfully fetched country info for $countryCode: ${countryInfo.countryName}" }
            countryInfo
        } catch (ex: Exception) {
            logger.error(ex) { "Failed to fetch country info for country code: $countryCode" }
            throw when (ex) {
                is WebClientResponseException -> {
                    logger.warn { "HTTP error ${ex.statusCode} fetching country info for $countryCode: ${ex.message}" }
                    RuntimeException("Country API returned error: ${ex.statusCode}")
                }
                else -> ex
            }
        }
    }

    private fun callExternalApi(countryCode: String): String {
        val formattedUrl = url.replace("{code}", countryCode)
        logger.debug { "Calling external country API: $formattedUrl" }

        return webClient.get()
            .uri(formattedUrl)
            .retrieve()
            .bodyToMono(String::class.java)
            .doOnSubscribe { logger.trace { "Initiating web request for country code: $countryCode" } }
            .doOnSuccess { logger.trace { "Received response for country code: $countryCode" } }
            .doOnError { error ->
                logger.debug(error) { "WebClient error for country code: $countryCode" }
            }
            .block(Duration.ofSeconds(timeoutSeconds))
            ?: throw RuntimeException("Country API returned empty response for country code: $countryCode")
    }

    private fun parseApiResponse(response: String, countryCode: String): CountryInfoDto {
        logger.debug { "Parsing API response for country code: $countryCode" }

        val jsonNode = try {
            objectMapper.readTree(response)
        } catch (ex: Exception) {
            logger.error(ex) { "Failed to parse JSON response for country code: $countryCode" }
            throw RuntimeException("Invalid JSON response from country API")
        }

        validateApiResponse(jsonNode, countryCode)

        val countryNode = jsonNode[0]
        return createCountryInfoDto(countryNode, countryCode).also {
            logger.trace { "Successfully parsed country info for $countryCode from API response" }
        }
    }

    private fun validateApiResponse(jsonNode: com.fasterxml.jackson.databind.JsonNode, countryCode: String) {
        if (!jsonNode.isArray || jsonNode.size() == 0) {
            logger.warn { "Empty or invalid array response from country API for code: $countryCode" }
            throw RuntimeException("No country data found for code: $countryCode")
        }

        if (jsonNode.size() > 1) {
            logger.debug { "Multiple countries found for code $countryCode, using first result" }
        }
    }

    private fun createCountryInfoDto(countryNode: com.fasterxml.jackson.databind.JsonNode, countryCode: String): CountryInfoDto {
        val countryName = countryNode.path("name").path("common").asText().takeIf { it.isNotBlank() }
            ?: run {
                logger.warn { "Missing country name in response for code: $countryCode" }
                "Unknown"
            }

        val capital = countryNode.path("capital").firstOrNull()?.asText() ?: "N/A"
        val region = countryNode.path("region").asText("Unknown")
        val population = countryNode.path("population").asDouble(0.0)

        logger.trace {
            "Extracted country details - Name: $countryName, Capital: $capital, Region: $region, Population: $population"
        }

        return CountryInfoDto.from(
            countryName = countryName,
            isIndependent = countryNode.path("independent").asBoolean(false),
            isUnMember = countryNode.path("unMember").asBoolean(false),
            capital = capital,
            region = region,
            population = population
        )
    }
}