package ch.obermuhlner.simul.javafx

import ch.obermuhlner.simul.*
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

class SimulView : View() {
    val controller: SimulController by inject()
    val countryAgricultureStorage = SimpleDoubleProperty()
    val regionPopulation = SimpleDoubleProperty()
    val regionAgricultureStorage = SimpleDoubleProperty()

    override val root = borderpane {
        top = hbox {
            button("Step") {
                action {
                    runAsync {
                        controller.simulate()
                    } ui {
                        updateProperties()
                    }
                }
            }
        }
        center = borderpane {
            left = listview(controller.countries)
            center = listview(controller.regions)
        }
        right = form {
            fieldset("Country") {
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

    fun updateProperties() {
        countryAgricultureStorage.value = controller.countries[0].agricultureStorage
        regionPopulation.value = controller.regions[0].population
        regionAgricultureStorage.value = controller.regions[0].agricultureStorage
    }
}

class SimulController : Controller() {
    val world: World
    val simulation: Simulation = SimulationLoader().load()

    val countries: ObservableList<Country>
    val regions: ObservableList<Region>

    init {
        world = World()
        val region = world.createRegion("region")
        region.population = 10.0
        region.agriculture = 20.0

        val country = world.createCountry("country")
        country.regions += region
        country.taxAgriculture = 0.1

        countries = FXCollections.observableList(world.countries)
        regions = FXCollections.observableList(world.regions)
    }

    fun simulate() {
        simulation.simulate(world)
    }
}

class SimulApp : App(SimulView::class)

fun main(args: Array<String>) {
    launch<SimulApp>(args)
}