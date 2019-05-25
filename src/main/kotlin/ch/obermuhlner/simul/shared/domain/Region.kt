package ch.obermuhlner.simul.shared.domain

import ch.obermuhlner.simul.server.model.domain.RegionModel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class Region(
        val id: Int,
        val name: String,
        val country: Int?,

        val agricultureRatio: Double,
        val population: Double,

        val agricultureStorage: Double,
        val gold: Double,
        val luxury: Double) {

    constructor(regionModel: RegionModel) : this(
            regionModel.id,
            regionModel.name,
            regionModel.countryModel?.id,
            regionModel.agricultureRatio,
            regionModel.population,
            regionModel.agricultureStorage,
            regionModel.gold,
            regionModel.luxury)

    class ListDeserializer : ResponseDeserializable<List<Region>> {
        override fun deserialize(content: String): List<Region>
                = Gson().fromJson(content, Array<Region>::class.java).toList()
    }

    class Deserializer : ResponseDeserializable<Region> {
        override fun deserialize(content: String): Region
                = Gson().fromJson(content, Region::class.java)
    }
}
