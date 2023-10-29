package cash.atto.work.coordinator

import cash.atto.commons.AttoNetwork
import cash.atto.commons.AttoOpenBlock
import cash.atto.commons.AttoWork
import cash.atto.work.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.google.cloud.spring.pubsub.core.PubSubTemplate
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import junit.framework.TestCase.assertEquals
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.env.Environment


class WorkStepDefinition(
    val pubSubTemplate: PubSubTemplate,
    val testRestTemplate: TestRestTemplate,
    val environment: Environment,
    val objectMapper: ObjectMapper,
) {

    @When("work is requested")
    fun request() {
        val block = PropertyHolder.get(AttoOpenBlock::class.java)

        stubFor(
            get(urlEqualTo("/callback")).willReturn(
                aResponse().withStatus(200)
            )
        )
        val wiremockPort = environment.getRequiredProperty("wiremock.server.port")
        val request = WorkRequest(AttoNetwork.LOCAL, block.timestamp, "http://localhost:${wiremockPort}/callback")
        testRestTemplate.postForLocation("/works/${block.hash}", request)
    }


    @When("work is generated")
    fun generate() {
        val shortKey = PropertyHolder.getActiveKey(AttoOpenBlock::class.java)!!
        val block = PropertyHolder.get(AttoOpenBlock::class.java, shortKey)
        val hash = block.hash.toString()
        val workRequested = Waiter.waitUntilNonNull {
            pubSubTemplate.pullAndConvertAsync(
                CucumberConfiguration.workRequestedSubscription,
                Integer.MAX_VALUE,
                true,
                WorkRequested::class.java
            ).get()
                .map {
                    it.ack()
                    it.payload
                }.firstOrNull { it.hash == hash }
        }!!
        val workGenerated = WorkGenerated(
            workRequested.callbackUrl,
            workRequested.hash,
            workRequested.threshold,
            AttoWork.work(workRequested.threshold, workRequested.hash.toByteArray()).toString()
        )
        PropertyHolder.add(shortKey, workGenerated)
        pubSubTemplate.publish(CucumberConfiguration.workGeneratedTopic, workGenerated)
    }

    @Then("work is sent to client")
    fun verifyCallback() {
        val workGenerated = PropertyHolder.get(WorkGenerated::class.java)

        val request = Waiter.waitUntilNonNull {
            findAll(postRequestedFor(urlEqualTo("/callback")))
                .map { it.bodyAsString }
                .map { objectMapper.readValue(it, CallbackRequest::class.java) }
                .firstOrNull { it.hash == workGenerated.hash }
        }!!

        assertEquals(workGenerated.work, request.work)
    }
}