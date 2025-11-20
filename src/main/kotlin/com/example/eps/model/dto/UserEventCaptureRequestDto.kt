package com.example.eps.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.time.Instant


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/18/25
 * Time: 10:35 PM
 */
data class UserEventCaptureRequestDto(
    @field:NotNull
    @field:NotBlank
    val userId: String,

    @field:NotBlank
    @field:Pattern(regexp = "^[A-Z]{2}\$", message = "ISO2 country code uppercase")
    val countryCode: String,

    val timestamp: Instant = Instant.now()

)