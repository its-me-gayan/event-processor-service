package com.example.eps.controller

import com.example.eps.helper.GenericResponseBuilder
import com.example.eps.model.dto.EventInquiryResponseDto
import com.example.eps.model.dto.UserEventCaptureRequestDto
import com.example.eps.model.dto.UserEventCaptureResponseDto
import com.example.eps.model.dto.abstract.ApiResponse
import com.example.eps.service.EventInquiryService
import com.example.eps.service.EventProcessingInboxService
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/18/25
 * Time: 10:01 PM
 */
@RequestMapping("/api/v1/event")
@RestController
@RequiredArgsConstructor
class UserEventController(
    private final val eventProcessingInboxService: EventProcessingInboxService,
    private final val eventInquiryService: EventInquiryService,
    private final val genericResponseBuilder: GenericResponseBuilder
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping
    fun postEvent(@RequestBody @Valid requestDto: UserEventCaptureRequestDto): ResponseEntity<ApiResponse<UserEventCaptureResponseDto>> {
        val response = eventProcessingInboxService.publishForEnrichment(requestDto);
        return genericResponseBuilder.ok(response , "Event captured and published successfully")
    }

    @GetMapping("/{eventId}")
    fun findEvent(@PathVariable("eventId") eventId: UUID):ResponseEntity<ApiResponse<EventInquiryResponseDto>>{
        val response = eventInquiryService.getEventDetailsByEventId(eventId)
        return genericResponseBuilder.ok(response)
    }
}