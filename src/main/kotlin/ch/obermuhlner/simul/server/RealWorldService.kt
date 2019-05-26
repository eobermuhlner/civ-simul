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

    @GetMapping("/world/countries")
    override fun allCountries() : List<Country> {
        return worldModel.countryModels
                .map { Country(it) }
    }

    @GetMapping("/world/country/{countryId}")
    override fun country(@PathVariable countryId: Int) : Country? {
        // TODO find first and then map
        return worldModel.countryModels
                .map { Country(it) }
                .find { it.id == countryId }
    }

    @GetMapping("/world/country/{countryId}/regions")
    override fun countryRegions(@PathVariable countryId: Int) : List<Region> {
        return worldModel.regionModels
                .filter { it.countryModel != null && it.countryModel?.id == countryId }
                .map { Region(it) }
    }

    @GetMapping("/world/region")
    override fun allRegions() : List<Region> {
        return worldModel.regionModels
                .map { Region(it) }
    }

    @GetMapping("/world/region/{regionId}")
    override fun region(@PathVariable regionId: Int) : Region? {
        // TODO find first and then map
        return worldModel.regionModels
                .map { Region(it) }
                .find { it.id == regionId }
    }

    @GetMapping("/world/action/declarewar/{actorCountryId}/{otherCountryId}")
    fun actionDeclareWar(@PathVariable actorCountryId: Int, @PathVariable otherCountryId: Int) {
        action(DeclareWarAction(actorCountryId, otherCountryId))
    }

    override fun action(action: DeclareWarAction) {
        worldModel.actions += action
    }

    @GetMapping("/world/action/proposepeace/{actorCountryId}/{otherCountryId}")
    fun actionProposePeace(@PathVariable actorCountryId: Int, @PathVariable otherCountryId: Int) {
        action(ProposePeaceAction(actorCountryId, otherCountryId))
    }

    override fun action(action: ProposePeaceAction) {
        worldModel.actions += action
    }

    @GetMapping("/world/action/acceptpeace/{actorCountryId}/{otherCountryId}")
    fun actionAcceptPeace(@PathVariable actorCountryId: Int, @PathVariable otherCountryId: Int) {
        action(AcceptPeaceAction(actorCountryId, otherCountryId))
    }

    override fun action(action: AcceptPeaceAction) {
        worldModel.actions += action
    }

    @GetMapping("/world/action/taxagriculture/{countryId}/{taxAgriculture}")
    fun actionTaxAgriculture(@PathVariable countryId: Int, @PathVariable taxAgriculture: Double) {
        action(TaxAgricultureAction(countryId, taxAgriculture))
    }

    override fun action(action: TaxAgricultureAction) {
        worldModel.actions += action
    }

    @GetMapping("/world/action/taxmanufacture/{countryId}/{taxManufacture}")
    fun actionTaxManufacture(@PathVariable countryId: Int, @PathVariable taxManufacture: Double) {
        action(TaxManufactureAction(countryId, taxManufacture))
    }

    override fun action(action: TaxManufactureAction) {
        worldModel.actions += action
    }

    @GetMapping("/world/action/agricultureratio/{regionId}/{agricultureRatio}")
    fun actionAgricultureRatio(@PathVariable regionId: Int, @PathVariable agricultureRatio: Double) {
        action(AgricultureRatioAction(regionId, agricultureRatio))
    }

    override fun action(action: AgricultureRatioAction) {
        worldModel.actions += action
    }

    @GetMapping("/world/simulate")
    override fun simulate(): Simulation {
        simulator.simulate(worldModel)
        return Simulation(simulator.ticks)
    }
}
