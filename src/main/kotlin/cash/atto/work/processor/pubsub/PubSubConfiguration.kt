package cash.atto.work.processor.pubsub

import cash.atto.commons.serialiazers.json.AttoJson
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter
import com.google.protobuf.ByteString
import com.google.pubsub.v1.PubsubMessage
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
@Profile(value = ["pubsub", "test"])
class PubSubConfiguration {
    @Bean
    fun pubSubMessageConverter(): PubSubMessageConverter {
        return object : PubSubMessageConverter {
            override fun toPubSubMessage(payload: Any, headers: MutableMap<String, String>?): PubsubMessage {
                val serializer = AttoJson.serializersModule.serializer(payload::class.java)
                val byteArray = AttoJson.encodeToString(serializer, payload).toByteArray(Charsets.UTF_8)
                return this.byteStringToPubSubMessage(ByteString.copyFrom(byteArray), headers)
            }

            @Suppress("UNCHECKED_CAST")
            override fun <T : Any?> fromPubSubMessage(message: PubsubMessage, payloadType: Class<T>): T {
                val serializer = AttoJson.serializersModule.serializer(payloadType) as KSerializer<T>
                return AttoJson.decodeFromString(serializer, message.data.toByteArray().toString(Charsets.UTF_8))
            }
        }
    }

}