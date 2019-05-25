package ch.obermuhlner.simul.shared.domain

import ch.obermuhlner.simul.server.model.domain.CountryModel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class Country(
        val id: Int,
        val name: String,
        val taxAgriculture: Double,
        val taxManufacture: Double,
        val gold: Double) {

    constructor(countryModel: CountryModel) : this(
            countryModel.id,
            countryModel.name,
            countryModel.taxAgriculture,
            countryModel.taxManufacture,
            countryModel.gold)

    class ListDeserializer : ResponseDeserializable<List<Country>> {
        override fun deserialize(content: String): List<Country>
                = Gson().fromJson(content, Array<Country>::class.java).toList()
    }

    class Deserializer : ResponseDeserializable<Country> {
        override fun deserialize(content: String): Country
                = Gson().fromJson(content, Country::class.java)
    }
}
