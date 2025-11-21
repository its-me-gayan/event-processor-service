package com.example.eps.service.impl

import com.example.eps.constants.ErrorMessages
import com.example.eps.exception.ApplicationException
import com.example.eps.model.dto.EventInquiryResponseDto
import com.example.eps.model.entity.InboxEvent
import com.example.eps.model.entity.OutboxEvent
import com.example.eps.respository.InboxRepository
import com.example.eps.respository.OutboxRepository
import com.example.eps.service.EventInquiryService
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

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
) : EventInquiryService {

    private val logger = KotlinLogging.logger {}

    override fun getEventDetailsByEventId(eventId: UUID): EventInquiryResponseDto {
        logger.info { "Starting event inquiry for eventId: $eventId" }

        return try {
            // 1st priority is outbox
            findInOutbox(eventId) ?: findInInbox(eventId)
        } catch (ex: Exception) {
            logger.error(ex) { "Error occurred while fetching event details for eventId: $eventId" }
            throw when (ex) {
                is ApplicationException -> ex
                else -> ApplicationException(ErrorMessages.EVENT_FETCH_ERROR)
            }
        }
    }

    private fun findInOutbox(eventId: UUID): EventInquiryResponseDto? {
        logger.debug { "Searching for event in outbox repository for eventId: $eventId" }

        return outboxRepository.findByOriginalEventId(eventId)
            .takeIf { it.isPresent }
            ?.let { outboxEvent ->
                logger.info { "Event found in outbox repository for eventId: $eventId" }
                convertOutboxEventToResponse(outboxEvent.get(), eventId)
            }
    }

    private fun findInInbox(eventId: UUID): EventInquiryResponseDto {
        logger.debug { "Searching for event in inbox repository for eventId: $eventId" }

        return inboxRepository.findById(eventId)
            .takeIf { it.isPresent }
            ?.let { inboxEvent ->
                logger.info { "Event found in inbox repository for eventId: $eventId" }
                convertInboxEventToResponse(inboxEvent.get())
            }
            ?: run {
                logger.warn { "Event not found in both outbox and inbox repositories for eventId: $eventId" }
                throw ApplicationException(ErrorMessages.EVENT_NOT_FOUND)
            }
    }

    private fun convertOutboxEventToResponse(outboxEvent: OutboxEvent, eventId: UUID): EventInquiryResponseDto {
        return try {
            logger.debug { "Converting outbox event to response DTO for eventId: $eventId" }

            val response = objectMapper.readValue(outboxEvent.payload, EventInquiryResponseDto::class.java)
            response.status = outboxEvent.status.toString()
            response.error = outboxEvent.error

            logger.debug { "Successfully converted outbox event to response DTO for eventId: $eventId" }
            response
        } catch (ex: Exception) {
            logger.error(ex) { "Failed to deserialize outbox event payload for eventId: $eventId" }
            throw ApplicationException(ErrorMessages.EVENT_DESERIALIZATION_ERROR)
        }
    }

    private fun convertInboxEventToResponse(inboxEvent: InboxEvent): EventInquiryResponseDto {
        logger.debug { "Converting inbox event to response DTO for eventId: ${inboxEvent.id}" }

        return EventInquiryResponseDto.from(
            eventId = inboxEvent.id,
            userId = inboxEvent.userId,
            countryCode = inboxEvent.countryCode,
            timestamp = inboxEvent.eventTimestamp,
            countryName = null,
            isIndependent = false,
            isUnMember = false,
            capital = null,
            region = null,
            population = 0.0,
            status = inboxEvent.status.toString(),
            error = inboxEvent.error
        ).also {
            logger.debug { "Successfully converted inbox event to response DTO for eventId: ${inboxEvent.id}" }
        }
    }
}