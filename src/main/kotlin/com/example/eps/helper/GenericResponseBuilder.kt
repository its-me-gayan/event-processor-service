package com.example.eps.helper

import com.example.eps.model.dto.abstract.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/19/25
 * Time: 12:00 AM
 */
@Component
class GenericResponseBuilder {

    fun <T> ok(data: T, message: String = "Success"): ResponseEntity<ApiResponse<T>> =
        ResponseEntity.ok(ApiResponse.success(data, message))

    fun <T> created(data: T, message: String = "Created"): ResponseEntity<ApiResponse<T>> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(data, message))

    fun error(message: String, status: HttpStatus): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.status(status)
            .body(ApiResponse.failure(message))
}