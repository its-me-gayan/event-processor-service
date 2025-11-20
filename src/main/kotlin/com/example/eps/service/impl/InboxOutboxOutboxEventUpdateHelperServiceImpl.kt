package com.example.eps.service.impl

import com.example.eps.constants.InboxStatus
import com.example.eps.constants.OutboxStatus
import com.example.eps.exception.ApplicationException
import com.example.eps.respository.InboxRepository
import com.example.eps.respository.OutboxRepository
import com.example.eps.service.InboxOutboxEventUpdateHelperService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/19/25
 * Time: 10:56 PM
 */
@Service
class InboxOutboxOutboxEventUpdateHelperServiceImpl(
    private val inboxRepository: InboxRepository,
    private val outboxRepository: OutboxRepository
): InboxOutboxEventUpdateHelperService {

    @Transactional(Transactional.TxType.REQUIRES_NEW)
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

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    override fun updateOutboxEvent(
        id: UUID,
        status: OutboxStatus,
        payload: String?
    ) {
        val outboxEvent = outboxRepository.findById(id).orElseThrow()
        outboxEvent.updatedAt = Instant.now()
        if (payload != null) {
            outboxEvent.payload =payload
        }
        outboxEvent.status = status
        try {
            outboxRepository.save(outboxEvent)
        }catch (ex:Exception){
            throw ApplicationException(ex.message,ex.cause)
        }

    }

}