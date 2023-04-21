import legacy.BaseLot

//Legacy!

import com.google.gson.annotations.SerializedName
import getCat

/*
"@id":"https://www.liveauctioneers.com/item/5058_1-14-assorted-fiesta-medium-green-pieces-a"
,"itemId":"5058"
,"lotNumber":"0001"
,"highBidEstimate":"900"
,"lowBidEstimate":"600"
,"startPrice":"300"
,"salePrice":"823"
,"sellerId":"121"
,"auction name":"Rago Modern Auctions, LLP"
,"sale name":"Roseville\u002FZanesville Vintage Fiesta Auction"
,"img":["https://p1.liveauctioneers.com/121/10/5058_1_x.jpg"]
,"lot title":"1: 14 assorted FIESTA medium green pieces: a"
,"lot description":"14 assorted FIESTA medium green pieces: a teapot and lid (stilt-pull chip does not reach glaze), creamer, sugar and lid (glaze scrape in making), four plates: 10², 9 1\u002F4², 7 1\u002F4² (glaze misses), 6 1\u002F4², a cup and saucer, and three bowls: 8 1\u002F4², 5 1\u002F2² (minute fleck to rim), 7 3\u002F4² (scratches)."
,"Category":["Home & Garden","Serveware, Flatware & Barware"]

*/
data class LALotData(
    override val platformCode: String = "LA",
    val itemId: String,
    val lotNumber: String,
    val highBidEstimate : String,
    val lowBidEstimate : String,
    val startPrice : String,
    val salePrice : String,
    val sellerId : String,
    @SerializedName("auction name")
    val auctionName : String,
    @SerializedName("sale name")
    val saleName : String,
    @SerializedName("lot title")
    val lotTitle: String,
    @SerializedName("lot description")
    val lotDescription : String,
    val url : String,
    @SerializedName("img")
    val imageList : List<String> = mutableListOf<String>(),
    @SerializedName("Category")
    val categories: List<String> = mutableListOf<String>()
) : BaseLot {
    override fun save(file: String) {
        TODO("Not yet implemented")
    }

    override fun fix() {

    }
}