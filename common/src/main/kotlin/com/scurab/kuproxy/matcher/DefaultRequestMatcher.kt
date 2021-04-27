package com.scurab.kuproxy.matcher

import com.scurab.kuproxy.comm.Headers
import com.scurab.kuproxy.comm.IRequest

/**
 * Default request matcher.
 * URL must match per [Url.equals], Method must match exactly,
 * all stored headers must be included & be equal to real headers
 */
class DefaultRequestMatcher : RequestMatcher {

    override fun isMatching(real: IRequest, stored: IRequest): Boolean {
        return real.url == stored.url &&
            real.method == stored.method &&
            isMatchingHeaders(real.headers, stored.headers)
    }

    private fun isMatchingHeaders(real: Headers, stored: Headers): Boolean {
        return stored.all { (storedKey, storedValues) ->
            val realValues = real[storedKey] ?: return false
            realValues == storedValues
        }
    }
}
