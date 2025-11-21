package com.example.eps.model.dto.abstract

import com.fasterxml.jackson.annotation.JsonInclude
import java.sql.Timestamp
import java.time.Instant


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/18/25
 * Time: 11:47 PM
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val timestamp: Timestamp,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T? = null, message: String = "Success") =
            ApiResponse(true, message, Timestamp.from(Instant.now()),data)

        fun failure(message: String) =
            ApiResponse(false, message, Timestamp.from(Instant.now()),null)
    }
}
