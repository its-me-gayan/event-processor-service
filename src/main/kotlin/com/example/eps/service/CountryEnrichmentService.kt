package com.example.eps.service

import com.example.eps.model.dto.CountryInfoDto


/**
 * Author: Gayan Sanjeewa
 * User: gayan
 * Date: 11/20/25
 * Time: 12:06 AM
 */
interface CountryEnrichmentService {

    fun fetchCountryInfo(countryCode:String): CountryInfoDto
}