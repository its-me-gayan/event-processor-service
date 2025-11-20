package com.example.eps.service.impl

import com.example.eps.constants.OutboxStatus
import com.example.eps.helper.TransactionalPublishHelper
import com.example.eps.model.dto.EnrichedEventMessage
import com.example.eps.model.dto.IncomingEventMessage
import com.example.eps.model.entity.OutboxEvent
import com.example.eps.publish.MessagePublisher
import com.example.eps.respository.OutboxRepository
import com.example.eps.service.CountryEnrichmentService
import com.example.eps.service.InboxOutboxEventUpdateHelperService
import com.example.eps.service.ListenerService
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/19/25
 * Time: 12:31 AM
 */
@Service
class KafkaListenerService(
    private val outboxRepository: OutboxRepository,
    private val countryEnrichmentService: CountryEnrichmentService,
    private val inboxOutboxEventUpdateHelperService: InboxOutboxEventUpdateHelperService,
    private val kafkaPublisher: MessagePublisher

): ListenerService {

    private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    //kafka listener
//    //@KafkaListener(topics = ["\${kafka.topic.incoming:events-raw}"])
    @KafkaListener(topics = ["events-raw"])
    override fun listen(record: ConsumerRecord<String, String>) {
        println("message consumed " + record.key()+" : " +  record.partition()+" : "+record.offset());
        println(record.value())

        //listsen incoming published message , then enriched and store enriched message as the outbox
        val payload = record.value()
        val incomingEventMessage: IncomingEventMessage = mapper.readValue(payload)
        val eventId = incomingEventMessage.eventId
        val outboxEvent = OutboxEvent.from(eventId, payload, null)

        outboxRepository.save(outboxEvent)
        var enrichedPayload: String? =null;
        try {
            val countryInfo = countryEnrichmentService.fetchCountryInfo(incomingEventMessage.countryCode)

            val enrichedEventMessage = EnrichedEventMessage.from(
                eventId,
                incomingEventMessage.userId,
                incomingEventMessage.countryCode,
                incomingEventMessage.timestamp,
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
                    enrichedPayload)
        }catch (ex : Exception){
            ex.printStackTrace()
            inboxOutboxEventUpdateHelperService
                .updateOutboxEvent(eventId ,OutboxStatus.ENRICHMENT_FAILED ,
                    null)
        }

        if(enrichedPayload!=null){
            TransactionalPublishHelper.registerAfterCommit {
                try {
                    kafkaPublisher.publishEnrichedMessage(eventId.toString() , enrichedPayload)

                    inboxOutboxEventUpdateHelperService
                        .updateOutboxEvent(eventId ,OutboxStatus.PUBLISHED_TO_DOWNSTREAM ,
                            null)
                }catch (ex : Exception){
                    ex.printStackTrace()
                    inboxOutboxEventUpdateHelperService
                        .updateOutboxEvent(eventId ,OutboxStatus.DOWNSTREAM_PUBLISH_FAILED ,
                            null)
                }

            }
        }else{
            println("no downstream publishing due to enrichiment failure")
        }

    }
}