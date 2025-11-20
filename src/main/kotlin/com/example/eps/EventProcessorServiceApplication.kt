package com.example.eps

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableTransactionManagement
@SpringBootApplication
@EnableKafka
class EventProcessorServiceApplication

fun main(args: Array<String>) {
	runApplication<EventProcessorServiceApplication>(*args)
}
