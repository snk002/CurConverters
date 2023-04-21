import legacy.BaseLot

//Legacy!

import com.google.gson.annotations.SerializedName
import getCat

/*33247708.json
{
"@context": "http://schema.org",
"@type": "CreativeWork",
"mainEntityOfPage": {
 "@type": "WebPage",
 "@id": "https://www.lotsearch.net/lot/suntory-dragon-1-distilled-blended-and-bottled-by-suntory-limited-in-33247708"
 },
"headline": "Suntory-Dragon (1) Distilled, Blended",
"image": "https://images3.bonhams.com/image?src=Images/live/2017-10/17/24686807-15-2.jpg",
"datePublished": "2018-01-18T17:08:40+00:00",
"publisher": {
 "name": "Bonhams London",
 "@type": "Organization"
 },
"description":
"Suntory-Dragon (1) Distilled, Blended and Bottled by Suntory Limited In ceramic bottle. In original carton. 600ml. Blended., 43% volume. Suntory-Dragon-12 year old (1) Distilled, Blended and Bottled by Suntory Limited In ceramic bottle. In original carton. 600ml. Blended., 43% volume. Suntory-Dragon-15 year old (1) Distilled, Blended and Bottled by Suntory Limited In ceramic bottle. In original carton. 600ml. Blended., 43% volume. 3 ceramic bottles",
"email":"info@lotsearch.de",
"Auction house":"Bonhams London",
"Title":"Whisky",
"Date of the auction":"2 Feb 2018",
"Address":"Bonhams London Hong Kong, Admiralty Suite 2001, One Pacific Place 88 Queensway, Admiralty Hong Kong Tel: +852 2918 4321 Fax : +852 2918 4320 [email protected]",
"Estimate":"HK$4,000 - HK$6,000 ca. US$511 - US$767",
"Price realised":"HK$5,145 ca. US$657",
"Lot number":"1",
"gotolot":"https://www.bonhams.com/auctions/24765/lot/1/?category=list\&length=100\&page=1",
"img":[]
}
*/

data class LSLotData(
    override val platformCode: String = "LS",
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