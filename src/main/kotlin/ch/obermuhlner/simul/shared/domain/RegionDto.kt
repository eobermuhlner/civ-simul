package ch.obermuhlner.simul.shared.domain

import ch.obermuhlner.simul.server.model.domain.Region
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class RegionDto(
        val id: Int,
        val name: String,
        val country: Int?,

        val agricultureRatio: Double,
        val population: Double,

        val agricultureStorage: Double,
        val gold: Double,
        val luxury: Double) {

    constructor(region: Region) : this(
            region.id,
            region.name,
            region.country?.id,
            region.agricultureRatio,
            region.population,
            region.agricultureStorage,
            region.gold,
            region.luxury)

    class ListDeserializer : ResponseDeserializable<List<RegionDto>> {
        override fun deserialize(content: String): List<RegionDto>
                = Gson().fromJson(content, Array<RegionDto>::class.java).toList()
    }

    class Deserializer : ResponseDeserializable<RegionDto> {
        override fun deserialize(content: String): RegionDto
                = Gson().fromJson(content, RegionDto::class.java)
    }

}
