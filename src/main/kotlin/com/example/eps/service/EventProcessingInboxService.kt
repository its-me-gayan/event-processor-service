package com.example.eps.service

import com.example.eps.model.dto.UserEventCaptureRequestDto
import com.example.eps.model.dto.UserEventCaptureResponseDto


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/18/25
 * Time: 11:33 PM
 */
interface EventProcessingInboxService {
    fun publishForEnrichment(requestDto: UserEventCaptureRequestDto): UserEventCaptureResponseDto;
}