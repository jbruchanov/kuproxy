package com.scurab.kuproxy.util

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class StringColorsTest {

    @Test
    @Disabled("manual")
    fun test() {
        StringColors.AnsiEffect.values().forEach {
            println(StringColors.apply(it.name, it))
        }
    }
}