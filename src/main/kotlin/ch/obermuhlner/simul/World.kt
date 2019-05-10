package ch.obermuhlner.simul

import kotlin.math.min

data class Region(
    val id: Int,
    val name: String,
    var agriculture: Double = 20.0,
    var population: Double = 10.0,

    var agricultureProduce: Double = 20.0)


data class RegionConnection(
    val to: Region)


data class Country(
    val id: Int,
    val name: String,
    var taxAgriculture: Double = 0.1,
    var agricultureProduce: Double = 0.0,
    val regions: MutableList<Region> = mutableListOf())


class World {
    val regions: MutableList<Region> = arrayListOf()
    val regionConnections: MutableMap<Region, MutableList<RegionConnection>> = mutableMapOf()
    val countries: MutableList<Country> = arrayListOf()

    fun createRegion(name: String): Region {
        val region = Region(regions.size, name)
        regions += region
        return region
    }

    fun createRegionConnection(from: Region, to: Region) {
        val connection = RegionConnection(to)
        regionConnections.computeIfAbsent(from) { mutableListOf() } += connection
    }

    fun createCountry(name: String): Country {
        val country = Country(regions.size, name)
        countries += country
        return country
    }
}



class Simulation {
    val populationGrowth = 0.1

    fun simulate(world: World) {
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
        region.agricultureProduce = min(region.population, region.agriculture)
    }

    private fun simulateTax(country: Country, region: Region) {
        val agricultureProduceTax = region.agricultureProduce * country.taxAgriculture
        region.agricultureProduce -= agricultureProduceTax
        country.agricultureProduce += agricultureProduceTax
    }

    private fun simulatePopulation(region: Region) {
        val agricultureProduceFood = region.agricultureProduce
        region.population = growToMax(region.population, agricultureProduceFood, region.agriculture, populationGrowth)
        region.agricultureProduce -= agricultureProduceFood
    }

    private fun growToMax(value: Double, maxValue: Double, growthFactor: Double): Double {
        return growToMax(value, value, maxValue, growthFactor)
    }

    private fun growToMax(value: Double, growValue: Double, maxValue: Double, growthFactor: Double): Double {
        val delta = growValue * growthFactor
        if (value + delta < maxValue) {
            return value + delta
        } else {
            return maxValue
        }
    }
}
