package cash.atto.work.processor.pubsub

import org.springframework.boot.test.context.TestConfiguration


@TestConfiguration(proxyBeanMethods = false)
class TestPubSubConfiguration {
    // Waiting for https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/1780
//    @Bean
//    @ServiceConnection(name = "pubsub")
////    @ServiceConnection(name = "pubsub", type = [PubSubConnectionDetails::class])
//    fun pubSubEmulatorContainer(registry: DynamicPropertyRegistry): PubSubEmulatorContainer {
//        val container = PubSubEmulatorContainer("gcr.io/google.com/cloudsdktool/cloud-sdk:emulators")
//        registry.add("spring.cloud.gcp.pubsub.emulator-host") {
//            println("felipe")
//            container.emulatorEndpoint
//        }
//        return container
//    }
//
//    @DynamicPropertySource
//    fun emulatorProperties(registry: DynamicPropertyRegistry, container: PubSubEmulatorContainer) {
//        registry.add("spring.cloud.gcp.pubsub.emulator-host") {
//            println("felipe")
//            container.emulatorEndpoint
//        }
//    }

//    @Bean
//    fun pubSubConnectionDetails(pubSubEmulatorContainer : PubSubEmulatorContainer): PubSubConnectionDetails {
//        return object : PubSubConnectionDetails {
//            override fun emulatorEndpoint(): String {
//               return pubSubEmulatorContainer.emulatorEndpoint
//            }
//        }
//    }

}

//class PubSubContainerConnectionDetailsFactory : ContainerConnectionDetailsFactory<PubSubEmulatorContainer, PubSubConnectionDetails>("pubsub") {
//    override fun getContainerConnectionDetails(source: ContainerConnectionSource<PubSubEmulatorContainer>): PubSubConnectionDetails {
//        return object : PubSubConnectionDetails {
//            override fun emulatorEndpoint(): String {
//                return source.getContainerSupplier().get().emulatorEndpoint
//            }
//        }
//    }
//
//}
//interface PubSubConnectionDetails : ConnectionDetails {
//    fun emulatorEndpoint(): String
//}