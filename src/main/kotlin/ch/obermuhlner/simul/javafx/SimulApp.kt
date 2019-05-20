package ch.obermuhlner.simul.javafx

import ch.obermuhlner.simul.*
import javafx.util.converter.DoubleStringConverter
import tornadofx.*

class SimulView : View() {
    val controller: SimulController by inject()

    val countryModel = CountryModel(controller.world.countries[0])
    val regionModel = RegionModel(controller.world.countries[0].regions[0])

    val countries = controller.world.countries.asObservable()
    val regions = mutableListOf<Region>().asObservable()

    override val root = borderpane {
        top = hbox {
            button("Step") {
                action {
                    runAsync {
                        controller.simulate()
                    } ui {
                        countryModel.rollback()
                        regionModel.rollback()
                    }
                }
            }
        }
        center = borderpane {
            left = listview(countries) {
                cellFormat {
                    text = item.name
                }

                selectionModel.selectedItemProperty().addListener { _, _, newCountry ->
                    regions.clear()
                    regions.addAll(newCountry.regions)
                }
                countryModel.rebindOnChange(this) {
                    item = it
                }
                selectionModel.select(countries[0])
            }
            center = listview(regions) {
                cellFormat {
                    text = item.name
                }

                regionModel.rebindOnChange(this) {
                    item = it
                }
            }
        }
        right = form {
            fieldset("Country") {
                field ("Name") {
                    label(countryModel.name)
                }
                field ("Tax") {
                    textfield(countryModel.taxAcriculture, DoubleStringConverter())
                }
                field("Agriculture Storage") {
                    label(countryModel.agricultureStorage)
                }
            }
            fieldset("Region") {
                field ("Name") {
                    label(regionModel.name)
                }
                field("Population") {
                    label(regionModel.population)
                }
                field("Agriculture Storage") {
                    label(regionModel.agricultureStorage)
                }
            }
        }
    }
}

class CountryModel(country: Country) : ItemViewModel<Country>(country) {
    var name = bind(Country::name)
    var taxAcriculture = bind(Country::taxAgriculture)
    var agricultureStorage = bind(Country::agricultureStorage)
}

class RegionModel(region: Region) : ItemViewModel<Region>(region) {
    var name = bind(Region::name)
    var population = bind (Region::population)
    var agricultureStorage = bind(Region::agricultureStorage)
}

class SimulController : Controller() {
    val world: World = World()

    val simulation: Simulation = SimulationLoader().load()

    init {
        with (world.createCountry("Castile")) {
            addRegion(with (world.createRegion("Toledo")) {
                population = 10.0
                agriculture = 20.0
                this
            })
            addRegion(with (world.createRegion("Sevilla")) {
                population = 10.0
                agriculture = 30.0
                this
            })
            taxAgriculture = 0.1
        }
        with (world.createCountry("Portugal")) {
            addRegion(with (world.createRegion("Lisbon")) {
                population = 10.0
                agriculture = 30.0
                this
            })
            addRegion(with (world.createRegion("Algarve")) {
                population = 10.0
                agriculture = 20.0
                this
            })
            taxAgriculture = 0.1
        }
    }

    fun simulate() {
        simulation.simulate(world)
    }
}

class SimulApp : App(SimulView::class)

fun main(args: Array<String>) {
    launch<SimulApp>(args)
}