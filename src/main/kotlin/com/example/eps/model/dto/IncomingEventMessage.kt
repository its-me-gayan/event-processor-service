package com.example.eps.model.dto

import java.time.Instant
import java.util.UUID


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/19/25
 * Time: 10:21 PM
 */
data class IncomingEventMessage (
    val eventId: UUID,
    val userId: String,
    val countryCode: String,
    val timestamp: Instant
){

    companion object {
        fun from(
            eventId: UUID,
            userId: String,
            countryCode: String,
            ts: Instant
        ): IncomingEventMessage =
            IncomingEventMessage(
                eventId = eventId,
                userId = userId,
                countryCode = countryCode,
                timestamp = ts
            )
    }
}