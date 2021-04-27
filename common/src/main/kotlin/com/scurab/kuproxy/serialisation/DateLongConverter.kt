package com.scurab.kuproxy.serialisation

import java.text.SimpleDateFormat

open class DateLongConverter(
    private val converter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
) {
    open fun convert(item: Long): String = converter.format(item)
    open fun convert(item: String): Long = converter.parse(item).time
}
