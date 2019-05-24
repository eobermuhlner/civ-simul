package ch.obermuhlner.simul.client.cli

import ch.obermuhlner.simul.server.RealWorldService
import ch.obermuhlner.simul.shared.service.WorldService

fun main(args: Array<String>) {
    val worldService : WorldService = RealWorldService()

    //println(worldService.country(0))
    //println(worldService.allCountries())

    //println(worldService.region(0))
    println(worldService.countryRegions(0))
    //println(worldService.allRegions())

}
