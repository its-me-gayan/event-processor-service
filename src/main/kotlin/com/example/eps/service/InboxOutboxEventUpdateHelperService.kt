package com.example.eps.service

import com.example.eps.constants.InboxStatus
import com.example.eps.constants.OutboxStatus
import java.util.UUID


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/18/25
 * Time: 11:33 PM
 */
interface InboxOutboxEventUpdateHelperService {
    fun updateInboxEvent(id: UUID, status: InboxStatus, isPublished: Boolean, error: String?)
    fun updateOutboxEvent(id: UUID, status: OutboxStatus, payload: String?)
}