package cash.atto.work

import cash.atto.commons.serialiazers.json.AttoJson
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class ApplicationConfiguration {

    @Bean
    fun kotlinSerializationJsonHttpMessageConverter(): HttpMessageConverter<*> {
        return KotlinSerializationJsonHttpMessageConverter(AttoJson)
    }

    @Bean
    fun restTemplate(
        applicationProperties: ApplicationProperties,
        builder: RestTemplateBuilder
    ): RestTemplate {
        return builder
            .setConnectTimeout(Duration.ofMillis(applicationProperties.callback.connectTimeout))
            .setReadTimeout(Duration.ofMillis(applicationProperties.callback.readTimeout))
            .build()
    }

}