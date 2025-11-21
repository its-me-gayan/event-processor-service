package com.example.eps.message.consumer

import com.example.eps.model.dto.IncomingEventMessage
import com.example.eps.respository.OutboxRepository
import com.example.eps.service.EventEnrichOutboxService
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/21/25
 * Time: 12:27 AM
 */
@Component
class KafkaConsumer(
    private val eventEnrichOutboxService: EventEnrichOutboxService,
    private val outboxRepository: OutboxRepository
) : MessageConsumer{
    private val logger = KotlinLogging.logger {}
    private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)


    @KafkaListener(topics = ["\${kafka.topic.incoming:topic-events-raw}"] ,groupId ="event-processor-test")
    override fun listen(record: ConsumerRecord<String, String>) {
        logger.info { "Message consumed key : ${record.key()} , partition: ${record.partition()} , Offset: ${record.offset()}" }
        val incomingEventMessage: IncomingEventMessage = mapper.readValue(record.value())
        if(!outboxRepository.findByOriginalEventId(incomingEventMessage.eventId).isPresent){
            eventEnrichOutboxService.submitForEnrichment(incomingEventMessage,record.value())
        }else{
            logger.info { "Event ${incomingEventMessage.eventId} already processed, skipping" }
        }

    }

}