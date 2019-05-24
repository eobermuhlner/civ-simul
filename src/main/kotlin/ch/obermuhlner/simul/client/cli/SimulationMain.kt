package ch.obermuhlner.simul.client.cli

import ch.obermuhlner.simul.domain.SimulationLoader
import ch.obermuhlner.simul.domain.World

fun main(args: Array<String>) {
    simulateTrivialWorld()
    //simulateSimpleWorld()
}

private fun simulateTrivialWorld() {
    val world = World()
    val region = world.createRegion("region")
    region.population = 10.0
    region.agriculture = 20.0

    val country = world.createCountry("country")
    country.addRegion(region)
    country.taxAgriculture = 0.1

    val simulation = SimulationLoader().load()

    for (i in 1..20) {
        //println("country.agri=${country.agricultureStorage} pop=${region.population} agri=${region.agricultureStorage}")
        println(country)
        simulation.simulate(world)
    }
}

private fun simulateSimpleWorld() {
    val world = World()

    val regionNW = world.createRegion("nw")
    val regionNE = world.createRegion("ne")
    val regionSW = world.createRegion("sw")
    val regionSE = world.createRegion("se")

    world.createRegionConnection(regionNW, regionNE)
    world.createRegionConnection(regionNW, regionSW)

    world.createRegionConnection(regionNE, regionNW)
    world.createRegionConnection(regionNE, regionSE)

    world.createRegionConnection(regionSW, regionNW)
    world.createRegionConnection(regionSW, regionSE)

    world.createRegionConnection(regionSE, regionNE)
    world.createRegionConnection(regionSE, regionSW)

    regionNW.population = 10.0
    regionNW.agriculture = 100.0


    val country = world.createCountry("country")
    country.addRegion(regionNW)
    country.taxAgriculture = 0.1

    val simulation = SimulationLoader().load()

    for (i in 1..20) {
        println(country)
        println(regionNE)
        simulation.simulate(world)
    }
}
