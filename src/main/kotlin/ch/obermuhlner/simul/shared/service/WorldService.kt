package ch.obermuhlner.simul.shared.service

import ch.obermuhlner.simul.shared.domain.CountryDto
import ch.obermuhlner.simul.shared.domain.RegionDto

interface WorldService {
    fun allCountries() : List<CountryDto>
    fun country(id: Int) : CountryDto?

    fun allRegions() : List<RegionDto>
    fun countryRegions(countryId: Int) : List<RegionDto>
    fun region(id: Int) : RegionDto?

    fun simulate()
}
