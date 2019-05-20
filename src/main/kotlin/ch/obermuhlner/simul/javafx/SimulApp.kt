package ch.obermuhlner.simul.javafx

import ch.obermuhlner.simul.*
import javafx.util.converter.DoubleStringConverter
import tornadofx.*

class SimulView : View() {
    val controller: SimulController by inject()

    val countryTaxAcriculture = observable(controller.country, Country::taxAgriculture)
    val countryAgricultureStorage = observable(controller.country, Country::agricultureStorage::get, Country::agricultureStorage::set)

    val regionPopulation = observable(controller.region, Region::population::get, Region::population::set)
    val regionAgricultureStorage = observable(controller.region, Region::agricultureStorage::get, Region::agricultureStorage::set)

    override val root = borderpane {
        top = hbox {
            button("Step") {
                action {
                    runAsync {
                        controller.simulate()
                    } ui {
                        countryAgricultureStorage.refresh()

                        regionPopulation.refresh()
                        regionAgricultureStorage.refresh()
                    }
                }
            }
        }
        center = borderpane {
            left = listview(controller.world.countries.asObservable()) {
                cellFormat {
                    text = item.name
                }
            }
            center = listview(controller.world.regions.asObservable()) {
                cellFormat {
                    text = item.name
                }
            }
        }
        right = form {
            fieldset("Country") {
                field ("Tax") {
                    textfield(countryTaxAcriculture, DoubleStringConverter())
                }
                field("Agriculture Storage") {
                    label(countryAgricultureStorage)
                }
            }
            fieldset("Region") {
                field("Population") {
                    label(regionPopulation)
                }
                field("Agriculture Storage") {
                    label(regionAgricultureStorage)
                }
            }
        }
    }
}

class SimulController : Controller() {
    val world: World = World()
    val country: Country
    val region: Region

    val simulation: Simulation = SimulationLoader().load()

    init {
        region = world.createRegion("Toledo")
        region.population = 10.0
        region.agriculture = 20.0

        country = world.createCountry("Castile")
        country.regions += region
        country.taxAgriculture = 0.1
    }

    fun simulate() {
        simulation.simulate(world)
    }
}

class SimulApp : App(SimulView::class)

fun main(args: Array<String>) {
    launch<SimulApp>(args)
}