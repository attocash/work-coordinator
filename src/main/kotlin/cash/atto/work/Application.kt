package cash.atto.work

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<cash.atto.work.Application>(*args)
        }
    }
}
