package cash.atto.work

import cash.atto.work.processor.pubsub.PubSubProcessor
import cash.atto.work.processor.pubsub.PubSubProperties
import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.grpc.GrpcTransportChannel
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.api.gax.rpc.TransportChannelProvider
import com.google.cloud.pubsub.v1.SubscriptionAdminClient
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings
import com.google.cloud.pubsub.v1.TopicAdminClient
import com.google.cloud.pubsub.v1.TopicAdminSettings
import com.google.cloud.spring.pubsub.PubSubAdmin
import com.google.cloud.spring.pubsub.core.subscriber.PubSubSubscriberTemplate
import io.cucumber.java.Before
import io.cucumber.spring.CucumberContextConfiguration
import io.grpc.ManagedChannelBuilder
import jakarta.annotation.PostConstruct
import org.awaitility.Awaitility.await
import org.hamcrest.Matchers.hasSize
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PubSubEmulatorContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@CucumberContextConfiguration
@Testcontainers
@ActiveProfiles(value = ["test"])
class CucumberConfiguration(
    val pubSubProperties: PubSubProperties,
    val subscriberTemplate: PubSubSubscriberTemplate
) {

    companion object {
        val workGeneratedTopic = "work-generated-topic"
        val workRequestedSubscription = "work-requested-subscription"

        @JvmStatic
        @Container
        val pubsubEmulator = PubSubEmulatorContainer(
            DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:emulators")
        ).apply {
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun emulatorProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.cloud.gcp.pubsub.emulator-host") { pubsubEmulator.emulatorEndpoint }
        }

        @AutoConfigureBefore(PubSubProcessor::class)
        @Configuration
        class PubSubInitializer(val environment: Environment, val pubSubProperties: PubSubProperties) {
            @PostConstruct
            fun init() {
                val channel = ManagedChannelBuilder.forTarget("dns:///" + pubsubEmulator.emulatorEndpoint)
                    .usePlaintext()
                    .build()
                val channelProvider: TransportChannelProvider =
                    FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel))
                val topicAdminClient = TopicAdminClient.create(
                    TopicAdminSettings.newBuilder()
                        .setCredentialsProvider(NoCredentialsProvider.create())
                        .setTransportChannelProvider(channelProvider)
                        .build()
                )
                val subscriptionAdminClient = SubscriptionAdminClient.create(
                    SubscriptionAdminSettings.newBuilder()
                        .setTransportChannelProvider(channelProvider)
                        .setCredentialsProvider(NoCredentialsProvider.create())
                        .build()
                )
                val admin = PubSubAdmin(
                    { environment.getRequiredProperty("spring.cloud.gcp.project-id") },
                    topicAdminClient,
                    subscriptionAdminClient
                )
                admin.createTopic(workGeneratedTopic)
                admin.createSubscription(pubSubProperties.workGeneratedSubscription, workGeneratedTopic)
                admin.createTopic(pubSubProperties.workRequestedTopic)
                admin.createSubscription(workRequestedSubscription, pubSubProperties.workRequestedTopic)
                admin.close()
                channel.shutdown()
            }
        }
    }

    @Before
    fun teardown() {
        await().until(
            { subscriberTemplate.pullAndAck(pubSubProperties.workGeneratedSubscription, Integer.MAX_VALUE, true) },
            hasSize(0)
        )

        await().until(
            { subscriberTemplate.pullAndAck(workRequestedSubscription, Integer.MAX_VALUE, true) },
            hasSize(0)
        )
    }

}