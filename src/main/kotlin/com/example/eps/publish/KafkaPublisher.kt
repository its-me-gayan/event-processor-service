package com.example.eps.publish

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
    @Value("\${kafka.topic.incoming:events-raw}") private val incomingTopic: String,
    @Value("\${kafka.topic.enriched:events-enriched}") private val enrichedTopic: String
): MessagePublisher{
    override fun publishIncomingMessage(key: String, payload: String) {
        kafkaTemplate.send(incomingTopic , key , payload)
        println("message published to raw event topic")
    }

    override fun publishEnrichedMessage(key: String, payload: String) {
        kafkaTemplate.send(incomingTopic , key , payload)
        println("message published to enriched event topic")
    }
}