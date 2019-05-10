package ch.obermuhlner.simul

import kotlin.math.max

data class Region(
    val id: Int,
    val name: String,
    var agriculture: Double = 30.0,
    var population: Double = 10.0)


data class RegionConnection(
    val to: Region)


class World {
    val regions: MutableList<Region> = arrayListOf()

    val regionConnections: MutableMap<Region, MutableList<RegionConnection>> = mutableMapOf()

    fun createRegion(name: String): Region {
        val region = Region(regions.size, name)
        regions += region
        return region
    }

    fun createRegionConnection(from: Region, to: Region) {
        val connection = RegionConnection(to)
        regionConnections.computeIfAbsent(from) { mutableListOf() } += connection
    }
}

class Simulation {
    val populationGrowth = 0.01

    fun simulate(world: World) {
        for (region in world.regions) {
            region.population = growByToMax(region.population, max(region.population, region.agriculture), region.agriculture, populationGrowth)
        }
    }

    private fun growToMax(value: Double, maxValue: Double, growthFactor: Double): Double {
        return growByToMax(value, value, maxValue, growthFactor)
    }

    private fun growByToMax(value: Double, growValue: Double, maxValue: Double, growthFactor: Double): Double {
        val delta = growValue * growthFactor
        if (value + delta < maxValue) {
            return value + delta
        } else {
            return maxValue
        }
    }
}
