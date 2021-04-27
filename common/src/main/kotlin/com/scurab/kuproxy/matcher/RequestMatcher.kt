package com.scurab.kuproxy.matcher

import com.scurab.kuproxy.comm.IRequest

interface RequestMatcher {
    fun isMatching(real: IRequest, stored: IRequest): Boolean
}
