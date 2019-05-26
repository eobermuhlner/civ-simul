package ch.obermuhlner.simul.client.service

import ch.obermuhlner.simul.shared.domain.*
import ch.obermuhlner.simul.shared.service.WorldService
import com.github.kittinunf.fuel.*
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.Gson
import org.slf4j.LoggerFactory

class RemoteWorldService(
        private val host: String = "localhost",
        private val port: Int = 8080) : WorldService {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun allCountries(): List<Country> {
        return requestObject(
                "http://$host:$port/world/countries",
                Country.ListDeserializer())
    }

    override fun country(countryId: Int): Country {
        return requestObject(
                "http://$host:$port/world/country/$countryId",
                Country.Deserializer())
    }

    override fun allRegions(): List<Region> {
        return requestObject(
                "http://$host:$port/world/regions",
                Region.ListDeserializer())
    }

    override fun region(regionId: Int): Region {
        return requestObject(
                "http://$host:$port/world/region/$regionId",
                Region.Deserializer())
    }

    override fun countryRegions(countryId: Int): List<Region> {
        return requestObject(
                "http://$host:$port/world/country/$countryId/regions",
                Region.ListDeserializer())
    }

    override fun action(action: DeclareWarAction) {
        requestNothing(
                "http://$host:$port/world/action/declarewar/${action.actorCountryId}/${action.otherCountryId}")
    }

    override fun action(action: ProposePeaceAction) {
        requestNothing(
                "http://$host:$port/world/action/proposepeace/${action.actorCountryId}/${action.otherCountryId}")
    }

    override fun action(action: AcceptPeaceAction) {
        requestNothing(
                "http://$host:$port/world/action/acceptpeace/${action.actorCountryId}/${action.otherCountryId}")
    }

    override fun action(action: TaxAgricultureAction) {
        requestNothing(
                "http://$host:$port/world/action/taxagriculture/${action.countryId}/${action.taxAgriculture}")
    }

    override fun action(action: TaxManufactureAction) {
        requestNothing(
                "http://$host:$port/world/action/taxmanufacture/${action.countryId}/${action.taxManufacture}")
    }

    override fun action(action: AgricultureRatioAction) {
        requestNothing(
                "http://$host:$port/world/action/agricultureratio/${action.regionId}/${action.agricultureRatio}")
    }

    override fun simulate(): Simulation {
        return requestObject(
                "http://$host:$port/world/simulate",
                Simulation.Deserializer())
    }

    fun requestNothing(url: String) {
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

    fun <T : Any> requestObject(url: String, deserializer: ResponseDeserializable<T>): T {
        logger.debug("URL: {}", url)
        val (request, response, result) = url
                .httpGet()
                .responseObject(deserializer)
        if (!response.isSuccessful) {
            logger.warn("Request: {}", request)
            logger.warn("Response: {}", response)
            logger.warn("Result: {}", result)
        }
        return result.get()
    }
}