package ch.obermuhlner.simul.app

import ch.obermuhlner.simul.client.service.RemoteAdminService
import ch.obermuhlner.simul.client.service.RemoteWorldService
import ch.obermuhlner.simul.server.RealAdminService
import ch.obermuhlner.simul.server.RealWorldService
import ch.obermuhlner.simul.server.SimulServer
import ch.obermuhlner.simul.shared.domain.*
import ch.obermuhlner.simul.shared.service.AdminService
import ch.obermuhlner.simul.shared.service.WorldService
import com.google.gson.Gson
import org.springframework.boot.SpringApplication

class Command(val name: String, val argumentCount: Int, val block: (List<String>) -> Unit)

enum class PrintFormat {
    ToString,
    Json,
    Pretty
}

class SimulApp {
    private var adminService : AdminService = RealAdminService()
    private var worldService : WorldService = RealWorldService()

    private var printFormat = PrintFormat.Pretty

    private val commands: List<Command> = listOf(
            Command("--pretty", 0) {
                printFormat = PrintFormat.Pretty
            },
            Command("--json", 0) {
                printFormat = PrintFormat.Json
            },
            Command("--tostring", 0) {
                printFormat = PrintFormat.ToString
            },
            Command("--connect", 0) {
                worldService = RemoteWorldService()
                adminService = RemoteAdminService()
            },
            Command("--standalone", 0) {
                worldService = RealWorldService()
                adminService = RealAdminService()
            },
            Command("help", 0) {
                printHelp()
            },
            Command("server", 0) {
                SpringApplication.run(SimulServer::class.java)
            },
            Command("countries", 0) {
                printCountries(worldService.allCountries())
            },
            Command("country", 1) { arguments ->
                val countryId = arguments[0].toInt()
                worldService.country(countryId)?.let { printCountry(it) }
            },
            Command("regions", 0) {
                printRegions(worldService.allRegions())
            },
            Command("country-regions", 1) { arguments ->
                val countryId = arguments[0].toInt()
                printRegions(worldService.countryRegions(countryId))
            },
            Command("region", 1) { arguments ->
                val regionId = arguments[0].toInt()
                worldService.region(regionId)?.let { printRegion(it) }
            },
            Command("set-tax-agriculture", 2) { arguments ->
                val countryId = arguments[0].toInt()
                val value = arguments[1].toDouble()
                worldService.action(TaxAgricultureAction(countryId, value))
            },
            Command("set-tax-manufacture", 2) { arguments ->
                val countryId = arguments[0].toInt()
                val value = arguments[1].toDouble()
                worldService.action(TaxManufactureAction(countryId, value))
            },
            Command("set-agriculture-ratio", 2) { arguments ->
                val regionId = arguments[0].toInt()
                val value = arguments[1].toDouble()
                worldService.action(AgricultureRatioAction(regionId, value))
            },
            Command("simulate", 0) {
                printSimulation(worldService.simulate())
            },
            Command("shutdown", 0) {
                adminService.shutdown()
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
                        command.block(commandArguments)
                    } else {
                        println("Command $commandName expects ${command.argumentCount} arguments")
                    }
                } else {
                    println("Unknown command: $commandName")
                }
            }
        }
    }

    private fun printCountries(countries: List<Country>) {
        for (country in countries) {
            printCountry(country)
        }
    }

    private fun printRegions(regions: List<Region>) {
        for (region in regions) {
            printRegion(region)
        }
    }

    private fun printCountry(country: Country) {
        when (printFormat) {
            PrintFormat.Pretty -> {
                println("CountryModel: ${country.name}")
                println("    id: ${country.id}")
                println("    taxAgriculture: ${country.taxAgriculture}")
                println("    taxManufacture: ${country.taxManufacture}")
                println("    gold: ${country.gold}")
            }
            PrintFormat.Json -> {
                println(Gson().toJson(country))
            }
            PrintFormat.ToString -> {
                println(country)
            }
        }
    }

    private fun printRegion(region: Region) {
        when (printFormat) {
            PrintFormat.Pretty -> {
                println("RegionModel: ${region.name}")
                println("    id: ${region.id}")
                println("    countryModel: ${region.country}")
                println("    population: ${region.population}")
                println("    agricultureRatio: ${region.agricultureRatio}")
                println("    agricultureStorag: ${region.agricultureStorage}")
                println("    gold: ${region.gold}")
                println("    luxury: ${region.luxury}")
            }
            PrintFormat.Json -> {
                println(Gson().toJson(region))
            }
            PrintFormat.ToString -> {
                println(region)
            }
        }
    }

    private fun printSimulation(simulation: Simulation) {
        when (printFormat) {
            PrintFormat.Pretty -> {
                println("Simulator:")
                println("    ticks: ${simulation.ticks}")
            }
            PrintFormat.Json -> {
                println(Gson().toJson(simulation))
            }
            PrintFormat.ToString -> {
                println(simulation)
            }
        }
    }

    private fun printHelp() {
        for (command in commands) {
            println("COMMAND ${command.name}")
            println("    Takes ${command.argumentCount} arguments.")
            println()
        }
    }
}


fun main(args: Array<String>) {
    SimulApp().execute(args)
}
