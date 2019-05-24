package ch.obermuhlner.simul.client.cli

import ch.obermuhlner.simul.client.service.RemoteWorldService
import ch.obermuhlner.simul.service.WorldService

fun main(args: Array<String>) {
    val worldService : WorldService = RemoteWorldService()

    //println(worldService.country(0))
    //println(worldService.allCountries())

    //println(worldService.region(0))
    println(worldService.countryRegions(0))
    //println(worldService.allRegions())

}
