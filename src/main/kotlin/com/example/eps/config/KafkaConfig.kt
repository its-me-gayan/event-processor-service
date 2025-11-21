package com.example.eps.config

import com.fasterxml.jackson.databind.JsonMappingException
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.FixedBackOff


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/18/25
 * Time: 10:30 PM
 */

@Configuration
class KafkaConfig (
    @Value("\${kafka.bootstrap-servers:localhost:9092}") private val bootstrapServers: String
){
    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, String>
    ): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory

        // Retry configuration
        val fixedBackOff = FixedBackOff(1000L, 3) // retry 3 times, 1s interval
        val errorHandler = DefaultErrorHandler(fixedBackOff)

        // Optional: Send failing message to a DLT
        errorHandler.addNotRetryableExceptions(JsonMappingException::class.java)
        factory.setCommonErrorHandler(errorHandler)

        return factory
    }

}