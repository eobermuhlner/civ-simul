package ch.obermuhlner.simul

import kotlin.math.min
import java.util.Random


data class Region(
        val id: Int,
        val name: String,
        var agriculture: Double = 100.0,
        var agriculturePerPopulation: Double = 1.2,
        var population: Double = 0.0,

        var agricultureProduce: Double = 0.0,
        var agricultureStorage: Double = 0.0)


data class RegionConnection(
        val to: Region)


data class Point(
        val x: Double,
        val y: Double)


data class Polygon(
        val points: List<Point>)


data class Country(
        val id: Int,
        val name: String,
        var taxAgriculture: Double = 0.1,
        var agricultureStorage: Double = 0.0,
        val regions: MutableList<Region> = mutableListOf())


class World {
    val regions: MutableList<Region> = mutableListOf()
    val regionConnections: MutableMap<Region, MutableList<RegionConnection>> = mutableMapOf()
    val regionPolygons: MutableMap<Region, Polygon> = mutableMapOf()
    val countries: MutableList<Country> = mutableListOf()

    fun createRegion(name: String): Region {
        val region = Region(regions.size, name)
        regions += region
        return region
    }

    fun createRegionConnection(from: Region, to: Region) {
        val connection = RegionConnection(to)
        regionConnections.getOrPut(from) { mutableListOf() } += connection
    }

    fun createRegionPolygon(region: Region, polygon: Polygon) {
        regionPolygons[region] = polygon
    }

    fun createCountry(name: String): Country {
        val country = Country(regions.size, name)
        countries += country
        return country
    }
}

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

        val rules = listOf(
                RegionRule { region ->
                    region.agricultureProduce = clampMin(randomizer.gaussian(min(region.population, region.agriculture) * region.agriculturePerPopulation))
                },
                CountryRegionRule { country, region ->
                    val agricultureProduceTax = region.agricultureProduce * country.taxAgriculture
                    region.agricultureProduce -= agricultureProduceTax
                    country.agricultureStorage += agricultureProduceTax

                    region.agricultureStorage += region.agricultureProduce
                    region.agricultureProduce = 0.0
                },
                RegionRule { region ->
                    region.population = clampMin(limitedGrowth(region.population, region.agricultureStorage, region.agriculture, 0.8, 0.8))
                    region.agricultureStorage = clampMin(region.agricultureStorage - region.population)
                },
                CountryRule { country ->
                    country.agricultureStorage *= ( 1.0 - clamp(randomizer.gaussian(agricultureStorageDecay)))
                },
                RegionRule { region ->
                    region.agricultureStorage *= ( 1.0 - clamp(randomizer.gaussian(agricultureStorageDecay)))
                }
        )

        return Simulation(rules)
    }
}