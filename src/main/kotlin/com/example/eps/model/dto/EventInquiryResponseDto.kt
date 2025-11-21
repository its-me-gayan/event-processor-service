package com.example.eps.model.dto

import java.time.Instant
import java.util.UUID


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/21/25
 * Time: 1:56 AM
 */
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class EventInquiryResponseDto @JsonCreator constructor(
    @param:JsonProperty("eventId") val eventId: UUID,
    @param:JsonProperty("userId") val userId: String,
    @param:JsonProperty("countryCode") val countryCode: String,
    @param:JsonProperty("timestamp") val timestamp: Instant,
    @param:JsonProperty("countryName") val countryName: String?,
    @param:JsonProperty("isIndependent") val isIndependent: Boolean,
    @param:JsonProperty("isUnMember") val isUnMember: Boolean,
    @param:JsonProperty("capital") val capital: String?,
    @param:JsonProperty("region") val region: String?,
    @param:JsonProperty("population") val population: Double,
    @param:JsonProperty("status") var status: String?,
    @param:JsonProperty("error") var error: String?
)

 {
    companion object {
        fun from(
            eventId: UUID,
            userId: String,
            countryCode: String,
            timestamp: Instant,
            countryName: String?,
            isIndependent: Boolean,
            isUnMember: Boolean,
            capital: String?,
            region: String?,
            population: Double,
            status: String?,
            error: String?
        ): EventInquiryResponseDto =
            EventInquiryResponseDto(
                eventId = eventId,
                userId = userId,
                countryCode = countryCode,
                timestamp = timestamp,
                countryName = countryName,
                isIndependent = isIndependent,
                isUnMember = isUnMember,
                capital = capital,
                region = region,
                population = population,
                status = status,
                error = error
            )
    }
}
