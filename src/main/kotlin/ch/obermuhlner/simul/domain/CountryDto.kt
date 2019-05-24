package ch.obermuhlner.simul.domain

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class CountryDto(
        val id: Int,
        val name: String,
        val taxAgriculture: Double,
        val taxManufacture: Double,
        val gold: Double) {

    constructor(country: Country) : this(
            country.id,
            country.name,
            country.taxAgriculture,
            country.taxManufacture,
            country.gold)

    class ListDeserializer : ResponseDeserializable<List<CountryDto>> {
        override fun deserialize(content: String): List<CountryDto>
                = Gson().fromJson(content, Array<CountryDto>::class.java).toList()
    }

    class Deserializer : ResponseDeserializable<CountryDto> {
        override fun deserialize(content: String): CountryDto
                = Gson().fromJson(content, CountryDto::class.java)
    }
}
