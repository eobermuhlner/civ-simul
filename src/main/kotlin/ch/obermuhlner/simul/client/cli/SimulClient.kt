package ch.obermuhlner.simul.client.cli

import ch.obermuhlner.simul.client.service.RemoteWorldService
import ch.obermuhlner.simul.shared.domain.CountryDto
import ch.obermuhlner.simul.shared.domain.RegionDto
import ch.obermuhlner.simul.shared.service.WorldService

class Command(val name: String, val argumentCount: Int, val block: (WorldService, List<String>) -> Unit)

class SimulClient {
    private val worldService : WorldService = RemoteWorldService()

    private val commands: List<Command> = listOf(
            Command("help", 0) { _, _ ->
                printHelp()
            },
            Command("countries", 0) { worldService, _ ->
                printCountries(worldService.allCountries())
            },
            Command("country", 1) { worldService, arguments ->
                val countryId = arguments[0].toInt()
                worldService.country(countryId)?.let { printCountry(it) }
            },
            Command("regions", 0) { worldService, _ ->
                printRegions(worldService.allRegions())
            },
            Command("country-regions", 1) { worldService, arguments ->
                val countryId = arguments[0].toInt()
                printRegions(worldService.countryRegions(countryId))
            },
            Command("region", 1) { worldService, arguments ->
                val regionId = arguments[0].toInt()
                worldService.region(regionId)?.let { printRegion(it) }
            }
    )

    fun execute(args: Array<String>) {
        if (args.isEmpty()) {
            printHelp()
        } else {
            var argumentIndex = 0
            while (argumentIndex < args.size) {
                val commandName = args[argumentIndex++]
                val command = commands.find { it.name == commandName}
                if (command != null) {
                    if (argumentIndex + command.argumentCount <= args.size) {
                        val commandArguments = args.slice(argumentIndex until (argumentIndex+command.argumentCount))
                        argumentIndex += command.argumentCount
                        command.block(worldService, commandArguments)
                    } else {
                        println("Command $commandName expects ${command.argumentCount} arguments")
                    }
                } else {
                    println("Unknown command: $commandName")
                }
            }
        }
    }

    fun printCountries(countries: List<CountryDto>) {
        for (country in countries) {
            printCountry(country)
        }
    }

    fun printRegions(regions: List<RegionDto>) {
        for (region in regions) {
            printRegion(region)
        }
    }

    fun printCountry(country: CountryDto) {
        println(country)
    }

    fun printRegion(region: RegionDto) {
        println(region)
    }

    fun printHelp() {
        for (command in commands) {
            println("COMMAND ${command.name}")
            println("Takes ${command.argumentCount} arguments.")
            println()
        }
    }
}


fun main(args: Array<String>) {
    SimulClient().execute(args)
}



