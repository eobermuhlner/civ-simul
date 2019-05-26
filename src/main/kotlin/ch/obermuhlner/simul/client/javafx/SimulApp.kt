package ch.obermuhlner.simul.client.javafx

import ch.obermuhlner.simul.client.service.RemoteWorldService
import ch.obermuhlner.simul.server.model.domain.CountryModel
import ch.obermuhlner.simul.server.model.domain.RegionModel
import ch.obermuhlner.simul.server.model.domain.WorldModel
import ch.obermuhlner.simul.server.model.service.Simulator
import ch.obermuhlner.simul.server.model.service.SimulationLoader
import ch.obermuhlner.simul.server.model.service.WorldLoader
import ch.obermuhlner.simul.shared.domain.Country
import ch.obermuhlner.simul.shared.domain.Region
import ch.obermuhlner.simul.shared.service.WorldService
import javafx.beans.property.ListProperty
import javafx.util.converter.DoubleStringConverter
import tornadofx.*

class SimulView : View() {
    private val controller: SimulController by inject()

    private val countryModel = CountryModel(controller.worldService.country(0)!!)
    private val regionModel = RegionModel(controller.worldService.region(0)!!)

    private val countries = controller.worldService.allCountries().asObservable()
    private val regions = mutableListOf<RegionModel>().asObservable()

    override val root = borderpane {
        top = hbox {
            button("Step") {
                action {
                    runAsync {
                        controller.worldService.simulate()
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
                    // TODO regions.addAll(newCountry.regions)
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

                /* TODO regions
                regionModel.rebindOnChange(this) {
                    item = it
                }
                */
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
    val worldService: WorldService = RemoteWorldService()
}

class SimulApp : App(SimulView::class)

fun main(args: Array<String>) {
    launch<SimulApp>(args)
}
