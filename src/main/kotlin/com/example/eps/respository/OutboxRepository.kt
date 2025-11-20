package com.example.eps.respository

import com.example.eps.model.entity.OutboxEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/18/25
 * Time: 10:31 PM
 */
@Repository
interface OutboxRepository : JpaRepository<OutboxEvent, UUID>{
}