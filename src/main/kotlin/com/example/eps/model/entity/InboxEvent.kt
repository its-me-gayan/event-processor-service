package com.example.eps.model.entity

import com.example.eps.constants.InboxStatus
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
@Table(name = "inbox_event")
data class InboxEvent(

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "country_code", nullable = false, length = 2)
    val countryCode: String,

    @Column(name = "event_ts", nullable = false)
    val eventTimestamp: Instant,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    var status: InboxStatus = InboxStatus.RECEIVED,

    @Column(name = "published", nullable = false)
    var published: Boolean = false,

    @Column(name = "published_at")
    var publishedAt: Instant? = null,

    @Column(name = "error", columnDefinition = "text")
    var error: String? = null
) {

    companion object {
        fun from(
            userId: String,
            countryCode: String,
            ts: Instant
        ): InboxEvent =
            InboxEvent(
                userId = userId,
                countryCode = countryCode,
                eventTimestamp = ts
            )
    }
}