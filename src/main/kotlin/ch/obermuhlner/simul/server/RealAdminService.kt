package ch.obermuhlner.simul.server

import ch.obermuhlner.simul.shared.service.AdminService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.concurrent.thread


@RestController
class RealAdminService : AdminService {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/admin/shutdown")
    override fun shutdown() {
        logger.info("Shutdown initiated")
        thread {
            Thread.sleep(100)
            logger.info("Shutdown now")
            System.exit(0)
        }
    }
}