import legacy.BaseLot

//Legacy!

import com.google.gson.annotations.SerializedName
import getCat

/*
"pageType" : "Lot - Expired",-
"platformCode" : "SR",
"referrer" : "",
"lotId" : "019314f2-2dc0-4566-adc0-a9b400fa79d5",
"lotName" : "Une grande carte de voeux signee par Herge et ornee dune illustration de la [...]",
"categoryCode" : "",
"lotCategory" : "",
"lotTags" : "",
"lotItemType" : "",
"minEstimate" : "200.00",
"maxEstimate" : "250.00",
"openingPrice" : "200.00",
"lotDescription" : "117",
"lotImageCount" : "1",
"auctioneer" : "68 Art Auction",
"lotStartDate" : "2018-12-16",
"lotEndDate" : "",
"lotPrimaryCategory" : "Unknown",
"lotStatus" : "WaitingToBeOffered",
"currentWatchers" : "2",
"catalogueId" : "68-art10001",
"catalogueName" : "Comics Sale",
"auctionCountry" : "Belgium",
"auctionCity" : "Wavre",
"enhancedSearch" : "False",
"lot-number":"23",
"auction_date":"2018-12-16T02-00-00Z",
"categoryCodeNew":"FAA",
"url":"https://www.the-saleroom.com/en-gb/auction-catalogues/68artauction/catalogue-id-68-art10001/lot-019314f2-2dc0-4566-adc0-a9b400fa79d5",
"Amount":"Not publish",
"Currency closed":"",
"Content translate":"Une grande carte de voeux signée par Hergé et ornée d\'une illustration de la bataille de Zileheroum. TBE+. Format A4.",
"img":["https://cdn.globalauctionplatform.com/e5074132-a1d8-457b-87ea-a9b400fa499b/44488818-09b3-49db-9749-a9b400fa7a93/original.jpg"]}
*/
data class SRLotData(
    override val platformCode: String = "SR",
    val pageType: String,
    val referrer: String,
    val lotId: String,
    val lotName: String,
    val categoryCode : String,
    var lotCategory : String,
    val lotTags : String,
    val lotItemType : String,
    val minEstimate : String,
    val maxEstimate : String,
    val openingPrice : String,
    val lotImageCount : String,
    val auctioneer : String,
    val lotStartDate : String,
    val lotPrimaryCategory : String,
    var lotStatus : String,
    val catalogueId : String,
    val catalogueName : String,
    val auctionCountry : String,
    val auctionCity : String,
    @SerializedName("lot-number")
    val lotNumber : String,
    var auction_date : String,
    val categoryCodeNew : String,
    val url : String,
    @SerializedName("Amount")
    var amount : String, //"Not publish",
    @SerializedName("Currency closed")
    val currency: String,
    @SerializedName("Content translate")
    val description: String,
    @SerializedName("img")
    val imageList : List<String> = mutableListOf<String>()
) : BaseLot {
    override fun save(file: String) {
        TODO("Not yet implemented")
    }

    override fun fix() {
        auction_date = auction_date.substring(0..9)
        if (auction_date=="") auction_date = lotStartDate
        if (amount == "Not publish") amount = "0"
        if (lotStatus == "WaitingToBeOffered") lotStatus = "Passed"
    }
}