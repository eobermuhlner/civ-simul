package ch.obermuhlner.simul.shared.service

import ch.obermuhlner.simul.shared.domain.*

interface WorldService {
    fun allCountries() : List<Country>
    fun country(countryId: Int) : Country?

    fun allRegions() : List<Region>
    fun countryRegions(countryId: Int) : List<Region>
    fun region(regionId: Int) : Region?

    fun action(action: DeclareWarAction)
    fun action(action: ProposePeaceAction)
    fun action(action: AcceptPeaceAction)
    fun action(action: TaxAgricultureAction)
    fun action(action: TaxManufactureAction)
    fun action(action: AgricultureRatioAction)

    fun simulate(): Simulation
}
