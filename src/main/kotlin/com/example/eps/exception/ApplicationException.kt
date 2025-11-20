package com.example.eps.exception

import java.lang.RuntimeException


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/19/25
 * Time: 11:03 PM
 */
class ApplicationException(
    message: String?,
    cause: Throwable? = null
) : RuntimeException(message, cause)