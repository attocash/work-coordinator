package cash.atto.work

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class ApplicationConfiguration {
    @Bean
    fun restTemplate(
        applicationProperties: cash.atto.work.ApplicationProperties,
        builder: RestTemplateBuilder
    ): RestTemplate {
        return builder
            .setConnectTimeout(Duration.ofMillis(applicationProperties.callback.readTimeout))
            .setReadTimeout(Duration.ofMillis(applicationProperties.callback.connectTimeout))
            .build()
    }

}