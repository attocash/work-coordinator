package cash.atto.work.processor.pubsub

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
@Profile(value = ["pubsub", "test"])
class PubSubConfiguration {
    @Bean
    fun jacksonPubSubMessageConverter(objectMapper: ObjectMapper): PubSubMessageConverter {
        return JacksonPubSubMessageConverter(objectMapper)
    }

}