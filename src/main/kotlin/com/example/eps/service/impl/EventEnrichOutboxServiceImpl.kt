package com.example.eps.service.impl

import com.example.eps.constants.OutboxStatus
import com.example.eps.helper.TransactionalPublishHelper
import com.example.eps.message.publisher.MessagePublisher
import com.example.eps.model.dto.CountryInfoDto
import com.example.eps.model.dto.EnrichedEventMessage
import com.example.eps.model.dto.IncomingEventMessage
import com.example.eps.model.entity.OutboxEvent
import com.example.eps.service.CountryEnrichmentService
import com.example.eps.service.EventEnrichOutboxService
import com.example.eps.service.InboxOutboxEventUpdateHelperService
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/21/25
 * Time: 12:29 AM
 */
@Service
class EventEnrichOutboxServiceImpl(
    private val countryEnrichmentService: CountryEnrichmentService,
    private val inboxOutboxEventUpdateHelperService: InboxOutboxEventUpdateHelperService,
    private val kafkaPublisher: MessagePublisher,
    private val objectMapper: ObjectMapper
) : EventEnrichOutboxService {

    private val logger = KotlinLogging.logger {}

    @Transactional
    override fun submitForEnrichment(event: IncomingEventMessage, payload: String) {
        val eventId = event.eventId
        logger.info { "Starting event enrichment process for eventId: $eventId" }

        try {
            val savedOutboxEvent = saveInitialOutboxEvent(event, payload)
            logger.debug { "Successfully saved initial outbox event for eventId: $eventId with status: ${savedOutboxEvent.status}" }

            val enrichedPayload = performCountryEnrichment(event, eventId)

            if (enrichedPayload != null) {
                scheduleDownstreamPublishing(eventId, enrichedPayload)
                logger.info { "Successfully scheduled downstream publishing for eventId: $eventId" }
            } else {
                logger.warn { "Downstream publishing skipped due to enrichment failure for eventId: $eventId" }
            }

            logger.info { "Completed event enrichment process for eventId: $eventId" }
        } catch (ex: Exception) {
            logger.error(ex) { "Unexpected error during event enrichment process for eventId: $eventId" }
            throw ex
        }
    }

    private fun saveInitialOutboxEvent(event: IncomingEventMessage, payload: String): OutboxEvent {
        logger.debug { "Creating and saving initial outbox event for eventId: ${event.eventId}" }
        val outboxEvent = OutboxEvent.from(event.eventId, payload, null)
        return inboxOutboxEventUpdateHelperService.saveOutboxEvent(outboxEvent).also {
            logger.info { "Initial outbox event saved successfully for eventId: ${event.eventId} with ID: ${it.id}" }
        }
    }

    private fun performCountryEnrichment(event: IncomingEventMessage, eventId: UUID): String? {
        logger.debug { "Starting country enrichment for eventId: $eventId with countryCode: ${event.countryCode}" }
        return try {
            val countryInfo = countryEnrichmentService.fetchCountryInfo(event.countryCode)
            logger.debug { "Successfully fetched country info for countryCode: ${event.countryCode}" }

            val enrichedEventMessage = createEnrichedEventMessage(event, countryInfo)
            val enrichedPayload = objectMapper.writeValueAsString(enrichedEventMessage)

            inboxOutboxEventUpdateHelperService.updateOutboxEvent(
                eventId,
                OutboxStatus.ENRICHED,
                enrichedPayload,
                null
            )
            logger.info { "Country enrichment completed successfully for eventId: $eventId" }

            enrichedPayload
        } catch (ex: Exception) {
            logger.error(ex) { "Country enrichment failed for eventId: $eventId, countryCode: ${event.countryCode}" }
            inboxOutboxEventUpdateHelperService.updateOutboxEvent(
                eventId,
                OutboxStatus.ENRICHMENT_FAILED,
                null,
                "Country enrichment failed: ${ex.localizedMessage}"
            )
            null
        }
    }

    private fun createEnrichedEventMessage(
        event: IncomingEventMessage,
        countryInfo: CountryInfoDto
    ): EnrichedEventMessage {
        return EnrichedEventMessage.from(
            eventId = event.eventId,
            userId = event.userId,
            countryCode = event.countryCode,
            timestamp = event.timestamp,
            countryName = countryInfo.countryName,
            isIndependent = countryInfo.isIndependent,
            isUnMember = countryInfo.isUnMember,
            capital = countryInfo.capital,
            region = countryInfo.region,
            population = countryInfo.population
        ).also {
            logger.trace { "Created enriched event message for eventId: ${event.eventId}" }
        }
    }

    private fun scheduleDownstreamPublishing(eventId: UUID, enrichedPayload: String) {
        logger.debug { "Scheduling downstream publishing for eventId: $eventId" }

        TransactionalPublishHelper.registerAfterCommit {
            try {
                logger.debug { "Executing downstream publishing for eventId: $eventId" }

                kafkaPublisher.publishEnrichedMessage(eventId.toString(), enrichedPayload)

                inboxOutboxEventUpdateHelperService.updateOutboxEvent(
                    eventId,
                    OutboxStatus.PUBLISHED_TO_DOWNSTREAM,
                    null,
                    null
                )
                logger.info { "Successfully published enriched message to downstream for eventId: $eventId" }
            } catch (ex: Exception) {
                logger.error(ex) { "Downstream publishing failed for eventId: $eventId" }

                inboxOutboxEventUpdateHelperService.updateOutboxEvent(
                    eventId,
                    OutboxStatus.DOWNSTREAM_PUBLISH_FAILED,
                    null,
                    "Downstream publishing failed: ${ex.localizedMessage}"
                )
            }
        }
    }
}