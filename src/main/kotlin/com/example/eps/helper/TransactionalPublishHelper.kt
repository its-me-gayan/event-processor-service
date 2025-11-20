package com.example.eps.helper

import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationAdapter
import org.springframework.transaction.support.TransactionSynchronizationManager


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/19/25
 * Time: 10:26 PM
 */
object TransactionalPublishHelper {

    fun registerAfterCommit(runnable: () -> Unit) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronizationAdapter(),
                TransactionSynchronization {
                override fun afterCommit() {
                    runnable()
                }
            })
        } else {
            // if not in transaction, just run
            runnable()
        }
    }

}