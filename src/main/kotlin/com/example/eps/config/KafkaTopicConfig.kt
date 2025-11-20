package com.example.eps.config


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/20/25
 * Time: 12:58 AM
 */
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaTopicConfig {

    @Value("\${kafka.topic.incoming}")
    private lateinit var incomingTopicName: String

    @Value("\${kafka.topic.enriched}")
    private lateinit var enrichedTopicName: String

    @Bean
    fun incomingTopic(): NewTopic {
        return NewTopic(incomingTopicName, 1, 1) // partitions=3, replication=1
    }

    @Bean
    fun enrichedTopic(): NewTopic {
        return NewTopic(enrichedTopicName, 1, 1)
    }
}