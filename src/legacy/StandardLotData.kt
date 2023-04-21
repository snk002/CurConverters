import auctionIndex
import com.google.gson.annotations.SerializedName
import getCat
import houseIndex
import prices
import java.lang.Exception

//Legacy!

data class StandardLotData(val platformCode: String = "SR") {
    var platformLotId: String = ""
    var lotTitle: String = ""
    var lotCategory : String = ""
    var lotTags : String = ""
    var minEstimate : Float = 0F
    var maxEstimate : Float = 0F
    var openingPrice : Float = 0F
    var lotStatus : String = ""
    var saleDate : String = ""
    var priceRealised : Float = 0F
    var houseName : String = ""
    var saleId : String = ""
    var saleName : String = ""
    var auctionCountry : String = ""
    var auctionCity : String = ""
    var lotNumber : String = ""
    var lotCurrency: String = ""
    var lotDescription: String = "'"
    var originalUrl : String = ""
    var imageCount : Int = 0
    @SerializedName("images")
    var imageList : List<String> = mutableListOf()
    var houseCode: String = ""
    var globalHouseCode: String = ""
    var premium: Float = 0F
    // Converts [SRLotData] to [StandardLotData]
    fun assignSR(data: SRLotData) {
        imageList = data.imageList
        imageCount = imageList.size
        originalUrl = data.url
        lotDescription = data.description
        lotCurrency = data.currency
        lotNumber = data.lotNumber
        auctionCity = data.auctionCity
        auctionCountry = data.auctionCountry
        saleName = data.catalogueName
        saleId = data.catalogueId
        houseName = data.auctioneer
        priceRealised = try {data.amount.toFloat() } catch (e: Exception) {
            prices++
            0F }
        saleDate = data.auction_date
        lotStatus = data.lotStatus
        openingPrice = try {data.openingPrice.toFloat() } catch (e: Exception) { 0F }
        minEstimate = try {data.minEstimate.toFloat() } catch (e: Exception) { 0F }
        maxEstimate = try {data.maxEstimate.toFloat() } catch (e: Exception) { 0F }
        lotTitle = data.lotName
        platformLotId = data.lotId   //"%3ca href%3d/de-de/search-amulet%3eSchmuck%3c/a%3e"
        lotTags = data.lotTags.replace("%3ca href%3d/[a-z\\-]*/[,& a-zA-Z3\\-%]*/a%3e".toRegex(), " ")
        lotCategory = data.lotCategory.replace("%3ca href%3d/[a-z\\-]*/[,& a-zA-Z3\\-%]*/a%3e".toRegex(), " ").trim()
        if (lotCategory == "") lotCategory = getCat(data.categoryCode)
        if (lotCategory == "") lotCategory = getCat(data.categoryCodeNew)
        var extraTags = "" //category, tags
        if (lotCategory!="") extraTags = lotCategory
        if (data.lotItemType!="") extraTags = extraTags +" " + data.lotItemType.replace("%3ca href%3d/[a-z\\-]*/[,& a-zA-Z3\\-%]*/a%3e".toRegex(), " ")
        lotTags = "$lotTags $extraTags"
        lotTags = lotTags.replace(" ,", " ").replace("  ", " ").trim()
        houseCode = saleId.dropLast(5)
        houseIndex[houseCode]?.let {
            globalHouseCode = it
        }
        auctionIndex[saleId]?.let {
            lotCurrency = it.currency
            premium = try { it.premium.removeSuffix("%").toFloat() } catch (e: Exception) { 0F }
        }
    }
}
