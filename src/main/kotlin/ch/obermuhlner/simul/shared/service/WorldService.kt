package ch.obermuhlner.simul.shared.service

import ch.obermuhlner.simul.shared.domain.*

interface WorldService {
    fun allCountries() : List<Country>
    fun country(id: Int) : Country?

    fun allRegions() : List<Region>
    fun countryRegions(countryId: Int) : List<Region>
    fun region(id: Int) : Region?

    fun action(action: DeclareWarAction)
    fun action(action: ProposePeaceAction)
    fun action(action: AcceptPeaceAction)
    fun action(action: TaxAgricultureAction)

    fun simulate(): Simulation
}
