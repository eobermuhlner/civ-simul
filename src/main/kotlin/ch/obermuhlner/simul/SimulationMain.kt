package ch.obermuhlner.simul

fun main(args: Array<String>) {
    simulateTrivialWorld()
    //simulateSimpleWorld()
}

private fun simulateTrivialWorld() {
    val world = World()
    val region = world.createRegion("region")
    val country = world.createCountry("country")
    country.regions += region

    country.taxAgriculture = 0.1

    val simulation = Simulation(NoRandomizer())

    for (i in 1..20) {
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

    println(regionNW)
    println(world.regionConnections[regionNW])
}
