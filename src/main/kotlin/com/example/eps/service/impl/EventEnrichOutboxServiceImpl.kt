package com.example.eps.service.impl

import com.example.eps.constants.OutboxStatus
import com.example.eps.helper.TransactionalPublishHelper
import com.example.eps.message.publisher.MessagePublisher
import com.example.eps.model.dto.EnrichedEventMessage
import com.example.eps.model.dto.IncomingEventMessage
import com.example.eps.model.entity.OutboxEvent
import com.example.eps.respository.OutboxRepository
import com.example.eps.service.CountryEnrichmentService
import com.example.eps.service.EventEnrichOutboxService
import com.example.eps.service.InboxOutboxEventUpdateHelperService
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


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
    private val kafkaPublisher: MessagePublisher
) : EventEnrichOutboxService{

    private val logger = KotlinLogging.logger {}
    private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)


    @Transactional
    override fun submitForEnrichment(event: IncomingEventMessage, payload:String) {
        logger.info("Started event enrichment")

        val eventId = event.eventId
        val outboxEvent = OutboxEvent.from(eventId, payload, null)
        val savedOutbox = inboxOutboxEventUpdateHelperService.saveOutboxEvent(outboxEvent)
        logger.info { "Event saved in outbox eventId : $eventId , status: ${savedOutbox.status}" }

        var enrichedPayload: String? =null;
        try {
            val countryInfo = countryEnrichmentService.fetchCountryInfo(event.countryCode)

            val enrichedEventMessage = EnrichedEventMessage.from(
                eventId,
                event.userId,
                event.countryCode,
                event.timestamp,
                countryInfo.countryName,
                countryInfo.isIndependent,
                countryInfo.isUnMember,
                countryInfo.capital,
                countryInfo.region,
                countryInfo.population
            )
            enrichedPayload = mapper.writeValueAsString(enrichedEventMessage)

            inboxOutboxEventUpdateHelperService
                .updateOutboxEvent(eventId ,OutboxStatus.ENRICHED ,
                    enrichedPayload,null)
        }catch (ex : Exception){
            inboxOutboxEventUpdateHelperService
                .updateOutboxEvent(eventId ,OutboxStatus.ENRICHMENT_FAILED ,
                    null,ex.localizedMessage)
        }

        if(enrichedPayload!=null){
            TransactionalPublishHelper.registerAfterCommit {
                try {
                    kafkaPublisher.publishEnrichedMessage(eventId.toString() , enrichedPayload)
                    inboxOutboxEventUpdateHelperService
                        .updateOutboxEvent(eventId ,OutboxStatus.PUBLISHED_TO_DOWNSTREAM ,
                            null,null)
                }catch (ex : Exception){
                    inboxOutboxEventUpdateHelperService
                        .updateOutboxEvent(eventId ,OutboxStatus.DOWNSTREAM_PUBLISH_FAILED ,
                            null,ex.localizedMessage)
                }
            }
        }else{
            logger.info("no downstream publishing due to enrichment failure")
        }

        logger.info("finished event enrichment")
    }
}