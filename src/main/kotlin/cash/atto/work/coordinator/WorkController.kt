package cash.atto.work.coordinator

import cash.atto.commons.AttoNetwork
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.Instant


@RestController
@RequestMapping("/works")
class WorkController(private val coordinator: WorkCoordinator, private val objectMapper: ObjectMapper) {
    @PostMapping("{hash}")
    @Operation(description = "Process request for work generation")
    suspend fun generate(@PathVariable hash: String, @RequestBody request: WorkRequest) {
        coordinator.request(request.network, hash, request.timestamp, request.callback)
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Data integrity violation")
    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(request: HttpServletRequest, e: IllegalArgumentException): ObjectNode {
        val response = objectMapper.createObjectNode()
        response.put("error", e.message)
        return response
    }
}

data class WorkRequest(val network: AttoNetwork, val timestamp: Instant, val callback: String)