package ch.obermuhlner.simul.client.service

import ch.obermuhlner.simul.shared.domain.*
import ch.obermuhlner.simul.shared.service.WorldService
import com.github.kittinunf.fuel.*
import com.google.gson.Gson

class RemoteWorldService(
        private val host: String = "localhost",
        private val port: Int = 8080) : WorldService {

    override fun allCountries(): List<Country> {
        val (_, _, result) = "http://$host:$port/countries"
                .httpGet()
                .responseObject(Country.ListDeserializer())
        return result.get()
    }

    override fun country(countryId: Int): Country {
        val (_, _, result) = "http://$host:$port/country/$countryId"
                .httpGet()
                .responseObject(Country.Deserializer())
        return result.get()
    }

    override fun allRegions(): List<Region> {
        val (_, _, result) = "http://$host:$port/regions"
                .httpGet()
                .responseObject(Region.ListDeserializer())
        return result.get()
    }

    override fun countryRegions(countryId: Int): List<Region> {
        val (_, _, result) = "http://$host:$port/country/$countryId/regions"
                .httpGet()
                .responseObject(Region.ListDeserializer())
        return result.get()
    }

    override fun region(regionId: Int): Region {
        val (_, _, result) = "http://$host:$port/region/$regionId"
                .httpGet()
                .responseObject(Region.Deserializer())
        return result.get()
    }

    override fun action(action: DeclareWarAction) {
        "http://$host:$port/action/declarewar/${action.actorCountryId}/${action.otherCountryId}"
                .httpGet()
                .response()
    }

    override fun action(action: ProposePeaceAction) {
        "http://$host:$port/action/proposepeace/${action.actorCountryId}/${action.otherCountryId}"
                .httpGet()
                .response()
    }

    override fun action(action: AcceptPeaceAction) {
        "http://$host:$port/action/acceptpeace/${action.actorCountryId}/${action.otherCountryId}"
                .httpGet()
                .response()
    }

    override fun action(action: TaxAgricultureAction) {
        "http://$host:$port/action/taxagriculture/${action.countryId}/${action.taxAgriculture}"
                .httpGet()
                .response()
    }

    override fun simulate(): Simulation {
        val (_, _, result) = "http://localhost:8080/simulate"
                .httpGet()
                .responseObject(Simulation.Deserializer())
        return result.get()
    }
}