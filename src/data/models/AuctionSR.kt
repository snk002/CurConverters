package data.models

import com.google.gson.annotations.SerializedName
import domain.models.Auction
import domain.models.House
import domain.models.Sources

/**
 * SR auction entity
 * See usage notes in [Auction]
 */
data class AuctionSR (
    @SerializedName("Currency")
    override var currency: String,
    @SerializedName("Buyers premium")
    override var premium: String,
    @SerializedName("www")
    override val site: String,
    @SerializedName("auctioneer")
    override val houseName: String,
    @SerializedName("auctionCountry")
    override val country: String,
    @SerializedName("auctionCity")
    override val city: String,
    override var code: String = "",
    ) : Auction() {

    override fun toCSV() : String {
        return "$id\t$code\t$houseName\t$country\t$city\t$currency\t$premium\t$site"
    }

    override fun toHouse(src: Sources, id: Int) =
        House(id, code,0, houseName, country, city, currency, premium.removeSuffix("%").toDouble(), site, src.toString())

    /**
     * Extract code from auction filename
     * @param value filename without extension
     */
    override fun fixCode(value: String) {
        code = value.dropLast(5)
    }
}
