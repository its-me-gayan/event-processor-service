package com.example.eps.message.publisher

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/18/25
 * Time: 10:40 PM
 */
@Component
class KafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${kafka.topic.incoming:topic-events-raw}") private val incomingTopic: String,
    @Value("\${kafka.topic.enriched:topic-events-enriched}") private val enrichedTopic: String
): MessagePublisher{
    private val logger = KotlinLogging.logger {}

    override fun publishIncomingMessage(key: String, payload: String) {
        kafkaTemplate.send(incomingTopic , payload)
        logger.info { "Raw Message published to -  $incomingTopic" }
    }

    override fun publishEnrichedMessage(key: String, payload: String) {
        kafkaTemplate.send(enrichedTopic , payload)
        logger.info { "Enriched Message published to -  $enrichedTopic" }
    }
}