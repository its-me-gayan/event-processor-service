package com.example.eps.service

import com.example.eps.model.dto.IncomingEventMessage


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/21/25
 * Time: 12:30 AM
 */
interface EventEnrichOutboxService {
    fun submitForEnrichment(event : IncomingEventMessage , payload:String)
}