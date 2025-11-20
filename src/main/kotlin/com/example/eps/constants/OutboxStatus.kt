package com.example.eps.constants


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/19/25
 * Time: 10:07 PM
 */
enum class OutboxStatus {
    ENRICHING,
    ENRICHED,
    ENRICHMENT_FAILED,
    PUBLISHED_TO_DOWNSTREAM,
    DOWNSTREAM_PUBLISH_FAILED,

}