package com.example.eps.message.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/21/25
 * Time: 12:34 AM
 */
interface MessageConsumer {
    fun listen(record: ConsumerRecord<String, String>)
}