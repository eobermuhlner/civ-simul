package ch.obermuhlner.simul.server.model.domain

import ch.obermuhlner.simul.shared.domain.Action


data class RegionModel(
        val id: Int,
        val name: String,
        var countryModel: CountryModel?,

        var agriculture: Double = 100.0,
        var agriculturePerPopulation: Double = 1.2,
        var agricultureRatio: Double = 0.9,
        var manufacturePerPopulation: Double = 1.3,
        var population: Double = 0.0,

        var agricultureProduce: Double = 0.0,
        var agricultureStorage: Double = 0.0,
        var manufactureProduce: Double = 0.0,
        var gold: Double = 0.0,
        var luxury: Double = 0.0)


data class RegionConnection(
        val to: RegionModel)


data class Point(
        val x: Double,
        val y: Double)


data class Polygon(
        val points: List<Point>)


data class CountryModel(
        val id: Int,
        val name: String,
        var taxAgriculture: Double = 0.1,
        var taxManufacture: Double = 0.2,
        var gold: Double = 0.0,
        val regionModels: MutableList<RegionModel> = mutableListOf(),
        val countriesWar: MutableList<CountryModel> = mutableListOf(),
        val countriesAlly: MutableList<CountryModel> = mutableListOf()) {

    fun addRegion(regionModel: RegionModel) {
        regionModels += regionModel
        regionModel.countryModel = this
    }

    fun removeRegion(regionModel: RegionModel) {
        regionModels -= regionModel
        regionModel.countryModel = null
    }
}


class WorldModel {
    val regionModels: MutableList<RegionModel> = mutableListOf()
    val regionModelConnections: MutableMap<RegionModel, MutableList<RegionConnection>> = mutableMapOf()
    val regionModelPolygons: MutableMap<RegionModel, Polygon> = mutableMapOf()
    val countryModels: MutableList<CountryModel> = mutableListOf()
    val actions: MutableList<Action> = mutableListOf()

    fun createRegion(name: String): RegionModel {
        val region = RegionModel(regionModels.size, name, null)
        regionModels += region
        return region
    }

    fun createRegionConnection(from: RegionModel, to: RegionModel) {
        val connection = RegionConnection(to)
        regionModelConnections.getOrPut(from) { mutableListOf() } += connection
    }

    fun createRegionPolygon(regionModel: RegionModel, polygon: Polygon) {
        regionModelPolygons[regionModel] = polygon
    }

    fun createCountry(name: String): CountryModel {
        val country = CountryModel(regionModels.size, name)
        countryModels += country
        return country
    }

    fun country(countryId: Int): CountryModel {
        return countryModels[countryId]
    }

    fun region(regionId: Int): RegionModel {
        return regionModels[regionId]
    }
}
