package cash.atto.work.coordinator

import cash.atto.commons.AttoNetwork
import cash.atto.commons.AttoWork
import cash.atto.work.WorkGenerated
import cash.atto.work.WorkRequested
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.Instant

data class CallbackRequest(val hash: String, val work: String)

@Service
class WorkCoordinator(private val publisher: ApplicationEventPublisher, private val restTemplate: RestTemplate) {
    fun request(network: AttoNetwork, hash: String, timestamp: Instant, callback: String) {
        if (timestamp > Instant.now()) {
            throw IllegalArgumentException("Timestamp $timestamp is in the future")
        }
        val threshold = AttoWork.threshold(network, timestamp)
        val event = WorkRequested(callback, hash, threshold)
        publisher.publishEvent(event)
    }

    @EventListener
    fun process(event: WorkGenerated) {
        callback(event.callbackUrl, event.hash, event.work)
    }

    private fun callback(callback: String, hash: String, work: String) {
        val request = CallbackRequest(hash, work)
        restTemplate.postForLocation(callback, request)
    }

}