//package com.example.eps.config
//
//import org.apache.kafka.clients.consumer.ConsumerConfig
//import org.apache.kafka.clients.producer.ProducerConfig
//import org.apache.kafka.common.serialization.StringDeserializer
//import org.apache.kafka.common.serialization.StringSerializer
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
//import org.springframework.kafka.core.ConsumerFactory
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory
//import org.springframework.kafka.core.DefaultKafkaProducerFactory
//import org.springframework.kafka.core.KafkaTemplate
//import org.springframework.kafka.core.ProducerFactory
//
//
///**
// * Author: Gayan Sanjeewa
// * User: gayan
// * Date: 11/18/25
// * Time: 10:30 PM
// */
//
//@Configuration
//class KafkaConfig (
//    @Value("\${kafka.bootstrap-servers:localhost:9092}") private val bootstrapServers: String
//){
//    @Bean
//    fun producerFactory(): ProducerFactory<String, String> {
//        val props: MutableMap<String, Any> = HashMap()
//        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
//        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
//        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
//        // safe producer settings could be added here
//        return DefaultKafkaProducerFactory(props)
//    }
//
//    @Bean
//    fun kafkaTemplate(): KafkaTemplate<String, String> = KafkaTemplate(producerFactory())
//
//    @Bean
//    fun consumerFactory(): ConsumerFactory<String, String> {
//        val props: MutableMap<String, Any> = HashMap()
//        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
//        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
//        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
//        props[ConsumerConfig.GROUP_ID_CONFIG] = "event-processor-group"
//        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
//        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "true"
//        return DefaultKafkaConsumerFactory(props)
//    }
//
//    @Bean
//    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
//        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
//        factory.consumerFactory = consumerFactory()
//        return factory
//    }
//}