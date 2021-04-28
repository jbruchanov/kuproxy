package test.junit

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.slf4j.LoggerFactory

class SilentLogsExtension :
    BeforeAllCallback, AfterAllCallback {

    private val loggers = listOf("io.mockk", "ktor", "io.netty")
    private fun logger(key: String) = (LoggerFactory.getLogger(key) as Logger)

    override fun beforeAll(context: ExtensionContext) {
        loggers.forEach {
            logger(it).level = Level.OFF
        }
    }

    override fun afterAll(context: ExtensionContext) {
        loggers.forEach {
            logger(it).level = Level.DEBUG
        }
    }
}
