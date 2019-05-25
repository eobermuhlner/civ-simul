package ch.obermuhlner.simul.shared.domain

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class Simulation(val ticks: Int) {

    class Deserializer : ResponseDeserializable<Simulation> {
        override fun deserialize(content: String): Simulation
                = Gson().fromJson(content, Simulation::class.java)
    }
}