package com.example.eps.service.impl

import com.example.eps.constants.InboxStatus
import com.example.eps.helper.TransactionalPublishHelper
import com.example.eps.model.dto.IncomingEventMessage
import com.example.eps.model.dto.UserEventCaptureRequestDto
import com.example.eps.model.dto.UserEventCaptureResponseDto
import com.example.eps.model.entity.InboxEvent
import com.example.eps.message.publisher.MessagePublisher
import com.example.eps.respository.InboxRepository
import com.example.eps.service.CountryEnrichmentService
import com.example.eps.service.InboxOutboxEventUpdateHelperService
import com.example.eps.service.EventProcessingInboxService
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/18/25
 * Time: 11:34 PM
 */
@Service
class EventProcessingInboxProcessingInboxServiceImpl (
    private val inboxRepository: InboxRepository,
    private val messagePublisher: MessagePublisher,
    private val inboxEventUpdateHelperService: InboxOutboxEventUpdateHelperService,
) : EventProcessingInboxService  {

    private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    @Transactional
    override fun publishForEnrichment(requestDto: UserEventCaptureRequestDto): UserEventCaptureResponseDto {


        val inboxEvent = InboxEvent.from(requestDto.userId, requestDto.countryCode, requestDto.timestamp);
        val saveInboxEvent = inboxRepository.save(inboxEvent);
        val inboxEventId = saveInboxEvent.id

        // Ensure publisher happens AFTER transaction commit (so message only sent if db commit succeeds)
        TransactionalPublishHelper.registerAfterCommit {
            try {
                val eventMessage = IncomingEventMessage.from(
                    saveInboxEvent.id,
                    saveInboxEvent.userId,
                    saveInboxEvent.countryCode,
                    saveInboxEvent.eventTimestamp
                )
                messagePublisher.publishIncomingMessage(inboxEventId.toString(), mapper.writeValueAsString(eventMessage));

                // update inbox under inside new transaction
                inboxEventUpdateHelperService.updateInboxEvent(saveInboxEvent.id , InboxStatus.PUBLISHED_FOR_ENRICHMENT,true,null)

            } catch (ex: Exception) {
                println("Failed to publisher inbox event {} to kafka: {}"+inboxEvent.id+ ":: " + ex.message)
                // update inbox under inside new transaction
                inboxEventUpdateHelperService.updateInboxEvent(saveInboxEvent.id , InboxStatus.ENRICHMENT_PUBLISH_FAILED,false,ex.localizedMessage)
            }
        }
        val userEventCaptureResponseDto = UserEventCaptureResponseDto(inboxEvent.id);
        return userEventCaptureResponseDto;
    }
}