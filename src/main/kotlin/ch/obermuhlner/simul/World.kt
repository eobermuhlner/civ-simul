package ch.obermuhlner.simul

import kotlin.math.min
import java.util.Random


data class Region(
    val id: Int,
    val name: String,
    var agriculture: Double = 20.0,
    var agriculturePerPopulation: Double = 1.2,
    var population: Double = 10.0,

    var agricultureProduce: Double = 0.0)


data class RegionConnection(
    val to: Region)


data class Country(
    val id: Int,
    val name: String,
    var taxAgriculture: Double = 0.1,
    var agricultureProduce: Double = 0.0,
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

    fun clampedGaussian(value: Double, stdDeviation: Double = 0.1, minValue: Double = 0.0, maxValue: Double = value * 2): Double {
        val valueNew = gaussian(value, stdDeviation)
        if (valueNew < minValue) {
            return minValue
        }
        if (valueNew > maxValue) {
            return maxValue
        }
        return valueNew
    }
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

class Simulation(private val randomizer: Randomizer = RandomRandomizer()) {
    val populationStarvationRate = 0.8
    val populationGrowthRate = 0.5

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
        region.agricultureProduce = randomizer.clampedGaussian(
                min(region.population, region.agriculture) * region.agriculturePerPopulation)
    }

    private fun simulateTax(country: Country, region: Region) {
        val agricultureProduceTax = region.agricultureProduce * country.taxAgriculture
        region.agricultureProduce -= agricultureProduceTax
        country.agricultureProduce += agricultureProduceTax
    }

    private fun simulatePopulation(region: Region) {
        region.population += limitedGrowth(region.population, region.agricultureProduce, region.agriculture, populationStarvationRate, populationGrowthRate)
        region.population = clampMin(region.population)

        region.agricultureProduce -= region.population
        region.agricultureProduce = clampMin(region.agricultureProduce)
    }

    private fun limitedGrowth(value: Double, growthValue: Double, maxValue: Double, shrinkFactor: Double = 0.8, growthFactor: Double = 0.5): Double {
        val populationGrowth = min(growthValue - value, maxValue - value)
        return when {
            populationGrowth < 0 -> populationGrowth * shrinkFactor
            populationGrowth > 0 -> populationGrowth * (maxValue - value - populationGrowth * growthFactor) / (maxValue - value)
            else -> populationGrowth
        }
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
