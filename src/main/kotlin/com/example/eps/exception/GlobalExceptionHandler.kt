package com.example.eps.exception

import com.example.eps.constants.ErrorMessages
import com.example.eps.model.dto.abstract.ApiResponse
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.lang.IllegalArgumentException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(ex: ApplicationException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.failure(ex.message ?: ErrorMessages.APPLICATION_ERROR))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.failure(ex.message ?: ErrorMessages.INVALID_REQUEST_ARGUMENT))

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Nothing>> {
        val msg = "Invalid value '${ex.value}' for parameter '${ex.name}'"
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.failure(msg))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val errorMessages = ex.bindingResult.fieldErrors.joinToString("; ") { fieldError ->
            "'${fieldError.field}' ${fieldError.defaultMessage ?: ErrorMessages.VALIDATION_ERROR}"
        }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.failure("Validation error $errorMessages"))
    }

    @ExceptionHandler(JsonParseException::class, JsonMappingException::class)
    fun handleJsonException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.failure("${ErrorMessages.MALFORMED_JSON}: ${ex.message}"))

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        ex.printStackTrace() // Optional: log stack trace
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.failure("${ErrorMessages.INTERNAL_SERVER_ERROR}: ${ex.message}"))
    }
}
