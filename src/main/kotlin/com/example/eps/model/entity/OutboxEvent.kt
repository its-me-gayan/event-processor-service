package com.example.eps.model.entity

import com.example.eps.constants.InboxStatus
import com.example.eps.constants.OutboxStatus
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/18/25
 * Time: 10:35 PM
 */
@Entity
@Table(name = "outbox_event")
data class OutboxEvent(

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "original_event_id", nullable = false)
    val originalEventId: UUID,

    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    var payload: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    var status: OutboxStatus = OutboxStatus.ENRICHING,

    @Column(name = "published", nullable = false)
    var published: Boolean = false,

    @Column(name = "published_at")
    var publishedAt: Instant? = null,

    @Column(name = "error", columnDefinition = "text")
    var error: String? = null
) {

    companion object {
        fun from(
            originalEventId: UUID,
            payload: String,
            error: String? = null
        ): OutboxEvent =
            OutboxEvent(
                originalEventId = originalEventId,
                payload = payload,
                error = error
            )
    }
}