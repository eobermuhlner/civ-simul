package ch.obermuhlner.simul.client.javafx

import ch.obermuhlner.simul.server.model.domain.CountryModel
import ch.obermuhlner.simul.server.model.domain.RegionModel
import ch.obermuhlner.simul.server.model.domain.WorldModel
import ch.obermuhlner.simul.server.model.service.Simulator
import ch.obermuhlner.simul.server.model.service.SimulationLoader
import ch.obermuhlner.simul.server.model.service.WorldLoader
import javafx.beans.property.ListProperty
import javafx.util.converter.DoubleStringConverter
import tornadofx.*

class SimulView : View() {
    private val controller: SimulController by inject()

    private val countryModel = CountryModel(controller.worldModel.countryModels[0])
    private val regionModel = RegionModel(controller.worldModel.countryModels[0].regionModels[0])

    private val countries = controller.worldModel.countryModels.asObservable()
    private val regions = mutableListOf<RegionModel>().asObservable()

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
                    regions.addAll(newCountry.regionModels)
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
            fieldset("CountryModel") {
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
                field("War") {
                    listview(countryModel.countriesWar)
                }
            }
            fieldset("RegionModel") {
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

class CountryModel(countryModel: CountryModel) : ItemViewModel<CountryModel>(countryModel) {
    var name = bind(CountryModel::name)
    var taxAcriculture = bind(CountryModel::taxAgriculture, true)
    var taxManufacture = bind(CountryModel::taxManufacture, true)
    var gold = bind(CountryModel::gold)
    var countriesWar = bind(CountryModel::countriesWar) as ListProperty<CountryModel>
}

class RegionModel(regionModel: RegionModel) : ItemViewModel<RegionModel>(regionModel) {
    var name = bind(RegionModel::name)
    var population = bind (RegionModel::population)
    var agricultureRatio = bind(RegionModel::agricultureRatio, true)
    var agricultureStorage = bind(RegionModel::agricultureStorage)
    var gold = bind(RegionModel::gold)
    var luxury = bind(RegionModel::luxury)
}

class SimulController : Controller() {
    val worldModel: WorldModel = WorldLoader().load()

    private val simulator: Simulator = SimulationLoader().load()

    fun simulate() {
        simulator.simulate(worldModel)
    }
}

class SimulApp : App(SimulView::class)

fun main(args: Array<String>) {
    launch<SimulApp>(args)
}
