package ch.obermuhlner.simul.server.model.service

import ch.obermuhlner.simul.server.model.domain.*
import ch.obermuhlner.simul.shared.domain.*
import java.util.*
import kotlin.math.min

interface Randomizer {
    fun gaussian(value: Double, stdDeviation: Double = 0.1): Double
}

class NoRandomizer : Randomizer {
    override fun gaussian(value: Double, stdDeviation: Double): Double {
        return value
    }
}

class RandomRandomizer : Randomizer {
    private val random : Random = Random()

    override fun gaussian(value: Double, stdDeviation: Double): Double {
        return random.nextGaussian() * stdDeviation * value + value
    }
}

private fun limitedGrowth(oldValue: Double, newValue: Double, maxValue: Double, shrinkFactor: Double, growthFactor: Double): Double {
    val internalGrowthFactor = 0.5
    val growth = min(newValue - oldValue, maxValue - oldValue)
    val correctedGrowth = when {
        growth < 0 -> growth * shrinkFactor
        growth > 0 -> growth * growthFactor * (maxValue - oldValue - growth * internalGrowthFactor) / (maxValue - oldValue)
        else -> 0.0
    }
    return oldValue + correctedGrowth
}

private fun clampMin(value: Double, minValue: Double = 0.0): Double {
    return when {
        value < minValue -> minValue
        else -> value
    }
}

private fun clamp(value: Double, minValue: Double = 0.0, maxValue: Double = 1.0): Double {
    return when {
        value < minValue -> minValue
        value > maxValue -> maxValue
        else -> value
    }
}

sealed class Rule

class CountryRule(val execute : (CountryModel)->Unit) : Rule()

class RegionRule(val execute : (RegionModel)->Unit) : Rule()

class CountryRegionRule(val execute : (CountryModel, RegionModel)->Unit) : Rule()

class Simulator(private val rules : List<Rule>) {

    var ticks = 0

    fun simulate(worldModel: WorldModel) {
        ticks++

        for (rule in rules) {
            when (rule) {
                is CountryRule -> {
                    for (country in worldModel.countryModels) {
                        rule.execute(country)
                    }
                }
                is RegionRule -> {
                    for (region in worldModel.regionModels) {
                        rule.execute(region)
                    }
                }
                is CountryRegionRule -> {
                    for (country in worldModel.countryModels) {
                        for (region in worldModel.regionModels) {
                            rule.execute(country, region)
                        }
                    }
                }
            }
        }

        val actions = mutableListOf<Action>()
        actions.addAll(worldModel.actions)
        worldModel.actions.clear()
        for (action in actions) {
            executeAction(worldModel, action)
        }
    }

    private fun executeAction(worldModel: WorldModel, action: Action) {
        val dummy = when(action) {
            is DeclareWarAction -> {
                val actor = worldModel.country(action.actorCountryId)
                val other = worldModel.country(action.otherCountryId)
                actor.countrieModelsWar += other
                other.countrieModelsWar += actor
            }
            is ProposePeaceAction -> {
                // TODO if peace is acceptable
                worldModel.actions += AcceptPeaceAction(action.otherCountryId, action.actorCountryId)
            }
            is AcceptPeaceAction -> {
                val actor = worldModel.country(action.actorCountryId)
                val other = worldModel.country(action.otherCountryId)
                actor.countrieModelsWar -= other
                other.countrieModelsWar -= actor
            }
            is TaxAgricultureAction -> {
                val country = worldModel.country(action.countryId)
                country.taxAgriculture = action.taxAgriculture
            }
            is TaxManufactureAction -> {
                val country = worldModel.country(action.countryId)
                country.taxAgriculture = action.taxManufacture
            }
            is AgricultureRatioAction -> {
                val region = worldModel.region(action.regionId)
                region.agricultureRatio = action.agricultureRatio
            }
        }
    }
}

class SimulationLoader {
    fun load(): Simulator {
        val randomizer = RandomRandomizer()

        val agricultureStorageDecay = 0.1
        val agricultureToGold = 1.0
        val manufactureToGold = 1.0
        val goldToLuxury = 0.2
        val luxuryDecay = 0.1

        val rules = listOf(
                // agrigulture and population
                RegionRule { region ->
                    region.agricultureProduce = clampMin(randomizer.gaussian(min(region.population, region.agriculture) * region.agricultureRatio * region.agriculturePerPopulation))
                },
                CountryRegionRule { country, region ->
                    val agricultureProduceTax = region.agricultureProduce * country.taxAgriculture
                    region.agricultureProduce -= agricultureProduceTax
                    country.gold += agricultureProduceTax * agricultureToGold

                    region.agricultureStorage += region.agricultureProduce
                    region.agricultureProduce = 0.0
                },
                RegionRule { region ->
                    region.population = clampMin(limitedGrowth(region.population, region.agricultureStorage, region.agriculture, 0.8, 0.8))
                    region.agricultureStorage = clampMin(region.agricultureStorage - region.population)
                },
                RegionRule { region ->
                    region.agricultureStorage *= (1.0 - clamp(randomizer.gaussian(agricultureStorageDecay)))
                },
                // manufacture
                RegionRule { region ->
                    val manufactureRatio = 1.0 - region.agricultureRatio
                    region.manufactureProduce = clampMin(region.population * manufactureRatio * region.manufacturePerPopulation)
                },
                CountryRegionRule { country, region ->
                    val manufactureProduceTax = region.manufactureProduce * country.taxManufacture
                    region.manufactureProduce -= manufactureProduceTax
                    country.gold += manufactureProduceTax * manufactureToGold

                    region.gold += region.manufactureProduce * manufactureToGold
                    region.manufactureProduce = 0.0

                    val goldForLuxury = region.gold * goldToLuxury
                    region.gold -= goldForLuxury
                    region.luxury += goldForLuxury / region.population
                    region.luxury *= (1.0 - luxuryDecay)
                }
        )

        return Simulator(rules)
    }
}


class WorldLoader {
    fun load(): WorldModel {
        return WorldModel().apply {
            createCountry("Castile").apply {
                addRegion(createRegion("Toledo").apply {
                    population = 10.0
                    agriculture = 20.0
                })
                addRegion(createRegion("Sevilla").apply {
                    population = 10.0
                    agriculture = 30.0
                })
                taxAgriculture = 0.1
            }
            createCountry("Portugal").apply {
                addRegion(createRegion("Lisbon").apply {
                    population = 10.0
                    agriculture = 30.0
                })
                addRegion(createRegion("Algarve").apply {
                    population = 10.0
                    agriculture = 20.0
                })
                taxAgriculture = 0.1
            }
        }
    }
}