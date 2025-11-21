package com.example.eps.service

import com.example.eps.model.dto.EventInquiryResponseDto
import java.util.UUID


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/20/25
 * Time: 12:06 AM
 */
interface EventInquiryService {

    fun getEventDetailsByEventId(eventId: UUID): EventInquiryResponseDto
}