package com.scurab.kuproxy.util

/**
 * Convert pairs and filter out null values
 */
@Suppress("UNCHECKED_CAST")
fun <K, V> mapOfNotNullValues(vararg pairs: Pair<K, V?>): Map<K, V> {
    val filtered = pairs.filter { it.second != null } as List<Pair<K, V>>
    return if (filtered.isNotEmpty()) filtered.toMap() else emptyMap()
}