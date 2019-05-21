package ch.obermuhlner.simul.javafx

import ch.obermuhlner.simul.*
import javafx.util.converter.DoubleStringConverter
import tornadofx.*

class SimulView : View() {
    private val controller: SimulController by inject()

    private val countryModel = CountryModel(controller.world.countries[0])
    private val regionModel = RegionModel(controller.world.countries[0].regions[0])

    private val countries = controller.world.countries.asObservable()
    private val regions = mutableListOf<Region>().asObservable()

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
                field ("Agriculture Tax") {
                    textfield(countryModel.taxAcriculture, DoubleStringConverter())
                }
                field ("Manufacture Tax") {
                    textfield(countryModel.taxManufacture, DoubleStringConverter())
                }
                field("Gold") {
                    label(countryModel.gold)
                }
            }
            fieldset("Region") {
                field ("Name") {
                    label(regionModel.name)
                }
                field("Population") {
                    label(regionModel.population)
                }
                field("Agriculture Ratio") {
                    textfield(regionModel.agricultureRatio, DoubleStringConverter())
                }
                field("Agriculture Storage") {
                    label(regionModel.agricultureStorage)
                }
                field("Gold") {
                    label(regionModel.gold)
                }
                field("Luxury") {
                    label(regionModel.luxury)
                }
            }
        }
    }
}

class CountryModel(country: Country) : ItemViewModel<Country>(country) {
    var name = bind(Country::name)
    var taxAcriculture = bind(Country::taxAgriculture, true)
    var taxManufacture = bind(Country::taxManufacture, true)
    var gold = bind(Country::gold)
}

class RegionModel(region: Region) : ItemViewModel<Region>(region) {
    var name = bind(Region::name)
    var population = bind (Region::population)
    var agricultureRatio = bind(Region::agricultureRatio, true)
    var agricultureStorage = bind(Region::agricultureStorage)
    var gold = bind(Region::gold)
    var luxury = bind(Region::luxury)
}

class SimulController : Controller() {
    val world: World = World().apply {
        createCountry("Castile").apply {
            addRegion(createRegion("Toledo").apply {
                population = 10.0
                agriculture = 20.0
            })
            addRegion(createRegion("Sevilla").apply {
                population = 10.0
                agriculture = 30.0
            })
            taxAgriculture = 0.1
        }
        createCountry("Portugal").apply {
            addRegion(createRegion("Lisbon").apply {
                population = 10.0
                agriculture = 30.0
            })
            addRegion(createRegion("Algarve").apply {
                population = 10.0
                agriculture = 20.0
            })
            taxAgriculture = 0.1
        }
    }

    private val simulation: Simulation = SimulationLoader().load()

    fun simulate() {
        simulation.simulate(world)
    }
}

class SimulApp : App(SimulView::class)

fun main(args: Array<String>) {
    launch<SimulApp>(args)
}
