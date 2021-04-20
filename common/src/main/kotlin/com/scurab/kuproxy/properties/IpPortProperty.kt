package com.scurab.kuproxy.properties

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun ipPort(default: Int) = IpPortProperty(default)

class IpPortProperty(private val default: Int) : ReadOnlyProperty<Any, Int>, ReadWriteProperty<Any, Int> {

    private var value = default

    override fun getValue(thisRef: Any, property: KProperty<*>): Int = value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        require(value in IpPortRange) { "Port must be in range of [$IpPortRange], was $value" }
        this.value = value
    }

    companion object {
        private val IpPortRange = 1..65535
    }
}
