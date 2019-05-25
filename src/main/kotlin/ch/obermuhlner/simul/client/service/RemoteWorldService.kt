package ch.obermuhlner.simul.client.service

import ch.obermuhlner.simul.shared.domain.*
import ch.obermuhlner.simul.shared.service.WorldService
import com.github.kittinunf.fuel.*
import com.google.gson.Gson
import org.slf4j.LoggerFactory

class RemoteWorldService(
        private val host: String = "localhost",
        private val port: Int = 8080) : WorldService {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun allCountries(): List<Country> {
        val (request, response, result) = "http://$host:$port/countries"
                .httpGet()
                .responseObject(Country.ListDeserializer())
        logger.trace("Request: {}", request)
        logger.trace("Response: {}", response)
        logger.trace("Result: {}", result)
        return result.get()
    }

    override fun country(countryId: Int): Country {
        val (request, response, result) = "http://$host:$port/country/$countryId"
                .httpGet()
                .responseObject(Country.Deserializer())
        logger.trace("Request: {}", request)
        logger.trace("Response: {}", response)
        logger.trace("Result: {}", result)
        return result.get()
    }

    override fun allRegions(): List<Region> {
        val (request, response, result) = "http://$host:$port/regions"
                .httpGet()
                .responseObject(Region.ListDeserializer())
        logger.trace("Request: {}", request)
        logger.trace("Response: {}", response)
        logger.trace("Result: {}", result)
        return result.get()
    }

    override fun countryRegions(countryId: Int): List<Region> {
        val (request, response, result) = "http://$host:$port/country/$countryId/regions"
                .httpGet()
                .responseObject(Region.ListDeserializer())
        logger.trace("Request: {}", request)
        logger.trace("Response: {}", response)
        logger.trace("Result: {}", result)
        return result.get()
    }

    override fun region(regionId: Int): Region {
        val (request, response, result) = "http://$host:$port/region/$regionId"
                .httpGet()
                .responseObject(Region.Deserializer())
        logger.trace("Request: {}", request)
        logger.trace("Response: {}", response)
        logger.trace("Result: {}", result)
        return result.get()
    }

    override fun action(action: DeclareWarAction) {
        val (request, response, result) = "http://$host:$port/action/declarewar/${action.actorCountryId}/${action.otherCountryId}"
                .httpGet()
                .response()
        logger.trace("Request: {}", request)
        logger.trace("Response: {}", response)
        logger.trace("Result: {}", result)
    }

    override fun action(action: ProposePeaceAction) {
        val (request, response, result) = "http://$host:$port/action/proposepeace/${action.actorCountryId}/${action.otherCountryId}"
                .httpGet()
                .response()
        logger.trace("Request: {}", request)
        logger.trace("Response: {}", response)
        logger.trace("Result: {}", result)
    }

    override fun action(action: AcceptPeaceAction) {
        val (request, response, result) = "http://$host:$port/action/acceptpeace/${action.actorCountryId}/${action.otherCountryId}"
                .httpGet()
                .response()
        logger.trace("Request: {}", request)
        logger.trace("Response: {}", response)
        logger.trace("Result: {}", result)
    }

    override fun action(action: TaxAgricultureAction) {
        val (request, response, result) = "http://$host:$port/action/taxagriculture/${action.countryId}/${action.taxAgriculture}"
                .httpGet()
                .response()
        logger.trace("Request: {}", request)
        logger.trace("Response: {}", response)
        logger.trace("Result: {}", result)
    }

    override fun simulate(): Simulation {
        val (request, response, result) = "http://localhost:8080/simulate"
                .httpGet()
                .responseObject(Simulation.Deserializer())
        logger.trace("Request: {}", request)
        logger.trace("Response: {}", response)
        logger.trace("Result: {}", result)
        return result.get()
    }
}