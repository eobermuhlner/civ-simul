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
        val agricultureProduceFood = region.agricultureProduce
        var populationGrowth = min(agricultureProduceFood - region.population, region.agriculture - region.population)
        if (populationGrowth < 0) {
            populationGrowth *= populationStarvationRate
        } else if (populationGrowth > 0) {
            val populationGrowthFactor = (region.agriculture - region.population - populationGrowth / 2) / (region.agriculture - region.population)
            populationGrowth *= populationGrowthFactor
        }

        region.population += populationGrowth
        if (region.population < 0) {
            region.population = 0.0
        }
        region.agricultureProduce -= region.population
        if (region.agricultureProduce < 0) {
            region.agricultureProduce = 0.0
        }
    }

    private fun clamp(value: Double, minValue: Double, maxValue: Double): Double {
        if (value < minValue) {
            return minValue
        } else if (value > maxValue) {
            return maxValue
        } else {
            return value
        }
    }
}
