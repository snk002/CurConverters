package data.mappers

import data.models.FinalLotData
import data.models.FinalLotSafeData

/** map [FinalLotSafeData] to the strict [FinalLotData] format */
fun FinalLotSafeData.asFinalLotData() : FinalLotData {
        //also skip possible thousands separator
        val minEstimate: Double = minEstimate.replace(",","").toDoubleOrNull() ?: 0.0
        val maxEstimate: Double = maxEstimate.replace(",","").toDoubleOrNull() ?: 0.0
        val openingPrice: Double = openingPrice.replace(",","").toDoubleOrNull() ?: 0.0
        val priceRealised: Double = priceRealised.replace(",","").toDoubleOrNull() ?: 0.0
        //skip possible % sign
        val premium: Double = premium.replace("%","").toDoubleOrNull() ?: 0.0
        //convert to int
        val globalHouseCode : Int = globalHouseCode.toIntOrNull() ?: 0
        return FinalLotData(platformLotId, lotTitle, lotCategory, lotTags, minEstimate, maxEstimate, openingPrice,
            lotStatus, saleDate, priceRealised, houseName, saleId, saleName, auctionCountry, auctionCity, lotNumber,
            lotCurrency, lotDescription, originalUrl, imageCount, imageList, houseCode, globalHouseCode, premium, platformCode)
    }
