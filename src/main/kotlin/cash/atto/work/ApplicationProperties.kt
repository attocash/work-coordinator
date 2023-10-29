package cash.atto.work

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "atto")
class ApplicationProperties {
    var callback: cash.atto.work.Callback = cash.atto.work.Callback(connectTimeout = 1_000, readTimeout = 1_000)
}

data class Callback(val connectTimeout: Long, val readTimeout: Long)