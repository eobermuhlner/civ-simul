package ch.obermuhlner.simul

import kotlin.math.min
import java.util.Random


data class Region(
    val id: Int,
    val name: String,
    var agriculture: Double = 100.0,
    var agriculturePerPopulation: Double = 1.2,
    var population: Double = 10.0,

    var agricultureProduce: Double = 0.0,
    var agricultureStorage: Double = 0.0)


data class RegionConnection(
    val to: Region)


data class Country(
        val id: Int,
        val name: String,
        var taxAgriculture: Double = 0.1,
        var agricultureStorage: Double = 0.0,
        val regions: MutableList<Region> = mutableListOf())


class World {
    val regions: MutableList<Region> = mutableListOf()
    val regionConnections: MutableMap<Region, MutableList<RegionConnection>> = mutableMapOf()
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

class Simulation(private val randomizer: Randomizer = RandomRandomizer(),
                 val populationStarvationRate: Double = 0.9,
                 val populationGrowthRate: Double = 0.9) {

    var ticks = 0

    fun simulate(world: World) {
        ticks++

        for (region in world.regions) {
            simulateProduction(region)
        }

        for (country in world.countries) {
            for (region in country.regions) {
                simulateTax(country, region)
            }
        }

        for (region in world.regions) {
            simulatePopulation(region)
        }
    }

    private fun simulateProduction(region: Region) {
        region.agricultureProduce = clampMin(randomizer.gaussian(min(region.population, region.agriculture) * region.agriculturePerPopulation))
    }

    private fun simulateTax(country: Country, region: Region) {
        val agricultureProduceTax = region.agricultureProduce * country.taxAgriculture
        region.agricultureProduce -= agricultureProduceTax
        country.agricultureStorage += agricultureProduceTax

        region.agricultureStorage += region.agricultureProduce
        region.agricultureProduce = 0.0
    }

    private fun simulatePopulation(region: Region) {
        region.population = clampMin(limitedGrowth(region.population, region.agricultureStorage, region.agriculture, populationStarvationRate, populationGrowthRate))

        region.agricultureStorage = clampMin(region.agricultureStorage - region.population)
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

    private fun clamp(value: Double, minValue: Double, maxValue: Double): Double {
        return when {
            value < minValue -> minValue
            value > maxValue -> maxValue
            else -> value
        }
    }
}
