package ch.obermuhlner.simul.shared.domain

sealed class Action

data class DeclareWarAction(val actorCountryId: Int, val otherCountryId: Int) : Action()
data class ProposePeaceAction(val actorCountryId: Int, val otherCountryId: Int) : Action()
data class AcceptPeaceAction(val actorCountryId: Int, val otherCountryId: Int) : Action()
data class TaxAgricultureAction(val countryId: Int, val taxAgriculture: Double) : Action()
