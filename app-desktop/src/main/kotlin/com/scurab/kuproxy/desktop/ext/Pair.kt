package com.scurab.kuproxy.desktop.ext

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Pair<T, T>.firstOddElseSecond(index: Int): T = firstIfTrueElseSecond(index % 2 == 0)

fun <T> Pair<T, T>.firstIfTrueElseSecond(value: Boolean): T = if (value) first else second
