package com.example.eps.service.impl

import com.example.eps.constants.InboxStatus
import com.example.eps.constants.OutboxStatus
import com.example.eps.exception.ApplicationException
import com.example.eps.model.entity.OutboxEvent
import com.example.eps.respository.InboxRepository
import com.example.eps.respository.OutboxRepository
import com.example.eps.service.InboxOutboxEventUpdateHelperService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/19/25
 * Time: 10:56 PM
 */
@Service
class InboxOutboxEventUpdateHelperServiceImpl(
    private val inboxRepository: InboxRepository,
    private val outboxRepository: OutboxRepository
): InboxOutboxEventUpdateHelperService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun updateInboxEvent(id: UUID, status: InboxStatus, isPublished: Boolean, error: String?) {
        val inboxEvent = inboxRepository.findById(id).orElseThrow();
        inboxEvent.updatedAt = Instant.now()
        if(isPublished) {
            inboxEvent.publishedAt = Instant.now()
            inboxEvent.published = true
        }
        inboxEvent.status = status
        inboxEvent.error = error
        try {
            inboxRepository.save(inboxEvent)
        }catch (ex:Exception){
            throw ApplicationException(ex.message,ex.cause)
        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun updateOutboxEvent(
        id: UUID,
        status: OutboxStatus,
        payload: String?,
        error: String?,
    ) {
        val outboxEvent = outboxRepository.findByOriginalEventId(id).orElseThrow()
        outboxEvent.updatedAt = Instant.now()
        if (payload != null) {
            outboxEvent.payload =payload
        }
        outboxEvent.error =error
        outboxEvent.status = status
        try {
            outboxRepository.save(outboxEvent)
        }catch (ex:Exception){
            throw ApplicationException(ex.message,ex.cause)
        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun saveOutboxEvent(outboxEvent: OutboxEvent): OutboxEvent {
        val save = outboxRepository.save(outboxEvent)
        outboxRepository.flush()
        return save;
    }

}