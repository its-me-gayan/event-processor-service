package com.example.eps.service.impl

import com.example.eps.constants.ErrorMessages
import com.example.eps.exception.ApplicationException
import com.example.eps.model.dto.EventInquiryResponseDto
import com.example.eps.respository.InboxRepository
import com.example.eps.respository.OutboxRepository
import com.example.eps.service.EventInquiryService
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.UUID


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/21/25
 * Time: 1:55 AM
 */
@Service
class EventInquiryServiceImpl(
    private val inboxRepository: InboxRepository,
    private val outboxRepository: OutboxRepository,
    private val objectMapper: ObjectMapper
): EventInquiryService {
    private val logger = KotlinLogging.logger {}

    override fun getEventDetailsByEventId(eventId: UUID): EventInquiryResponseDto {
        //1st priority is outbox
        val outboxEvent = outboxRepository.findByOriginalEventId(eventId)
        if(!outboxEvent.isPresent){
            val inboxEventOptional = inboxRepository.findById(eventId)
            if(!inboxEventOptional.isPresent){
                throw ApplicationException(ErrorMessages.EVENT_NOT_FOUND)
            }
            var inboxEvent =  inboxEventOptional.get()
            return EventInquiryResponseDto
                .from(inboxEvent.id ,inboxEvent.userId,inboxEvent.countryCode,inboxEvent.eventTimestamp,null,false,false,null,null,0.0,inboxEvent.status.toString(),inboxEvent.error )
        }else{
            val event = outboxEvent.get()
            val response = objectMapper.readValue(event.payload , EventInquiryResponseDto::class.java)
            response.status = event.status.toString()
            response.error = event.error
            return response;
        }
    }
}