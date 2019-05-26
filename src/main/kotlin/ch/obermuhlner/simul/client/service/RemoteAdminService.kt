package ch.obermuhlner.simul.client.service

import ch.obermuhlner.simul.shared.domain.Country
import ch.obermuhlner.simul.shared.service.AdminService
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpGet
import org.slf4j.LoggerFactory

class RemoteAdminService(
        private val host: String = "localhost",
        private val port: Int = 8080) : AdminService {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun shutdown() {
        val url = "http://$host:$port/admin/shutdown"
        logger.debug("URL: {}", url)
        val (request, response, result) = url
                .httpGet()
                .response()
        if (!response.isSuccessful) {
            logger.warn("Request: {}", request)
            logger.warn("Response: {}", response)
            logger.warn("Result: {}", result)
        }
    }
}