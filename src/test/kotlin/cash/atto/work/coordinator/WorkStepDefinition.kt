package cash.atto.work.coordinator

import cash.atto.commons.AttoNetwork
import cash.atto.commons.AttoOpenBlock
import cash.atto.commons.AttoWork
import cash.atto.work.*
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.google.cloud.spring.pubsub.core.PubSubTemplate
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.env.Environment


class WorkStepDefinition(
    val pubSubTemplate: PubSubTemplate,
    val testRestTemplate: TestRestTemplate,
    val environment: Environment,
) {

    @When("work is requested")
    fun request() {
        val block = PropertyHolder.get(AttoOpenBlock::class.java)

        stubFor(
            post(urlEqualTo("/callback")).willReturn(
                aResponse().withStatus(200)
            )
        )
        val wiremockPort = environment.getRequiredProperty("wiremock.server.port")
        val request = WorkRequest(AttoNetwork.LOCAL, block.timestamp, "http://localhost:${wiremockPort}/${block.hash}/callback")
        val response = testRestTemplate.postForEntity("/works/${block.hash}", request, String::class.java)
        assertTrue(response.body, response.statusCode.is2xxSuccessful)
    }


    @When("work is generated")
    fun generate() {
        val shortKey = PropertyHolder.getActiveKey(AttoOpenBlock::class.java)!!
        val block = PropertyHolder.get(AttoOpenBlock::class.java, shortKey)
        val hash = block.hash
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
            AttoWork.work(workRequested.threshold, workRequested.hash.value)
        )
        PropertyHolder.add(shortKey, workGenerated)
        pubSubTemplate.publish(CucumberConfiguration.workGeneratedTopic, workGenerated)
    }

    @Then("work is sent to client")
    fun verifyCallback() {
        val workGenerated = PropertyHolder.get(WorkGenerated::class.java)

        val work = Waiter.waitUntilNonNull {
            findAll(postRequestedFor(urlEqualTo("/${workGenerated.hash}/callback")))
                .map { it.bodyAsString }
                .firstOrNull()
        }!!

        assertEquals(workGenerated.work, AttoWork.parse(work))
    }
}