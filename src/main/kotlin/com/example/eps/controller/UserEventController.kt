package com.example.eps.controller

import com.example.eps.helper.GenericResponseBuilder
import com.example.eps.model.dto.UserEventCaptureRequestDto
import com.example.eps.model.dto.UserEventCaptureResponseDto
import com.example.eps.model.dto.abstract.ApiResponse
import com.example.eps.service.UserEventService
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/18/25
 * Time: 10:01 PM
 */
@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
class UserEventController(
    private final val userEventService: UserEventService,
    private final val genericResponseBuilder: GenericResponseBuilder
) {

    @PostMapping("/event")
    fun postEvent(@RequestBody @Valid requestDto: UserEventCaptureRequestDto): ResponseEntity<ApiResponse<UserEventCaptureResponseDto>> {
        val response = userEventService.publishForEnrichment(requestDto);
        return genericResponseBuilder.ok(response , "Event captured and published successfully")
    }

    @GetMapping("/welcome")
    fun welcome(): ResponseEntity<String>{
        return ResponseEntity.accepted().body("Hi im working!")
    }
}