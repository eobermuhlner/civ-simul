package ch.obermuhlner.simul.server

import ch.obermuhlner.simul.server.model.domain.WorldModel
import ch.obermuhlner.simul.server.model.service.Simulator
import ch.obermuhlner.simul.server.model.service.SimulationLoader
import ch.obermuhlner.simul.server.model.service.WorldLoader
import ch.obermuhlner.simul.shared.domain.*
import ch.obermuhlner.simul.shared.service.WorldService
import org.springframework.web.bind.annotation.*

@RestController
class RealWorldService(val simulator: Simulator = SimulationLoader().load(),
                       val worldModel: WorldModel = WorldLoader().load()) : WorldService {

    @GetMapping("/countries")
    override fun allCountries() : List<Country> {
        return worldModel.countryModels
                .map { Country(it) }
    }

    @GetMapping("/country/{countryId}")
    override fun country(@PathVariable countryId: Int) : Country? {
        println("COUNTRY $countryId")
        // TODO find first and then map
        return worldModel.countryModels
                .map { Country(it) }
                .find { it.id == countryId }
    }

    @GetMapping("/country/{countryId}/regionModels")
    override fun countryRegions(@PathVariable countryId: Int) : List<Region> {
        return worldModel.regionModels
                .filter { it.countryModel != null && it.countryModel?.id == countryId }
                .map { Region(it) }
    }

    @GetMapping("/region")
    override fun allRegions() : List<Region> {
        return worldModel.regionModels
                .map { Region(it) }
    }

    @GetMapping("/region/{regionId}")
    override fun region(@PathVariable regionId: Int) : Region? {
        println("REGION $regionId")
        // TODO find first and then map
        return worldModel.regionModels
                .map { Region(it) }
                .find { it.id == regionId }
    }

    @GetMapping("/action/declarewar/{actorCountryId}/{otherCountryId}")
    fun actionDeclareWar(@PathVariable actorCountryId: Int, @PathVariable otherCountryId: Int) {
        action(DeclareWarAction(actorCountryId, otherCountryId))
    }

    override fun action(action: DeclareWarAction) {
        println("ACTION $action")
        worldModel.actions += action
    }

    @GetMapping("/action/proposepeace/{actorCountryId}/{otherCountryId}")
    fun actionProposePeace(@PathVariable actorCountryId: Int, @PathVariable otherCountryId: Int) {
        action(ProposePeaceAction(actorCountryId, otherCountryId))
    }

    override fun action(action: ProposePeaceAction) {
        println("ACTION $action")
        worldModel.actions += action
    }

    @GetMapping("/action/acceptpeace/{actorCountryId}/{otherCountryId}")
    fun actionAcceptPeace(@PathVariable actorCountryId: Int, @PathVariable otherCountryId: Int) {
        action(AcceptPeaceAction(actorCountryId, otherCountryId))
    }

    override fun action(action: AcceptPeaceAction) {
        println("ACTION $action")
        worldModel.actions += action
    }

    @GetMapping("/action/taxagriculture/{countryId}/{taxAgriculture}")
    fun actionTaxAgriculture(@PathVariable countryId: Int, @PathVariable taxAgriculture: Double) {
        action(TaxAgricultureAction(countryId, taxAgriculture))
    }

    override fun action(action: TaxAgricultureAction) {
        println("ACTION $action")
        worldModel.actions += action
    }

    @GetMapping("/simulate")
    override fun simulate(): Simulation {
        simulator.simulate(worldModel)
        return Simulation(simulator.ticks)
    }
}
