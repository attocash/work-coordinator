package cash.atto.work.processor.pubsub

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "atto.pubsub")
class PubSubProperties {
    var project: String? = null
    var workRequestedTopic: String? = null
    var workGeneratedSubscription: String? = null
}