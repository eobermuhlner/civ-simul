package ch.obermuhlner.simul.client.service

import ch.obermuhlner.simul.domain.CountryDto
import ch.obermuhlner.simul.domain.RegionDto
import ch.obermuhlner.simul.service.WorldService
import com.github.kittinunf.fuel.*

class RemoteWorldService : WorldService {

    override fun allCountries(): List<CountryDto> {
        val (_, _, result) = "http://localhost:8080/countries".httpGet().responseObject(CountryDto.ListDeserializer())
        return result.get()
    }

    override fun country(countryId: Int): CountryDto {
        val (_, _, result) = "http://localhost:8080/country/${countryId}".httpGet().responseObject(CountryDto.Deserializer())
        return result.get()
    }

    override fun allRegions(): List<RegionDto> {
        val (_, _, result) = "http://localhost:8080/regions".httpGet().responseObject(RegionDto.ListDeserializer())
        return result.get()
    }

    override fun countryRegions(countryId: Int): List<RegionDto> {
        val (_, _, result) = "http://localhost:8080/country/${countryId}/regions".httpGet().responseObject(RegionDto.ListDeserializer())
        return result.get()
    }

    override fun region(regionId: Int): RegionDto {
        val (_, _, result) = "http://localhost:8080/region/${regionId}".httpGet().responseObject(RegionDto.Deserializer())
        return result.get()
    }
}