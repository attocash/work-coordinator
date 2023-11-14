package cash.atto.work.processor.pubsub


import cash.atto.work.WorkGenerated
import cash.atto.work.WorkRequested
import com.google.cloud.spring.pubsub.core.PubSubTemplate
import com.google.cloud.spring.pubsub.support.converter.ConvertedBasicAcknowledgeablePubsubMessage
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.function.Consumer


@Component
@Profile(value = ["pubsub", "test"])
class PubSubProcessor(
    private val properties: PubSubProperties,
    private val pubSubTemplate: PubSubTemplate,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val logger = KotlinLogging.logger {}

    @PostConstruct
    fun init() {
        val consumer = Consumer<ConvertedBasicAcknowledgeablePubsubMessage<WorkGenerated>> {
            Thread.startVirtualThread {
                val workGenerated = it.payload
                try {
                    applicationEventPublisher.publishEvent(workGenerated)
                    it.ack()
                } catch (e: Exception) {
                    logger.error(e) { "Callback failed for $workGenerated" }
                }
            }
        }


        pubSubTemplate
            .subscribeAndConvert(properties.workGeneratedSubscription!!, consumer, WorkGenerated::class.java)
            .awaitRunning()
    }

    @EventListener
    fun send(requestedEvent: WorkRequested) {
        pubSubTemplate.publish(properties.workRequestedTopic!!, requestedEvent)
    }
}