package ch.obermuhlner.simul

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
    val random : Random = Random()

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

class CountryRule(val execute : (Country)->Unit) : Rule()

class RegionRule(val execute : (Region)->Unit) : Rule()

class CountryRegionRule(val execute : (Country, Region)->Unit) : Rule()

class Simulation(private val rules : List<Rule>) {

    var ticks = 0

    fun simulate(world: World) {
        ticks++

        for (rule in rules) {
            when (rule) {
                is CountryRule -> {
                    for (country in world.countries) {
                        rule.execute(country)
                    }
                }
                is RegionRule -> {
                    for (region in world.regions) {
                        rule.execute(region)
                    }
                }
                is CountryRegionRule -> {
                    for (country in world.countries) {
                        for (region in world.regions) {
                            rule.execute(country, region)
                        }
                    }
                }
            }
        }
    }
}

class SimulationLoader {
    fun load(): Simulation {
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
                    region.agricultureStorage *= ( 1.0 - clamp(randomizer.gaussian(agricultureStorageDecay)))
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
                    region.luxury *= ( 1.0 - luxuryDecay)
                }
        )

        return Simulation(rules)
    }
}
