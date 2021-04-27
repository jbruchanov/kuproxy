package test.junit

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.slf4j.LoggerFactory

class SilentLogsExtension :
    BeforeAllCallback, AfterAllCallback {

    private var initLevel: Level? = null
    private val logger get() = (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger)

    override fun beforeAll(context: ExtensionContext) {
        initLevel = logger.level
        logger.level = Level.OFF
    }

    override fun afterAll(context: ExtensionContext) {
        initLevel?.let { logger.level = it }
    }
}
