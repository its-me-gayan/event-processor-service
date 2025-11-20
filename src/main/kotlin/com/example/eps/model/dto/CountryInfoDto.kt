package com.example.eps.model.dto

import java.time.Instant
import java.util.UUID


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/19/25
 * Time: 11:53 PM
 */
data class CountryInfoDto (
    val countryName: String,
    val isIndependent: Boolean,
    val isUnMember: Boolean,
    val capital: String,
    val region: String,
    val population: Double,

)
{
    companion object {
    fun from(
        countryName: String,
        isIndependent: Boolean,
        isUnMember: Boolean,
        capital: String,
        region: String,
        population: Double,
    ): CountryInfoDto =
        CountryInfoDto(
            countryName = countryName,
            isIndependent = isIndependent,
            isUnMember = isUnMember,
            capital = capital,
            region = region,
            population = population
        )
}
}