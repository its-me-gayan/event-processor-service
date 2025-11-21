package com.example.eps.message.publisher


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/19/25
 * Time: 12:24 AM
 */
interface MessagePublisher {
    fun publishIncomingMessage(key: String , payload: String);
    fun publishEnrichedMessage(key: String , payload: String);
}