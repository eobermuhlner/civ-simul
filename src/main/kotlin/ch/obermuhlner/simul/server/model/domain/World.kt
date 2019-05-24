package ch.obermuhlner.simul.server.model.domain


data class Region(
        val id: Int,
        val name: String,
        var country: Country?,

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
        val to: Region)


data class Point(
        val x: Double,
        val y: Double)


data class Polygon(
        val points: List<Point>)


data class Country(
        val id: Int,
        val name: String,
        var taxAgriculture: Double = 0.1,
        var taxManufacture: Double = 0.2,
        var gold: Double = 0.0,
        val regions: MutableList<Region> = mutableListOf(),
        val countriesWar: MutableList<Country> = mutableListOf(),
        val countriesAlly: MutableList<Country> = mutableListOf()) {

    fun addRegion(region: Region) {
        regions += region
        region.country = this
    }

    fun removeRegion(region: Region) {
        regions -= region
        region.country = null
    }
}


sealed class Action

class DeclareWarAction(val actor: Country, val other: Country) : Action()
class ProposePeaceAction(val actor: Country, val other: Country) : Action()
class AcceptPeaceAction(val actor: Country, val other: Country) : Action()

class World {
    val regions: MutableList<Region> = mutableListOf()
    val regionConnections: MutableMap<Region, MutableList<RegionConnection>> = mutableMapOf()
    val regionPolygons: MutableMap<Region, Polygon> = mutableMapOf()
    val countries: MutableList<Country> = mutableListOf()
    val actions: MutableList<Action> = mutableListOf()

    fun createRegion(name: String): Region {
        val region = Region(regions.size, name, null)
        regions += region
        return region
    }

    fun createRegionConnection(from: Region, to: Region) {
        val connection = RegionConnection(to)
        regionConnections.getOrPut(from) { mutableListOf() } += connection
    }

    fun createRegionPolygon(region: Region, polygon: Polygon) {
        regionPolygons[region] = polygon
    }

    fun createCountry(name: String): Country {
        val country = Country(regions.size, name)
        countries += country
        return country
    }
}
