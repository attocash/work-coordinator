package cash.atto.work.coordinator

import cash.atto.commons.AttoHash
import cash.atto.commons.AttoNetwork
import cash.atto.commons.fromHexToByteArray
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/works")
class WorkController(private val coordinator: WorkCoordinator, private val objectMapper: ObjectMapper) {
    @PostMapping("{hash}")
    @Operation(description = "Process request for work generation")
    suspend fun generate(@PathVariable hash: String, @RequestBody request: WorkRequest) {
        coordinator.request(request.network, AttoHash(hash.fromHexToByteArray()), request.timestamp, request.callback)
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun badRequest(request: HttpServletRequest, e: IllegalArgumentException): ResponseEntity<ObjectNode> {
        val response = objectMapper.createObjectNode()
        response.put("error", e.message)
        return ResponseEntity.badRequest().body(response)
    }
}

@Serializable
data class WorkRequest(val network: AttoNetwork, val timestamp: Instant, val callback: String)