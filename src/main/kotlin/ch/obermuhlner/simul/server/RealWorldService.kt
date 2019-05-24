package ch.obermuhlner.simul.server

import ch.obermuhlner.simul.server.model.domain.World
import ch.obermuhlner.simul.server.model.service.Simulation
import ch.obermuhlner.simul.server.model.service.SimulationLoader
import ch.obermuhlner.simul.server.model.service.WorldLoader
import ch.obermuhlner.simul.shared.domain.*
import ch.obermuhlner.simul.shared.service.WorldService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class RealWorldService(val simulation: Simulation = SimulationLoader().load(),
                       val world: World = WorldLoader().load()) : WorldService {

    @GetMapping("/countries")
    override fun allCountries() : List<CountryDto> {
        return world.countries
                .map { CountryDto(it) }
    }

    @GetMapping("/country/{countryId}")
    override fun country(@PathVariable countryId: Int) : CountryDto? {
        // TODO find first and then map
        return world.countries
                .map { CountryDto(it) }
                .find { it.id == countryId }
    }

    @GetMapping("/country/{countryId}/regions")
    override fun countryRegions(@PathVariable countryId: Int) : List<RegionDto> {
        return world.regions
                .filter { it.country != null && it.country?.id == countryId }
                .map { RegionDto(it) }
    }

    @GetMapping("/regions")
    override fun allRegions() : List<RegionDto> {
        return world.regions
                .map { RegionDto(it) }
    }

    @GetMapping("/region/{regionId}")
    override fun region(@PathVariable regionId: Int) : RegionDto? {
        // TODO find first and then map
        return world.regions
                .map { RegionDto(it) }
                .find { it.id == regionId }
    }
}
