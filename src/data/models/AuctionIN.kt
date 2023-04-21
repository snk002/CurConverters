package data.models

import com.google.gson.annotations.SerializedName
import domain.models.Auction
import domain.models.House
import domain.models.Sources
import java.lang.Exception

/**
 * IN auction entity
 * See usage notes in [Auction]
 */
data class AuctionIN (
    @SerializedName("location_countryCode")
    override var currency: String,
    @SerializedName("groupView_buyersPremiums_buyersPremiumAmount")
    override var premium: String,
    @SerializedName("web")
    override val site: String,
    @SerializedName("sellerName")
    override val houseName: String,
    @SerializedName("addressCountry")
    override val country: String,
    @SerializedName("addressLocality")
    override val city: String,
    @SerializedName("sellerId")
    override var code: String = "",
    @SerializedName("eventDateIso8601")
    var date: String = ""
) : Auction() {

    /** Return currency code based on country code */
    private fun countryToCurrency() : String = when (currency.trim().uppercase()) {
        "US" -> "USD"
        "SE" -> "SEK"
        "DK" -> "DKK"
        "NO" -> "NOK"
        "CA" -> "CAD"
        "AU" -> "AUD"
        "NZ" -> "NZD"
        "HK" -> "HKD"
        "CN" -> "CNY"
        "CH" -> "CHF"
        "AR" -> "ARS"
        "ZA" -> "ZAR"
        "TR" -> "TRY"
        "PE" -> "PEN"
        "MX" -> "MXN"
        "IN" -> "INR"
        "ID" -> "IDR"
        "TW" -> "TWD"
        "HU" -> "HUF"
        "IL" -> "ILS"
        "PL" -> "PLN"
        "MY" -> "MYR"
        "MA" -> "MAD"
        "UA" -> "UAH"
        "CZ" -> "CZK"
        "SG" -> "SGD"
        "JP" -> "JPY"
        "RU" -> "RUB"
        "TH" -> "THB"
        "RO" -> "RON"
        "RS" -> "RSD"
        "KR" -> "KRW"
        "PH" -> "PHP"
        "DE","FR","BE","NL","AT","FI","IT","ES","PT","LV","LT","IE","EE","MC","GR","SK","LU" -> "EUR"
        "UK","GB" -> "GBP"
        else -> ""
    }

    private fun fixPremium() : String = try {
        premium.equals(null)
        premium
    } catch (E: Exception) {
        "0"
    }

    override fun toCSV(): String {
        return "$id\t$code\t$houseName\t$country\t$city\t${countryToCurrency()}\t${fixPremium()}\t$site"
    }

    override fun toHouse(src: Sources, id: Int) =
        House(id, code,0, houseName, country, city, countryToCurrency(), fixPremium().removeSuffix("%").toDouble(), site, src.toString())

    override fun fixCode(value: String) {}

    fun toShortCSV(): String {
        return "$id\t${code.uppercase()}\t$date"
    }

    fun fixDate() : Boolean {
        if (date.isNullOrBlank()) {
            return false
        }
        date = date.dropLast(12)
        return true
    }
}
