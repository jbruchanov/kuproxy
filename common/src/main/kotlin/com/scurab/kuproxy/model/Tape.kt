package com.scurab.kuproxy.model

import com.scurab.kuproxy.storage.RequestResponse

class Tape(
    val name: String,
    val interactions: List<RequestResponse>
)
