package cash.atto.work.coordinator

import cash.atto.commons.*
import cash.atto.work.Day
import cash.atto.work.PropertyHolder
import io.cucumber.java.en.Given
import kotlin.random.Random

class BlockStepDefinition {

    @Given("{day}'s block {word}")
    fun createBlock(day: Day, shortKey: String) {
        val publicKey = AttoPublicKey(Random.Default.nextBytes(ByteArray(32)))
        val block = AttoOpenBlock(
            version = 0u,
            algorithm = AttoAlgorithm.V1,
            publicKey = publicKey,
            balance = AttoAmount.MAX,
            timestamp = day.getInstant(),
            sendHashAlgorithm = AttoAlgorithm.V1,
            sendHash = AttoHash(ByteArray(32)),
            representative = publicKey,
        )
        PropertyHolder.add(shortKey, block)

    }
}