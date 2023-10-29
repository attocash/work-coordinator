package cash.atto.work.coordinator

import cash.atto.commons.AttoAmount
import cash.atto.commons.AttoHash
import cash.atto.commons.AttoOpenBlock
import cash.atto.commons.AttoPublicKey
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
            publicKey = publicKey,
            balance = AttoAmount.MAX,
            timestamp = day.getInstant(),
            sendHash = AttoHash(ByteArray(32)),
            representative = publicKey,
        )
        PropertyHolder.add(shortKey, block)

    }
}