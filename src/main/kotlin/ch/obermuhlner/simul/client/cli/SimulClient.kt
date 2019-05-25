package ch.obermuhlner.simul.client.cli

import ch.obermuhlner.simul.client.service.RemoteWorldService
import ch.obermuhlner.simul.shared.domain.CountryDto
import ch.obermuhlner.simul.shared.domain.RegionDto
import ch.obermuhlner.simul.shared.service.WorldService
import com.google.gson.Gson

class Command(val name: String, val argumentCount: Int, val block: (WorldService, List<String>) -> Unit)

enum class PrintFormat {
    Simple,
    Json,
    Pretty
}

class SimulClient(val worldService : WorldService) {
    private var printFormat = PrintFormat.Pretty

    private val commands: List<Command> = listOf(
            Command("--pretty", 0) { _, _ ->
                printFormat = PrintFormat.Pretty
            },
            Command("--json", 0) { _, _ ->
                printFormat = PrintFormat.Json
            },
            Command("--simple", 0) { _, _ ->
                printFormat = PrintFormat.Simple
            },
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

    private fun printCountries(countries: List<CountryDto>) {
        for (country in countries) {
            printCountry(country)
        }
    }

    private fun printRegions(regions: List<RegionDto>) {
        for (region in regions) {
            printRegion(region)
        }
    }

    private fun printCountry(country: CountryDto) {
        when (printFormat) {
            PrintFormat.Pretty -> {
                println("Country: ${country.name}")
                println("    id: ${country.id}")
                println("    taxAgriculture: ${country.taxAgriculture}")
                println("    taxManufacture: ${country.taxManufacture}")
                println("    gold: ${country.gold}")
            }
            PrintFormat.Json -> {
                println(Gson().toJson(country))
            }
            PrintFormat.Simple -> {
                println(country)
            }
        }
    }

    private fun printRegion(region: RegionDto) {
        when (printFormat) {
            PrintFormat.Pretty -> {
                println("Region: ${region.name}")
                println("    id: ${region.id}")
                println("    country: ${region.country}")
                println("    population: ${region.population}")
                println("    agricultureRatio: ${region.agricultureRatio}")
                println("    agricultureStorag: ${region.agricultureStorage}")
                println("    gold: ${region.gold}")
                println("    luxury: ${region.luxury}")
            }
            PrintFormat.Json -> {
                println(Gson().toJson(region))
            }
            PrintFormat.Simple -> {
                println(region)
            }
        }
    }

    private fun printHelp() {
        for (command in commands) {
            println("COMMAND ${command.name}")
            println("Takes ${command.argumentCount} arguments.")
            println()
        }
    }
}


fun main(args: Array<String>) {
    SimulClient(RemoteWorldService()).execute(args)
}



