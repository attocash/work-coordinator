package cash.atto.work

import com.google.api.gax.core.CredentialsProvider
import com.google.api.gax.core.NoCredentialsProvider
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean


@TestConfiguration
class TestApplicationConfiguration {

    @Bean
    fun googleCredentials(): CredentialsProvider {
        return NoCredentialsProvider.create()
    }

}