package ch.obermuhlner.simul.shared.domain

sealed class Action

data class DeclareWarAction(val actorCountryId: Int, val otherCountryId: Int) : Action()
data class ProposePeaceAction(val actorCountryId: Int, val otherCountryId: Int) : Action()
data class AcceptPeaceAction(val actorCountryId: Int, val otherCountryId: Int) : Action()

data class TaxAgricultureAction(val countryId: Int, val taxAgriculture: Double) : Action()
data class TaxManufactureAction(val countryId: Int, val taxManufacture: Double) : Action()
data class AgricultureRatioAction(val regionId: Int, val agricultureRatio: Double) : Action()
